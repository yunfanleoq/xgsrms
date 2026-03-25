package org.jeecg.modules.recruitment.resume.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.core.response.HttpxBinaryResponseContent;
import com.zhipu.oapi.service.v4.file.FileApiResponse;
import com.zhipu.oapi.service.v4.file.UploadFileRequest;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jeecg.chatgpt.service.AiChatService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.Md5Util;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.recruitment.resume.entity.XgsUserResumeFile;
import org.jeecg.modules.recruitment.resume.mapper.XgsUserResumeFileMapper;
import org.jeecg.modules.recruitment.resume.service.IXgsUserResumeFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 用户简历文件分析
 * @Author: Liuyl
 * @Date:   2024-12-31
 * @Version: V1.0
 */
@Slf4j
@Service
public class XgsUserResumeFileServiceImpl extends ServiceImpl<XgsUserResumeFileMapper, XgsUserResumeFile> implements IXgsUserResumeFileService {

    /** 智谱 ClientV4；未配置 Bean 时不影响系统启动，简历 AI 解析将不可用 */
    @Autowired(required = false)
    private ClientV4 clientV4;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Autowired
    private BaseCommonService baseCommonService;

    /**
     * 分析简历
     * @param xgsUserResumeFile
     * @return
     */
    @Override
    public XgsUserResumeFile analysisResume(XgsUserResumeFile xgsUserResumeFile) {
        if (clientV4 == null) {
            throw new JeecgBootException("智谱 AI 未配置（未注册 ClientV4 Bean），简历智能解析功能暂不可用。");
        }
        String filePath = upLoadPath + File.separator + xgsUserResumeFile.getFilePath();
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();
            // 查询缓存
//            QueryWrapper<XgsUserResumeFile> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("file_md5", md5);
//            List<XgsUserResumeFile> list = list(queryWrapper);
//            if (list.size() > 0) {
//                return list.get(list.size() - 1);
//            }
            UploadFileRequest request = UploadFileRequest.builder().purpose("file-extract").filePath(filePath).build();
            com.zhipu.oapi.service.v4.file.File resFile = clientV4.getChatApiService().uploadFile(request).blockingGet();
            HttpxBinaryResponseContent responseContent = clientV4.getChatApiService().fileContent(resFile.getId());
            String text = responseContent.getText();
            xgsUserResumeFile.setFileName(FileNameUtil.getName(filePath));
            xgsUserResumeFile.setFileExt(FileNameUtil.getSuffix(filePath));
            xgsUserResumeFile.setFileSize(resFile.getBytes().intValue());
            xgsUserResumeFile.setParentId(resFile.getId());
            xgsUserResumeFile.setFileMd5(md5);
            xgsUserResumeFile.setResumeInfo(text);
            Map<String, Object> extraJson = new HashMap<>();
            extraJson.put("temperature", 0.5);
            JSONObject json = JSONObject.parseObject(xgsUserResumeFile.getResumeInfo());
//            String messageContent = String.format("请对以下个人简历信息进行分析，并且生成JSON格式：\n\n简历内容如下：\n%s}", json.getString("content"));
            String[] jsonTemplate = getResumeInfoJsonTemplate();
            String messageContent = String.format("请对以下个人简历信息进行分析，根据JSON模板生成JSON格式数据\n\n" +
//                    "#JSON Key 注释\n%s\n\n" +
                    "#JSON模板\n%s\n\n" +
                    "#个人简历信息\n%s\n\n", jsonTemplate[0], json.getString("content"));
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage currentMessage = new ChatMessage(ChatMessageRole.USER.value(), messageContent);
            messages.add(currentMessage);
            Map<String, Object> completionRequest = new HashMap<>();
            completionRequest.put("model", "GLM-4-Plus");
            completionRequest.put("messages", messages);
            ModelData modelData = clientV4.getChatApiService().createChatCompletion(completionRequest).blockingGet();
            String markdownText = modelData.getChoices().get(0).getMessage().getContent().toString();
            // 提取 JSON 数据
            List<String> jsonDataList = extractJsonFromMarkdown(markdownText);
            String jsonStr = jsonDataList.iterator().next();
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            xgsUserResumeFile.setFileJson(jsonObject.toJSONString());
            saveOrUpdate(xgsUserResumeFile);
            Thread.sleep(1500);
            clientV4.getChatApiService().deletedFile(resFile.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return xgsUserResumeFile;
    }

    /**
     * 获取JSON 模板的数据
     * @return
     */
    private String[] getResumeInfoJsonTemplate() {
        String[] jsonTemplate = new String[2];
        jsonTemplate[0] = "\n" +
                "//个人基本信息开始\n" +
                "name //姓名\n" +
                "photograph //本人照片\n" +
                "sex //性别\n" +
                "nativePlace //籍贯\n" +
                "birthday //出生年月 yyyy-MM-dd\n" +
                "nation //民族\n" +
                "idNumber //身份证号\n" +
                "politicBackground //政治面貌\n" +
                "hukou //户口所在地\n" +
                "yjbys //是否应届毕业生\n" +
                "graduateCollege //毕业院校\n" +
                "education //学历\n" +
                "degree //学位\n" +
                "profession //专业\n" +
                "graduateDate //毕业时间 yyyy-MM-dd\n" +
                "workDate //参加工作时间 yyyy-MM-dd\n" +
                "workUnit //目前工作单位\n" +
                "personFilesUnit //档案所在单位\n" +
                "adminPosition //现行政职务\n" +
                "adminPositionDate //任职时间 yyyy-MM-dd\n" +
                "professionLevel //现岗位\n" +
                "professionLevelDate //聘任时间 yyyy-MM-dd\n" +
                "mobile //联系电话\n" +
                "email //邮箱\n" +
                "//个人基本信息结束\n" +
                "//工作经历开始\n" +
                "xgsResumeWorks //工作经历 数组\n" +
                "  beginDate //开始日期 yyyy-MM-dd\n" +
                "  endDate //终止日期 格式 yyyy-MM-dd 如果是至今不填充\n" +
                "  workUnit //工作单位\n" +
                "  deptment //工作部门\n" +
                "  position //职务\n" +
                "  professionLevel //专业技术岗位或职员职级\n" +
                "//工作经历结束\n" +
                "// 教育经历开始\n" +
                "xgsResumeEdus //教育经历 数组\n" +
                "  graduateCollege // 毕业院校\n" +
                "  profession // 专业\n" +
                "  education // 学历\n" +
                "  degree // 学位\n" +
                "  beginDate // 起始日期 yyyy-MM-dd\n" +
                "  endDate // 终止日期 格式 yyyy-MM-dd 如果是至今不填充 \n" +
                "// 教育经历结束\n" +
                "//家庭状况开始\n" +
                "xgsResumeHome //家庭状况 数组\n" +
                "  relation // 关系\n" +
                "  name // 家属姓名\n" +
                "  sex // 性别\n" +
                "  nationality // 国籍\n" +
                "  nativePlace // 籍贯\n" +
                "  birthday // 出生年月 yyyy-MM-dd\n" +
                "  nation // 民族\n" +
                "  idNumber // 身份证号\n" +
                "  politicBackground // 政治面貌\n" +
                "  hukou // 户口所在地\n" +
                "  graduateCollege // 毕业院校\n" +
                "  education // 学历\n" +
                "  degree // 学位\n" +
                "  profession // 专业\n" +
                "  graduateDate // 毕业时间 yyyy-MM-dd\n" +
                "  workDate // 参加工作时间 yyyy-MM-dd\n" +
                "  workUnit // 目前工作单位\n" +
                "//家庭状况结束\n";
        jsonTemplate[1] = "";
        return jsonTemplate;
    }

    /**
     * 从 Markdown 文本中提取 JSON 数据
     *
     * @param markdownText Markdown 文本
     * @return 提取的 JSON 数据列表
     */
    public static List<String> extractJsonFromMarkdown(String markdownText) {
        List<String> jsonDataList = new ArrayList<>();

        // 正则表达式匹配代码块中的 JSON（以 ```json 开头）
        String jsonBlockPattern = "```json\\s*([\\s\\S]*?)\\s*```";
        // 正则表达式匹配内联的 JSON（用反引号包裹）
        String inlineJsonPattern = "`(\\{.*?\\}|\\[.*?\\])`";

        // 提取代码块中的 JSON
        extractMatches(markdownText, jsonBlockPattern, jsonDataList);
        // 提取内联的 JSON
        extractMatches(markdownText, inlineJsonPattern, jsonDataList);

        return jsonDataList;
    }

    /**
     * 使用正则表达式提取匹配的内容
     *
     * @param text    输入文本
     * @param pattern 正则表达式
     * @param matches 存储匹配结果的列表
     */
    private static void extractMatches(String text, String pattern, List<String> matches) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);

        while (matcher.find()) {
            matches.add(matcher.group(1).trim()); // 提取匹配的内容并去除空白字符
        }
    }
}

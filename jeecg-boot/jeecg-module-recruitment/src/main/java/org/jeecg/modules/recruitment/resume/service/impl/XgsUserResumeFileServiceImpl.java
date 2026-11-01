package org.jeecg.modules.recruitment.resume.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.core.response.HttpxBinaryResponseContent;
import com.zhipu.oapi.service.v4.file.UploadFileRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.recruitment.resume.entity.XgsUserResumeFile;
import org.jeecg.modules.recruitment.resume.mapper.XgsUserResumeFileMapper;
import org.jeecg.modules.recruitment.resume.parse.LocalResumeTikaParser;
import org.jeecg.modules.recruitment.resume.parse.ResumeRuleMapper;
import org.jeecg.modules.recruitment.resume.service.IXgsUserResumeFileService;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
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

    /** 智谱 ClientV4；仅 resume.parse.mode=zhipu 时需要 */
    @Autowired(required = false)
    private ClientV4 clientV4;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    /**
     * 简历解析模式：tika（默认，本地抽文本+规则）| zhipu（可选云端 GLM）
     */
    @Value("${resume.parse.mode:tika}")
    private String resumeParseMode;

    @Autowired
    private BaseCommonService baseCommonService;

    @Autowired
    private LocalResumeTikaParser localResumeTikaParser;

    @Autowired
    private ResumeRuleMapper resumeRuleMapper;

    @Override
    public XgsUserResumeFile analysisResume(XgsUserResumeFile xgsUserResumeFile) {
        String mode = resumeParseMode == null ? "tika" : resumeParseMode.trim().toLowerCase();
        if ("zhipu".equals(mode)) {
            return analysisResumeByZhipu(xgsUserResumeFile);
        }
        return analysisResumeByTika(xgsUserResumeFile);
    }

    /**
     * 默认：Apache Tika 抽文本 + 规则映射；不要求 ClientV4。
     */
    private XgsUserResumeFile analysisResumeByTika(XgsUserResumeFile xgsUserResumeFile) {
        String filePath = upLoadPath + File.separator + xgsUserResumeFile.getFilePath();
        try {
            File file = new File(filePath);
            if (!file.isFile()) {
                throw new JeecgBootException("简历文件不存在：" + xgsUserResumeFile.getFilePath());
            }
            String md5;
            try (FileInputStream fis = new FileInputStream(file)) {
                md5 = DigestUtils.md5Hex(fis);
            }
            String text = localResumeTikaParser.extractText(Path.of(file.getAbsolutePath()));
            ResumeRuleMapper.MapResult mapped = resumeRuleMapper.map(text);

            xgsUserResumeFile.setFileName(FileNameUtil.getName(filePath));
            xgsUserResumeFile.setFileExt(FileNameUtil.getSuffix(filePath));
            xgsUserResumeFile.setFileSize((int) Math.min(file.length(), Integer.MAX_VALUE));
            xgsUserResumeFile.setFileMd5(md5);
            xgsUserResumeFile.setResumeInfo(text.length() > 8000 ? text.substring(0, 8000) : text);
            xgsUserResumeFile.setFileJson(mapped.json.toJSONString());
            xgsUserResumeFile.setParseMode("tika");
            xgsUserResumeFile.setFilledFields(mapped.filled);
            xgsUserResumeFile.setMissedFields(mapped.missed);
            xgsUserResumeFile.setParseSummary(String.format(
                    "本地 Tika 解析完成：已识别 %d 项，未识别 %d 项。未识别：%s",
                    mapped.filled.size(),
                    mapped.missed.size(),
                    String.join("、", mapped.missed.isEmpty() ? List.of("无") : mapped.missed)
            ));
            xgsUserResumeFile.setRemarks(xgsUserResumeFile.getParseSummary());
            saveOrUpdate(xgsUserResumeFile);
            return xgsUserResumeFile;
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            log.error("Tika 简历解析失败", e);
            throw new JeecgBootException("本地简历解析失败：" + e.getMessage());
        }
    }

    /**
     * 可选：智谱云抽取。未配置 ClientV4 时明确失败。
     */
    private XgsUserResumeFile analysisResumeByZhipu(XgsUserResumeFile xgsUserResumeFile) {
        if (clientV4 == null) {
            throw new JeecgBootException("简历解析模式为 zhipu，但智谱 AI 未配置（未注册 ClientV4 Bean）。可改回 resume.parse.mode=tika。");
        }
        String filePath = upLoadPath + File.separator + xgsUserResumeFile.getFilePath();
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();
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
            JSONObject json = JSONObject.parseObject(xgsUserResumeFile.getResumeInfo());
            String[] jsonTemplate = getResumeInfoJsonTemplate();
            String messageContent = String.format("请对以下个人简历信息进行分析，根据JSON模板生成JSON格式数据\n\n" +
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
            List<String> jsonDataList = extractJsonFromMarkdown(markdownText);
            String jsonStr = jsonDataList.iterator().next();
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            xgsUserResumeFile.setFileJson(jsonObject.toJSONString());
            xgsUserResumeFile.setParseMode("zhipu");
            xgsUserResumeFile.setParseSummary("智谱 GLM 解析完成");
            saveOrUpdate(xgsUserResumeFile);
            Thread.sleep(1500);
            clientV4.getChatApiService().deletedFile(resFile.getId());
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return xgsUserResumeFile;
    }

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

    public static List<String> extractJsonFromMarkdown(String markdownText) {
        List<String> jsonDataList = new ArrayList<>();
        String jsonBlockPattern = "```json\\s*([\\s\\S]*?)\\s*```";
        String inlineJsonPattern = "`(\\{.*?\\}|\\[.*?\\])`";
        extractMatches(markdownText, jsonBlockPattern, jsonDataList);
        extractMatches(markdownText, inlineJsonPattern, jsonDataList);
        return jsonDataList;
    }

    private static void extractMatches(String text, String pattern, List<String> matches) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group(1).trim());
        }
    }
}

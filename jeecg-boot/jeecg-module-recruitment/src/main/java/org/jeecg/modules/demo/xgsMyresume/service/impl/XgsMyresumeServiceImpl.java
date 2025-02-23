package org.jeecg.modules.demo.xgsMyresume.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.alibaba.fastjson.JSONObject;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.core.response.HttpxBinaryResponseContent;
import com.zhipu.oapi.service.v4.file.UploadFileRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelData;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.demo.xgsMyresume.entity.XgsMyresume;
import org.jeecg.modules.demo.xgsMyresume.mapper.XgsMyresumeMapper;
import org.jeecg.modules.demo.xgsMyresume.service.IXgsMyresumeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 简历列表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Service
public class XgsMyresumeServiceImpl extends ServiceImpl<XgsMyresumeMapper, XgsMyresume> implements IXgsMyresumeService {

    @Autowired
    ClientV4 clientV4;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Autowired
    private BaseCommonService baseCommonService;

    @Override
    public void analysisResume(XgsMyresume xgsMyresume) {
        String filePath = upLoadPath + File.separator + xgsMyresume.getResumeFile();
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            fis.close();

            UploadFileRequest request = UploadFileRequest.builder().purpose("file-extract").filePath(filePath).build();
            com.zhipu.oapi.service.v4.file.File resFile = clientV4.getChatApiService().uploadFile(request).blockingGet();
            HttpxBinaryResponseContent responseContent = clientV4.getChatApiService().fileContent(resFile.getId());
            String text = responseContent.getText();
            Map<String, Object> extraJson = new HashMap<>();
            extraJson.put("temperature", 0.5);
            JSONObject json = JSONObject.parseObject(text);
            String[] jsonTemplate = getResumeInfoJsonTemplate();
            String messageContent = String.format("请对以下个人简历信息进行分析，根据JSON模板生成JSON格式数据，结果直接返回JSON格式\n\n" +
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
            System.out.println("-------->" + markdownText);
            // 提取 JSON 数据
            List<String> jsonDataList = extractJsonFromMarkdown(markdownText);
            String jsonStr = jsonDataList.iterator().next();
            XgsMyresume xgsUserResumeFile = JSONObject.parseObject(jsonStr, XgsMyresume.class);
            BeanUtils.copyProperties(xgsUserResumeFile, xgsMyresume);
            saveOrUpdate(xgsMyresume);
            clientV4.getChatApiService().deletedFile(resFile.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    /**
     * 获取JSON 模板的数据
     * @return
     */
    private String[] getResumeInfoJsonTemplate() {
        String[] jsonTemplate = new String[2];
        jsonTemplate[0] = "/**姓名*/\n" +
                "name;\n" +
                "/**本人照片*/\n" +
                "photograph;\n" +
                "/**性别*/\n" +
                "sex;\n" +
                "/**籍贯*/\n" +
                "nativePlace;\n" +
                "/**出生年月 \"yyyy-MM-dd\"*/ \n" +
                "birthday;\n" +
                "/**民族*/\n" +
                "nation;\n" +
                "/**身份证号*/\n" +
                "idNumber;\n" +
                "/**婚姻状况*/\n" +
                "maritalStatus;\n" +
                "/**政治面貌*/\n" +
                "politicBackground;\n" +
                "/**国籍*/\n" +
                "nationality;\n" +
                "/**户口所在地*/\n" +
                "hukou;\n" +
                "/**是否应届毕业生*/\n" +
                "yjbys;\n" +
                "/**是否统招统分*/\n" +
                "tztf;\n" +
                "/**毕业院校*/\n" +
                "graduateCollege;\n" +
                "/**学历*/\n" +
                "education;\n" +
                "/**学位*/\n" +
                "degree;\n" +
                "/**专业*/\n" +
                "profession;\n" +
                "/**毕业时间*/\n" +
                "graduateDate;\n" +
                "/**参加工作时间*/\n" +
                "workDate;\n" +
                "/**目前工作单位*/\n" +
                "workUnit;\n" +
                "/**档案所在单位*/\n" +
                "personFilesUnit;\n" +
                "/**现行政职务*/\n" +
                "adminPosition;\n" +
                "/**任职时间*/\n" +
                "adminPositionDate;\n" +
                "/**现岗位*/\n" +
                "professionLevel;\n" +
                "/**聘任时间*/\n" +
                "professionLevelDate;\n" +
                "/**与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）*/\n" +
                "xgsRelation;\n" +
                "/**联系电话*/\n" +
                "mobile;\n" +
                "/**E-mail*/\n" +
                "email;\n" +
                "/**学习经历*/\n" +
                "studyExperience;\n" +
                "/**研究方向与专长*/\n" +
                "researchDirection;\n" +
                "/**承担科研、管理工作情况*/\n" +
                "researchWork;\n" +
                "/**主要论著目录*/\n" +
                "researchWorks;\n" +
                "/**工作主要业绩*/\n" +
                "researchResult;\n" +
                "/**论文专著专利*/\n" +
                "researchPaper;\n" +
                "/**获科技奖情况*/\n" +
                "carryPrizeScience;\n" +
                "/**获荣誉奖情况*/\n" +
                "carryPrizeHonor;\n" +
                "/**拟研究计划*/\n" +
                "researchProposal;\n" +
                "/**科研条件*/\n" +
                "researchCondition;\n" +
                "/**研究问题协助*/\n" +
                "researchQuestionsSolve;\n" +
                "/**应聘岗位陈述*/\n" +
                "positionDescription;\n" +
                "/**应聘部门*/\n" +
                "applyDept;\n" +
                "/**应聘岗位名称*/\n" +
                "applyPosition;\n" +
                "/**岗位类型*/\n" +
                "positionType;\n" +
                "/**所在地*/\n" +
                "areaId;\n" +
                "/**被推荐人*/\n" +
                "propositus;\n" +
                "/**推荐人*/\n" +
                "referrer;\n" +
                "/**推荐人技术职务*/\n" +
                "referrerPosition;\n" +
                "/**推荐人工作单位*/\n" +
                "referrerUnit;\n" +
                "/**推荐人联系方式*/\n" +
                "referrerMobile;\n" +
                "/**推荐人电子邮箱*/\n" +
                "referrerEmail;\n" +
                "/**推荐人签名*/\n" +
                "referrerSignature;\n" +
                "/**拟申报岗位等级*/\n" +
                "positionClass;\n" +
                "/**推荐原因*/\n" +
                "cause;\n" +
                "/**研究室*/\n" +
                "lob;\n" +
                "/**博士后研究方向*/\n" +
                "researchOrientation;\n" +
                "/**申请人当前身份*/\n" +
                "proposerStatus;\n" +
                "/**申报博士后类型*/\n" +
                "postdoctorType;\n" +
                "/**合作导师*/\n" +
                "teacher;\n" +
                "/**博士毕业院校*/\n" +
                "graduateInstitutions;\n" +
                "/**一级学科*/\n" +
                "subject1;\n" +
                "/**二级学科*/\n" +
                "subject2;\n" +
                "/**博士学位证书 获得时间*/\n" +
                "certificateTime;\n" +
                "/**现专业技术职务*/\n" +
                "technicalPosition;\n" +
                "/**在站期间研究内容有无涉密内容*/\n" +
                "secretText;\n" +
                "/**一站单位名称*/\n" +
                "workFirst;\n" +
                "/**导师评价*/\n" +
                "teacherEvaluate;\n" +
                "/**导师签字*/\n" +
                "teacherSignature;\n" +
                "/**研究室意见*/\n" +
                "labIdea;\n" +
                "/**研究室负责人签字*/\n" +
                "labSignature;\n" +
                "/**人力资源处意见*/\n" +
                "resourcesIdea;\n" +
                "/**人力资源处签字*/\n" +
                "resourceSsignature;\n" +
                "/**研究所意见*/\n" +
                "schoolSsignature;\n" +
                "/**研究所签字*/\n" +
                "schoolIdea;\n";
        jsonTemplate[1] = "";
        return jsonTemplate;
    }
}

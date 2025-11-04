package org.jeecg.modules.recruitment.xgsExport.vo;

import lombok.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 简历导出VO对象
 * @Author: System
 * @Date: 2025-01-23
 * @Version: V1.0
 */
@Data
public class XgsResumeExportVO {

    private String id;
    private String userId;
    private String name;
    private String photograph;
    private String sex;
    private String nativePlace;
    private Date birthday;
    private String nation;
    private String idNumber;
    private String maritalStatus;
    private String politicBackground;
    private String nationality;
    private String hukou;
    private String yjbys;
    private String tztf;
    private String graduateCollege;
    private String education;
    private String degree;
    private String profession;
    private Date graduateDate;
    private Date workDate;
    private String workUnit;
    private String personFilesUnit;
    private String adminPosition;
    private Date adminPositionDate;
    private String professionLevel;
    private Date professionLevelDate;
    private String xgsRelation;
    private String mobile;
    private String email;
    private String studyExperience;
    private String researchDirection;
    private String researchWork;
    private String researchWorks;
    private String researchResult;
    private String researchPaper;
    private String carryPrizeScience;
    private String carryPrizeHonor;
    private String researchProposal;
    private String researchCondition;
    private String researchQuestionsSolve;
    private String positionDescription;
    private String applyDept;
    private String applyPosition;
    private String positionType;
    private Integer areaId;
    private String propositus;
    private String referrer;
    private String referrerPosition;
    private String referrerUnit;
    private String referrerMobile;
    private String referrerEmail;
    private String referrerSignature;
    private String positionClass;
    private String cause;
    private String lob;
    private String researchOrientation;
    private String proposerStatus;
    private String postdoctorType;
    private String teacher;
    private String graduateInstitutions;
    private String subject1;
    private String subject2;
    private Date certificateTime;
    private String technicalPosition;
    private String secretText;
    private String workFirst;
    private String teacherEvaluate;
    private String teacherSignature;
    private String labIdea;
    private String labSignature;
    private String resourcesIdea;
    private String resourceSsignature;
    private String schoolSsignature;
    private String schoolIdea;
    private String resumeName;
    private String resumeType;

    /**
     * 获取字段与中文书签的映射关系（用于auto-poi模板导出）
     * auto-poi会自动替换模板中的{{变量名}}占位符
     * @return 字段名->字段值的映射
     */
    public Map<String, Object> getBookmarkMap() {
        Map<String, Object> bookmarkMap = new HashMap<>();
        
        // 基本信息
        bookmarkMap.put("姓名", nvl(name));
        bookmarkMap.put("性别", nvl(sex));
        bookmarkMap.put("籍贯", nvl(nativePlace));
        bookmarkMap.put("出生年月", formatDate(birthday));
        bookmarkMap.put("民族", nvl(nation));
        bookmarkMap.put("身份证号", nvl(idNumber));
        bookmarkMap.put("婚姻状况", nvl(maritalStatus));
        bookmarkMap.put("政治面貌", nvl(politicBackground));
        bookmarkMap.put("国籍", nvl(nationality));
        bookmarkMap.put("户口所在地", nvl(hukou));
        
        // 学历信息
        bookmarkMap.put("是否应届毕业生", nvl(yjbys));
        bookmarkMap.put("是否统招统分", nvl(tztf));
        bookmarkMap.put("毕业院校", nvl(graduateCollege));
        bookmarkMap.put("学历", nvl(education));
        bookmarkMap.put("学位", nvl(degree));
        bookmarkMap.put("专业", nvl(profession));
        bookmarkMap.put("毕业时间", formatDate(graduateDate));
        
        // 工作信息
        bookmarkMap.put("参加工作时间", formatDate(workDate, "yyyy年MM月"));
        bookmarkMap.put("目前工作单位", nvl(workUnit));
        bookmarkMap.put("档案所在单位", nvl(personFilesUnit));
        bookmarkMap.put("现行政职务", nvl(adminPosition));
        bookmarkMap.put("任职时间", formatDate(adminPositionDate, "yyyy年MM月"));
        bookmarkMap.put("任职时间年", formatDate(adminPositionDate, "yyyy"));
        bookmarkMap.put("任职时间月", formatDate(adminPositionDate, "MM"));
        bookmarkMap.put("现岗位", nvl(professionLevel));
        bookmarkMap.put("聘任时间", formatDate(professionLevelDate, "yyyy年MM月"));
        bookmarkMap.put("聘任时间年", formatDate(professionLevelDate, "yyyy"));
        bookmarkMap.put("聘任时间月", formatDate(professionLevelDate, "MM"));

        // 联系方式
        bookmarkMap.put("与本所职工关系", nvl(xgsRelation));
        bookmarkMap.put("联系电话", nvl(mobile));
        bookmarkMap.put("电子邮箱", nvl(email));
        
        // 研究相关
        bookmarkMap.put("学习经历", nvl(studyExperience));
        bookmarkMap.put("研究方向与专长", nvl(researchDirection));
        bookmarkMap.put("承担科研管理工作情况", nvl(researchWork));
        bookmarkMap.put("工作主要业绩", nvl(researchResult));
        bookmarkMap.put("主要论著目录", nvl(researchWorks));
        bookmarkMap.put("论文专著专利", nvl(researchPaper));
        bookmarkMap.put("获科技奖情况", nvl(carryPrizeScience));
        bookmarkMap.put("获荣誉奖情况", nvl(carryPrizeHonor));
        bookmarkMap.put("拟研究计划", nvl(researchProposal));
        bookmarkMap.put("科研条件", nvl(researchCondition));
        bookmarkMap.put("研究问题协助", nvl(researchQuestionsSolve));
        
        // 应聘信息
        bookmarkMap.put("应聘岗位陈述", nvl(positionDescription));  // [[{"id":"","positionDescription":"asdfasdf","tempId":"temp_1"}]]
        bookmarkMap.put("应聘部门", nvl(applyDept));
        bookmarkMap.put("应聘岗位名称", nvl(applyPosition));
        bookmarkMap.put("岗位类型", nvl(positionType));
        
        // 推荐信息（专家推荐岗位）
        bookmarkMap.put("被推荐人", nvl(propositus));
        bookmarkMap.put("推荐人", nvl(referrer));
        bookmarkMap.put("推荐人技术职务", nvl(referrerPosition));
        bookmarkMap.put("推荐人工作单位", nvl(referrerUnit));
        bookmarkMap.put("推荐人联系方式", nvl(referrerMobile));
        bookmarkMap.put("推荐人电子邮箱", nvl(referrerEmail));
        bookmarkMap.put("推荐人签名", nvl(referrerSignature));
        bookmarkMap.put("拟申报岗位等级", nvl(positionClass));
        bookmarkMap.put("推荐原因", nvl(cause));
        
        // 博士后相关
        bookmarkMap.put("研究室", nvl(lob));
        bookmarkMap.put("博士后研究方向", nvl(researchOrientation));
        bookmarkMap.put("申请人当前身份", nvl(proposerStatus));
        bookmarkMap.put("申报博士后类型", nvl(postdoctorType));
        bookmarkMap.put("合作导师", nvl(teacher));
        bookmarkMap.put("博士毕业院校", nvl(graduateInstitutions));
        bookmarkMap.put("一级学科", nvl(subject1));
        bookmarkMap.put("二级学科", nvl(subject2));
        bookmarkMap.put("博士学位证书获得时间", formatDate(certificateTime));
        bookmarkMap.put("现专业技术职务", nvl(technicalPosition));
        bookmarkMap.put("涉密内容", nvl(secretText));
        bookmarkMap.put("一站单位名称", nvl(workFirst));
        bookmarkMap.put("导师评价", nvl(teacherEvaluate));
        bookmarkMap.put("导师签字", nvl(teacherSignature));
        bookmarkMap.put("研究室意见", nvl(labIdea));
        bookmarkMap.put("研究室负责人签字", nvl(labSignature));
        bookmarkMap.put("人力资源处意见", nvl(resourcesIdea));
        bookmarkMap.put("人力资源处签字", nvl(resourceSsignature));
        bookmarkMap.put("研究所意见", nvl(schoolIdea));
        bookmarkMap.put("研究所签字", nvl(schoolSsignature));
        
        // 简历信息
        bookmarkMap.put("简历名称", nvl(resumeName));
        bookmarkMap.put("简历类别", nvl(resumeType));
        
        return bookmarkMap;
    }

    /**
     * null值转换为空字符串
     */
    private String nvl(String value) {
        return value == null ? "" : value;
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    /**
     * 格式化日期
     */
    private String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}


package org.jeecg.modules.demo.positions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 岗位申请
 * @Author: jeecg-boot
 * @Date:   2024-12-24
 * @Version: V1.0
 */
@Data
@ApiModel(value="xgs_position_apply VO对象", description="岗位申请VO")
public class XgsPositionApplyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "岗位申请信息")
    XgsPositionApply xgsPositionApply;

    /**编号*/
    @ApiModelProperty(value = "编号")
    private java.lang.String id;
    /**用户ID*/
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
    /**姓名*/
    @ApiModelProperty(value = "姓名")
    private java.lang.String name;
    /**本人照片*/
    @ApiModelProperty(value = "本人照片")
    private java.lang.String photograph;
    /**性别*/
    @Dict(dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private java.lang.String sex;
    /**籍贯*/
    @ApiModelProperty(value = "籍贯")
    private java.lang.String nativePlace;
    /**出生年月*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "出生年月")
    private java.util.Date birthday;
    /**民族*/
    @ApiModelProperty(value = "民族")
    private java.lang.String nation;
    /**身份证号*/
    @ApiModelProperty(value = "身份证号")
    private java.lang.String idNumber;
    /**婚姻状况*/
    @ApiModelProperty(value = "婚姻状况")
    private java.lang.String maritalStatus;
    /**政治面貌*/
    @ApiModelProperty(value = "政治面貌")
    private java.lang.String politicBackground;
    /**国籍*/
    @ApiModelProperty(value = "国籍")
    private java.lang.String nationality;
    /**户口所在地*/
    @ApiModelProperty(value = "户口所在地")
    private java.lang.String hukou;
    /**是否应届毕业生*/
    @Dict(dicCode = "yes_or_no")
    @ApiModelProperty(value = "是否应届毕业生")
    private java.lang.String yjbys;
    /**是否统招统分*/
    @Dict(dicCode = "yes_or_no")
    @ApiModelProperty(value = "是否统招统分")
    private java.lang.String tztf;
    /**毕业院校*/
    @ApiModelProperty(value = "毕业院校")
    private java.lang.String graduateCollege;
    /**学历*/
    @ApiModelProperty(value = "学历")
    private java.lang.String education;
    /**学位*/
    @ApiModelProperty(value = "学位")
    private java.lang.String degree;
    /**专业*/
    @ApiModelProperty(value = "专业")
    private java.lang.String profession;
    /**毕业时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "毕业时间")
    private java.util.Date graduateDate;
    /**参加工作时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "参加工作时间")
    private java.util.Date workDate;
    /**目前工作单位*/
    @ApiModelProperty(value = "目前工作单位")
    private java.lang.String workUnit;
    /**档案所在单位*/
    @ApiModelProperty(value = "档案所在单位")
    private java.lang.String personFilesUnit;
    /**现行政职务*/
    @ApiModelProperty(value = "现行政职务")
    private java.lang.String adminPosition;
    /**任职时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任职时间")
    private java.util.Date adminPositionDate;
    /**现岗位*/
    @ApiModelProperty(value = "现岗位")
    private java.lang.String professionLevel;
    /**聘任时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "聘任时间")
    private java.util.Date professionLevelDate;
    /**与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）*/
    @ApiModelProperty(value = "与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）")
    private java.lang.String xgsRelation;
    /**联系电话*/
    @ApiModelProperty(value = "联系电话")
    private java.lang.String mobile;
    /**E-mail*/
    @ApiModelProperty(value = "E-mail")
    private java.lang.String email;
    /**学习经历*/
    @ApiModelProperty(value = "学习经历")
    private java.lang.String studyExperience;
    /**研究方向与专长*/
    @ApiModelProperty(value = "研究方向与专长")
    private java.lang.String researchDirection;
    /**承担科研、管理工作情况*/
    @ApiModelProperty(value = "承担科研、管理工作情况")
    private java.lang.String researchWork;
    /**主要论著目录*/
    @ApiModelProperty(value = "主要论著目录")
    private java.lang.String researchWorks;
    /**工作主要业绩*/
    @ApiModelProperty(value = "工作主要业绩")
    private java.lang.String researchResult;
    /**论文专著专利*/
    @ApiModelProperty(value = "论文专著专利")
    private java.lang.String researchPaper;
    /**获科技奖情况*/
    @ApiModelProperty(value = "获科技奖情况")
    private java.lang.String carryPrizeScience;
    /**获荣誉奖情况*/
    @ApiModelProperty(value = "获荣誉奖情况")
    private java.lang.String carryPrizeHonor;
    /**拟研究计划*/
    @ApiModelProperty(value = "拟研究计划")
    private java.lang.String researchProposal;
    /**科研条件*/
    @ApiModelProperty(value = "科研条件")
    private java.lang.String researchCondition;
    /**研究问题协助*/
    @ApiModelProperty(value = "研究问题协助")
    private java.lang.String researchQuestionsSolve;
    /**应聘岗位陈述*/
    @ApiModelProperty(value = "应聘岗位陈述")
    private java.lang.String positionDescription;
    /**应聘部门*/
    @ApiModelProperty(value = "应聘部门")
    private java.lang.String applyDept;
    /**应聘岗位名称*/
    @ApiModelProperty(value = "应聘岗位名称")
    private java.lang.String applyPosition;
    /**岗位类型*/
    @Dict(dicCode = "岗位类型")
    @ApiModelProperty(value = "岗位类型")
    private java.lang.String positionType;
    /**所在地*/
    @ApiModelProperty(value = "所在地")
    private java.lang.Integer areaId;
    /**被推荐人*/
    @ApiModelProperty(value = "被推荐人")
    private java.lang.String propositus;
    /**推荐人*/
    @ApiModelProperty(value = "推荐人")
    private java.lang.String referrer;
    /**推荐人技术职务*/
    @ApiModelProperty(value = "推荐人技术职务")
    private java.lang.String referrerPosition;
    /**推荐人工作单位*/
    @ApiModelProperty(value = "推荐人工作单位")
    private java.lang.String referrerUnit;
    /**推荐人联系方式*/
    @ApiModelProperty(value = "推荐人联系方式")
    private java.lang.String referrerMobile;
    /**推荐人电子邮箱*/
    @ApiModelProperty(value = "推荐人电子邮箱")
    private java.lang.String referrerEmail;
    /**推荐人签名*/
    @ApiModelProperty(value = "推荐人签名")
    private java.lang.String referrerSignature;
    /**拟申报岗位等级*/
    @ApiModelProperty(value = "拟申报岗位等级")
    private java.lang.String positionClass;
    /**推荐原因*/
    @ApiModelProperty(value = "推荐原因")
    private java.lang.String cause;
    /**研究室*/
    @ApiModelProperty(value = "研究室")
    private java.lang.String lob;
    /**博士后研究方向*/
    @ApiModelProperty(value = "博士后研究方向")
    private java.lang.String researchOrientation;
    /**申请人当前身份*/
    @Dict(dicCode = "申请人身份")
    @ApiModelProperty(value = "申请人当前身份")
    private java.lang.String proposerStatus;
    /**申报博士后类型*/
    @Dict(dicCode = "博士后类型")
    @ApiModelProperty(value = "申报博士后类型")
    private java.lang.String postdoctorType;
    /**合作导师*/
    @ApiModelProperty(value = "合作导师")
    private java.lang.String teacher;
    /**博士毕业院校*/
    @ApiModelProperty(value = "博士毕业院校")
    private java.lang.String graduateInstitutions;
    /**一级学科*/
    @ApiModelProperty(value = "一级学科")
    private java.lang.String subject1;
    /**二级学科*/
    @ApiModelProperty(value = "二级学科")
    private java.lang.String subject2;
    /**博士学位证书 获得时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "博士学位证书 获得时间")
    private java.util.Date certificateTime;
    /**现专业技术职务*/
    @ApiModelProperty(value = "现专业技术职务")
    private java.lang.String technicalPosition;
    /**在站期间研究内容有无涉密内容*/
    @Dict(dicCode = "yes_or_no")
    @ApiModelProperty(value = "在站期间研究内容有无涉密内容")
    private java.lang.String secretText;
    /**一站单位名称*/
    @ApiModelProperty(value = "一站单位名称")
    private java.lang.String workFirst;
    /**导师评价*/
    @ApiModelProperty(value = "导师评价")
    private java.lang.String teacherEvaluate;
    /**导师签字*/
    @ApiModelProperty(value = "导师签字")
    private java.lang.String teacherSignature;
    /**研究室意见*/
    @Dict(dicCode = "研究室意见")
    @ApiModelProperty(value = "研究室意见")
    private java.lang.String labIdea;
    /**研究室负责人签字*/
    @ApiModelProperty(value = "研究室负责人签字")
    private java.lang.String labSignature;
    /**人力资源处意见*/
    @ApiModelProperty(value = "人力资源处意见")
    private java.lang.String resourcesIdea;
    /**人力资源处签字*/
    @ApiModelProperty(value = "人力资源处签字")
    private java.lang.String resourceSsignature;
    /**研究所意见*/
    @ApiModelProperty(value = "研究所意见")
    private java.lang.String schoolSsignature;
    /**研究所签字*/
    @ApiModelProperty(value = "研究所签字")
    private java.lang.String schoolIdea;
    /**标签编号列表*/
    @ApiModelProperty(value = "标签编号列表")
    private java.lang.String tagIds;
    /**等级编号*/
    @ApiModelProperty(value = "等级编号")
    private java.lang.Integer levelId;
    /**分组编号*/
    @ApiModelProperty(value = "分组编号")
    private java.lang.Integer groupId;
    /**创建者*/
    @ApiModelProperty(value = "创建者")
    private java.lang.String creator;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**更新者*/
    @ApiModelProperty(value = "更新者")
    private java.lang.String updater;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    /**是否删除*/
    @ApiModelProperty(value = "是否删除")
    private java.lang.String deleted;
    /**租户编号*/
    @ApiModelProperty(value = "租户编号")
    private java.lang.Integer tenantId;
    /**简历名称*/
    @ApiModelProperty(value = "简历名称")
    private java.lang.String resumeName;
    /**简历类别*/
    @Dict(dicCode = "岗位分类")
    @ApiModelProperty(value = "简历类别")
    private java.lang.String resumeType;

    @ApiModelProperty(value = "工作经历")
    private List<XgsResumeWorks> xgsResumeWorksList;
    @ApiModelProperty(value = "教育经历")
    private List<XgsResumeEdus> xgsResumeEdusList;
    @ApiModelProperty(value = "家庭状况")
    private List<XgsResumeHome> xgsResumeHomeList;
}

package org.jeecg.modules.demo.xgsResume.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 基本信息
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
@ApiModel(value="xgs_resume_base对象", description="基本信息")
@Data
@TableName("xgs_resume_base")
public class XgsResumeBase implements Serializable {
    private static final long serialVersionUID = 1L;

	/**编号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "编号")
    private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private java.lang.String name;
	/**性别*/
	@Excel(name = "性别", width = 15, dicCode = "sex")
    @Dict(dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private java.lang.String sex;
	/**籍贯*/
	@Excel(name = "籍贯", width = 15)
    @ApiModelProperty(value = "籍贯")
    private java.lang.String nativePlace;
	/**出生年月*/
	@Excel(name = "出生年月", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "出生年月")
    private java.util.Date birthday;
	/**民族*/
	@Excel(name = "民族", width = 15)
    @ApiModelProperty(value = "民族")
    private java.lang.String nation;
	/**身份证号*/
	@Excel(name = "身份证号", width = 15)
    @ApiModelProperty(value = "身份证号")
    private java.lang.String idNumber;
	/**政治面貌*/
	@Excel(name = "政治面貌", width = 15)
    @ApiModelProperty(value = "政治面貌")
    private java.lang.String politicBackground;
	/**户口所在地*/
	@Excel(name = "户口所在地", width = 15)
    @ApiModelProperty(value = "户口所在地")
    private java.lang.String hukou;
	/**是否应届毕业生*/
	@Excel(name = "是否应届毕业生", width = 15, dicCode = "yes_or_no")
    @Dict(dicCode = "yes_or_no")
    @ApiModelProperty(value = "是否应届毕业生")
    private java.lang.String yjbys;
	/**是否统招统分*/
	@Excel(name = "是否统招统分", width = 15, dicCode = "yes_or_no")
    @Dict(dicCode = "yes_or_no")
    @ApiModelProperty(value = "是否统招统分")
    private java.lang.String tztf;
	/**毕业院校*/
	@Excel(name = "毕业院校", width = 15)
    @ApiModelProperty(value = "毕业院校")
    private java.lang.String graduateCollege;
	/**学历*/
	@Excel(name = "学历", width = 15)
    @ApiModelProperty(value = "学历")
    private java.lang.String education;
	/**学位*/
	@Excel(name = "学位", width = 15)
    @ApiModelProperty(value = "学位")
    private java.lang.String degree;
	/**专业*/
	@Excel(name = "专业", width = 15)
    @ApiModelProperty(value = "专业")
    private java.lang.String profession;
	/**毕业时间*/
	@Excel(name = "毕业时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "毕业时间")
    private java.util.Date graduateDate;
	/**参加工作时间*/
	@Excel(name = "参加工作时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "参加工作时间")
    private java.util.Date workDate;
	/**目前工作单位*/
	@Excel(name = "目前工作单位", width = 15)
    @ApiModelProperty(value = "目前工作单位")
    private java.lang.String workUnit;
	/**档案所在单位*/
	@Excel(name = "档案所在单位", width = 15)
    @ApiModelProperty(value = "档案所在单位")
    private java.lang.String personFilesUnit;
	/**现行政职务*/
	@Excel(name = "现行政职务", width = 15)
    @ApiModelProperty(value = "现行政职务")
    private java.lang.String adminPosition;
	/**任职时间*/
	@Excel(name = "任职时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "任职时间")
    private java.util.Date adminPositionDate;
	/**现岗位*/
	@Excel(name = "现岗位", width = 15)
    @ApiModelProperty(value = "现岗位")
    private java.lang.String professionLevel;
	/**聘任时间*/
	@Excel(name = "聘任时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "聘任时间")
    private java.util.Date professionLevelDate;
	/**与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）*/
	@Excel(name = "与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）", width = 15)
    @ApiModelProperty(value = "与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）")
    private java.lang.String xgsRelation;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String mobile;
	/**E-mail*/
	@Excel(name = "E-mail", width = 15)
    @ApiModelProperty(value = "E-mail")
    private java.lang.String email;
	/**研究方向与专长*/
	@Excel(name = "研究方向与专长", width = 15)
    @ApiModelProperty(value = "研究方向与专长")
    private java.lang.String researchDirection;
	/**承担科研、管理工作情况*/
	@Excel(name = "承担科研、管理工作情况", width = 15)
    @ApiModelProperty(value = "承担科研、管理工作情况")
    private java.lang.String researchWork;
	/**工作主要业绩*/
	@Excel(name = "工作主要业绩", width = 15)
    @ApiModelProperty(value = "工作主要业绩")
    private java.lang.String researchResult;
	/**论文专著专利*/
	@Excel(name = "论文专著专利", width = 15)
    @ApiModelProperty(value = "论文专著专利")
    private java.lang.String researchPaper;
	/**应聘岗位陈述*/
	@Excel(name = "应聘岗位陈述", width = 15)
    @ApiModelProperty(value = "应聘岗位陈述")
    private java.lang.String positionDescription;
	/**所在地*/
	@Excel(name = "所在地", width = 15)
    @ApiModelProperty(value = "所在地")
    private java.lang.Integer areaId;
	/**标签编号列表*/
	@Excel(name = "标签编号列表", width = 15)
    @ApiModelProperty(value = "标签编号列表")
    private java.lang.String tagIds;
	/**等级编号*/
	@Excel(name = "等级编号", width = 15)
    @ApiModelProperty(value = "等级编号")
    private java.lang.Integer levelId;
	/**分组编号*/
	@Excel(name = "分组编号", width = 15)
    @ApiModelProperty(value = "分组编号")
    private java.lang.Integer groupId;
	/**创建者*/
	@Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
    private java.lang.String creator;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新者*/
	@Excel(name = "更新者", width = 15)
    @ApiModelProperty(value = "更新者")
    private java.lang.String updater;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
    @ApiModelProperty(value = "是否删除")
    private java.lang.String deleted;
	/**租户编号*/
	@Excel(name = "租户编号", width = 15)
    @ApiModelProperty(value = "租户编号")
    private java.lang.Integer tenantId;
}

package org.jeecg.modules.demo.xgsResume.entity;

import java.io.Serializable;
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
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 家庭状况
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
@ApiModel(value="xgs_resume_home对象", description="家庭状况")
@Data
@TableName("xgs_resume_home")
public class XgsResumeHome implements Serializable {
    private static final long serialVersionUID = 1L;

	/**编号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "编号")
    private java.lang.String id;
	/**申请人基本表编号*/
    @ApiModelProperty(value = "申请人基本表编号")
    private java.lang.String baseId;
	/**申请人姓名*/
	@Excel(name = "申请人姓名", width = 15)
    @ApiModelProperty(value = "申请人姓名")
    private java.lang.String baseName;
	/**关系*/
	@Excel(name = "关系", width = 15)
    @ApiModelProperty(value = "关系")
    private java.lang.String relation;
	/**家属姓名*/
	@Excel(name = "家属姓名", width = 15)
    @ApiModelProperty(value = "家属姓名")
    private java.lang.String name;
	/**性别*/
	@Excel(name = "性别", width = 15, dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private java.lang.String sex;
	/**国籍*/
	@Excel(name = "国籍", width = 15)
    @ApiModelProperty(value = "国籍")
    private java.lang.String nationality;
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

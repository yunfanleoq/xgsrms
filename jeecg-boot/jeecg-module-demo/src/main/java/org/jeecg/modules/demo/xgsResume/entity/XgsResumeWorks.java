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
 * @Description: 工作经历
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
@ApiModel(value="xgs_resume_works对象", description="工作经历")
@Data
@TableName("xgs_resume_works")
public class XgsResumeWorks implements Serializable {
    private static final long serialVersionUID = 1L;

	/**编号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "编号")
    private java.lang.String id;
	/**申请人基本信息主键*/
    @ApiModelProperty(value = "申请人基本信息主键")
    private java.lang.String baseId;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private java.lang.String name;
	/**身份证号*/
	@Excel(name = "身份证号", width = 15)
    @ApiModelProperty(value = "身份证号")
    private java.lang.String idNumber;
	/**开始日期*/
	@Excel(name = "开始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private java.util.Date beginDate;
	/**终止日期*/
	@Excel(name = "终止日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "终止日期")
    private java.util.Date endDate;
	/**工作单位*/
	@Excel(name = "工作单位", width = 15)
    @ApiModelProperty(value = "工作单位")
    private java.lang.String workUnit;
	/**工作部门*/
	@Excel(name = "工作部门", width = 15)
    @ApiModelProperty(value = "工作部门")
    private java.lang.String deptment;
	/**职务*/
	@Excel(name = "职务", width = 15)
    @ApiModelProperty(value = "职务")
    private java.lang.String position;
	/**专业技术岗位或职员职级*/
	@Excel(name = "专业技术岗位或职员职级", width = 15)
    @ApiModelProperty(value = "专业技术岗位或职员职级")
    private java.lang.String professionLevel;
	/**创建者*/
	@Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新者*/
	@Excel(name = "更新者", width = 15)
    @ApiModelProperty(value = "更新者")
    private java.lang.String updateBy;
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
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private java.lang.Integer sysOrgCode;
}

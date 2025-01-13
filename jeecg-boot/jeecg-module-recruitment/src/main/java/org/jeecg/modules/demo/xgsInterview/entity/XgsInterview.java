package org.jeecg.modules.demo.xgsInterview.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 面试管理
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Data
@TableName("xgs_interview")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="xgs_interview对象", description="面试管理")
public class XgsInterview implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**职位id*/
	@Excel(name = "职位id", width = 15)
    @ApiModelProperty(value = "职位id")
    private java.lang.String positionId;
	/**职位名称*/
	@Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
    private java.lang.String positionName;
	/**候选人id*/
	@Excel(name = "候选人id", width = 15)
    @ApiModelProperty(value = "候选人id")
    private java.lang.String candidateId;
	/**候选人*/
	@Excel(name = "候选人", width = 15)
    @ApiModelProperty(value = "候选人")
    private java.lang.String candidate;
	/**面试官id*/
	@Excel(name = "面试官id", width = 15)
    @ApiModelProperty(value = "面试官id")
    private java.lang.String interviewerId;
	/**面试官*/
	@Excel(name = "面试官", width = 15)
    @ApiModelProperty(value = "面试官")
    private java.lang.String interviewer;
	/**面试状态*/
	@Excel(name = "面试状态", width = 15, dicCode = "面试状态")
	@Dict(dicCode = "面试状态")
    @ApiModelProperty(value = "面试状态")
    private java.lang.String status;
	/**面试时间*/
	@Excel(name = "面试时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "面试时间")
    private java.util.Date interviewDate;
	/**面试反馈*/
	@Excel(name = "面试反馈", width = 15)
    @ApiModelProperty(value = "面试反馈")
    private java.lang.String interviewFeedback;
}

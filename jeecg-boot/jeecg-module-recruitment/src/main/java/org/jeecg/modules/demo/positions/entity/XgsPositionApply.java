package org.jeecg.modules.demo.positions.entity;

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
 * @Description: 岗位申请
 * @Author: jeecg-boot
 * @Date:   2024-12-24
 * @Version: V1.0
 */
@Data
@TableName("xgs_position_apply")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="xgs_position_apply对象", description="岗位申请")
public class XgsPositionApply implements Serializable {
    private static final long serialVersionUID = 1L;

	/**编号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "编号")
    private java.lang.String id;
	/**申请人ID*/
	@Excel(name = "申请人ID", width = 15)
    @ApiModelProperty(value = "申请人ID")
    private java.lang.String userId;
	/**申请人姓名*/
	@Excel(name = "申请人姓名", width = 15)
    @ApiModelProperty(value = "申请人姓名")
    private java.lang.String userName;
	/**简历ID*/
	@Excel(name = "简历ID", width = 15)
    @ApiModelProperty(value = "简历ID")
    private java.lang.String resumeId;
	/**简历名称*/
	@Excel(name = "简历名称", width = 15)
    @ApiModelProperty(value = "简历名称")
    private java.lang.String resumeName;
	/**岗位ID*/
	@Excel(name = "岗位ID", width = 15)
    @ApiModelProperty(value = "岗位ID")
    private java.lang.String positionId;
	/**岗位名称*/
	@Excel(name = "岗位名称", width = 15)
    @ApiModelProperty(value = "岗位名称")
    private java.lang.String positionName;
	/**岗位部门*/
	@Excel(name = "岗位部门", width = 15)
    @ApiModelProperty(value = "岗位部门")
    private java.lang.String positionDept;
	/**岗位类型*/
	@Excel(name = "岗位类型", width = 15)
    @ApiModelProperty(value = "岗位类型")
    private java.lang.String positionType;
	/**申请状态*/
	@Excel(name = "申请状态", width = 15)
    @ApiModelProperty(value = "申请状态")
    private java.lang.String status;
	/**申请备注*/
	@Excel(name = "申请备注", width = 15)
    @ApiModelProperty(value = "申请备注")
    private java.lang.String mark;
	/**申请标签*/
	@Excel(name = "申请标签", width = 15)
    @ApiModelProperty(value = "申请标签")
    private java.lang.String tagIds;
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
    /**审批环节*/
    @Excel(name = "审批环节", width = 15)
    @ApiModelProperty(value = "审批环节")
    private java.lang.String approvalNode;
    /**审批状态*/
    @Excel(name = "审批状态", width = 15)
    @ApiModelProperty(value = "审批状态")
    private java.lang.String approvalStatus;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**申请状态*/
    @Excel(name = "申请状态", width = 15)
    @ApiModelProperty(value = "申请状态")
    private java.lang.String applyStatus;
    /**申请结果*/
    @Excel(name = "申请结果", width = 15)
    @ApiModelProperty(value = "申请结果")
    private java.lang.String applyResult;
    /**简历文件*/
    @Excel(name = "简历文件", width = 15)
    @ApiModelProperty(value = "简历文件")
    private java.lang.String resumeFile;
    /**邀请状态*/
    @Excel(name = "邀请状态", width = 15, dicCode = "邀请状态")
    @Dict(dicCode = "邀请状态")
    @ApiModelProperty(value = "邀请状态")
    private java.lang.String inviteStatus;
    /**面试时间*/
    @Excel(name = "面试时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "面试时间")
    private java.util.Date interviewDate;
    /**面试信息*/
    @Excel(name = "面试信息", width = 15)
    @ApiModelProperty(value = "面试信息")
    private java.lang.String interviewInformation;
    /**面试状态*/
    @Excel(name = "面试状态", width = 15, dicCode = "面试状态")
    @Dict(dicCode = "面试状态")
    @ApiModelProperty(value = "面试状态")
    private java.lang.String interviewStatus;
}

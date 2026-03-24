package org.jeecg.modules.recruitment.xgsInviteToInterview.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 面试邀请
 * @Author: Liuyl
 * @Date:   2025-01-06
 * @Version: V1.0
 */
@Data
@ApiModel(value="xgs_invite_to_interview对象", description="面试邀请")
public class XgsInviteToInterviewVO implements Serializable {
    private static final long serialVersionUID = 1L;
	/**主键*/
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**申请id*/
    @Excel(name = "申请id", width = 15)
    @ApiModelProperty(value = "申请id")
    private String applyId;
    /** 简历主表 id（来自岗位投递，供详情弹窗 loadFormData 使用） */
    @ApiModelProperty(value = "简历id")
    private String resumeId;
	/**职位id*/
	@Excel(name = "职位id", width = 15)
    @ApiModelProperty(value = "职位id")
    private String positionId;
	/**职位名称*/
	@Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
    private String positionName;
    private String positionDept;
    private String positionType;
	/**候选人id*/
	@Excel(name = "候选人id", width = 15)
    @ApiModelProperty(value = "候选人id")
    private String candidateId;
	/**候选人*/
	@Excel(name = "候选人", width = 15)
    @ApiModelProperty(value = "候选人")
    private String candidate;
	/**面试官id*/
	@Excel(name = "面试官id", width = 15)
    @ApiModelProperty(value = "面试官id")
    private String interviewerId;
	/**面试官*/
	@Excel(name = "面试官", width = 15)
    @ApiModelProperty(value = "面试官")
    private String interviewer;
	/**面试状态*/
	@Excel(name = "面试状态", width = 15, dicCode = "面试状态")
	@Dict(dicCode = "面试状态")
    @ApiModelProperty(value = "面试状态")
    private String status;
	/**面试时间*/
	@Excel(name = "面试时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "面试时间")
    private Date interviewDate;
	/**面试反馈*/
	@Excel(name = "面试反馈", width = 15)
    @ApiModelProperty(value = "面试反馈")
    private String interviewFeedback;
    /**面试地点*/
    @Excel(name = "面试地点", width = 15)
    @ApiModelProperty(value = "面试地点")
    private String interviewPlace;
    /**邀请信息*/
    @Excel(name = "邀请信息", width = 15)
    @ApiModelProperty(value = "邀请信息")
    private String inviteLetter;
	/**邀请状态*/
	@Excel(name = "邀请状态", width = 15, dicCode = "邀请状态")
	@Dict(dicCode = "邀请状态")
    @ApiModelProperty(value = "邀请状态")
    private String inviteStatus;
    /** 面试结果 */
    @Excel(name = "面试结果", width = 15, dicCode = "面试结果")
    @Dict(dicCode = "面试结果")
    @ApiModelProperty(value = "面试结果")
    private String interviewResult;
    /** 邀请结果 */
    @Excel(name = "邀请结果", width = 15, dicCode = "邀请结果")
    @Dict(dicCode = "邀请结果")
    @ApiModelProperty(value = "邀请结果")
    private String inviteResult;
}

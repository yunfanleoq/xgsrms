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
 * @Description: 招聘岗位列表
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
@Data
@TableName("xgs_positions")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="xgs_positions对象", description="招聘岗位列表")
public class XgsPositions implements Serializable {
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
	/**招聘部门*/
	@Excel(name = "招聘部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
	@Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    @ApiModelProperty(value = "招聘部门")
    private java.lang.String dept;
	/**课题组*/
	@Excel(name = "课题组", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
	@Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    @ApiModelProperty(value = "课题组")
    private java.lang.String ktz;
	/**咨询电话*/
	@Excel(name = "咨询电话", width = 15)
    @ApiModelProperty(value = "咨询电话")
    private java.lang.String telphone;
	/**邮箱*/
	@Excel(name = "邮箱", width = 15)
    @ApiModelProperty(value = "邮箱")
    private java.lang.String email;
	/**岗位名称*/
	@Excel(name = "岗位名称", width = 15)
    @ApiModelProperty(value = "岗位名称")
    private java.lang.String positionName;
	/**研究方向*/
	@Excel(name = "研究方向", width = 15)
    @ApiModelProperty(value = "研究方向")
    private java.lang.String researchDirection;
	/**招聘人数*/
	@Excel(name = "招聘人数", width = 15)
    @ApiModelProperty(value = "招聘人数")
    private java.lang.String personCount;
	/**岗位职责*/
	@Excel(name = "岗位职责", width = 15)
    @ApiModelProperty(value = "岗位职责")
    private java.lang.String duty;
	/**学历学位*/
	@Excel(name = "学历学位", width = 15)
    @ApiModelProperty(value = "学历学位")
    private java.lang.String xlxw;
	/**专业*/
	@Excel(name = "专业", width = 15)
    @ApiModelProperty(value = "专业")
    private java.lang.String professional;
	/**工作年限*/
	@Excel(name = "工作年限", width = 15)
    @ApiModelProperty(value = "工作年限")
    private java.lang.String workYears;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String memo;
	/**岗位状态*/
	@Excel(name = "岗位状态", width = 15, dicCode = "岗位状态")
	@Dict(dicCode = "岗位状态")
    @ApiModelProperty(value = "岗位状态")
    private java.lang.String status;
    /**岗位分类*/
    @Excel(name = "岗位分类", width = 15, dicCode = "岗位分类")
    @Dict(dicCode = "岗位分类")
    @ApiModelProperty(value = "岗位分类")
    private java.lang.String category;
    /**意见*/
    @Excel(name = "意见", width = 15)
    @ApiModelProperty(value = "意见")
    private java.lang.String opinions;
    /**岗位状态*/
    @Excel(name = "审批状态", width = 15, dicCode = "审批状态")
    @Dict(dicCode = "审批状态")
    @ApiModelProperty(value = "审批状态")
    private java.lang.String approvalStatus;
    /**审批人*/
    @Excel(name = "审批人", width = 15)
    @ApiModelProperty(value = "审批人")
    private java.lang.String approvalUser;
}

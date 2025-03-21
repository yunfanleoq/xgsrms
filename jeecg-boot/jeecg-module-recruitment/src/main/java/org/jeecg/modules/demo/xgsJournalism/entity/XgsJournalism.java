package org.jeecg.modules.demo.xgsJournalism.entity;

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
 * @Description: 新闻内容
 * @Author: jeecg-boot
 * @Date:   2024-12-23
 * @Version: V1.0
 */
@Data
@TableName("xgs_journalism")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="xgs_journalism对象", description="新闻内容")
public class XgsJournalism implements Serializable {
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
	/**新闻标题*/
	@Excel(name = "新闻标题", width = 15)
    @ApiModelProperty(value = "新闻标题")
    private java.lang.String journalismHead;
	/**新闻内容*/
	@Excel(name = "新闻内容", width = 15)
    @ApiModelProperty(value = "新闻内容")
    private java.lang.String journalismText;
	/**新闻类型*/
	@Excel(name = "新闻类型", width = 15, dicCode = "jour_type")
	@Dict(dicCode = "jour_type")
    @ApiModelProperty(value = "新闻类型")
    private java.lang.String type;
	/**新闻状态*/
	@Excel(name = "新闻状态", width = 15, dicCode = "jour_state")
	@Dict(dicCode = "jour_state")
    @ApiModelProperty(value = "新闻状态")
    private java.lang.String state;
    @ApiModelProperty(value = "新闻日期")
    private String newsDate;
    @ApiModelProperty(value = "新闻链接")
    private java.lang.String href;
    @ApiModelProperty(value = "新闻排序")
    private java.lang.Integer newsSort;


}

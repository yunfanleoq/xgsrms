package org.jeecg.modules.recruitment.xgsJournalism.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Author: Liuyl
 * @Date:   2025-03-23
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
	/**新闻标题*/
	@Excel(name = "新闻标题", width = 15)
    @ApiModelProperty(value = "新闻标题")
    private String journalismHead;
	/**新闻内容*/
	@Excel(name = "新闻内容", width = 15)
    @ApiModelProperty(value = "新闻内容")
    private String journalismText;
	/**简述*/
	@Excel(name = "简述", width = 15)
    @ApiModelProperty(value = "简述")
    private String shortText;
	/**简述2*/
	@Excel(name = "简述2", width = 15)
    @ApiModelProperty(value = "简述2")
    private String shortText2;
	/**新闻类型（数据库列名为 MySQL 保留字 type，须显式转义）*/
	@TableField("`type`")
	@Excel(name = "新闻类型", width = 15, dicCode = "jour_type")
	@Dict(dicCode = "jour_type")
    @ApiModelProperty(value = "新闻类型")
    private String type;
	/**新闻状态*/
	@Excel(name = "新闻状态", width = 15, dicCode = "jour_state")
	@Dict(dicCode = "jour_state")
    @ApiModelProperty(value = "新闻状态")
    private String state;
	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private String href;
	/**日期*/
	@Excel(name = "日期", width = 15)
    @ApiModelProperty(value = "日期")
    private String newsDate;
	/**日期2*/
	@Excel(name = "日期2", width = 15)
    @ApiModelProperty(value = "日期2")
    private String newDate2;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer newsSort;
	/**类型代码*/
	@Excel(name = "类型代码", width = 15)
    @ApiModelProperty(value = "类型代码")
    private String typeCode;
	/**图片1*/
	@Excel(name = "图片1", width = 15)
    @ApiModelProperty(value = "图片1")
    private String img1;
	/**图片2*/
	@Excel(name = "图片2", width = 15)
    @ApiModelProperty(value = "图片2")
    private String img2;
	/**栏目路由*/
	@Excel(name = "栏目路由", width = 15)
    @ApiModelProperty(value = "栏目路由")
    private String categoryPath;
	/**内容路由*/
	@Excel(name = "内容路由", width = 15)
    @ApiModelProperty(value = "内容路由")
    private String newsPath;
}

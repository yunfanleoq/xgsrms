package org.jeecg.modules.recruitment.xgsHome.entity;

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
 * @Description: 首页
 * @Author: Liuyl
 * @Date:   2025-03-22
 * @Version: V1.0
 */
@Data
@TableName("xgs_home")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="xgs_home对象", description="首页")
public class XgsHome implements Serializable {
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
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**招聘公告标题*/
	@Excel(name = "招聘公告标题", width = 15)
    @ApiModelProperty(value = "招聘公告标题")
    private String recruitAnnouncementTitle;
	/**招聘公告*/
	@Excel(name = "招聘公告", width = 15)
    @ApiModelProperty(value = "招聘公告")
    private String recruitAnnouncement;
	/**新闻标题*/
	@Excel(name = "新闻标题", width = 15)
    @ApiModelProperty(value = "新闻标题")
    private String newTitle;
	/**新闻*/
	@Excel(name = "新闻", width = 15)
    @ApiModelProperty(value = "新闻")
    private String news;
	/**图片*/
	@Excel(name = "图片", width = 15)
    @ApiModelProperty(value = "图片")
    private String photograph;
	/**图片文件*/
	@Excel(name = "图片文件", width = 15)
    @ApiModelProperty(value = "图片文件")
    private String images;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
    @ApiModelProperty(value = "图片地址")
    private String imgHref;
	/**图片序号*/
	@Excel(name = "图片序号", width = 15)
    @ApiModelProperty(value = "图片序号")
    private String imgNum;
	/**类型*/
	@Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
    private String newsType;
	/**本地图片路径*/
	@Excel(name = "本地图片路径", width = 15)
    @ApiModelProperty(value = "本地图片路径")
    private String localImagePath;
}

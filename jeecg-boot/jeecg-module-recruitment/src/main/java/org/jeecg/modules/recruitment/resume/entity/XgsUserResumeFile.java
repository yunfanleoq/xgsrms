package org.jeecg.modules.recruitment.resume.entity;

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
 * @Description: 用户简历文件分析
 * @Author: jeecg-boot
 * @Date:   2025-01-01
 * @Version: V1.0
 */
@Data
@TableName("xgs_user_resume_file")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="xgs_user_resume_file对象", description="用户简历文件分析")
public class XgsUserResumeFile implements Serializable {
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
	/**文件*/
	@Excel(name = "文件", width = 15)
    private transient String resumeFileString;

    private byte[] resumeFile;

    public byte[] getResumeFile(){
        if(resumeFileString==null){
            return null;
        }
        try {
            return resumeFileString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getResumeFileString(){
        if(resumeFile==null || resumeFile.length==0){
            return "";
        }
        try {
            return new String(resumeFile,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
	/**文件md5*/
	@Excel(name = "文件md5", width = 15)
    @ApiModelProperty(value = "文件md5")
    private String fileMd5;
	/**文件大小*/
	@Excel(name = "文件大小", width = 15)
    @ApiModelProperty(value = "文件大小")
    private Integer fileSize;
	/**文件名称*/
	@Excel(name = "文件名称", width = 15)
    @ApiModelProperty(value = "文件名称")
    private String fileName;
	/**扩展名*/
	@Excel(name = "扩展名", width = 15)
    @ApiModelProperty(value = "扩展名")
    private String fileExt;
	/**文件JSON*/
	@Excel(name = "文件JSON", width = 15)
    @ApiModelProperty(value = "文件JSON")
    private String fileJson;
	/**简历字段信息*/
	@Excel(name = "简历字段信息", width = 15)
    @ApiModelProperty(value = "简历字段信息")
    private String resumeInfo;
	/**文件路径*/
	@Excel(name = "文件路径", width = 15)
    @ApiModelProperty(value = "文件路径")
    private String filePath;
	/**关联ID*/
	@Excel(name = "关联ID", width = 15)
    @ApiModelProperty(value = "关联ID")
    private String parentId;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
}

package org.jeecg.modules.demo.positions.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.xgsResume.vo.XgsResumeBasePage;

import java.io.Serializable;

/**
 * @Description: 岗位申请
 * @Author: jeecg-boot
 * @Date:   2024-12-24
 * @Version: V1.0
 */
@Data
@ApiModel(value="xgs_position_apply VO对象", description="岗位申请VO")
public class XgsPositionApplyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "岗位申请信息")
    XgsPositionApply xgsPositionApply;

    @ApiModelProperty(value = "简历信息")
    XgsResumeBasePage xgsResumeBasePage;

}

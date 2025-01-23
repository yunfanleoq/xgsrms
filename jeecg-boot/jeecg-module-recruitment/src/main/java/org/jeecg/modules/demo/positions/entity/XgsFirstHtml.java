package org.jeecg.modules.demo.positions.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("xgs_positions")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class XgsFirstHtml {

    //岗位数量（总计/个人）
    private Integer positionsNumber;

    //招聘数量/申请数量
    private Integer personCountNumber;

    //审核数量/提交审核数量
    private Integer contactNumber;

    //简历数量/我的简历数量
    private Integer resumeNumber;

    public XgsFirstHtml(Integer positionsNumber, Integer personCountNumber, Integer contactNumber, Integer resumeNumber) {
        this.positionsNumber = positionsNumber;
        this.personCountNumber = personCountNumber;
        this.contactNumber = contactNumber;
        this.resumeNumber = resumeNumber;
    }
}

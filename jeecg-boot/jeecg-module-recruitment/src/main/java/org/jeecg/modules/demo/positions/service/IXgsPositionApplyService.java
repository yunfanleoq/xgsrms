package org.jeecg.modules.demo.positions.service;

import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.positions.vo.XgsPositionApplyVO;

/**
 * @Description: 岗位申请
 * @Author: jeecg-boot
 * @Date:   2024-12-24
 * @Version: V1.0
 */
public interface IXgsPositionApplyService extends IService<XgsPositionApply> {

    /**
     * 在线添加岗位信息
     * @param xgsPositionApplyVO
     */
    void doPositionApply(XgsPositionApplyVO xgsPositionApplyVO);
}

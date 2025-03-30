package org.jeecg.modules.demo.positions.service;

import org.jeecg.common.api.vo.Result;
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
    Result<String> doPositionApply(XgsPositionApplyVO xgsPositionApplyVO);

    /**
     * 检查岗位是否已申请
     * @param xgsPositionApplyVO
     * @return
     */
    Boolean checkHasApplied(XgsPositionApplyVO xgsPositionApplyVO);

    /**
     * 获取 岗位信息
     * @param xgsPositionApplyVO
     */
    XgsPositionApplyVO getPositionApply(XgsPositionApplyVO xgsPositionApplyVO);

    /**
     * 检查岗位是否已申请
     * @param xgsPositionApplyVO
     * @return
     * @throws Exception
     */
    Result<XgsPositionApplyVO> checkApplyByPosId(XgsPositionApplyVO xgsPositionApplyVO);
}

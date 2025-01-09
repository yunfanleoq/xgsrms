package org.jeecg.modules.recruitment.position.service.impl;

import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.demo.positions.mapper.XgsPositionApplyMapper;
import org.jeecg.modules.recruitment.position.entity.XgsFlowOpinions;
import org.jeecg.modules.recruitment.position.mapper.XgsFlowOpinionsMapper;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 审批办理过程表
 * @Author: jeecg-boot
 * @Date:   2025-01-09
 * @Version: V1.0
 */
@Service
public class XgsFlowOpinionsServiceImpl extends ServiceImpl<XgsFlowOpinionsMapper, XgsFlowOpinions> implements IXgsFlowOpinionsService {

    @Autowired
    XgsPositionApplyMapper positionApplyMapper;

    @Override
    @Transactional
    public void add(XgsFlowOpinions xgsFlowOpinions) {
        XgsPositionApply positionApply = positionApplyMapper.selectById(xgsFlowOpinions.getParentId());
        if (NODE_DEPT.equals(xgsFlowOpinions.getApprovalNode())) {
            if (APPROVAL_RESULT_AGREE.equals(xgsFlowOpinions.getApprovalStatus())) {
                positionApply.setApprovalNode(NODE_HR);
                positionApply.setApprovalStatus(APPROVAL_STATUS_DEPT_PASS);
                positionApply.setApplyStatus(APPROVAL_STATUS_DEPT_PASS);
            } else {
                positionApply.setApprovalNode(NODE_USER);
                positionApply.setApplyStatus(APPROVAL_STATUS_DEPT_NOT_PASS);
            }
        } else if (NODE_HR.equals(xgsFlowOpinions.getApprovalNode())) {
            if (APPROVAL_RESULT_AGREE.equals(xgsFlowOpinions.getApprovalStatus())) {
                positionApply.setApprovalNode(NODE_END);
                positionApply.setApprovalStatus(APPROVAL_STATUS_HR_PASS);
                positionApply.setApplyStatus(APPROVAL_STATUS_HR_PASS);
                positionApply.setStatus(APPROVAL_STATUS_PASS);
            } else {
                positionApply.setApprovalNode(NODE_DEPT);
                positionApply.setApprovalStatus(APPROVAL_STATUS_HR_NOT_PASS);
                positionApply.setApplyStatus(APPROVAL_STATUS_HR_NOT_PASS);
            }
        }
        positionApplyMapper.updateById(positionApply);
        save(xgsFlowOpinions);
    }
}

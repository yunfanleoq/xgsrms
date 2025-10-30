package org.jeecg.modules.recruitment.position.service.impl;

import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.mapper.XgsPositionApplyMapper;
import org.jeecg.modules.recruitment.position.entity.XgsFlowOpinions;
import org.jeecg.modules.recruitment.position.mapper.XgsFlowOpinionsMapper;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;
import org.jeecg.modules.recruitment.xgsInviteToInterview.entity.XgsInviteToInterview;
import org.jeecg.modules.recruitment.xgsInviteToInterview.mapper.XgsInviteToInterviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 审批办理过程表
 * @Author: Liuyl
 * @Date:   2025-01-09
 * @Version: V1.0
 */
@Service
public class XgsFlowOpinionsServiceImpl extends ServiceImpl<XgsFlowOpinionsMapper, XgsFlowOpinions> implements IXgsFlowOpinionsService {

    @Autowired
    XgsPositionApplyMapper positionApplyMapper;
    @Autowired
    XgsInviteToInterviewMapper inviteToInterviewMapper;

    @Override
    @Transactional
    public String add(XgsFlowOpinions xgsFlowOpinions) {
        XgsPositionApply positionApply = positionApplyMapper.selectById(xgsFlowOpinions.getParentId());
        String resultMessage = "";
        boolean isAgree = APPROVAL_RESULT_AGREE.equals(xgsFlowOpinions.getOpinions());
        
        if (NODE_USER.equals(xgsFlowOpinions.getApprovalNode())) { // 申请人
            positionApply.setApprovalNode(NODE_DEPT);
            positionApply.setApprovalStatus(APPROVAL_STATUS_DEPT_TODO);
            resultMessage = "提交成功，已转至部门审核！";
        } else if (NODE_HR_PENDING_REVIEW.equals(xgsFlowOpinions.getApprovalNode())) { // 待查看（人力处查看）
            // 人力处查看后，可以进入部门审核或直接通过
            if (isAgree) {
                positionApply.setApprovalNode(NODE_DEPT);
                positionApply.setApprovalStatus(APPROVAL_STATUS_DEPT_TODO);
                positionApply.setStatus(APPROVAL_STATUS_GOING); // 审核中
                resultMessage = "审核通过，已转至部门审核！";
            } else {
                positionApply.setApprovalNode(NODE_USER);
                positionApply.setApprovalStatus(APPROVAL_STATUS_HR_NOT_PASS);
                positionApply.setStatus(APPROVAL_STATUS_HR_NOT_PASS); // 人力处未通过
                resultMessage = "审核不通过，已退回申请人！";
            }
        } else if (NODE_HR_PENDING_REVIEW.equals(xgsFlowOpinions.getApprovalNode()) && APPROVAL_STATUS_HR_PENDING_REVIEW.equals(xgsFlowOpinions.getApprovalStatus())) { // 待查看
            // 人力处查看后，可以进入部门审核或直接通过
            if (isAgree) {
                positionApply.setApprovalNode(NODE_DEPT);
                positionApply.setApprovalStatus(APPROVAL_STATUS_DEPT_TODO);
                resultMessage = "审核通过，已转至部门审核！";
            } else {
                positionApply.setApprovalNode(NODE_USER);
                positionApply.setApprovalStatus(APPROVAL_STATUS_HR_NOT_PASS);
                positionApply.setStatus(APPROVAL_STATUS_HR_NOT_PASS); // 人力处未通过
                resultMessage = "审核不通过，已退回申请人！";
            }
        } else if (NODE_DEPT.equals(xgsFlowOpinions.getApprovalNode())) { // 部门审核
            if (isAgree) {
                positionApply.setApprovalNode(NODE_HR);
                positionApply.setApprovalStatus(APPROVAL_STATUS_HR_TODO);
                positionApply.setApplyStatus(APPROVAL_STATUS_DEPT_PASS);
                resultMessage = "部门审核通过，已转至人力处审核！";
            } else {
                positionApply.setApprovalNode(NODE_USER);
                positionApply.setApprovalStatus(APPROVAL_STATUS_USER_TODO);
                positionApply.setApplyStatus(APPROVAL_STATUS_DEPT_NOT_PASS);
                positionApply.setStatus(APPROVAL_STATUS_DEPT_NOT_PASS); // 部门审核未通过
                resultMessage = "部门审核不通过，已退回申请人！";
            }
        } else if (NODE_HR.equals(xgsFlowOpinions.getApprovalNode())) { // 人力处审核
            if (isAgree) {
                positionApply.setApprovalNode(NODE_END);
                positionApply.setApprovalStatus(APPROVAL_STATUS_HR_PASS);
                positionApply.setApplyStatus(APPROVAL_STATUS_HR_PASS);
                positionApply.setStatus(APPROVAL_STATUS_PASS); // 初审通过
                xgsInviteToInterview(positionApply);
                resultMessage = "人力处审核通过，初审已完成！";
            } else {
                positionApply.setApprovalNode(NODE_DEPT);
                positionApply.setApprovalStatus(APPROVAL_STATUS_HR_NOT_PASS);
                positionApply.setApplyStatus(APPROVAL_STATUS_HR_NOT_PASS);
                resultMessage = "人力处审核不通过，已退回部门！";
            }
        }
        
        positionApplyMapper.updateById(positionApply);
        save(xgsFlowOpinions);
        
        return resultMessage;
    }

    /**
     * 审核通过 发送 邀请面试 消息
     * @param positionApply
     */
    @Transactional
    public void xgsInviteToInterview(XgsPositionApply positionApply) {
        XgsInviteToInterview xgsInviteToInterview = new XgsInviteToInterview();
        xgsInviteToInterview.setApplyId(positionApply.getId());
        xgsInviteToInterview.setPositionId(positionApply.getPositionId());
        xgsInviteToInterview.setPositionName(positionApply.getPositionName());
        xgsInviteToInterview.setCandidateId(positionApply.getUserId());
        xgsInviteToInterview.setCandidate(positionApply.getUserName());
        xgsInviteToInterview.setInviteStatus("待邀请");
        inviteToInterviewMapper.insert(xgsInviteToInterview);
    }
}

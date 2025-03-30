package org.jeecg.modules.recruitment.position.service;

import org.jeecg.modules.recruitment.position.entity.XgsFlowOpinions;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 审批办理过程表
 * @Author: Liuyl
 * @Date:   2025-01-09
 * @Version: V1.0
 */
public interface IXgsFlowOpinionsService extends IService<XgsFlowOpinions> {
    /**
     * 部门审核  见数据字典 审核环节 申请人，部门审核，人力处审核，初审通过
      */
    String NODE_USER = "申请人";
    /**
     * 部门审核
     */
    String NODE_DEPT = "部门审核";
    /**
     * HR 审核
     */
    String NODE_HR = "人力处审核";
    /**
     * 初审通过
     */
    String NODE_END = "初审完成";
    /**
     * 初审通过
     */
    String NODE_INVITE = "邀请";
    /**
     * 初审通过
     */
    String NODE_INTERVIEW = "面试";

    String APPROVAL_STATUS_USER_TODO = "待提交"; // 退回
    String APPROVAL_STATUS_SUBMIT = "已提交";
    String APPROVAL_STATUS_DEPT_TODO = "待部门审核";
    String APPROVAL_STATUS_DEPT_PASS = "部门通过";
    String APPROVAL_STATUS_DEPT_NOT_PASS = "部门未通过";
    String APPROVAL_STATUS_HR_TODO = "待人力处审核";
    String APPROVAL_STATUS_HR_PASS = "人力处通过";
    String APPROVAL_STATUS_HR_NOT_PASS = "人力处未通过";
    String APPROVAL_STATUS_PASS = "初审通过";
    String APPROVAL_STATUS_GOING = "审核中";

    String APPROVAL_RESULT_AGREE = "同意";
    String APPROVAL_RESULT_REJECT = "驳回";

    /**
     * 办理
     * @param xgsFlowOpinions
     */
    void add(XgsFlowOpinions xgsFlowOpinions);
}

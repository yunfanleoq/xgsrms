package org.jeecg.modules.recruitment.xgsInviteToInterview.service.impl;

import org.jeecg.modules.recruitment.xgsInviteToInterview.entity.XgsInviteToInterview;
import org.jeecg.modules.recruitment.xgsInviteToInterview.mapper.XgsInviteToInterviewMapper;
import org.jeecg.modules.recruitment.xgsInviteToInterview.service.IXgsInviteToInterviewService;
import org.jeecg.modules.recruitment.xgsInviteToInterview.vo.XgsInviteToInterviewVO;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 面试邀请
 * @Author: Liuyl
 * @Date:   2025-01-06
 * @Version: V1.0
 */
@Service
public class XgsInviteToInterviewServiceImpl extends ServiceImpl<XgsInviteToInterviewMapper, XgsInviteToInterview> implements IXgsInviteToInterviewService {

    @Override
    @Transactional
    public void addInterview(XgsInviteToInterviewVO interviewVO) {
        XgsInviteToInterview entity = getById(interviewVO.getId());
        entity.setInterviewFeedback(interviewVO.getInterviewFeedback());
        entity.setInterviewDate(interviewVO.getInterviewDate());
        entity.setInterviewPlace(interviewVO.getInterviewPlace());
        entity.setInviteLetter(interviewVO.getInviteLetter());
        entity.setStatus(interviewVO.getStatus());
        entity.setInviteStatus("已发送邀请");
        saveOrUpdate(entity);
    }
}

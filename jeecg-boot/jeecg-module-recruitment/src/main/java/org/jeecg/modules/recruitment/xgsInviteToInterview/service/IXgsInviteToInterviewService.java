package org.jeecg.modules.recruitment.xgsInviteToInterview.service;

import org.jeecg.modules.recruitment.xgsInviteToInterview.entity.XgsInviteToInterview;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.recruitment.xgsInviteToInterview.vo.XgsInviteToInterviewVO;

/**
 * @Description: 面试邀请
 * @Author: Liuyl
 * @Date:   2025-01-06
 * @Version: V1.0
 */
public interface IXgsInviteToInterviewService extends IService<XgsInviteToInterview> {

    void addInterview(XgsInviteToInterviewVO interviewVO);
}

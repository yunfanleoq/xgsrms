package org.jeecg.modules.recruitment.ontology;

import lombok.Data;

/**
 * 智能体动作入参。审批结论由动作本身决定，不把自由文本写进「同意/驳回」字段。
 */
@Data
public class RmsAgentActionRequest {
    private String actionId;
    private String applyId;
    private String interviewDate;
    private String interviewPlace;
    private String inviteLetter;
}

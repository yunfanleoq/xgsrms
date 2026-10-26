package org.jeecg.modules.recruitment.ontology;

import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.xgsInviteToInterview.entity.XgsInviteToInterview;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RmsHirePhaseTest {

    @Test
    void pendingReviewStaysOnHrScreen() {
        XgsPositionApply apply = apply("待查看");
        assertEquals(RmsHireOntologyCatalog.PHASE_HR_SCREEN, RmsAgentWorkflowService.phaseOf(apply, null));
        assertTrue(RmsHireOntologyCatalog.actionsForPhase(RmsHireOntologyCatalog.PHASE_HR_SCREEN).contains("HrScreenPass"));
    }

    @Test
    void deptAndHrNodesMapToTheirAgents() {
        assertEquals(RmsHireOntologyCatalog.PHASE_DEPT, RmsAgentWorkflowService.phaseOf(apply("部门审核"), null));
        assertEquals(RmsHireOntologyCatalog.PHASE_HR_DECISION, RmsAgentWorkflowService.phaseOf(apply("人力处审核"), null));
    }

    @Test
    void passedReviewWaitsForInviteUntilItIsSent() {
        XgsPositionApply apply = apply("初审完成");
        XgsInviteToInterview waiting = new XgsInviteToInterview();
        waiting.setInviteStatus("待邀请");
        assertEquals(RmsHireOntologyCatalog.PHASE_INVITE, RmsAgentWorkflowService.phaseOf(apply, waiting));

        XgsInviteToInterview sent = new XgsInviteToInterview();
        sent.setInviteStatus("已发送邀请");
        assertEquals(RmsHireOntologyCatalog.PHASE_INTERVIEW, RmsAgentWorkflowService.phaseOf(apply, sent));

        sent.setInterviewResult("面试通过");
        assertEquals(RmsHireOntologyCatalog.PHASE_INTERVIEW, RmsAgentWorkflowService.phaseOf(apply, sent));
        assertTrue(RmsHireOntologyCatalog.actionsForPhase(RmsHireOntologyCatalog.PHASE_INTERVIEW).contains("RecordInterviewPass"));
    }

    @Test
    void returnedApplicationGoesBackToIntake() {
        assertEquals(RmsHireOntologyCatalog.PHASE_INTAKE, RmsAgentWorkflowService.phaseOf(apply("申请人"), null));
        assertTrue(RmsHireOntologyCatalog.actionsForPhase(RmsHireOntologyCatalog.PHASE_INTAKE).isEmpty());
    }

    private XgsPositionApply apply(String node) {
        XgsPositionApply apply = new XgsPositionApply();
        apply.setApprovalNode(node);
        return apply;
    }
}

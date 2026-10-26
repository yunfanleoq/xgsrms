package org.jeecg.modules.recruitment.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 招聘主流程本体。结构对齐知识产权智能体：对象、属性、关系、动作、工具、技能、智能体、工作流阶段。
 * 业务状态仍落在现有岗位申请与面试邀请表上，这里是唯一的流程定义。
 */
public final class RmsHireOntologyCatalog {

    public static final String WORKFLOW_CODE = "rms_hire";

    public static final String PHASE_INTAKE = "intake";
    public static final String PHASE_HR_SCREEN = "hr_screen";
    public static final String PHASE_DEPT = "dept_review";
    public static final String PHASE_HR_DECISION = "hr_decision";
    public static final String PHASE_INVITE = "invite";
    public static final String PHASE_INTERVIEW = "interview";

    private RmsHireOntologyCatalog() {
    }

    public static List<Map<String, Object>> schemaPhases() {
        return phases();
    }

    public static List<Map<String, Object>> schemaActions() {
        return actions();
    }

    public static Map<String, Object> schema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("version", "1.0");
        schema.put("domain", "rms_hire");
        schema.put("description", "简历接入、审批、面试邀请的对象—动作—智能体工作流");
        schema.put("workflow", workflow());
        schema.put("objects", objects());
        schema.put("relations", relations());
        schema.put("actions", actions());
        schema.put("tools", tools());
        schema.put("skills", skills());
        schema.put("agents", agents());
        schema.put("phases", phases());
        return schema;
    }

    public static List<String> actionsForPhase(String phaseKey) {
        if (PHASE_HR_SCREEN.equals(phaseKey)) {
            return Arrays.asList("HrScreenPass", "HrScreenReject");
        }
        if (PHASE_DEPT.equals(phaseKey)) {
            return Arrays.asList("DeptReviewPass", "DeptReviewReject");
        }
        if (PHASE_HR_DECISION.equals(phaseKey)) {
            return Arrays.asList("HrDecisionPass", "HrDecisionReject");
        }
        if (PHASE_INVITE.equals(phaseKey)) {
            return Collections.singletonList("SendInterviewInvite");
        }
        if (PHASE_INTERVIEW.equals(phaseKey)) {
            return Arrays.asList("RecordInterviewPass", "RecordInterviewFail");
        }
        return Collections.emptyList();
    }

    private static Map<String, Object> workflow() {
        Map<String, Object> workflow = new LinkedHashMap<>();
        workflow.put("code", WORKFLOW_CODE);
        workflow.put("name", "招聘办理");
        workflow.put("status", "active");
        workflow.put("description", "候选人投递简历后，由人力初筛、部门审核、人力决审、面试邀请、面试结论五个智能体按序办理。");
        return workflow;
    }

    private static List<Map<String, Object>> objects() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(object("Candidate", "候选人", "#44403c", "投递简历的申请人",
                prop("userId", "用户编号", "string"),
                prop("userName", "姓名", "string")));
        list.add(object("Resume", "简历", "#0f766e", "在线简历或上传的简历文件",
                prop("resumeId", "简历编号", "string"),
                prop("resumeName", "简历名称", "string"),
                prop("resumeFile", "简历文件", "file")));
        list.add(object("Position", "岗位", "#1d4ed8", "在招岗位",
                prop("positionId", "岗位编号", "string"),
                prop("positionName", "岗位名称", "string"),
                prop("positionDept", "用人部门", "string"),
                prop("positionType", "岗位类型", "string")));
        list.add(object("Application", "岗位申请", "#9a3412", "一次投递，审批状态的载体",
                prop("id", "申请编号", "string"),
                prop("approvalNode", "审批环节", "string"),
                prop("approvalStatus", "审批状态", "string"),
                prop("status", "申请人可见状态", "string")));
        list.add(object("ApprovalRecord", "审批记录", "#a16207", "一次办理意见",
                prop("approvalNode", "办理环节", "string"),
                prop("opinions", "办理结论", "enum:同意|驳回"),
                prop("approvalUser", "办理人", "string")));
        list.add(object("InterviewInvite", "面试邀请", "#6d28d9", "初审通过后生成，发送后进入面试",
                prop("inviteStatus", "邀请状态", "string"),
                prop("inviteLetter", "邀请说明", "text"),
                prop("interviewDate", "面试时间", "datetime"),
                prop("interviewPlace", "面试地点", "string"),
                prop("inviteResult", "候选人答复", "string"),
                prop("interviewResult", "面试结果", "string")));
        list.add(object("Workflow", "工作流", "#1c1917", "可运行的招聘流水线",
                prop("code", "编码", "string"),
                prop("name", "名称", "string")));
        list.add(object("Phase", "阶段", "#44403c", "工作流中的一级阶段",
                prop("key", "阶段键", "string"),
                prop("name", "名称", "string")));
        list.add(object("Agent", "智能体", "#0f5c56", "阶段内的办理者，绑定技能与工具",
                prop("agentKey", "标识", "string"),
                prop("usesLlm", "是否调用模型", "boolean")));
        list.add(object("Tool", "工具", "#047857", "智能体可调用的确定性能力",
                prop("name", "名称", "string"),
                prop("write", "是否写库", "boolean")));
        list.add(object("Skill", "技能", "#7c3aed", "约束智能体何时可以动作",
                prop("code", "编码", "string"),
                prop("body", "规范", "text")));
        return list;
    }

    private static List<Map<String, Object>> relations() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(rel("submits", "提交", "Candidate", "Resume", "候选人提交简历"));
        list.add(rel("applies_for", "投递", "Resume", "Position", "简历投向岗位，生成岗位申请"));
        list.add(rel("has_application", "形成申请", "Candidate", "Application", "一次投递对应一条申请"));
        list.add(rel("reviewed_by", "办理", "Application", "ApprovalRecord", "申请产生审批记录"));
        list.add(rel("yields_invite", "生成邀请", "Application", "InterviewInvite", "人力决审通过后生成待邀请"));
        list.add(rel("has_phase", "含有阶段", "Workflow", "Phase", "工作流拥有有序阶段"));
        list.add(rel("has_step", "由智能体办理", "Phase", "Agent", "每个阶段由一个智能体负责"));
        list.add(rel("next", "下一阶段", "Phase", "Phase", "阶段顺序"));
        list.add(rel("uses_skill", "使用技能", "Agent", "Skill", "技能约束该智能体的动作"));
        list.add(rel("uses_tool", "调用工具", "Agent", "Tool", "工具执行读写"));
        list.add(rel("reads", "读取", "Agent", "Application", "办理前读取申请"));
        list.add(rel("writes", "写入", "Agent", "ApprovalRecord", "办理后写入记录或邀请"));
        return list;
    }

    private static List<Map<String, Object>> actions() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(action("HrScreenPass", "初筛通过", "人力初筛智能体", "HrScreenAgent", PHASE_HR_SCREEN,
                "当前环节必须是待查看", "写入同意，申请进入部门审核"));
        list.add(action("HrScreenReject", "初筛退回", "人力初筛智能体", "HrScreenAgent", PHASE_HR_SCREEN,
                "当前环节必须是待查看", "写入驳回，申请退回申请人"));
        list.add(action("DeptReviewPass", "部门通过", "部门审核智能体", "DeptReviewAgent", PHASE_DEPT,
                "当前环节必须是部门审核", "写入同意，申请进入人力决审"));
        list.add(action("DeptReviewReject", "部门退回", "部门审核智能体", "DeptReviewAgent", PHASE_DEPT,
                "当前环节必须是部门审核", "写入驳回，申请退回申请人"));
        list.add(action("HrDecisionPass", "决审通过", "人力决审智能体", "HrDecisionAgent", PHASE_HR_DECISION,
                "当前环节必须是人力处审核", "写入同意，生成待发送的面试邀请"));
        list.add(action("HrDecisionReject", "决审退回", "人力决审智能体", "HrDecisionAgent", PHASE_HR_DECISION,
                "当前环节必须是人力处审核", "写入驳回，申请退回部门"));
        list.add(action("SendInterviewInvite", "发送面试邀请", "面试邀请智能体", "InterviewInviteAgent", PHASE_INVITE,
                "邀请状态必须是待邀请", "写入时间、地点和邀请说明，状态改为已发送邀请",
                param("interviewDate", "面试时间", true),
                param("interviewPlace", "面试地点", true),
                param("inviteLetter", "邀请说明", true)));
        list.add(action("RecordInterviewPass", "面试通过", "面试结论智能体", "InterviewResultAgent", PHASE_INTERVIEW,
                "邀请已发送且尚未填写面试结果", "写入面试通过"));
        list.add(action("RecordInterviewFail", "面试未通过", "面试结论智能体", "InterviewResultAgent", PHASE_INTERVIEW,
                "邀请已发送且尚未填写面试结果", "写入面试未通过"));
        return list;
    }

    private static List<Map<String, Object>> tools() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(tool("apply_lookup", "读取岗位申请", "按申请编号读取环节、状态、简历和岗位", false));
        list.add(tool("approval_transition", "审批流转", "调用现有审批办理，写入审批记录并推进环节", true));
        list.add(tool("invite_open", "打开面试邀请", "决审通过时创建待邀请记录", true));
        list.add(tool("invite_send", "发送邀请", "补全面试时间、地点和说明，并标记已发送", true));
        list.add(tool("interview_result", "记录面试结果", "写入通过或未通过", true));
        return list;
    }

    private static List<Map<String, Object>> skills() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(skill("rms-resume-intake", "简历接入规范",
                "只接收已经落库的简历和岗位申请。不在此智能体里改审批结论。"));
        list.add(skill("rms-hr-screen", "人力初筛规范",
                "只处理待查看的申请。同意进入部门，驳回退回申请人。结论只能是同意或驳回。"));
        list.add(skill("rms-dept-review", "部门审核规范",
                "只处理部门审核环节。同意进入人力决审，驳回退回申请人。"));
        list.add(skill("rms-hr-decision", "人力决审规范",
                "只处理人力处审核。同意必须同时生成一条待邀请，驳回退回部门。"));
        list.add(skill("rms-interview-invite", "面试邀请规范",
                "只发送待邀请记录。时间、地点、说明缺一不可。发送后候选人才能在我的面试里看到。"));
        list.add(skill("rms-interview-result", "面试结论规范",
                "只记录已发送且尚未有结果的面试。不覆盖已有结论。"));
        return list;
    }

    private static List<Map<String, Object>> agents() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(agent(1, "resume_intake", "ResumeIntakeAgent", "简历接入",
                PHASE_INTAKE, false, "候选人提交简历并生成岗位申请，停在待查看。",
                Arrays.asList("rms-resume-intake"),
                Arrays.asList("apply_lookup"),
                "现有在线投递 doPositionApply"));
        list.add(agent(2, "hr_screen", "HrScreenAgent", "人力初筛",
                PHASE_HR_SCREEN, false, "人力处先看简历，决定是否送给用人部门。",
                Arrays.asList("rms-hr-screen"),
                Arrays.asList("apply_lookup", "approval_transition"),
                "审批环节：待查看"));
        list.add(agent(3, "dept_review", "DeptReviewAgent", "部门审核",
                PHASE_DEPT, false, "用人部门给出是否进入人力决审的结论。",
                Arrays.asList("rms-dept-review"),
                Arrays.asList("apply_lookup", "approval_transition"),
                "审批环节：部门审核"));
        list.add(agent(4, "hr_decision", "HrDecisionAgent", "人力决审",
                PHASE_HR_DECISION, false, "人力处终审。通过后打开一条待发送的面试邀请。",
                Arrays.asList("rms-hr-decision"),
                Arrays.asList("apply_lookup", "approval_transition", "invite_open"),
                "审批环节：人力处审核"));
        list.add(agent(5, "interview_invite", "InterviewInviteAgent", "面试邀请",
                PHASE_INVITE, false, "填写时间、地点和说明，把待邀请发送给候选人。",
                Arrays.asList("rms-interview-invite"),
                Arrays.asList("apply_lookup", "invite_send"),
                "邀请状态：待邀请 → 已发送邀请"));
        list.add(agent(6, "interview_result", "InterviewResultAgent", "面试结论",
                PHASE_INTERVIEW, false, "面试结束后记录通过或未通过。",
                Arrays.asList("rms-interview-result"),
                Arrays.asList("apply_lookup", "interview_result"),
                "邀请状态：已发送邀请"));
        return list;
    }

    private static List<Map<String, Object>> phases() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(phase(1, PHASE_INTAKE, "简历接入", "ResumeIntakeAgent", "#44403c"));
        list.add(phase(2, PHASE_HR_SCREEN, "人力初筛", "HrScreenAgent", "#0f766e"));
        list.add(phase(3, PHASE_DEPT, "部门审核", "DeptReviewAgent", "#1d4ed8"));
        list.add(phase(4, PHASE_HR_DECISION, "人力决审", "HrDecisionAgent", "#9a3412"));
        list.add(phase(5, PHASE_INVITE, "面试邀请", "InterviewInviteAgent", "#6d28d9"));
        list.add(phase(6, PHASE_INTERVIEW, "面试结论", "InterviewResultAgent", "#166534"));
        return list;
    }

    private static Map<String, Object> object(String type, String label, String color, String description, Map<String, Object>... properties) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("type", type);
        item.put("label", label);
        item.put("color", color);
        item.put("description", description);
        item.put("properties", Arrays.asList(properties));
        return item;
    }

    private static Map<String, Object> prop(String name, String label, String type) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("label", label);
        item.put("type", type);
        return item;
    }

    private static Map<String, Object> rel(String relation, String label, String from, String to, String description) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("relation", relation);
        item.put("label", label);
        item.put("from", from);
        item.put("to", to);
        item.put("description", description);
        return item;
    }

    private static Map<String, Object> action(String id, String label, String agentLabel, String agentKey, String phaseKey,
                                              String criteria, String sideEffects, Map<String, Object>... parameters) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("label", label);
        item.put("agentLabel", agentLabel);
        item.put("agentKey", agentKey);
        item.put("phaseKey", phaseKey);
        item.put("submissionCriteria", criteria);
        item.put("sideEffects", sideEffects);
        item.put("parameters", Arrays.asList(parameters));
        return item;
    }

    private static Map<String, Object> param(String name, String label, boolean required) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("label", label);
        item.put("required", required);
        return item;
    }

    private static Map<String, Object> tool(String name, String label, String description, boolean write) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("label", label);
        item.put("description", description);
        item.put("write", write);
        item.put("workflowCode", WORKFLOW_CODE);
        return item;
    }

    private static Map<String, Object> skill(String code, String name, String body) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("code", code);
        item.put("name", name);
        item.put("body", body);
        item.put("workflowCode", WORKFLOW_CODE);
        return item;
    }

    private static Map<String, Object> agent(int order, String agentKey, String agentName, String label, String phaseKey,
                                             boolean usesLlm, String description, List<String> skills, List<String> tools, String note) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("order", order);
        item.put("agentKey", agentKey);
        item.put("agentName", agentName);
        item.put("label", label);
        item.put("phaseKey", phaseKey);
        item.put("usesLlm", usesLlm);
        item.put("description", description);
        item.put("skills", skills);
        item.put("tools", tools);
        item.put("note", note);
        return item;
    }

    private static Map<String, Object> phase(int order, String key, String name, String agentName, String color) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("order", order);
        item.put("key", key);
        item.put("name", name);
        item.put("agentName", agentName);
        item.put("color", color);
        item.put("workflowCode", WORKFLOW_CODE);
        return item;
    }
}

package org.jeecg.modules.recruitment.ontology;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.recruitment.position.entity.XgsFlowOpinions;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.entity.XgsPositions;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionsService;
import org.jeecg.modules.recruitment.security.XgsRecruitmentAuthUtil;
import org.jeecg.modules.recruitment.xgsInviteToInterview.entity.XgsInviteToInterview;
import org.jeecg.modules.recruitment.xgsInviteToInterview.service.IXgsInviteToInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 把岗位申请映射到招聘智能体阶段，并按本体动作调用现有审批与邀请服务。
 */
@Service
public class RmsAgentWorkflowService {

    private static final int BOARD_LIMIT = 200;
    private static final String PERM_INVITE_LIST = "xgsInviteToInterview:xgs_invite_to_interview:list";

    @Autowired
    private IXgsPositionApplyService positionApplyService;
    @Autowired
    private IXgsPositionsService positionsService;
    @Autowired
    private IXgsInviteToInterviewService inviteService;
    @Autowired
    private IXgsFlowOpinionsService flowOpinionsService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    public boolean canViewBoard() {
        return XgsRecruitmentAuthUtil.canManageApplies() || SecurityUtils.getSubject().isPermitted(PERM_INVITE_LIST);
    }

    private boolean canRun(String actionId) {
        if (XgsRecruitmentAuthUtil.canManageApplies()) {
            return true;
        }
        return SecurityUtils.getSubject().isPermitted(PERM_INVITE_LIST) && isInviteAction(actionId);
    }

    private boolean isInviteAction(String actionId) {
        return "SendInterviewInvite".equals(actionId)
                || "RecordInterviewPass".equals(actionId)
                || "RecordInterviewFail".equals(actionId);
    }

    public Map<String, Object> board(boolean myDeptOnly) {
        List<XgsPositionApply> applies = positionApplyService.list(new QueryWrapper<XgsPositionApply>()
                .orderByDesc("update_time")
                .orderByDesc("create_time")
                .last("LIMIT " + BOARD_LIMIT));
        Map<String, XgsPositions> positions = loadPositions(applies);
        overlayDisplay(applies, positions);
        if (myDeptOnly) {
            applies = filterMyDept(applies, positions);
        }
        Map<String, XgsInviteToInterview> invites = latestInvites(applies);

        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> phase : RmsHireOntologyCatalog.schemaPhases()) {
            grouped.put(String.valueOf(phase.get("key")), new ArrayList<>());
        }
        for (XgsPositionApply apply : applies) {
            XgsInviteToInterview invite = invites.get(apply.getId());
            String phaseKey = phaseOf(apply, invite);
            List<Map<String, Object>> bucket = grouped.get(phaseKey);
            if (bucket == null) {
                bucket = grouped.computeIfAbsent(RmsHireOntologyCatalog.PHASE_INTAKE, key -> new ArrayList<>());
            }
            bucket.add(toCase(apply, invite, phaseKey));
        }

        List<Map<String, Object>> phases = new ArrayList<>();
        for (Map<String, Object> defined : RmsHireOntologyCatalog.schemaPhases()) {
            String key = String.valueOf(defined.get("key"));
            Map<String, Object> column = new LinkedHashMap<>(defined);
            List<Map<String, Object>> cases = grouped.getOrDefault(key, new ArrayList<>());
            column.put("count", cases.size());
            column.put("cases", cases);
            phases.add(column);
        }
        Map<String, Object> board = new LinkedHashMap<>();
        board.put("workflowCode", RmsHireOntologyCatalog.WORKFLOW_CODE);
        board.put("limit", BOARD_LIMIT);
        board.put("phases", phases);
        return board;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> run(RmsAgentActionRequest request) {
        if (request == null || StringUtils.isBlank(request.getActionId()) || StringUtils.isBlank(request.getApplyId())) {
            throw new IllegalArgumentException("请指定动作和申请");
        }
        XgsPositionApply apply = positionApplyService.getById(request.getApplyId());
        if (apply == null) {
            throw new IllegalArgumentException("申请不存在");
        }
        String actionId = request.getActionId();
        if (!canRun(actionId)) {
            throw new IllegalArgumentException("只有招聘办理人员可以推动这条工作流");
        }
        XgsInviteToInterview invite = latestInvite(apply.getId());
        String before = phaseOf(apply, invite);
        if (!RmsHireOntologyCatalog.actionsForPhase(before).contains(actionId)) {
            throw new IllegalArgumentException("当前阶段不能执行这个动作");
        }

        String message;
        switch (actionId) {
            case "HrScreenPass":
                message = transit(apply, IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW, true);
                break;
            case "HrScreenReject":
                message = transit(apply, IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW, false);
                break;
            case "DeptReviewPass":
                message = transit(apply, IXgsFlowOpinionsService.NODE_DEPT, true);
                break;
            case "DeptReviewReject":
                message = transit(apply, IXgsFlowOpinionsService.NODE_DEPT, false);
                break;
            case "HrDecisionPass":
                message = transit(apply, IXgsFlowOpinionsService.NODE_HR, true);
                break;
            case "HrDecisionReject":
                message = transit(apply, IXgsFlowOpinionsService.NODE_HR, false);
                break;
            case "SendInterviewInvite":
                message = sendInvite(apply, invite, request);
                break;
            case "RecordInterviewPass":
                message = recordInterview(apply, invite, "面试通过");
                break;
            case "RecordInterviewFail":
                message = recordInterview(apply, invite, "面试未通过");
                break;
            default:
                throw new IllegalArgumentException("未注册的动作");
        }

        XgsPositionApply afterApply = positionApplyService.getById(apply.getId());
        XgsInviteToInterview afterInvite = latestInvite(apply.getId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("actionId", actionId);
        result.put("agentName", agentNameOf(actionId));
        result.put("message", message);
        result.put("phaseBefore", before);
        result.put("phaseAfter", phaseOf(afterApply, afterInvite));
        result.put("applyId", apply.getId());
        return result;
    }

    private String transit(XgsPositionApply apply, String node, boolean agree) {
        XgsFlowOpinions opinion = new XgsFlowOpinions();
        opinion.setParentId(apply.getId());
        opinion.setApprovalNode(node);
        opinion.setOpinions(agree ? IXgsFlowOpinionsService.APPROVAL_RESULT_AGREE : IXgsFlowOpinionsService.APPROVAL_RESULT_REJECT);
        opinion.setApprovalStatus(apply.getApprovalStatus());
        opinion.setApprovalUser(XgsRecruitmentAuthUtil.currentUser().getRealname());
        String message = flowOpinionsService.add(opinion);
        return StringUtils.defaultIfBlank(message, "已办理");
    }

    private String sendInvite(XgsPositionApply apply, XgsInviteToInterview invite, RmsAgentActionRequest request) {
        if (StringUtils.isAnyBlank(request.getInterviewDate(), request.getInterviewPlace(), request.getInviteLetter())) {
            throw new IllegalArgumentException("发送邀请需要面试时间、地点和说明");
        }
        if (invite != null && "已发送邀请".equals(invite.getInviteStatus())) {
            throw new IllegalArgumentException("邀请已发送，不能重复发送");
        }
        if (invite != null && StringUtils.isNotBlank(invite.getInterviewResult())) {
            throw new IllegalArgumentException("面试已有结论，不能再发邀请");
        }
        if (invite == null) {
            invite = openInvite(apply);
        }
        Date when = parseDate(request.getInterviewDate());
        invite.setInterviewDate(when);
        invite.setInterviewPlace(request.getInterviewPlace().trim());
        invite.setInviteLetter(request.getInviteLetter().trim());
        invite.setInviteStatus("已发送邀请");
        invite.setInviteResult(null);
        invite.setStatus("待面试");
        inviteService.updateById(invite);

        apply.setApprovalNode(IXgsFlowOpinionsService.NODE_INVITE);
        apply.setInviteStatus("已发送邀请");
        apply.setInterviewDate(when);
        apply.setInterviewInformation(request.getInviteLetter().trim());
        apply.setInterviewStatus("待面试");
        positionApplyService.updateById(apply);
        return "面试邀请已发送";
    }

    private String recordInterview(XgsPositionApply apply, XgsInviteToInterview invite, String resultText) {
        if (invite == null || !"已发送邀请".equals(invite.getInviteStatus())) {
            throw new IllegalArgumentException("邀请尚未发送，不能记录面试结果");
        }
        if (StringUtils.isNotBlank(invite.getInterviewResult())) {
            throw new IllegalArgumentException("面试结果已填写，不能覆盖");
        }
        invite.setInterviewResult(resultText);
        invite.setStatus(resultText);
        inviteService.updateById(invite);

        apply.setApprovalNode(IXgsFlowOpinionsService.NODE_INTERVIEW);
        apply.setInterviewStatus(resultText);
        positionApplyService.updateById(apply);
        return "已记录" + resultText;
    }

    private XgsInviteToInterview openInvite(XgsPositionApply apply) {
        XgsInviteToInterview invite = new XgsInviteToInterview();
        invite.setApplyId(apply.getId());
        invite.setPositionId(apply.getPositionId());
        invite.setPositionName(apply.getPositionName());
        invite.setCandidateId(apply.getUserId());
        invite.setCandidate(apply.getUserName());
        invite.setInviteStatus("待邀请");
        inviteService.save(invite);
        return invite;
    }

    private Map<String, XgsInviteToInterview> latestInvites(List<XgsPositionApply> applies) {
        Map<String, XgsInviteToInterview> map = new LinkedHashMap<>();
        if (applies.isEmpty()) {
            return map;
        }
        List<String> ids = new ArrayList<>();
        for (XgsPositionApply apply : applies) {
            if (StringUtils.isNotBlank(apply.getId())) {
                ids.add(apply.getId());
            }
        }
        if (ids.isEmpty()) {
            return map;
        }
        List<XgsInviteToInterview> invites = inviteService.list(new QueryWrapper<XgsInviteToInterview>().in("apply_id", ids));
        for (XgsInviteToInterview invite : invites) {
            XgsInviteToInterview prev = map.get(invite.getApplyId());
            if (prev == null || later(invite, prev)) {
                map.put(invite.getApplyId(), invite);
            }
        }
        return map;
    }

    private XgsInviteToInterview latestInvite(String applyId) {
        List<XgsInviteToInterview> invites = inviteService.list(new QueryWrapper<XgsInviteToInterview>().eq("apply_id", applyId));
        XgsInviteToInterview latest = null;
        for (XgsInviteToInterview invite : invites) {
            if (latest == null || later(invite, latest)) {
                latest = invite;
            }
        }
        return latest;
    }

    private boolean later(XgsInviteToInterview left, XgsInviteToInterview right) {
        Date leftTime = left.getCreateTime();
        Date rightTime = right.getCreateTime();
        if (leftTime == null) {
            return false;
        }
        if (rightTime == null) {
            return true;
        }
        return leftTime.after(rightTime);
    }

    static String phaseOf(XgsPositionApply apply, XgsInviteToInterview invite) {
        if (apply == null) {
            return RmsHireOntologyCatalog.PHASE_INTAKE;
        }
        if (invite != null && StringUtils.isNotBlank(invite.getInterviewResult())) {
            return RmsHireOntologyCatalog.PHASE_INTERVIEW;
        }
        if (invite != null && "已发送邀请".equals(invite.getInviteStatus())) {
            return RmsHireOntologyCatalog.PHASE_INTERVIEW;
        }
        String node = apply.getApprovalNode();
        if (IXgsFlowOpinionsService.NODE_INTERVIEW.equals(node)) {
            return RmsHireOntologyCatalog.PHASE_INTERVIEW;
        }
        if (IXgsFlowOpinionsService.NODE_INVITE.equals(node) || IXgsFlowOpinionsService.NODE_END.equals(node)) {
            return RmsHireOntologyCatalog.PHASE_INVITE;
        }
        if (IXgsFlowOpinionsService.NODE_HR.equals(node)) {
            return RmsHireOntologyCatalog.PHASE_HR_DECISION;
        }
        if (IXgsFlowOpinionsService.NODE_DEPT.equals(node)) {
            return RmsHireOntologyCatalog.PHASE_DEPT;
        }
        if (IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW.equals(node)) {
            return RmsHireOntologyCatalog.PHASE_HR_SCREEN;
        }
        return RmsHireOntologyCatalog.PHASE_INTAKE;
    }

    private Map<String, Object> toCase(XgsPositionApply apply, XgsInviteToInterview invite, String phaseKey) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("applyId", apply.getId());
        item.put("resumeId", apply.getResumeId());
        item.put("positionType", apply.getPositionType());
        item.put("userName", blank(apply.getUserName(), "未填姓名"));
        item.put("resumeName", blank(apply.getResumeName(), "未命名简历"));
        item.put("positionName", blank(apply.getPositionName(), "未填岗位"));
        item.put("positionDept", blank(apply.getPositionDept(), ""));
        item.put("approvalNode", apply.getApprovalNode());
        item.put("approvalStatus", apply.getApprovalStatus());
        item.put("status", apply.getStatus());
        item.put("phaseKey", phaseKey);
        item.put("inviteId", invite == null ? null : invite.getId());
        item.put("inviteStatus", invite == null ? apply.getInviteStatus() : invite.getInviteStatus());
        item.put("interviewResult", invite == null ? null : invite.getInterviewResult());
        boolean closed = invite != null && StringUtils.isNotBlank(invite.getInterviewResult());
        item.put("closed", closed);
        item.put("actions", closed ? new ArrayList<>() : RmsHireOntologyCatalog.actionsForPhase(phaseKey));
        return item;
    }

    private Map<String, XgsPositions> loadPositions(List<XgsPositionApply> applies) {
        Map<String, XgsPositions> map = new LinkedHashMap<>();
        Set<String> ids = new HashSet<>();
        for (XgsPositionApply apply : applies) {
            if (StringUtils.isNotBlank(apply.getPositionId())) {
                ids.add(apply.getPositionId());
            }
        }
        if (ids.isEmpty()) {
            return map;
        }
        Collection<XgsPositions> positions = positionsService.listByIds(ids);
        if (positions == null) {
            return map;
        }
        for (XgsPositions position : positions) {
            map.put(position.getId(), position);
        }
        return map;
    }

    private void overlayDisplay(List<XgsPositionApply> applies, Map<String, XgsPositions> positions) {
        Map<String, String> deptNames = new LinkedHashMap<>();
        for (XgsPositionApply apply : applies) {
            XgsPositions position = positions.get(apply.getPositionId());
            if (position != null) {
                if (StringUtils.isBlank(apply.getPositionName())) {
                    apply.setPositionName(position.getPositionName());
                }
                if (StringUtils.isBlank(apply.getPositionType())) {
                    apply.setPositionType(position.getCategory());
                }
                if (StringUtils.isBlank(apply.getPositionDept()) && StringUtils.isNotBlank(position.getDept())) {
                    apply.setPositionDept(deptNames.computeIfAbsent(position.getDept(), this::departName));
                }
            }
            if (StringUtils.isBlank(apply.getUserName()) && StringUtils.isNotBlank(apply.getUserId())) {
                apply.setUserName(userName(apply.getUserId()));
            }
        }
    }

    private List<XgsPositionApply> filterMyDept(List<XgsPositionApply> applies, Map<String, XgsPositions> positions) {
        LoginUser user = XgsRecruitmentAuthUtil.currentUser();
        Set<String> names = new HashSet<>();
        List<String> departNames = sysBaseAPI.getDepartNamesByUsername(user.getUsername());
        if (departNames != null) {
            names.addAll(departNames);
        }
        Set<String> departIds = new HashSet<>();
        if (StringUtils.isNotBlank(user.getOrgCode())) {
            String ids = sysBaseAPI.getDepartIdsByOrgCode(user.getOrgCode());
            if (StringUtils.isNotBlank(ids)) {
                for (String id : ids.split(",")) {
                    if (StringUtils.isNotBlank(id)) {
                        departIds.add(id.trim());
                    }
                }
            }
        }
        if (names.isEmpty() && departIds.isEmpty()) {
            return applies;
        }
        List<XgsPositionApply> kept = new ArrayList<>();
        for (XgsPositionApply apply : applies) {
            XgsPositions position = positions.get(apply.getPositionId());
            String positionDeptId = position == null ? null : position.getDept();
            boolean nameHit = StringUtils.isNotBlank(apply.getPositionDept()) && names.contains(apply.getPositionDept());
            boolean idHit = StringUtils.isNotBlank(positionDeptId) && departIds.contains(positionDeptId);
            boolean storedIdHit = StringUtils.isNotBlank(apply.getPositionDept()) && departIds.contains(apply.getPositionDept());
            if (nameHit || idHit || storedIdHit) {
                kept.add(apply);
            }
        }
        return kept;
    }

    private String departName(String deptId) {
        try {
            List<JSONObject> departs = sysBaseAPI.queryDepartsByIds(deptId);
            if (departs != null && !departs.isEmpty() && departs.get(0) != null) {
                String name = departs.get(0).getString("departName");
                if (StringUtils.isNotBlank(name)) {
                    return name;
                }
            }
        } catch (Exception ignored) {
            return deptId;
        }
        return deptId;
    }

    private String userName(String userId) {
        try {
            JSONObject user = sysBaseAPI.queryUserById(userId);
            if (user == null) {
                return "";
            }
            return StringUtils.defaultIfBlank(user.getString("realname"), user.getString("username"));
        } catch (Exception ignored) {
            return "";
        }
    }

    private String agentNameOf(String actionId) {
        for (Map<String, Object> action : RmsHireOntologyCatalog.schemaActions()) {
            if (actionId.equals(action.get("id"))) {
                return String.valueOf(action.get("agentLabel"));
            }
        }
        return "";
    }

    private Date parseDate(String text) {
        String value = text.trim();
        String pattern = value.length() > 10 ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd";
        try {
            return new SimpleDateFormat(pattern).parse(value);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("面试时间格式应为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private String blank(String value, String fallback) {
        return StringUtils.isBlank(value) ? fallback : value;
    }
}

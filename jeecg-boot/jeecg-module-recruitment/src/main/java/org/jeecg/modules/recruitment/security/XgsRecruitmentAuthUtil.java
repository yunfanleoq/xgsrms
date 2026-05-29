package org.jeecg.modules.recruitment.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeBase;

/**
 * 招聘模块资源所有权与审核/管理权限校验（B-002 / B-003 / PROJ-001）。
 */
public final class XgsRecruitmentAuthUtil {

    private static final String PERM_RESUME_LIST = "xgsResume:xgs_resume_base:list";
    private static final String PERM_APPLY_LIST = "positions:xgs_position_apply:list";
    private static final String PERM_APPLY_EXPORT = "positions:xgs_position_apply:exportXls";

    private XgsRecruitmentAuthUtil() {
    }

    public static LoginUser currentUser() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            throw new UnauthorizedException("未登录");
        }
        return user;
    }

    public static boolean canManageResumes() {
        return SecurityUtils.getSubject().isPermitted(PERM_RESUME_LIST);
    }

    public static boolean canManageApplies() {
        return SecurityUtils.getSubject().isPermitted(PERM_APPLY_LIST);
    }

    public static boolean canExportApplies() {
        return SecurityUtils.getSubject().isPermitted(PERM_APPLY_EXPORT);
    }

    public static boolean ownsResume(LoginUser user, XgsResumeBase resume) {
        if (resume == null || user == null) {
            return false;
        }
        if (StringUtils.isNotBlank(resume.getUserId()) && resume.getUserId().equals(user.getId())) {
            return true;
        }
        return StringUtils.isNotBlank(resume.getCreateBy()) && resume.getCreateBy().equals(user.getUsername());
    }

    public static boolean ownsApply(LoginUser user, XgsPositionApply apply) {
        if (apply == null || user == null) {
            return false;
        }
        if (StringUtils.isNotBlank(apply.getUserId()) && apply.getUserId().equals(user.getId())) {
            return true;
        }
        return StringUtils.isNotBlank(apply.getCreateBy()) && apply.getCreateBy().equals(user.getUsername());
    }

    public static void assertCanReadResume(XgsResumeBase resume) {
        LoginUser user = currentUser();
        if (canManageResumes() || ownsResume(user, resume)) {
            return;
        }
        throw new UnauthorizedException("无权限访问该简历");
    }

    public static void assertCanWriteResume(XgsResumeBase resume) {
        assertCanReadResume(resume);
    }

    public static void assertCanReadApply(XgsPositionApply apply) {
        LoginUser user = currentUser();
        if (canManageApplies() || ownsApply(user, apply)) {
            return;
        }
        throw new UnauthorizedException("无权限访问该申请");
    }

    public static void assertCanExportApply(XgsPositionApply apply) {
        LoginUser user = currentUser();
        if (canExportApplies() || canManageApplies() || ownsApply(user, apply)) {
            return;
        }
        throw new UnauthorizedException("无权限导出该申请");
    }
}

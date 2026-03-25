package org.jeecg.modules.recruitment.security;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.TokenUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 门户接口可选 Token：Shiro 对 @IgnoreAuth 不注入 Principal，需自行解析 Header 中的 JWT。
 */
public final class RecruitmentPortalAuthUtil {

	private RecruitmentPortalAuthUtil() {
	}

	public static LoginUser tryGetLoginUser(HttpServletRequest req, CommonAPI commonApi, RedisUtil redisUtil) {
		if (req == null || commonApi == null || redisUtil == null) {
			return null;
		}
		String token = TokenUtils.getTokenByRequest(req);
		if (StringUtils.isBlank(token)) {
			return null;
		}
		try {
			String username = JwtUtil.getUsername(token);
			if (username == null) {
				return null;
			}
			return TokenUtils.getLoginUser(username, commonApi, redisUtil);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isRecruitmentPrivileged(LoginUser u) {
		if (u == null || u.getRoleCode() == null) {
			return false;
		}
		Set<String> roles = new HashSet<>();
		for (String r : u.getRoleCode().split(",")) {
			roles.add(r.trim());
		}
		return roles.contains("admin") || roles.contains("depart_position_manager");
	}
}

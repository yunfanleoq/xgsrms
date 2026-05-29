package org.jeecg.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.config.shiro.filters.JwtFilter;
import org.springframework.context.annotation.Configuration;

/**
 * 启动时注入 jeecg.security 到 JWT / 鉴权过滤器。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JeecgSecurityConfiguration {

    private final JeecgSecurityProperties securityProperties;

    @PostConstruct
    public void applySecurityProperties() {
        JwtFilter.configure(securityProperties.isAllowTokenInQuery(), securityProperties.getCorsAllowedOrigins());

        JeecgSecurityProperties.Token token = securityProperties.getToken();
        if (token != null) {
            Long pc = token.getExpireTime();
            Long app = token.getAppExpireTime();
            if (pc != null || app != null) {
                JwtUtil.configureTokenExpire(
                        pc != null ? pc : JwtUtil.EXPIRE_TIME,
                        app != null ? app : (pc != null ? pc : JwtUtil.APP_EXPIRE_TIME));
                log.info("JWT 过期时间已配置: PC={}ms, APP={}ms", JwtUtil.EXPIRE_TIME, JwtUtil.APP_EXPIRE_TIME);
            }
        }

        log.info("安全策略: allowTokenInQuery={}, allowPasswordChangeGet={}",
                securityProperties.isAllowTokenInQuery(), securityProperties.isAllowPasswordChangeGet());
    }

    public JeecgSecurityProperties getSecurityProperties() {
        return securityProperties;
    }
}

package org.jeecg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用安全相关配置（与 application-*.yml 中 jeecg.security 对应）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jeecg.security")
public class JeecgSecurityProperties {

    /**
     * 是否允许 GET /sys/user/passwordChange（生产建议 false）。
     */
    private boolean allowPasswordChangeGet = true;

    /**
     * 是否允许从 URL 查询参数读取 JWT（生产建议 false）。
     */
    private boolean allowTokenInQuery = true;

    /**
     * CORS 允许来源，逗号分隔；为空时保持 Jeecg 默认（反射请求 Origin / 通配）。
     */
    private String corsAllowedOrigins = "";

    private Token token = new Token();

    @Data
    public static class Token {
        /**
         * PC 端 JWT 有效期（毫秒）。未配置时使用 JwtUtil 内置默认值。
         */
        private Long expireTime;

        /**
         * APP 端 JWT 有效期（毫秒）。未配置时与 PC 相同或使用 JwtUtil 内置 APP 默认。
         */
        private Long appExpireTime;
    }
}

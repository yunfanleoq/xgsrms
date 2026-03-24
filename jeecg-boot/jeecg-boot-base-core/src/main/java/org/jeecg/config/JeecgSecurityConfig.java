package org.jeecg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jeecg.security")
public class JeecgSecurityConfig {
    
    private Password password = new Password();
    private Login login = new Login();
    private Token token = new Token();
    private Session session = new Session();
    /** VULN-028：生产是否强制要求 Redis requirepass，防止未授权写入 Token 白名单 */
    private Redis redis = new Redis();

    /**
     * 是否允许通过 GET /sys/user/passwordChange 重置密码（敏感信息易进 URL/日志）。生产应为 false，仅兼容旧客户端时可临时 true。
     */
    private boolean allowPasswordChangeGet = false;

    /**
     * 是否允许从 URL 查询参数读取 token（易进 Referer/日志）。生产建议 false；依赖 query token 的旧集成可 true。
     */
    private boolean allowTokenInQuery = true;

    /**
     * CORS 允许的 Origin 列表（逗号分隔，精确匹配）。为空则保持旧行为：反射请求 Origin（开发便利，生产应配置白名单）。
     */
    private String corsAllowedOrigins = "";
    
    @Data
    public static class Password {
        /**
         * 密码有效期天数，-1表示永不过期
         */
        private int expireDays = 90;
    }
    
    @Data
    public static class Login {
        /**
         * 最大失败次数
         */
        private int maxFailTimes = 5;
        
        /**
         * 锁定时长（秒）
         */
        private int lockDuration = 600;
    }
    
    @Data
    public static class Token {
        /**
         * JWT Token过期时间（毫秒）；默认 8h（与 prod 建议一致，避免 7 天长会话 C-004）
         */
        private long expireTime = 28800000;

        /**
         * JWT签名密钥（生产环境请通过环境变量注入）
         */
        private String jwtSecret = "CHANGE_ME_IN_PRODUCTION";
    }
    
    @Data
    public static class Session {
        /**
         * Session超时时间（秒）
         */
        private int timeout = 1800; // 默认30分钟
    }

    @Data
    public static class Redis {
        /**
         * 为 true 时（默认），生产启动校验要求配置 spring.redis.password。
         * 仅在内网隔离且经安全评估后可设为 false（对应环境变量 JEECG_SECURITY_REDIS_REQUIRE_PASSWORD=false）。
         */
        private boolean requirePassword = true;
    }
}

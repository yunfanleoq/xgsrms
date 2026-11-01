package org.jeecg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

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

    /**
     * 注册是否跳过短信/邮箱验证码（内网无法出网发信时为 true）。
     */
    private boolean registerSkipSms = false;

    private Password password = new Password();

    private Login login = new Login();

    private Token token = new Token();

    @Data
    public static class Password {
        /**
         * 最短长度（与弱口令清单「≥12 位」对齐）。
         */
        private int minLength = 12;

        /**
         * 口令有效天数。&lt;0 表示不强制过期。
         */
        private int expireDays = 90;
    }

    @Data
    public static class Login {
        /**
         * 同一账号连续失败次数上限，达到后锁定。
         */
        private int maxFailTimes = 5;

        /**
         * 失败锁定时长（秒），默认 30 分钟。
         */
        private int lockDuration = 1800;

        /**
         * 拦截境外登录：优先看 X-Country-Code，没有头则按国内 IPv4 段判断。
         */
        private boolean blockOverseas = false;

        /**
         * 生产建议 true：登录密码必须走 RSA，拒绝明文。
         */
        private boolean requireRsa = false;

        /**
         * 密码验证通过后再校验短信验证码（账号已绑手机时）。
         */
        private boolean mfaSms = false;

        /**
         * 允许登录的国家/地区代码，逗号分隔。
         */
        private String allowedCountryCodes = "CN,HK,MO,TW";
    }

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

    public boolean isPasswordExpired(Date lastPwdUpdateTime) {
        int days = password == null ? 90 : password.getExpireDays();
        if (days < 0) {
            return false;
        }
        if (lastPwdUpdateTime == null) {
            return true;
        }
        long elapsed = System.currentTimeMillis() - lastPwdUpdateTime.getTime();
        return elapsed >= days * 24L * 3600_000L;
    }
}

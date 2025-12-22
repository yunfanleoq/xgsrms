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
         * JWT Token过期时间（毫秒）
         */
        private long expireTime = 604800000; // 默认7天
    }
    
    @Data
    public static class Session {
        /**
         * Session超时时间（秒）
         */
        private int timeout = 1800; // 默认30分钟
    }
}
package org.jeecg.modules.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 忘记密码 / 重置密码（POST 请求体，避免敏感参数出现在 URL 与访问日志中）
 */
@Data
public class PasswordResetRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String smscode;
    private String emailCode;
    private String phone;
    private String email;
}

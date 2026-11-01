package org.jeecg.common.util.security;

import org.jeecg.common.util.oConvertUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 口令复杂度 + 弱口令字典（对齐弱口令整治清单：长度≥12，大小写+数字+特殊字符）。
 */
public final class PasswordPolicy {

    public static final int MIN_LENGTH = 12;
    public static final String MESSAGE = "密码至少12位，且须同时包含大写字母、小写字母、数字和特殊字符";

    private static final Pattern STRONG = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{" + MIN_LENGTH + ",}$");

    private static final Set<String> WEAK = new HashSet<>(Arrays.asList(
            "123456", "1234567", "12345678", "123456789", "1234567890",
            "password", "password1", "password123", "passw0rd",
            "admin", "admin123", "admin888", "administrator",
            "qwerty", "qwerty123", "abc123", "abc123456",
            "111111", "000000", "666666", "888888", "88888888",
            "iloveyou", "welcome", "letmein", "monkey", "dragon",
            "root", "toor", "test", "test123", "user", "user123",
            "jeecg", "jeecgboot", "xiaoguan", "xgsrms",
            "1qaz2wsx", "1q2w3e4r", "qwe123", "asd123",
            "p@ssw0rd", "p@ssword", "pass@123", "admin@123",
            "Aa123456", "Abc123456", "Admin@123", "Password1",
            "123qwe", "123abc", "qazwsx", "zxcvbnm"
    ));

    private PasswordPolicy() {
    }

    /**
     * @return 校验失败原因；通过返回 null
     */
    public static String validate(String username, String rawPassword) {
        if (oConvertUtils.isEmpty(rawPassword)) {
            return "密码不能为空";
        }
        if (!STRONG.matcher(rawPassword).matches()) {
            return MESSAGE;
        }
        if (oConvertUtils.isNotEmpty(username)
                && rawPassword.toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT))) {
            return "密码不能包含用户名";
        }
        if (isWeak(rawPassword)) {
            return "该密码为常见弱口令，请更换";
        }
        return null;
    }

    public static boolean isWeak(String rawPassword) {
        if (oConvertUtils.isEmpty(rawPassword)) {
            return true;
        }
        return WEAK.contains(rawPassword) || WEAK.contains(rawPassword.toLowerCase(Locale.ROOT));
    }
}

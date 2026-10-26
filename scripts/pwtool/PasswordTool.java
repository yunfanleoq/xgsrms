package org.jeecg.common.util;

/** 本地计算与线上一致的口令摘要。参数：用户名、新口令、盐。 */
public class PasswordTool {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.exit(2);
        }
        String hex = PasswordUtil.encrypt(args[0], args[1], args[2]);
        if (hex == null || hex.isEmpty()) {
            System.exit(3);
        }
        System.out.print(hex);
    }
}

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * CLI wrapper matching org.jeecg.common.util.PasswordUtil (PBEWithMD5AndDES).
 * Usage: java JeecgPasswordHash <username> <plaintextPassword> [salt8chars]
 * If salt omitted, generates random 8-char salt and prints: salt|hexPassword
 */
public class JeecgPasswordHash {
    private static final String ALGORITHM = "PBEWithMD5AndDES";
    private static final int ITERATIONCOUNT = 1000;

    public static void main(String[] args) throws Exception {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: JeecgPasswordHash <username> <password> [salt]");
            System.exit(1);
        }
        String username = args[0];
        String password = args[1];
        String salt = args.length == 3 ? args[2] : randomSalt(8);
        String encoded = encrypt(username, password, salt);
        System.out.println(salt + "|" + encoded);
    }

    private static String randomSalt(int len) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String encrypt(String plaintext, String password, String salt) throws Exception {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt.getBytes(StandardCharsets.UTF_8), ITERATIONCOUNT);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] encipheredData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return bytesToHexString(encipheredData);
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}

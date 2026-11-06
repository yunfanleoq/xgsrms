package org.jeecg.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.enums.DySmsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;

/**
 * 统一身份认证短信。票据与发送地址、账号加密方式按短信接口手册。
 * 四个环境变量都非空才发送，否则调用方继续走原有短信通道。密钥只从环境变量读取。
 * <p>
 * 票据：POST {XGSRMS_SMS_BASE_URL}/bit-msa-sso/security/oauth/appToken
 * 发送：POST {XGSRMS_SMS_BASE_URL}/bit-fsm-smsservice/api/v2.0/sms/sendMessage?client_id&message&phoneNum&version
 */
public class IdentitySmsClient {

    private static final Logger logger = LoggerFactory.getLogger(IdentitySmsClient.class);

    static final String ENV_BASE_URL = "XGSRMS_SMS_BASE_URL";
    static final String ENV_CLIENT_ID = "XGSRMS_SMS_CLIENT_ID";
    static final String ENV_CLIENT_SECRET = "XGSRMS_SMS_CLIENT_SECRET";
    static final String ENV_ACCOUNT = "XGSRMS_SMS_ACCOUNT";
    static final String ENV_SIGN_NAME = "XGSRMS_SMS_SIGN_NAME";

    private static final String TOKEN_PATH = "/bit-msa-sso/security/oauth/appToken";
    private static final String SEND_PATH = "/bit-fsm-smsservice/api/v2.0/sms/sendMessage";
    private static final String API_VERSION = "v2.0";
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(10);
    private static final char[] BASE58_ALPHABET =
            "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(HTTP_TIMEOUT)
            .build();

    private static volatile String cachedToken;
    private static volatile long cachedUntilMs;

    private IdentitySmsClient() {
    }

    /**
     * 基址、系统标识、应用密码、票据账号四项都有值，且基址带 http(s) 时启用。
     */
    public static boolean isEnabled() {
        return completeConfig() != null;
    }

    /**
     * 四项里只填了一部分，或基址没有协议。此时不再改走阿里云或腾讯云。
     * 四项都为空时返回 null，表示仍用原来的短信通道。
     */
    public static String configError() {
        String baseUrl = env(ENV_BASE_URL);
        String clientId = env(ENV_CLIENT_ID);
        String clientSecret = env(ENV_CLIENT_SECRET);
        String account = env(ENV_ACCOUNT);
        if (StringUtils.isAllBlank(baseUrl, clientId, clientSecret, account)) {
            return null;
        }
        StringBuilder missing = new StringBuilder();
        appendMissing(missing, ENV_BASE_URL, baseUrl);
        appendMissing(missing, ENV_CLIENT_ID, clientId);
        appendMissing(missing, ENV_CLIENT_SECRET, clientSecret);
        appendMissing(missing, ENV_ACCOUNT, account);
        if (missing.length() > 0) {
            return "缺少 " + missing;
        }
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            return ENV_BASE_URL + " 需要以 http:// 或 https:// 开头，并写上端口";
        }
        return null;
    }

    public static boolean isPartial() {
        return configError() != null;
    }

    public static boolean send(String phone, JSONObject templateParamJson, DySmsEnum scene) {
        Config config = completeConfig();
        if (config == null) {
            logger.error("统一身份认证短信配置不完整，已停止发送");
            return false;
        }
        if (StringUtils.isBlank(phone)) {
            logger.warn("统一身份认证短信缺少手机号");
            return false;
        }
        String message = buildMessage(templateParamJson, scene, env(ENV_SIGN_NAME));
        if (message == null) {
            logger.warn("统一身份认证短信缺少验证码");
            return false;
        }
        try {
            String token = accessToken(config);
            return postSms(config, token, phone, message);
        } catch (Exception e) {
            logger.warn("统一身份认证短信发送失败: {}", e.getMessage());
            return false;
        }
    }

    static String buildMessage(JSONObject templateParamJson, DySmsEnum scene, String signName) {
        String code = templateParamJson == null ? null : templateParamJson.getString("code");
        if (StringUtils.isBlank(code)) {
            return null;
        }
        String purpose = "登录验证";
        if (scene == DySmsEnum.REGISTER_TEMPLATE_CODE) {
            purpose = "注册";
        } else if (scene == DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE
                || scene == DySmsEnum.CHANGE_PASSWORD_TEMPLATE_CODE) {
            purpose = "重置密码";
        }
        String text = "验证码" + code + "，用于" + purpose + "，10分钟内有效。";
        if (StringUtils.isBlank(signName)) {
            return text;
        }
        String sign = signName.trim();
        if (sign.startsWith("【") && sign.endsWith("】")) {
            return sign + text;
        }
        return "【" + sign + "】" + text;
    }

    private static boolean postSms(Config config, String token, String phone, String message) throws Exception {
        // 平台不接受 JSON 正文，字段必须放在查询串上，正文留空。
        String query = "client_id=" + urlEncode(config.clientId)
                + "&message=" + urlEncode(message)
                + "&phoneNum=" + urlEncode(phone)
                + "&version=" + urlEncode(API_VERSION);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(join(config.baseUrl, SEND_PATH) + "?" + query))
                .timeout(HTTP_TIMEOUT)
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            logger.warn("统一身份认证短信提交失败 http={}", response.statusCode());
            return false;
        }
        JSONObject json = JSONObject.parseObject(response.body());
        if (json == null) {
            logger.warn("统一身份认证短信响应无法解析");
            return false;
        }
        boolean ok = "0".equals(String.valueOf(json.get("status")));
        if (!ok) {
            logger.warn("统一身份认证短信发送失败 status={}", json.get("status"));
        }
        return ok;
    }

    private static String accessToken(Config config) throws Exception {
        long now = System.currentTimeMillis();
        String token = cachedToken;
        if (token != null && now < cachedUntilMs) {
            return token;
        }
        synchronized (IdentitySmsClient.class) {
            now = System.currentTimeMillis();
            if (cachedToken != null && now < cachedUntilMs) {
                return cachedToken;
            }
            JSONObject body = new JSONObject();
            body.put("client_id", config.clientId);
            body.put("grant_type", "sso");
            body.put("client_secret", config.clientSecret);
            body.put("account", encryptAccount(config.account, config.clientSecret));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(join(config.baseUrl, TOKEN_PATH)))
                    .timeout(HTTP_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString(), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("token http " + response.statusCode());
            }
            JSONObject json = JSONObject.parseObject(response.body());
            String code = json == null ? "" : String.valueOf(json.get("code"));
            if (json == null || StringUtils.isBlank(json.getString("access_token"))
                    || !("200".equals(code) || "1000".equals(code))) {
                throw new IllegalStateException("token rejected");
            }
            cachedToken = json.getString("access_token");
            cachedUntilMs = expiryMillis(json.getLongValue("expires_in")) - 300_000L;
            return cachedToken;
        }
    }

    /**
     * 手册附件算法：AES/ECB/PKCS5Padding，密钥为 client_secret 原文，再做 URL Base64 与 Base58。
     */
    static String encryptAccount(String account, String clientSecret) throws Exception {
        byte[] key = clientSecret.getBytes(StandardCharsets.UTF_8);
        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new IllegalArgumentException("aes key length " + key.length);
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        byte[] encrypted = cipher.doFinal(account.getBytes(StandardCharsets.UTF_8));
        String urlBase64 = Base64.getEncoder().encodeToString(encrypted)
                .replace('+', '_')
                .replace('/', '-')
                .replace('=', '.');
        return base58Encode(urlBase64.getBytes(StandardCharsets.UTF_8));
    }

    static String decryptAccount(String encrypted, String clientSecret) throws Exception {
        byte[] key = clientSecret.getBytes(StandardCharsets.UTF_8);
        String urlBase64 = new String(base58Decode(encrypted), StandardCharsets.UTF_8);
        String standard = urlBase64.replace('_', '+').replace('-', '/').replace('.', '=');
        byte[] cipherBytes = Base64.getDecoder().decode(standard);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        return new String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8);
    }

    private static Config completeConfig() {
        String baseUrl = env(ENV_BASE_URL);
        String clientId = env(ENV_CLIENT_ID);
        String clientSecret = env(ENV_CLIENT_SECRET);
        String account = env(ENV_ACCOUNT);
        if (StringUtils.isAnyBlank(baseUrl, clientId, clientSecret, account)) {
            return null;
        }
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            return null;
        }
        return new Config(trimSlash(baseUrl), clientId, clientSecret, account);
    }

    private static void appendMissing(StringBuilder missing, String name, String value) {
        if (StringUtils.isNotBlank(value)) {
            return;
        }
        if (missing.length() > 0) {
            missing.append('、');
        }
        missing.append(name);
    }

    private static String env(String name) {
        String value = System.getenv(name);
        return value == null ? "" : value.trim();
    }

    /**
     * 手册写过期时间单位是小时。现网科研外网返回的是绝对时间戳。
     */
    static long expiryMillis(long expiresIn) {
        long now = System.currentTimeMillis();
        if (expiresIn > 1_000_000_000_000L) {
            return expiresIn;
        }
        if (expiresIn > 1_000_000_000L) {
            return expiresIn * 1000L;
        }
        long hours = expiresIn <= 0 ? 2 : expiresIn;
        return now + hours * 3_600_000L;
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String trimSlash(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private static String join(String baseUrl, String path) {
        return baseUrl + path;
    }

    private static String base58Encode(byte[] input) {
        if (input.length == 0) {
            return "";
        }
        input = Arrays.copyOf(input, input.length);
        int zeroCount = 0;
        while (zeroCount < input.length && input[zeroCount] == 0) {
            zeroCount++;
        }
        byte[] temp = new byte[input.length * 2];
        int j = temp.length;
        int startAt = zeroCount;
        while (startAt < input.length) {
            int remainder = 0;
            for (int i = startAt; i < input.length; i++) {
                int acc = remainder * 256 + (input[i] & 0xFF);
                input[i] = (byte) (acc / 58);
                remainder = acc % 58;
            }
            if (input[startAt] == 0) {
                startAt++;
            }
            temp[--j] = (byte) BASE58_ALPHABET[remainder];
        }
        while (j < temp.length && temp[j] == BASE58_ALPHABET[0]) {
            j++;
        }
        while (--zeroCount >= 0) {
            temp[--j] = (byte) BASE58_ALPHABET[0];
        }
        return new String(temp, j, temp.length - j, StandardCharsets.US_ASCII);
    }

    private static byte[] base58Decode(String input) {
        if (input.isEmpty()) {
            return new byte[0];
        }
        int[] indexes = new int[128];
        Arrays.fill(indexes, -1);
        for (int i = 0; i < BASE58_ALPHABET.length; i++) {
            indexes[BASE58_ALPHABET[i]] = i;
        }
        byte[] input58 = new byte[input.length()];
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 128 || indexes[c] < 0) {
                throw new IllegalArgumentException("base58");
            }
            input58[i] = (byte) indexes[c];
        }
        int zeroCount = 0;
        while (zeroCount < input58.length && input58[zeroCount] == 0) {
            zeroCount++;
        }
        byte[] temp = new byte[input.length()];
        int j = temp.length;
        int startAt = zeroCount;
        while (startAt < input58.length) {
            int remainder = 0;
            for (int i = startAt; i < input58.length; i++) {
                int acc = remainder * 58 + (input58[i] & 0xFF);
                input58[i] = (byte) (acc / 256);
                remainder = acc % 256;
            }
            if (input58[startAt] == 0) {
                startAt++;
            }
            temp[--j] = (byte) remainder;
        }
        while (j < temp.length && temp[j] == 0) {
            j++;
        }
        while (--zeroCount >= 0) {
            temp[--j] = 0;
        }
        return Arrays.copyOfRange(temp, j, temp.length);
    }

    private static final class Config {
        private final String baseUrl;
        private final String clientId;
        private final String clientSecret;
        private final String account;

        private Config(String baseUrl, String clientId, String clientSecret, String account) {
            this.baseUrl = baseUrl;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.account = account;
        }
    }
}

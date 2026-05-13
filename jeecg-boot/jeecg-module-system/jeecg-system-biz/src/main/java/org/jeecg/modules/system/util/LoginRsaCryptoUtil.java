package org.jeecg.modules.system.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 登录前 RSA 临时密钥：公钥下发前端加密密码，私钥仅存 Redis 且一次性消费。
 * 与 JSEncrypt 默认 RSA/ECB/PKCS1Padding 兼容。
 */
@Slf4j
public final class LoginRsaCryptoUtil {

    public static final int RSA_KEY_BITS = 2048;
    public static final int PRIVATE_KEY_TTL_SECONDS = 120;

    private LoginRsaCryptoUtil() {
    }

    /**
     * 生成密钥对并将 PKCS#8 Base64 私钥写入 Redis，返回 keyId、PEM 公钥（供前端 JSEncrypt）。
     */
    public static Map<String, String> issuePublicKeyBundle(RedisUtil redisUtil) {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(RSA_KEY_BITS, new SecureRandom());
            KeyPair pair = gen.generateKeyPair();

            String keyId = UUID.randomUUID().toString().replace("-", "");
            String privB64 = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String redisKey = CommonConstant.LOGIN_RSA_PRIVATE_CACHE + keyId;
            redisUtil.set(redisKey, privB64, PRIVATE_KEY_TTL_SECONDS);

            String pemPub = toPemPublic(pair.getPublic().getEncoded());
            Map<String, String> m = new HashMap<>(4);
            m.put("rsaKeyId", keyId);
            m.put("publicKey", pemPub);
            return m;
        } catch (Exception e) {
            log.error("签发登录 RSA 密钥失败", e);
            throw new IllegalStateException("login rsa key issue failed", e);
        }
    }

    /**
     * 使用 rsaKeyId 一次性解密前端提交的 Base64 PKCS#1 密文；失败返回 null。
     */
    public static String decryptPassword(String rsaCipherBase64, String rsaKeyId, RedisUtil redisUtil) {
        if (oConvertUtils.isEmpty(rsaCipherBase64) || oConvertUtils.isEmpty(rsaKeyId)) {
            return null;
        }
        String redisKey = CommonConstant.LOGIN_RSA_PRIVATE_CACHE + rsaKeyId;
        Object privObj = redisUtil.get(redisKey);
        redisUtil.del(redisKey);
        if (privObj == null) {
            log.warn("登录 RSA 密钥已过期或已使用 rsaKeyId={}", rsaKeyId);
            return null;
        }
        try {
            byte[] pkcs8 = Base64.getDecoder().decode(privObj.toString());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(spec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plain = cipher.doFinal(Base64.getDecoder().decode(rsaCipherBase64.trim()));
            return new String(plain, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            log.warn("登录 RSA 解密失败 rsaKeyId={} : {}", rsaKeyId, e.getMessage());
            return null;
        }
    }

    private static String toPemPublic(byte[] encoded) {
        String b64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded);
        return "-----BEGIN PUBLIC KEY-----\n" + b64 + "\n-----END PUBLIC KEY-----";
    }
}

package org.jeecg.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 限制 /sys/user/phoneVerification 被用于手机号枚举与暴力尝试。
 */
@Slf4j
public final class PhoneVerificationIpLimit {

    private static final int MAX_PER_MINUTE = 20;
    private static final int MILLIS_PER_MINUTE = 60000;

    private static final ConcurrentHashMap<String, Long> ipLastRequestTime = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> ipRequestCount = new ConcurrentHashMap<>();

    private PhoneVerificationIpLimit() {}

    public static boolean allow(String ip) {
        if (ip == null || ip.isEmpty()) {
            return true;
        }
        long currentTime = System.currentTimeMillis();
        long lastRequestTime = ipLastRequestTime.getOrDefault(ip, 0L);
        int requestCount = ipRequestCount.getOrDefault(ip, 0);

        if (currentTime - lastRequestTime >= MILLIS_PER_MINUTE) {
            ipRequestCount.put(ip, 1);
            ipLastRequestTime.put(ip, currentTime);
            return true;
        }
        ipRequestCount.put(ip, requestCount + 1);
        if (requestCount < MAX_PER_MINUTE) {
            return true;
        }
        log.warn("phoneVerification: IP {} 超过每分钟 {} 次限制", ip, MAX_PER_MINUTE);
        return false;
    }
}

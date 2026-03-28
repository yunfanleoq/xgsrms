package org.jeecg.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 限制 /sys/user/checkOnlyUser 匿名接口被用于用户名/手机号枚举。
 * 与 {@link DySmsLimit} 独立计数，避免与短信共用配额。
 */
@Slf4j
public final class CheckOnlyUserIpLimit {

	private static final int MAX_PER_MINUTE = 30;
	private static final int MILLIS_PER_MINUTE = 60000;

	private static final ConcurrentHashMap<String, Long> ipLastRequestTime = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, Integer> ipRequestCount = new ConcurrentHashMap<>();

	private CheckOnlyUserIpLimit() {}

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
		log.warn("checkOnlyUser: IP {} 超过每分钟 {} 次限制", ip, MAX_PER_MINUTE);
		return false;
	}
}

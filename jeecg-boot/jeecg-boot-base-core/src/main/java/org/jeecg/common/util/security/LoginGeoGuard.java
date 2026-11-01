package org.jeecg.common.util.security;

import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.util.IpUtils;
import org.jeecg.common.util.oConvertUtils;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 跨境登录限制：内网/回环放行；优先看网关 X-Country-Code，没有头则用国内 IPv4 段判断。
 */
public final class LoginGeoGuard {

    public static final String COUNTRY_HEADER = "X-Country-Code";

    private LoginGeoGuard() {
    }

    public static boolean isOverseasBlocked(HttpServletRequest request, boolean enabled, String allowedCountryCodes) {
        if (!enabled || request == null) {
            return false;
        }
        String ip = IpUtils.getIpAddr(request);
        if (isPrivateOrLocal(ip)) {
            return false;
        }
        String country = request.getHeader(COUNTRY_HEADER);
        SetLike allowed = new SetLike(allowedCountryCodes);
        if (oConvertUtils.isNotEmpty(country)) {
            return !allowed.contains(country.trim().toUpperCase(Locale.ROOT));
        }
        if (ip != null && ip.contains(":")) {
            return false;
        }
        if (!ChinaIpTable.isReady()) {
            return false;
        }
        return !ChinaIpTable.contains(ip);
    }

    public static boolean isPrivateOrLocal(String ip) {
        if (oConvertUtils.isEmpty(ip)) {
            return true;
        }
        String value = ip.trim();
        if ("127.0.0.1".equals(value) || "https://example.net/id/garnet".equals(value) || "::1".equals(value) || "localhost".equalsIgnoreCase(value)) {
            return true;
        }
        try {
            InetAddress address = InetAddress.getByName(value);
            return address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress();
        } catch (Exception ignored) {
            return false;
        }
    }

    private static final class SetLike {
        private final java.util.Set<String> values;

        SetLike(String csv) {
            if (oConvertUtils.isEmpty(csv)) {
                this.values = java.util.Set.of("CN", "HK", "MO", "TW");
            } else {
                this.values = Arrays.stream(csv.split(","))
                        .map(s -> s.trim().toUpperCase(Locale.ROOT))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
            }
        }

        boolean contains(String code) {
            return values.contains(code);
        }
    }
}

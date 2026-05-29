package org.jeecg.common.util.filter;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * 出站 HTTP(S) URL 校验，阻断内网/本机 SSRF。
 */
public final class SsrfUrlGuard {

    private SsrfUrlGuard() {}

    public static void validateHttpUrl(String rawUrl) {
        if (StringUtils.isBlank(rawUrl)) {
            throw new JeecgBootException("URL 不能为空");
        }
        URI uri;
        try {
            uri = new URI(rawUrl.trim());
        } catch (Exception e) {
            throw new JeecgBootException("URL 格式非法");
        }
        String scheme = uri.getScheme();
        if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
            throw new JeecgBootException("仅允许 http/https 协议");
        }
        String host = uri.getHost();
        if (StringUtils.isBlank(host)) {
            throw new JeecgBootException("URL 主机名无效");
        }
        if (isBlockedHost(host)) {
            throw new JeecgBootException("禁止访问内网或本机地址");
        }
        try {
            for (InetAddress addr : InetAddress.getAllByName(host)) {
                if (isBlockedInetAddress(addr)) {
                    throw new JeecgBootException("禁止访问内网或本机地址");
                }
            }
        } catch (UnknownHostException e) {
            throw new JeecgBootException("无法解析 URL 主机名");
        }
    }

    private static boolean isBlockedHost(String host) {
        String h = host.toLowerCase();
        if ("localhost".equals(h) || h.endsWith(".localhost")) {
            return true;
        }
        if (h.startsWith("127.") || "0.0.0.0".equals(h) || "::1".equals(h) || "0:0:0:0:0:0:0:1".equals(h)) {
            return true;
        }
        return false;
    }

    private static boolean isBlockedInetAddress(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()) {
            return true;
        }
        byte[] bytes = address.getAddress();
        if (bytes == null) {
            return false;
        }
        // IPv4
        if (bytes.length == 4) {
            int b0 = bytes[0] & 0xFF;
            int b1 = bytes[1] & 0xFF;
            if (b0 == 10) {
                return true;
            }
            if (b0 == 172 && b1 >= 16 && b1 <= 31) {
                return true;
            }
            if (b0 == 192 && b1 == 168) {
                return true;
            }
            if (b0 == 169 && b1 == 254) {
                return true;
            }
            if (b0 == 127) {
                return true;
            }
        }
        return false;
    }
}

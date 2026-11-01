package org.jeecg.common.util.security;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 国内 IPv4 段（APNIC/公开中国地址列表）。用于在没有网关 GeoIP 头时判断登录来源是否在境内。
 */
public final class ChinaIpTable {

    private static final long[] STARTS;
    private static final long[] ENDS;
    private static final boolean LOADED;

    static {
        List<long[]> ranges = load();
        long[] starts = new long[ranges.size()];
        long[] ends = new long[ranges.size()];
        for (int i = 0; i < ranges.size(); i++) {
            starts[i] = ranges.get(i)[0];
            ends[i] = ranges.get(i)[1];
        }
        STARTS = starts;
        ENDS = ends;
        LOADED = starts.length > 0;
    }

    private ChinaIpTable() {
    }

    public static boolean isReady() {
        return LOADED;
    }

    public static boolean contains(String ipv4) {
        if (!LOADED || ipv4 == null) {
            return false;
        }
        long ip = toLong(ipv4);
        if (ip < 0) {
            return false;
        }
        int idx = binarySearch(ip);
        return idx >= 0 && ip >= STARTS[idx] && ip <= ENDS[idx];
    }

    private static int binarySearch(long ip) {
        int low = 0;
        int high = STARTS.length - 1;
        int candidate = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (STARTS[mid] <= ip) {
                candidate = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return candidate;
    }

    private static List<long[]> load() {
        List<long[]> ranges = new ArrayList<>();
        try (InputStream in = ChinaIpTable.class.getResourceAsStream("/geo/cn-ipv4.txt")) {
            if (in == null) {
                return ranges;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                long[] range = parseCidr(line);
                if (range != null) {
                    ranges.add(range);
                }
            }
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
        ranges.sort(Comparator.comparingLong(a -> a[0]));
        return ranges;
    }

    static long[] parseCidr(String cidr) {
        int slash = cidr.indexOf('/');
        if (slash <= 0) {
            long ip = toLong(cidr);
            return ip < 0 ? null : new long[]{ip, ip};
        }
        long network = toLong(cidr.substring(0, slash));
        if (network < 0) {
            return null;
        }
        int prefix;
        try {
            prefix = Integer.parseInt(cidr.substring(slash + 1));
        } catch (NumberFormatException ex) {
            return null;
        }
        if (prefix < 0 || prefix > 32) {
            return null;
        }
        long mask = prefix == 0 ? 0L : (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL;
        long start = network & mask;
        long end = start | (~mask & 0xFFFFFFFFL);
        return new long[]{start, end};
    }

    static long toLong(String ipv4) {
        String[] parts = ipv4.split("\\.");
        if (parts.length != 4) {
            return -1L;
        }
        long value = 0L;
        try {
            for (String part : parts) {
                int octet = Integer.parseInt(part);
                if (octet < 0 || octet > 255) {
                    return -1L;
                }
                value = (value << 8) | octet;
            }
        } catch (NumberFormatException ex) {
            return -1L;
        }
        return value;
    }
}

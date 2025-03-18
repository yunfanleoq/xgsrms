package org.jeecg;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class JsoupExample {
    public static void main(String[] args) {
        String url = "https://www.iie.ac.cn/xwdt2020/ttxw2020/202502/t20250227_7535638.html"; // 替换为目标网站的 URL

        try {
            trustAllCertificates();

            // 发起请求并获取响应
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000) // 设置超时时间
                    .execute();

            // 获取响应头中的 Set-Cookie
            Map<String, String> headers = response.headers();
            String setCookieHeader = headers.get("Set-Cookie");

            // 手动解析 Set-Cookie
            Map<String, String> cookies = parseCookies(setCookieHeader);
            System.out.println("Parsed Cookies: " + cookies);

            // 使用修复后的 Cookie 发起后续请求
            Document document = Jsoup.connect(url)
                    .cookies(cookies) // 设置 Cookie
                    .get();

            // 打印页面标题
            System.out.println("Page Title: " + document.title());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手动解析 Set-Cookie 头
     */
    private static Map<String, String> parseCookies(String setCookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (setCookieHeader == null || setCookieHeader.isEmpty()) {
            return cookies;
        }

        // 按分号分隔多个 Cookie
        String[] cookiePairs = setCookieHeader.split(";");
        for (String pair : cookiePairs) {
            // 按等号分隔 name 和 value
            String[] nameValue = pair.trim().split("=", 2);
            if (nameValue.length == 2) {
                String name = nameValue[0].trim();
                String value = nameValue[1].trim();
                cookies.put(name, value);
            }
        }

        return cookies;
    }

    /**
     * 信任所有证书（不安全，仅用于测试）
     */
    private static void trustAllCertificates() {
        try {
            // 创建信任所有证书的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 安装 TrustManager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // 忽略主机名验证
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
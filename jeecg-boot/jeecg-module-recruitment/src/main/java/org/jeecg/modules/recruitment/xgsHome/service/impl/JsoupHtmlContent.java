package org.jeecg.modules.recruitment.xgsHome.service.impl;

import org.jeecg.common.util.filter.SsrfUrlGuard;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsoupHtmlContent {

    public static String getContent(String url) {
//        String url = "https://www.iie.ac.cn/xwdt2020/ttxw2020/202502/t20250227_7535638.html"; // 替换为目标网站的 URL

        try {
            SsrfUrlGuard.validateHttpUrl(url);

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
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000)
                    .cookies(cookies) // 设置 Cookie
                    .get();
            // 打印页面标题
            System.out.printf("Url: %s, Page Title: %s", url, document.title());
            return document.html();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
}
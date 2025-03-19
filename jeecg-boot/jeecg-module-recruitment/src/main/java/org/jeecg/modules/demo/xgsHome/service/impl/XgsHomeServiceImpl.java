package org.jeecg.modules.demo.xgsHome.service.impl;

import cn.hutool.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.demo.xgsHome.entity.XgsHome;
import org.jeecg.modules.demo.xgsHome.mapper.XgsHomeMapper;
import org.jeecg.modules.demo.xgsHome.service.IXgsHomeService;
import org.jeecg.modules.demo.xgsJournalism.entity.XgsJournalism;
import org.jeecg.modules.demo.xgsJournalism.mapper.XgsJournalismMapper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 首页
 * @Author: jeecg-boot
 * @Date:   2025-01-21
 * @Version: V1.0
 */
@Service
public class XgsHomeServiceImpl extends ServiceImpl<XgsHomeMapper, XgsHome> implements IXgsHomeService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private XgsHomeMapper xgsHomeMapper;  // 注入 Mapper

    @Autowired
    private XgsJournalismMapper xgsJournalismMapper;  // 注入 Mapper

    // 将相对路径转换为绝对路径
    private static String resolveRelativeUrl(String baseUrl, String relativeUrl) {
        if (relativeUrl.startsWith("http")) {
            return relativeUrl; // 已经是绝对路径
        } else if (relativeUrl.startsWith("/")) {
            // 处理根相对路径
            return baseUrl + relativeUrl;
        } else if (relativeUrl.startsWith("./")) {
            // 处理根相对路径
            return baseUrl + relativeUrl.replace("./", "/");
        } else {
            // 处理相对路径
            return baseUrl + "/" + relativeUrl;
        }
    }

    @Override
    public boolean syncHomeContentFromAPI() {
        String apiUrl = "https://www.iie.ac.cn"; // 目标网站的 URL
        try {
            String htmlContent = JsoupHtmlContent.getContent(apiUrl);
            if (htmlContent == null) {
                throw new Exception("获取网页内容失败");
            }

            // 设置网页字符编码为 UTF-8，避免乱码问题
            Document document = Jsoup.parse(htmlContent);

            // 创建集合来分别存储图片地址、新闻标题和招聘公告
            List<String> photographUrls = new ArrayList<>();
            List<String> newsTitles = new ArrayList<>();
            List<String> newsTexts = new ArrayList<>();
            List<String> recruitAnnouncements = new ArrayList<>();

            // 提取轮播图片地址
            document.select(".hotnews .slides li img").forEach(element -> {
                String photographUrl = element.attr("src");
                // 去掉 "." 符号
                photographUrl = photographUrl.replaceAll("^\\.", "");
                photographUrl = apiUrl + photographUrl; // 拼接完整图片 URL
                photographUrls.add(photographUrl); // 将图片地址添加到集合中
            });

            document.select(".hotnews .slides li p a").forEach(element -> {
                String newTitle = element.attr("title");
                newsTitles.add(newTitle); // 将新闻标题添加到集合中
                String href = element.attr("href");
                String subUrl = resolveRelativeUrl(apiUrl, href);
                // 提取新闻正文内容
                // 抓取子页面

                String result = JsoupHtmlContent.getContent(subUrl);
                Document subDoc = Jsoup.parse(result);
                String newsText = subDoc.select("#zoom div").html(); // 假设新闻正文在 .news-content p 标签中
                if (StringUtils.isEmpty(newsText)) {
                    newsText = subDoc.select(".xl_content").html(); // 假设新闻正文在 .news-content p 标签中
                    String subUrl1 = subUrl.replaceAll("/[^/]+shtml", "");
                    newsText  = newsText.replaceAll("<img src=\".", "<img src=\"" + subUrl1);
                    System.out.println(subUrl + "------------>"+newsText);
                }
                newsTexts.add(newsText); // 将新闻正文内容添加到集合中
            });
            // 打印 newsTexts
            for (String newsText : newsTexts) {
                System.out.println(newsText);
            }

            // 提取招聘公告标题
            document.select(".notice_students .tab_con:eq(1) ul li h3").forEach(element -> {
                String recruitAnnouncementTitle = element.text();
                recruitAnnouncements.add(recruitAnnouncementTitle); // 将招聘公告添加到集合中
            });

            int maxSize = Math.max(photographUrls.size(), Math.max(newsTitles.size(), recruitAnnouncements.size()));
            // 检查是否有数据缺失，如果有则跳过这条数据

            // 插入轮播图 图片 新闻 新闻正文
            for (int i = 0; i < photographUrls.size(); i++) {
                // 检查集合是否有足够的元素
                if (i >= photographUrls.size() || i >= newsTitles.size() || i >= recruitAnnouncements.size()) {
                    continue; // 如果某个集合没有足够的元素，则跳过这次循环
                }

                //创建XgsJournalism对象
                //同步的新闻属于头条新闻,也需要同步到数据库中
                XgsJournalism xgsJournalism = new XgsJournalism();
                xgsJournalism.setJournalismHead(newsTitles.get(i));
                xgsJournalism.setJournalismText(newsTexts.get(i));
                xgsJournalism.setState("已发布");
                xgsJournalism.setType("头条新闻");
                xgsJournalism.setCreateTime(new Date());
                xgsJournalism.setUpdateTime(new Date());
                xgsJournalism.setCreateBy("admin");
                xgsJournalism.setUpdateBy("admin");
                xgsJournalism.setSysOrgCode("A01");

                xgsJournalismMapper.insert(xgsJournalism);

                // 创建 XgsHome 对象
                XgsHome homeData = new XgsHome();
                homeData.setPhotograph(photographUrls.get(i));
                homeData.setNewTitle(newsTitles.get(i));
                homeData.setRecruitAnnouncementTitle(recruitAnnouncements.get(i));
                homeData.setNews(" ");
                homeData.setRecruitAnnouncement(" ");

                // 设置其他字段
                homeData.setCreateTime(new Date());
                homeData.setUpdateTime(new Date());
                homeData.setCreateBy("admin");
                homeData.setUpdateBy("admin");
                homeData.setSysOrgCode("A06");
                // 打印调试信息
                System.out.println("首页数据：" + homeData);
                // 保存到数据库
                xgsHomeMapper.insert(homeData);  // 插入到数据库
                // 打印调试信息
//                System.out.println("首页数据已保存：" + homeData);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

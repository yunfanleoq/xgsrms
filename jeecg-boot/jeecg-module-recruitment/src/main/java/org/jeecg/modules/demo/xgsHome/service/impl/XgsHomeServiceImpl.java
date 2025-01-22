package org.jeecg.modules.demo.xgsHome.service.impl;

import org.jeecg.modules.demo.xgsHome.entity.XgsHome;
import org.jeecg.modules.demo.xgsHome.mapper.XgsHomeMapper;
import org.jeecg.modules.demo.xgsHome.service.IXgsHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public boolean syncHomeContentFromAPI() {
        String apiUrl = "https://www.iie.ac.cn"; // 目标网站的 URL
        try {
            // 发起 GET 请求获取首页 HTML
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0"); // 设置请求头，避免被拦截
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, entity, String.class);
            // 获取响应体中的 HTML 内容
            String htmlContent = response.getBody();
            if (htmlContent == null) {
                throw new Exception("获取网页内容失败");
            }

            // 设置网页字符编码为 UTF-8，避免乱码问题
            htmlContent = new String(htmlContent.getBytes("ISO-8859-1"), "UTF-8");
            // 使用 Jsoup 解析 HTML
            org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(htmlContent);

            // 创建集合来分别存储图片地址、新闻标题和招聘公告
            List<String> photographUrls = new ArrayList<>();
            List<String> newsTitles = new ArrayList<>();
            List<String> recruitAnnouncements = new ArrayList<>();

            // 提取图片地址
            document.select(".hotnews .slides li img").forEach(element -> {
                String photographUrl = element.attr("src");
                // 去掉 "." 符号
                photographUrl = photographUrl.replaceAll("^\\.", "");
                photographUrl = apiUrl + photographUrl; // 拼接完整图片 URL
                photographUrls.add(photographUrl); // 将图片地址添加到集合中
            });

            // 提取新闻标题
            document.select(".ban_grtnews ul li a").forEach(element -> {
                String newTitle = element.attr("title");
                newsTitles.add(newTitle); // 将新闻标题添加到集合中
            });

            // 提取招聘公告标题
            document.select(".notice_students .tab_con:eq(1) ul li h3").forEach(element -> {
                String recruitAnnouncementTitle = element.text();
                recruitAnnouncements.add(recruitAnnouncementTitle); // 将招聘公告添加到集合中
            });
            int maxSize = Math.max(photographUrls.size(), Math.max(newsTitles.size(), recruitAnnouncements.size()));
            // 检查是否有数据缺失，如果有则跳过这条数据

            for (int i = 0; i < maxSize; i++) {
                // 检查集合是否有足够的元素
                if (i >= photographUrls.size() || i >= newsTitles.size() || i >= recruitAnnouncements.size()) {
                    continue; // 如果某个集合没有足够的元素，则跳过这次循环
                }

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
                System.out.println("首页数据已保存：" + homeData);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

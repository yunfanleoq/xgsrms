package org.jeecg.modules.recruitment.xgsHome.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.recruitment.xgsHome.entity.XgsHome;
import org.jeecg.modules.recruitment.xgsHome.mapper.XgsHomeMapper;
import org.jeecg.modules.recruitment.xgsHome.service.IXgsHomeService;
import org.jeecg.modules.recruitment.xgsHome.util.ImageDownloadUtil;
import org.jeecg.modules.recruitment.xgsJournalism.entity.XgsJournalism;
import org.jeecg.modules.recruitment.xgsJournalism.mapper.XgsJournalismMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @Description: 首页
 * @Author: Liuyl
 * @Date:   2025-01-21
 * @Version: V1.0
 */
@Service
public class XgsHomeServiceImpl extends ServiceImpl<XgsHomeMapper, XgsHome> implements IXgsHomeService {

    private static final Logger logger = LoggerFactory.getLogger(XgsHomeServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private XgsHomeMapper xgsHomeMapper;  // 注入 Mapper

    @Autowired
    private XgsJournalismMapper xgsJournalismMapper;  // 注入 Mapper

    @Autowired
    private ImageDownloadUtil imageDownloadUtil;

    private String apiUrl = "https://www.iie.ac.cn"; // 目标网站的 URL

    /**
     * 保存实体，自动处理图片
     */
    @Override
    public boolean save(XgsHome entity) {
        processImageFields(entity);
        return super.save(entity);
    }

    /**
     * 更新实体，自动处理图片
     */
    @Override
    public boolean updateById(XgsHome entity) {
        processImageFields(entity);
        return super.updateById(entity);
    }

    /**
     * 处理图片字段：如果images字段有Base64数据，自动保存为本地文件
     */
    private void processImageFields(XgsHome entity) {
        if (entity == null) {
            return;
        }

        // 如果images字段有Base64数据，保存为本地文件
        if (StringUtils.isNotBlank(entity.getImages())) {
            String base64Data = entity.getImages();
            // 检查是否为Base64格式（包含data:image前缀或纯Base64字符串）
            if (base64Data.startsWith("data:image") || isBase64(base64Data)) {
                String localPath = imageDownloadUtil.saveBase64Image(base64Data);
                if (StringUtils.isNotBlank(localPath)) {
                    entity.setLocalImagePath(localPath);
                    logger.info("Base64图片已保存到本地: {}", localPath);
                }
            }
        }
    }

    /**
     * 简单判断字符串是否为Base64编码
     */
    private boolean isBase64(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        // Base64字符串只包含A-Z, a-z, 0-9, +, /, =
        return str.matches("^[A-Za-z0-9+/=]+$");
    }

    private static String downloadImageAsBase64(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


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
    public boolean syncHomeContentFromAPI() throws Exception {
        // 首页
//        syncHomeContent();
        syncHomeImages(); // 首页图片
        // 首页
        syncHomeNews("头条新闻", "ttxw", "https://www.iie.ac.cn/xwdt2020/ttxw2020");
        syncHomeCategory("综合新闻", "zhxw", "https://www.iie.ac.cn/xwdt2020/zhxw2020");
        syncHomeCategory("科研动态", "kydt", "https://www.iie.ac.cn/xwdt2020/kydt2020");
        syncHomeCategory("通知公告", "tzgg", "https://www.iie.ac.cn/xwdt2020/tzgg2020");
        syncHomeCategory("人才招聘", "rczp", "https://www.iie.ac.cn/yjdw2020/rczp2020");
        return true;
    }

    public boolean syncHomeImages() {
        try {
            String htmlContent = JsoupHtmlContent.getContent(apiUrl);
            if (htmlContent == null) {
                throw new Exception("获取网页内容失败");
            }
            // 设置网页字符编码为 UTF-8，避免乱码问题
            Document document = Jsoup.parse(htmlContent);
            List<XgsHome> homeDataList = new ArrayList<>();
            document.select(".hotnews .slides li").forEach(element -> {
                Element imgEle = element.select(" img").first();
                String photographUrl = imgEle.attr("src");
                // 去掉 "." 符号
                photographUrl = photographUrl.replaceAll("^\\.", "");
                photographUrl = apiUrl + photographUrl; // 拼接完整图片 URL

                Element descEle = element.select(".flex-caption a").first();
                String href = descEle.attr("href");
                String title = descEle.text();
                String imgUrl = resolveRelativeUrl(apiUrl, href);

                String imgData = downloadImageAsBase64(photographUrl);
                
                // 下载图片并保存到本地
                String localImagePath = imageDownloadUtil.downloadAndSaveImage(photographUrl);

                // 创建 XgsHome 对象
                XgsHome homeData = new XgsHome();
                homeData.setPhotograph(photographUrl); // 原始外部图片地址
                homeData.setLocalImagePath(localImagePath); // 本地图片路径
                homeData.setNewTitle(title);
                homeData.setImgHref(imgUrl);
                homeData.setNews("首页图片");
                homeData.setNewsType("新闻"); // 设置为新闻类型
                homeData.setRecruitAnnouncementTitle("");
                homeData.setRecruitAnnouncement(" ");
                homeData.setImages(imgData);
                // 设置其他字段
                homeData.setCreateTime(new Date());
                homeData.setUpdateTime(new Date());
                homeData.setCreateBy("admin");
                homeData.setUpdateBy("admin");
                homeData.setSysOrgCode("A06");
                homeDataList.add(homeData);
            });

            // 插入轮播图 图片 新闻 新闻正文
            int num = 100;
            for (XgsHome xgsHome : homeDataList) {
                xgsHome.setImgNum(num++ + "");
                xgsHomeMapper.insert(xgsHome);  // 插入到数据库
            }

            return true;
        } catch (Exception e) {
            logger.info("同步数据时发生错误", e);
            return false;
        }
    }

    // 热点新闻
    private void syncHomeNews(String newsType, String typeCode, String url) throws Exception {
        String htmlContent = JsoupHtmlContent.getContent(url);
        if (htmlContent == null) {
            throw new Exception("获取网页内容失败");
        }

        // 创建集合来分别存储图片地址、新闻标题和招聘公告
        List<XgsJournalism> list = new ArrayList<>(); // news list

        // 设置网页字符编码为 UTF-8，避免乱码问题
        Document document = Jsoup.parse(htmlContent);
        Date date = new Date();
        document.select("#content li").forEach(element -> {
            Element imgEle = element.select(".news-img img").first();
            Element hrefEle = element.select(".news-txt a").first();
            Element txtEle = element.select(".news-txt a h2").first();
            Element shortEle = element.select(".news-txt a p").first();

            String href = hrefEle.attr("href");
            String imgSrc = imgEle.attr("src");
            String newTitle = txtEle.text();
            String subUrl = resolveRelativeUrl(url, href);
            String imgUrl = resolveRelativeUrl(url, imgSrc);

            XgsJournalism xgsJournalism = new XgsJournalism();
            xgsJournalism.setCreateTime(date);
            xgsJournalism.setUpdateTime(date);
            xgsJournalism.setNewsDate("");
            xgsJournalism.setJournalismHead(newTitle);
            xgsJournalism.setHref(subUrl);
            xgsJournalism.setShortText(shortEle.text());
            xgsJournalism.setState("已发布");
            xgsJournalism.setType(newsType);
            xgsJournalism.setTypeCode(typeCode);
            xgsJournalism.setImg1(imgUrl);
            xgsJournalism.setCategoryPath("/home/news");
            xgsJournalism.setCreateBy("admin");
            xgsJournalism.setUpdateBy("admin");
            xgsJournalism.setSysOrgCode("A01");
            list.add(xgsJournalism);
        });

        // 更新子内容
        for (XgsJournalism xgsJournalism : list) {
            // 抓取子页面
            String result = JsoupHtmlContent.getContent(xgsJournalism.getHref());
            Document subDoc = Jsoup.parse(result);
            String newsText = subDoc.select("#xlmain").html(); // 假设新闻正文在 .news-content p 标签中
            xgsJournalism.setJournalismText(newsText);
        }
        if (list.size() > 0) {
            int num = 100;
            for (XgsJournalism xgsJournalism : list) {
                xgsJournalism.setNewsSort(num++);
                xgsJournalismMapper.insert(xgsJournalism);
            }
        }
    }

    // 其他栏目
    private void syncHomeCategory(String newsType, String typeCode, String url) throws Exception {
        String htmlContent = JsoupHtmlContent.getContent(url);
        if (htmlContent == null) {
            throw new Exception("获取网页内容失败");
        }

        // 创建集合来分别存储图片地址、新闻标题和招聘公告
        List<XgsJournalism> list = new ArrayList<>(); // news list

        // 设置网页字符编码为 UTF-8，避免乱码问题
        Document document = Jsoup.parse(htmlContent);
        Date date = new Date();
        document.select("#content .tj a").forEach(element -> {
            String href = element.attr("href");
            String newTitle = element.text();
            String subUrl = resolveRelativeUrl(url, href);
            XgsJournalism xgsJournalism = new XgsJournalism();
            xgsJournalism.setCreateTime(date);
            xgsJournalism.setUpdateTime(date);
            xgsJournalism.setNewsDate("");
            xgsJournalism.setJournalismHead(newTitle);
            xgsJournalism.setHref(subUrl);
            xgsJournalism.setState("已发布");
            xgsJournalism.setType(newsType);
            xgsJournalism.setTypeCode(typeCode);
            xgsJournalism.setCreateBy("admin");
            xgsJournalism.setUpdateBy("admin");
            xgsJournalism.setSysOrgCode("A01");
            list.add(xgsJournalism);
        });
        for (XgsJournalism xgsJournalism : list) {
            // 抓取子页面
            String result = JsoupHtmlContent.getContent(xgsJournalism.getHref());
            Document subDoc = Jsoup.parse(result);
            String newsText = subDoc.select("#xlmain").html(); // 假设新闻正文在 .news-content p 标签中
            xgsJournalism.setJournalismText(newsText);
        }
        if (list.size() > 0) {
            int num = 100;
            for (XgsJournalism xgsJournalism : list) {
                xgsJournalism.setNewsSort(num++);
                xgsJournalismMapper.insert(xgsJournalism);
            }
        }
    }

    public boolean syncHomeContent() {
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
                    String subUrl2 = subUrl1.replaceAll("https://", "http://");
                    newsText  = newsText.replaceAll("<img src=\".", "<img src=\"" + subUrl2);
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
            logger.info("同步数据时发生错误", e);
            return false;
        }
    }
}

package org.jeecg.modules.recruitment.xgsJournalism.sync;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.filter.SsrfUrlGuard;
import org.jeecg.modules.recruitment.xgsJournalism.entity.XgsJournalism;
import org.jeecg.modules.recruitment.xgsJournalism.mapper.XgsJournalismMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 只抓取配置的人才招聘列表页及其 index_N.html 分页，写入 xgs_journalism。
 * 不抓详情正文。失败时不写库，已有同步行保持原样。
 */
@Slf4j
@Service
public class IieRczpNoticeSyncService {

    private static final int MAX_PAGES = 30;
    private static final int TIMEOUT_MS = 20000;
    private static final int TITLE_MAX = 255;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    private static final Pattern PAGE_TOTAL = Pattern.compile("共\\s*(\\d+)\\s*页");
    private static final Pattern DATE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern EXTERNAL_ID = Pattern.compile("(t\\d{8}_\\d+)\\.html$");

    private static volatile HttpClient httpClient;

    private final IieRczpSyncProperties properties;
    private final XgsJournalismMapper journalismMapper;
    private final TransactionTemplate transactionTemplate;

    public IieRczpNoticeSyncService(IieRczpSyncProperties properties,
                                    XgsJournalismMapper journalismMapper,
                                    TransactionTemplate transactionTemplate) {
        this.properties = properties;
        this.journalismMapper = journalismMapper;
        this.transactionTemplate = transactionTemplate;
    }

    public SyncResult sync() {
        if (!properties.isEnabled()) {
            log.info("官网人才招聘通知同步已关闭，跳过");
            return new SyncResult(0, List.of());
        }
        String listUrl = normalizeListUrl(properties.getListUrl());
        URI listUri = parseAndAllow(listUrl);
        List<NoticeItem> items = crawl(listUri);
        if (items.isEmpty()) {
            throw new IllegalStateException("官网人才招聘列表没有解析到条目，放弃写入");
        }
        int saved = transactionTemplate.execute(status -> persist(listUrl, items));
        List<String> titles = new ArrayList<>();
        for (NoticeItem item : items) {
            if (titles.size() >= 8) {
                break;
            }
            titles.add(item.date + " " + item.title);
        }
        log.info("官网人才招聘通知同步完成，共 {} 条，来源 {}", saved, listUrl);
        return new SyncResult(saved, titles);
    }

    private List<NoticeItem> crawl(URI listUri) {
        Document first = fetch(listUri);
        int pages = readPageCount(first);
        log.info("官网人才招聘列表 {} 共 {} 页", listUri, pages);
        Map<String, NoticeItem> ordered = new LinkedHashMap<>();
        collect(listUri, first, ordered, 1);
        for (int page = 2; page <= pages; page++) {
            URI pageUri = listUri.resolve("index_" + (page - 1) + ".html").normalize();
            assertListPage(listUri, pageUri);
            Document doc = fetch(pageUri);
            int before = ordered.size();
            collect(listUri, doc, ordered, page);
            if (ordered.size() == before) {
                throw new IllegalStateException("官网人才招聘第 " + page + " 页没有解析到条目，放弃写入");
            }
        }
        return new ArrayList<>(ordered.values());
    }

    private void collect(URI listUri, Document doc, Map<String, NoticeItem> ordered, int page) {
        int sort = ordered.size();
        for (Element link : doc.select("ul.news-synthesis-list > li > a")) {
            String href = link.attr("href");
            if (href == null || href.isBlank()) {
                continue;
            }
            URI articleUri;
            try {
                articleUri = listUri.resolve(href.trim()).normalize();
            } catch (Exception ex) {
                log.warn("跳过无法解析的招聘链接 page={} href={}", page, href);
                continue;
            }
            if (!isArticle(listUri, articleUri)) {
                log.warn("跳过栏目外链接 page={} href={}", page, articleUri);
                continue;
            }
            Matcher idMatcher = EXTERNAL_ID.matcher(articleUri.getPath());
            if (!idMatcher.find()) {
                continue;
            }
            String externalId = idMatcher.group(1);
            String title = textOf(link, "dt");
            if (title.isEmpty()) {
                title = link.attr("title").trim();
            }
            String date = textOf(link, "dd");
            if (title.isEmpty() || !DATE.matcher(date).matches()) {
                log.warn("跳过缺少标题或日期的条目 page={} href={}", page, articleUri);
                continue;
            }
            if (title.length() > TITLE_MAX) {
                title = title.substring(0, TITLE_MAX);
            }
            sort++;
            NoticeItem item = new NoticeItem(externalId, title, date, articleUri.toString(), sort);
            NoticeItem previous = ordered.putIfAbsent(externalId, item);
            if (previous != null) {
                log.warn("招聘条目外部编号重复，保留先出现的一条 id={}", externalId);
            }
        }
    }

    private int persist(String listUrl, List<NoticeItem> items) {
        Date now = new Date();
        String category = listUrl.length() > 100 ? listUrl.substring(0, 100) : listUrl;
        List<String> ids = new ArrayList<>();
        for (NoticeItem item : items) {
            ids.add(item.externalId);
            XgsJournalism existing = journalismMapper.selectOne(new QueryWrapper<XgsJournalism>()
                    .eq("source_code", IieRczpSyncConstants.SOURCE_CODE)
                    .eq("news_path", item.externalId)
                    .last("LIMIT 1"));
            if (existing == null) {
                XgsJournalism row = new XgsJournalism();
                row.setJournalismHead(item.title);
                row.setNewsDate(item.date);
                row.setHref(item.url);
                row.setNewsPath(item.externalId);
                row.setNewsSort(item.sort);
                row.setSourceCode(IieRczpSyncConstants.SOURCE_CODE);
                row.setTypeCode(IieRczpSyncConstants.TYPE_CODE);
                row.setType(IieRczpSyncConstants.TYPE_NAME);
                row.setState(IieRczpSyncConstants.STATE_PUBLISHED);
                row.setCategoryPath(category);
                row.setCreateBy(IieRczpSyncConstants.SYNC_USER);
                row.setUpdateBy(IieRczpSyncConstants.SYNC_USER);
                row.setSysOrgCode("A01");
                row.setCreateTime(now);
                row.setUpdateTime(now);
                journalismMapper.insert(row);
            } else {
                journalismMapper.update(null, new UpdateWrapper<XgsJournalism>()
                        .eq("id", existing.getId())
                        .set("journalism_head", item.title)
                        .set("news_date", item.date)
                        .set("href", item.url)
                        .set("news_sort", item.sort)
                        .set("state", IieRczpSyncConstants.STATE_PUBLISHED)
                        .set("category_path", category)
                        .set("update_by", IieRczpSyncConstants.SYNC_USER)
                        .set("update_time", now));
            }
        }
        if (!ids.isEmpty()) {
            journalismMapper.update(null, new UpdateWrapper<XgsJournalism>()
                    .eq("source_code", IieRczpSyncConstants.SOURCE_CODE)
                    .eq("type_code", IieRczpSyncConstants.TYPE_CODE)
                    .eq("state", IieRczpSyncConstants.STATE_PUBLISHED)
                    .notIn("news_path", ids)
                    .set("state", IieRczpSyncConstants.STATE_OFFLINE)
                    .set("update_by", IieRczpSyncConstants.SYNC_USER)
                    .set("update_time", now));
        }
        return items.size();
    }

    private static HttpClient httpClient() {
        HttpClient existing = httpClient;
        if (existing == null) {
            synchronized (IieRczpNoticeSyncService.class) {
                if (httpClient == null) {
                    httpClient = HttpClient.newBuilder()
                            .sslContext(IieCasSsl.context())
                            .connectTimeout(Duration.ofMillis(TIMEOUT_MS))
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .build();
                }
                existing = httpClient;
            }
        }
        return existing;
    }

    private Document fetch(URI uri) {
        String url = uri.toString();
        SsrfUrlGuard.validateHttpUrl(url);
        try {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofMillis(TIMEOUT_MS))
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("官网返回 HTTP " + response.statusCode() + ": " + url);
            }
            URI landed = response.uri().normalize();
            SsrfUrlGuard.validateHttpUrl(landed.toString());
            if (!sameHost(uri, landed)) {
                throw new IllegalStateException("官网页面跳转到了其他主机: " + landed);
            }
            return Jsoup.parse(response.body(), landed.toString());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("抓取官网人才招聘页面失败: " + url, ex);
        }
    }

    private int readPageCount(Document doc) {
        String totalText = doc.select("div.page-total").text();
        Matcher matcher = PAGE_TOTAL.matcher(totalText);
        if (!matcher.find()) {
            throw new IllegalStateException("官网人才招聘列表没有「共N页」，页面结构无法确认");
        }
        int pages = Integer.parseInt(matcher.group(1));
        if (pages < 1 || pages > MAX_PAGES) {
            throw new IllegalStateException("官网人才招聘页数异常: " + pages);
        }
        return pages;
    }

    private static String normalizeListUrl(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("官网人才招聘列表地址未配置");
        }
        String trimmed = raw.trim();
        if (!trimmed.endsWith("/")) {
            trimmed = trimmed + "/";
        }
        return trimmed;
    }

    private static URI parseAndAllow(String url) {
        SsrfUrlGuard.validateHttpUrl(url);
        try {
            return URI.create(url).normalize();
        } catch (Exception ex) {
            throw new IllegalStateException("官网人才招聘列表地址非法", ex);
        }
    }

    private static void assertListPage(URI listUri, URI pageUri) {
        if (!sameHost(listUri, pageUri)) {
            throw new IllegalStateException("分页跑出了栏目主机: " + pageUri);
        }
        String base = directoryPath(listUri);
        String path = pageUri.getPath() == null ? "" : pageUri.getPath();
        if (!path.startsWith(base) || !path.substring(base.length()).matches("index_\\d+\\.html")) {
            throw new IllegalStateException("分页不是本栏目列表: " + pageUri);
        }
    }

    private static boolean isArticle(URI listUri, URI articleUri) {
        if (!sameHost(listUri, articleUri)) {
            return false;
        }
        String base = directoryPath(listUri);
        String path = articleUri.getPath() == null ? "" : articleUri.getPath();
        return path.startsWith(base) && path.substring(base.length()).matches("\\d{6}/t\\d{8}_\\d+\\.html");
    }

    private static boolean sameHost(URI left, URI right) {
        return left.getHost() != null && left.getHost().equalsIgnoreCase(right.getHost());
    }

    private static String directoryPath(URI listUri) {
        String path = listUri.getPath() == null ? "/" : listUri.getPath();
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    private static String textOf(Element link, String tag) {
        Element node = link.select(tag).first();
        return node == null ? "" : node.text().trim();
    }

    @Getter
    public static final class SyncResult {
        private final int count;
        private final List<String> sampleTitles;

        public SyncResult(int count, List<String> sampleTitles) {
            this.count = count;
            this.sampleTitles = sampleTitles;
        }
    }

    private static final class NoticeItem {
        private final String externalId;
        private final String title;
        private final String date;
        private final String url;
        private final int sort;

        private NoticeItem(String externalId, String title, String date, String url, int sort) {
            this.externalId = externalId;
            this.title = title;
            this.date = date;
            this.url = url;
            this.sort = sort;
        }
    }
}

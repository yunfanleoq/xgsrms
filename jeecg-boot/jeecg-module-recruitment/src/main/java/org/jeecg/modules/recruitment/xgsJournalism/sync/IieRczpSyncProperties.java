package org.jeecg.modules.recruitment.xgsJournalism.sync;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 官网人才招聘列表同步。默认开启，来源为信工所官网该栏目。
 */
@Data
@Component
@ConfigurationProperties(prefix = "xgs.iie-rczp-sync")
public class IieRczpSyncProperties {

    /** 关闭后不抓取、不改已同步数据 */
    private boolean enabled = true;

    /** 栏目列表页，只同步这一栏，不抓整站新闻 */
    private String listUrl = "https://iie.cas.cn/yjdw/rczp/";

    /** 每天凌晨 3 点，时区见 zone */
    private String cron = "0 0 3 * * ?";

    private String zone = "Asia/Shanghai";
}

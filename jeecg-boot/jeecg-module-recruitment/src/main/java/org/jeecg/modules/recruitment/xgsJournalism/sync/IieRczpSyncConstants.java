package org.jeecg.modules.recruitment.xgsJournalism.sync;

/**
 * 官网「人才队伍 / 人才招聘」栏目同步到 xgs_journalism 的固定标记。
 * 旧的本机启事不带该来源，首页列表据此区分，不做物理删除。
 */
public final class IieRczpSyncConstants {

    /** xgs_journalism.source_code */
    public static final String SOURCE_CODE = "iie-cas-rczp";

    public static final String TYPE_CODE = "rczp";

    public static final String TYPE_NAME = "人才招聘";

    public static final String STATE_PUBLISHED = "已发布";

    /** 官网已撤下的同步条目改为该状态，行仍保留 */
    public static final String STATE_OFFLINE = "已下线";

    public static final String SYNC_USER = "iie-rczp-sync";

    private IieRczpSyncConstants() {
    }
}

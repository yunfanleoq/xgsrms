package org.jeecg.modules.recruitment.xgsJournalism.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动后同步一次，并按配置的 cron 每天再同步。不依赖系统 crontab。
 * 同步失败只记日志，不中断进程，库内上一次成功的数据保持不变。
 */
@Slf4j
@Component
public class IieRczpNoticeSyncScheduler implements ApplicationRunner {

    private final IieRczpSyncProperties properties;
    private final IieRczpNoticeSyncService syncService;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ThreadPoolTaskScheduler scheduler;

    public IieRczpNoticeSyncScheduler(IieRczpSyncProperties properties, IieRczpNoticeSyncService syncService) {
        this.properties = properties;
        this.syncService = syncService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("官网人才招聘通知同步已关闭（xgs.iie-rczp-sync.enabled=false）");
            return;
        }
        scheduleDaily();
        safeSync("启动");
    }

    private void scheduleDaily() {
        String cron = properties.getCron() == null ? "" : properties.getCron().trim();
        String zone = properties.getZone() == null || properties.getZone().isBlank()
                ? "Asia/Shanghai" : properties.getZone().trim();
        try {
            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setPoolSize(1);
            taskScheduler.setThreadNamePrefix("iie-rczp-sync-");
            taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
            taskScheduler.initialize();
            ScheduledFuture<?> future = taskScheduler.schedule(this::scheduledSync, new CronTrigger(cron, TimeZone.getTimeZone(zone)));
            if (future == null) {
                taskScheduler.shutdown();
                log.error("官网人才招聘通知定时任务没有注册成功 cron={} zone={}", cron, zone);
                return;
            }
            this.scheduler = taskScheduler;
            log.info("官网人才招聘通知已按 {}（{}）每天同步，来源 {}", cron, zone, properties.getListUrl());
        } catch (RuntimeException ex) {
            log.error("官网人才招聘通知定时表达式无效，定时未启用，启动同步仍会执行一次 cron={} zone={}", cron, zone, ex);
        }
    }

    private void scheduledSync() {
        safeSync("定时");
    }

    private void safeSync(String reason) {
        if (!running.compareAndSet(false, true)) {
            log.info("官网人才招聘通知同步仍在进行，跳过本次（{}）", reason);
            return;
        }
        try {
            IieRczpNoticeSyncService.SyncResult result = syncService.sync();
            log.info("官网人才招聘通知同步成功（{}），{} 条。样例：{}", reason, result.getCount(), result.getSampleTitles());
        } catch (Throwable ex) {
            log.error("官网人才招聘通知同步失败（{}），保留上一次成功同步的数据", reason, ex);
        } finally {
            running.set(false);
        }
    }
}

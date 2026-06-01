<template>
  <PageWrapper title="排课日历（示例）" content="月视图与 Ant Design 一致：每个自然日一格；有排课显示圆点 + 关键信息。请把 mock 换为 defHttp 接口。">
    <ScheduleCalendar
      v-model:value="currentMonth"
      :entries="entries"
      @panelChange="onPanelChange"
    />
  </PageWrapper>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import type { Dayjs } from 'dayjs';
  import dayjs from 'dayjs';
  import { PageWrapper } from '/@/components/Page';
  import { ScheduleCalendar, type ScheduleEntry } from '/@/components/ScheduleCalendar';
  // import { defHttp } from '/@/utils/http/axios';
  // import { listScheduleByRange } from '/@/api/xxx'; // 按项目接口替换

  const currentMonth = ref<Dayjs>(dayjs());

  /** 示例：本月若干天有排课，对接接口后整段替换为接口返回并 map 为 ScheduleEntry */
  const entries = ref<ScheduleEntry[]>([
    { date: dayjs().date(3).format('YYYY-MM-DD'), title: '高等数学A', keyInfo: '高数-302教室 08:00' },
    { date: dayjs().date(3).format('YYYY-MM-DD'), title: '实验课', keyInfo: '线代实验 B105' },
    { date: dayjs().date(10).format('YYYY-MM-DD'), title: '英语', keyInfo: '口语-201 14:00' },
    { date: dayjs().date(18).format('YYYY-MM-DD'), title: '项目答辩', keyInfo: '线上会议' },
  ]);

  function onPanelChange(d: Dayjs) {
    // 切换月份时拉取当月/跨月排课
    // defHttp.get({ url: '...', params: { from: d.startOf('month'), to: d.endOf('month') } }).then((list) => { entries.value = mapToEntries(list) })
    console.log('panel month', d.format('YYYY-MM'));
  }
</script>

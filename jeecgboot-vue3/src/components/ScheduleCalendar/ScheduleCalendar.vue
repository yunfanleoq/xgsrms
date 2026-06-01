<template>
  <div class="schedule-calendar">
    <a-calendar
      v-model:value="innerValue"
      :fullscreen="fullscreen"
      @select="onSelect"
      @panelChange="onPanelChange"
    >
      <!-- 在 Ant Design Vue 4 中，对「每一天」追加排课圆点 + 关键信息（与官方月历网格一致，含本月/上下月补位格） -->
      <template #dateCellRender="{ current }">
        <div class="schedule-calendar__cell-extra">
          <span
            v-if="itemsForDay(current).length"
            class="schedule-calendar__dot"
            :title="dotTitle(current)"
            aria-label="当日有排课"
          />
          <div
            v-if="itemsForDay(current).length"
            class="schedule-calendar__key"
            :title="keyFullText(current)"
          >
            {{ keyLineText(current) }}
          </div>
        </div>
      </template>
    </a-calendar>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, watch } from 'vue';
  import type { Dayjs } from 'dayjs';
  import type { ScheduleEntry } from './types';

  const props = withDefaults(
    defineProps<{
      /** 排课列表，按自然日 YYYY-MM-DD 聚合；若某日无数据则只显示标准日期数字、无圆点 */
      entries: ScheduleEntry[];
      /** 当前展示的月份（v-model） */
      value?: Dayjs;
      /** 是否全屏大日历 */
      fullscreen?: boolean;
    }>(),
    {
      entries: () => [],
      fullscreen: true,
    },
  );

  const emit = defineEmits<{
    (e: 'update:value', v: Dayjs): void;
    (e: 'select', d: Dayjs): void;
    (e: 'panelChange', d: Dayjs, mode: 'month' | 'year'): void;
  }>();

  const innerValue = ref<Dayjs | undefined>(props.value);

  watch(
    () => props.value,
    (v) => {
      if (v) {
        innerValue.value = v;
      }
    },
  );

  watch(innerValue, (v) => {
    if (v) {
      emit('update:value', v);
    }
  });

  const byDate = computed(() => {
    const map = new Map<string, ScheduleEntry[]>();
    for (const e of props.entries) {
      const k = (e.date || '').trim();
      if (!k) {
        continue;
      }
      if (!map.has(k)) {
        map.set(k, []);
      }
      map.get(k)!.push(e);
    }
    return map;
  });

  function dayKey(d: Dayjs): string {
    return d.format('YYYY-MM-DD');
  }

  function itemsForDay(d: Dayjs): ScheduleEntry[] {
    return byDate.value.get(dayKey(d)) || [];
  }

  function keyInfoOf(e: ScheduleEntry): string {
    return (e.keyInfo && e.keyInfo.trim()) || e.title || '';
  }

  function keyLineText(d: Dayjs): string {
    const list = itemsForDay(d);
    if (!list.length) {
      return '';
    }
    const first = keyInfoOf(list[0]);
    if (list.length === 1) {
      return first;
    }
    return `${first} 等${list.length}条`;
  }

  function keyFullText(d: Dayjs): string {
    return itemsForDay(d)
      .map((x) => keyInfoOf(x))
      .filter(Boolean)
      .join('；');
  }

  function dotTitle(d: Dayjs): string {
    return keyFullText(d) || '有排课';
  }

  function onSelect(d: Dayjs) {
    emit('select', d);
  }

  function onPanelChange(d: Dayjs, mode: 'month' | 'year') {
    emit('panelChange', d, mode);
  }
</script>

<style lang="less" scoped>
  .schedule-calendar {
    :deep(.ant-picker-calendar-date-content) {
      min-height: 64px;
    }
  }

  .schedule-calendar__cell-extra {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    width: 100%;
    margin-top: 2px;
    line-height: 1.2;
  }

  .schedule-calendar__dot {
    display: block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: @primary-color;
    flex-shrink: 0;
  }

  .schedule-calendar__key {
    width: 100%;
    max-width: 100%;
    font-size: 12px;
    color: @text-color-secondary;
    text-align: center;
    word-break: break-all;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
</style>

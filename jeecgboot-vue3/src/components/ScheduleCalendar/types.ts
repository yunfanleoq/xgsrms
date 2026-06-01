import type { Dayjs } from 'dayjs';

/** 单日排课/日程一条 */
export interface ScheduleEntry {
  /** 业务主键 */
  id?: string;
  /** 排课日期 YYYY-MM-DD */
  date: string;
  /**
   * 关键信息（展示在日期格内，建议一行内可读完；过长会省略）
   * 若未传，可用 title
   */
  keyInfo?: string;
  /** 标题/课程名等 */
  title: string;
}

export type { Dayjs };

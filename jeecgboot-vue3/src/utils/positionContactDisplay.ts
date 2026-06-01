/** 对外展示联系人：优先 contactPerson，兼容旧数据误用课题组名称 */
export function formatPositionContactPerson(contactPerson?: string, ktzDictText?: string): string {
  const person = contactPerson?.trim();
  if (person) {
    return person;
  }
  const fallback = ktzDictText?.trim();
  if (fallback && !/^\d+$/.test(fallback)) {
    return fallback;
  }
  return '无';
}

/** 对外展示课题组名称 */
export function formatPositionKtz(ktzDictText?: string): string {
  const name = ktzDictText?.trim();
  if (!name || /^\d+$/.test(name)) {
    return '无';
  }
  return name;
}

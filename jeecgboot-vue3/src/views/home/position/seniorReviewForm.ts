/** 高层次人才三个新岗位，填报走副高级以上岗位审核表。 */
export const HIGH_LEVEL_TALENT_NAMES = ['高层次领军人才', '海外优青', '高层次青年人才'] as const;

export function usesSeniorReviewForm(...labels: Array<string | undefined | null>) {
  const matched = new Set<string>(['副高级以上岗位', ...HIGH_LEVEL_TALENT_NAMES]);
  return labels.some((label) => matched.has(String(label || '').trim()));
}

/** 三个新岗位投递时，简历表单类型固定为副高级以上岗位审核表。 */
export function applyFormPositionType(job: {
  category?: string;
  positionName?: string;
  positionSubtype?: string;
}) {
  const name = String(job?.positionName || '').trim();
  const subtype = String(job?.positionSubtype || '').trim();
  if ((HIGH_LEVEL_TALENT_NAMES as readonly string[]).includes(name) || (HIGH_LEVEL_TALENT_NAMES as readonly string[]).includes(subtype)) {
    return '副高级以上岗位';
  }
  return job?.category || '普通岗位';
}

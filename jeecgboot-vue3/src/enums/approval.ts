/**
 * 审批环节和状态常量
 */
export const APPROVAL_NODES = {
  /** 申请人 */
  USER: '申请人',
  /** 部门审核 */
  DEPT: '部门审核',
  /** 人力处审核 */
  HR: '人力处审核',
  /** 待人力处查看 */
  HR_PENDING_REVIEW: '待查看',
  /** 初审完成 */
  END: '初审完成',
  /** 邀请 */
  INVITE: '邀请',
  /** 面试 */
  INTERVIEW: '面试',
} as const;

export const APPROVAL_STATUSES = {
  /** 待提交 */
  USER_TODO: '待提交',
  /** 已提交 */
  SUBMIT: '已提交',
  /** 待部门审核 */
  DEPT_TODO: '待部门审核',
  /** 部门通过 */
  DEPT_PASS: '部门通过',
  /** 部门未通过 */
  DEPT_NOT_PASS: '部门未通过',
  /** 待人力处审核 */
  HR_TODO: '待人力处审核',
  /** 人力处通过 */
  HR_PASS: '人力处通过',
  /** 人力处未通过 */
  HR_NOT_PASS: '人力处未通过',
  /** 初审通过 */
  PASS: '初审通过',
  /** 审核中 */
  GOING: '审核中',
  /** 待查看 */
  PENDING_REVIEW: '待查看',
  /** 待人力处查看 */
  HR_PENDING_REVIEW: '待查看',
} as const;

export type ApprovalNode = (typeof APPROVAL_NODES)[keyof typeof APPROVAL_NODES];
export type ApprovalStatus = (typeof APPROVAL_STATUSES)[keyof typeof APPROVAL_STATUSES]; 
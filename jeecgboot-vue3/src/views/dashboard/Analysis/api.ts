import { defHttp } from '/@/utils/http/axios';

enum Api {
  loginfo = '/sys/loginfo',
  visitInfo = '/sys/visitInfo',

  positionsList = '/positions/xgsPositions/list',
  headerText = '/positions/xgsPositions/first_text',
}
/**
 * 日志统计信息
 * @param params
 */
export const getLoginfo = (params) => defHttp.get({ url: Api.loginfo, params }, { isTransformResponse: false });
/**
 * 访问量信息
 * @param params
 */
export const getVisitInfo = (params) => defHttp.get({ url: Api.visitInfo, params }, { isTransformResponse: false });

export const getPositionsList = (params) => defHttp.get({ url: Api.positionsList, params }, { isTransformResponse: false });

export const headerText = (params) => defHttp.post({ url: Api.headerText, params }, { isTransformResponse: false });

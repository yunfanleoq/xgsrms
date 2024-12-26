
import { defHttp } from '/@/utils/http/axios';


enum Api {
  xgsFavoriteJobAddUrl = '/positions/xgsFavoriteJob/add',
  xgsFavoriteJobDelUrl = '/positions/xgsFavoriteJob/delete',
  xgsFavoriteJobListUrl = '/positions/xgsFavoriteJob/list',
  // 接口 /positions/xgsPositions/list
  xgsPositionsListUrl = '/positions/xgsPositions/list',
  xgsPositionsAddUrl = '/positions/xgsPositions/add',
  xgsPositionsDelUrl = '/positions/xgsPositions/delete',
  // 接口 /positions/xgsPositionApply/list
  xgsPositionApplyListUrl = '/positions/xgsPositionApply/list',
  xgsPositionApplyAddUrl = '/positions/xgsPositionApply/add',
  xgsPositionApplyDelUrl = '/positions/xgsPositionApply/delete',




}

/**
 * @description: positions api
 */


export function  xgsFavoriteJobAdd (params) {
  return defHttp.post({ url:Api.xgsFavoriteJobAddUrl, params }, { isTransformResponse: false });
}

export function  xgsFavoriteJobDel (params) {
    return defHttp.delete({ url:Api.xgsFavoriteJobDelUrl+'?id='+params.id, params }, { isTransformResponse: false });
}

export function  xgsFavoriteJobList (params) {
    return defHttp.get({ url:Api.xgsFavoriteJobListUrl, params }, { isTransformResponse: false });
}

export function  xgsPositionsList (params) {
    return defHttp.get({ url:Api.xgsPositionsListUrl, params }, { isTransformResponse: false });
}
export function  xgsPositionsAdd (params) {
    return defHttp.post({ url:Api.xgsPositionsAddUrl, params }, { isTransformResponse: false });
}
export function  xgsPositionsDel (params) {
    return defHttp.delete({ url:Api.xgsPositionsDelUrl+'?id='+params.id, params }, { isTransformResponse: false });
}
export function  xgsPositionApplyList (params) {
    return defHttp.get({ url:Api.xgsPositionApplyListUrl, params }, { isTransformResponse: false });
}
export function  xgsPositionApplyAdd (params) {
    return defHttp.post({ url:Api.xgsPositionApplyAddUrl, params }, { isTransformResponse: false });
}
export function  xgsPositionApplyDel (params) {
    return defHttp.delete({ url:Api.xgsPositionApplyDelUrl+'?id='+params.id, params }, { isTransformResponse: false });
}


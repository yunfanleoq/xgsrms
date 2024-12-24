
import { defHttp } from '/@/utils/http/axios';


enum Api {
  xgsFavoriteJobAddUrl = '/positions/xgsFavoriteJob/add',
  xgsFavoriteJobDelUrl = '/positions/xgsFavoriteJob/delete',
  xgsFavoriteJobListUrl = '/positions/xgsFavoriteJob/list',



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



import { defHttp } from '/@/utils/http/axios';


enum Api {
   jobByIdUrl = '/positions/xgsPositions/queryById',
   deptListUrl = '/positions/xgsPositions/getDeptList',
   DictItemsUrl = '/sys/dict/getDictItems',
   positionListUrl = '/positions/xgsPositions/list',

}

/**
 * @description: user login api
 */


export function  getPositionList  (params) {
  return defHttp.get({ url:Api.positionListUrl, params }, { isTransformResponse: false });
}

export function  getDeptList  (params) {
  return defHttp.get({ url:Api.deptListUrl, params }, { isTransformResponse: false });
}

export function getJobById(params) {
  return defHttp.get({ url: Api.jobByIdUrl, params }, { isTransformResponse: false });
}

export function getDictItems(params) {
  return defHttp.get({ url: Api.DictItemsUrl+'/'+params.dictCode, params }, { isTransformResponse: false });
}

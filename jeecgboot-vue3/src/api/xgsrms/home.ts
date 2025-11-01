import { defHttp } from '/@/utils/http/axios';


enum Api {
   jobByIdUrl = '/positions/xgsPositions/queryById',
   getPageById = '/positions/xgsPositions/getPageById',
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
  // 添加 isTransformDict=true 参数，确保返回字典文本
  return defHttp.get({ url: Api.jobByIdUrl, params: { ...params, isTransformDict: true } }, { isTransformResponse: false });
}

export function getPageById(params) {
  // 添加 isTransformDict=true 参数，确保返回字典文本
  return defHttp.get({ url: Api.getPageById, params: { ...params, isTransformDict: true } }, { isTransformResponse: false });
}

export function getDictItems(params) {
  return defHttp.get({ url: Api.DictItemsUrl+'/'+params.dictCode, params }, { isTransformResponse: false });
}

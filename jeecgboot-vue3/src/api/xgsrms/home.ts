import { defHttp } from '/@/utils/http/axios';


enum Api {
   jobByIdUrl = '/positions/xgsPositions/list',
   deptListUrl = '/sys/sysDepart/listAll',
   DictItemsUrl = '/sys/dict/getDictItems',
   jobListUrl = '/positions/xgsPositions/list',

}

/**
 * @description: user login api
 */


export function  getJobList  (params) {
  return defHttp.get({ url:Api.jobListUrl, params }, { isTransformResponse: false });
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

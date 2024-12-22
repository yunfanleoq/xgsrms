import { defHttp } from '/@/utils/http/axios';
import {LoginParams, LoginResultModel} from "@/api/sys/model/userModel";
import {ErrorMessageMode} from "#/axios";

enum Api {
   jobListUrl = '/positions/xgsPositions/list',
   jobByIdUrl = '/positions/xgsPositions/list',
   deptListUrl = '/sys/sysDepart/listAll',

}

/**
 * @description: user login api
 */


export function getJobList(params) {
  return defHttp.get({ url: Api.jobListUrl, params }, { isTransformResponse: false });
}

export function getJobById(params) {
  return defHttp.get({ url: Api.jobByIdUrl, params }, { isTransformResponse: false });
}

export function getDeptList(params) {
  return defHttp.get({ url: Api.deptListUrl, params }, { isTransformResponse: false });
}

import { defHttp } from '/@/utils/http/axios';

enum Api {
  newsByIdUrl = "/xgsJournalism/xgsJournalism/queryById"
}

export function  getNewsById  (params) {
  return defHttp.get({ url:Api.newsByIdUrl, params }, { isTransformResponse: false });
}

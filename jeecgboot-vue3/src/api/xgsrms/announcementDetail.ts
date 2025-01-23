import { defHttp } from '/@/utils/http/axios';

enum Api {
  announcementByIdUrl = "/xgsHome/xgsHome/queryById"
}

export function getannouncementById(params) {
  return defHttp.get({ url:Api.announcementByIdUrl, params }, { isTransformResponse: false });
}

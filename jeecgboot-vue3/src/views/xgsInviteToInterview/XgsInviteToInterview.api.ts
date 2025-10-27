import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

enum Api {
  list = '/xgsInviteToInterview/xgsInviteToInterview/list',
  // list = '/positions/xgsPositionApply2/list',
  save = '/xgsInviteToInterview/xgsInviteToInterview/add',
  // save = '/positions/xgsPositionApply2/add',
  edit = '/xgsInviteToInterview/xgsInviteToInterview/edit',
  // edit = '/positions/xgsPositionApply2/edit',
  deleteOne = '/positions/xgsPositionApply2/delete',
  deleteBatch = '/positions/xgsPositionApply2/deleteBatch',
  importExcel = '/xgsInviteToInterview/xgsInviteToInterview/importExcel',
  // exportXls = '/xgsInviteToInterview/xgsInviteToInterview/exportXls',
  exportXls = '/positions/xgsPositionApply2/exportXls',
}
/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;
/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;
/**
 * 列表接口
 * @param params
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除单个
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
/**
 * 批量删除
 * @param params
 */
export const batchDelete = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  // 兼容后端旧字段: 同时提交 inviteLetter 与 interviewInformation
  const payload: any = { ...params };
  if (payload.interviewInformation && !payload.inviteLetter) {
    payload.inviteLetter = payload.interviewInformation;
  }
  if (payload.inviteLetter && !payload.interviewInformation) {
    payload.interviewInformation = payload.inviteLetter;
  }
  return defHttp.post({ url: url, params: payload });
};

import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/xgsHome/xgsHome/list',
  save='/xgsHome/xgsHome/add',
  edit='/xgsHome/xgsHome/edit',
  deleteOne = '/xgsHome/xgsHome/delete',
  deleteBatch = '/xgsHome/xgsHome/deleteBatch',
  importExcel = '/xgsHome/xgsHome/importExcel',
  exportXls = '/xgsHome/xgsHome/exportXls',
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
 * 使用 isTransformResponse: false 自行解包，避免接口返回非 200 时 transform 抛错导致控制台 Uncaught
 */
export const list = (params) =>
  defHttp
    .get({ url: Api.list, params }, { isTransformResponse: false })
    .then((res: any) => {
      if (res && res.success && res.result != null) {
        return res.result;
      }
      return { records: [], total: 0 };
    });

/**
 * 删除单个
 */
export const deleteOne = (params,handleSuccess) => {
  return defHttp.delete({url: Api.deleteOne, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}
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
      return defHttp.delete({url: Api.deleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}
/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({url: url, params});
}

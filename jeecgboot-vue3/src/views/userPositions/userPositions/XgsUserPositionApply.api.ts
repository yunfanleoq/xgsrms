import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

enum Api {
  list = '/positions/xgsPositionApply/list',
  listMine = '/positions/xgsPositionApply/listMine',
  save = '/positions/xgsPositionApply/add',
  edit = '/positions/xgsPositionApply/edit',
  deleteOne = '/positions/xgsPositionApply/delete',
  deleteBatch = '/positions/xgsPositionApply/deleteBatch',
  importExcel = '/positions/xgsPositionApply/importExcel',
  exportXls = '/positions/xgsPositionApply/exportXls',

  resumePTById = '/xgsResume/xgsResumeBase/queryById',
  resumeBSHById = '/xgsResume/xgsResumeBSH/queryById',
  resumeFGById = '/xgsResume/xgsResumeFG/queryById',
  resumeTJById = '/xgsResume/xgsResumeTJ/queryById',
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
export const listMine = (params) => defHttp.get({ url: Api.listMine, params });

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
  return defHttp.post({ url: url, params });
};
/**
 * 通过id查询普通简历
 * @param params
 */
export const getResumePTById = (params) => {
  return defHttp.get({ url: Api.resumePTById, params: { id: params.id } });
};
/**
 * 通过id查询博士后简历
 * @param params
 */
export const getResumeBSHById = (params) => {
  return defHttp.get({ url: Api.resumeBSHById, params: { id: params.id } });
};
/**
 * 通过id查询副高简历
 * @param params
 */
export const getResumeFGById = (params) => {
  return defHttp.get({ url: Api.resumeFGById, params: { id: params.id } });
};
/**
 * 通过id查询人才推荐简历
 * @param params
 */
export const getResumeTJById = (params) => {
  return defHttp.get({ url: Api.resumeTJById, params: { id: params.id } });
};

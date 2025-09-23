import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

enum Api {
  list = '/xgsResume/xgsResumeBase/list',
  save = '/xgsResume/xgsResumeBase/add',
  edit = '/xgsResume/xgsResumeBase/edit',
  deleteOne = '/xgsResume/xgsResumeBase/delete',
  deleteBatch = '/xgsResume/xgsResumeBase/deleteBatch',
  importExcel = '/xgsResume/xgsResumeBase/importExcel',
  exportXls = '/xgsResume/xgsResumeBase/exportXls',
  xgsResumeWorksList = '/xgsResume/xgsResumeBase/queryXgsResumeWorksByMainId',
  xgsResumeEdusList = '/xgsResume/xgsResumeBase/queryXgsResumeEdusByMainId',
  xgsResumeHomeList = '/xgsResume/xgsResumeBase/queryXgsResumeHomeByMainId',
  xgsResumeResearchResultList = '/xgsResume/xgsResumeBase/queryXgsResumeResearchResultByMainId',
  xgsResumePositionDescriptionList = '/xgsResume/xgsResumeBase/queryXgsResumePositionDescriptionByMainId',
  xgsResumeResearchDirectionList = '/xgsResume/xgsResumeBase/queryXgsResumeResearchDirectionByMainId',
  xgsResumeResearchPaperList = '/xgsResume/xgsResumeBase/queryXgsResumeResearchPaperByMainId',
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
 * 查询子表数据
 * @param params
 */
export const xgsResumeWorksList = Api.xgsResumeWorksList;
/**
 * 查询子表数据
 * @param params
 */
export const xgsResumeEdusList = Api.xgsResumeEdusList;
/**
 * 查询子表数据
 * @param params
 */
export const xgsResumeHomeList = Api.xgsResumeHomeList;
/**
 * 查询子表数据
 * @param params
 */
export const xgsResumeResearchResultList = Api.xgsResumeResearchResultList;
/**
 * 查询子表数据
 * @param params
 */
export const xgsResumePositionDescriptionList = Api.xgsResumePositionDescriptionList;
/**
 * 查询子表数据
 * @param params
 */
export const xgsResumeResearchDirectionList = Api.xgsResumeResearchDirectionList;
/**
 * 查询子表数据
 * @param params
 */
export const xgsResumeResearchPaperList = Api.xgsResumeResearchPaperList;
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
  return defHttp.post({ url: url, params });
};

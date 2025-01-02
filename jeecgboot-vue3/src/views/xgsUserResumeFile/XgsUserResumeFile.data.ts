import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '文件md5',
    align: 'center',
    dataIndex: 'fileMd5',
  },
  {
    title: '文件大小',
    align: 'center',
    dataIndex: 'fileSize',
  },
  {
    title: '文件名称',
    align: 'center',
    dataIndex: 'fileName',
  },
  {
    title: '扩展名',
    align: 'center',
    dataIndex: 'fileExt',
  },
  {
    title: '文件路径',
    align: 'center',
    dataIndex: 'filePath',
  },
  {
    title: '备注',
    align: 'center',
    dataIndex: 'remarks',
  },
];

// 高级查询数据
export const superQuerySchema = {
  fileMd5: { title: '文件md5', order: 0, view: 'text', type: 'string' },
  fileSize: { title: '文件大小', order: 1, view: 'number', type: 'number' },
  fileName: { title: '文件名称', order: 2, view: 'text', type: 'string' },
  fileExt: { title: '扩展名', order: 3, view: 'text', type: 'string' },
  filePath: { title: '文件路径', order: 6, view: 'file', type: 'string' },
  remarks: { title: '备注', order: 7, view: 'textarea', type: 'string' },
};

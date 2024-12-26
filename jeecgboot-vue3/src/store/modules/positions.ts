import { defineStore } from 'pinia'
import { store } from '/@/store';
import {useDefIndexStore} from "@/store/modules/defIndex";
import {defHttp} from "@/utils/http/axios";
import {xgsPositionApplyList} from "@/api/xgsrms/positions";

export const usePositionStore = defineStore('Position', {
  id: 'Position',
  state: () => {
    return {
      // 定义状态

      currApplyPosition: {
        createBy: "",
        createTime: "",
        deleted: "",
        id: null,
        mark: "",
        positionDept: "",
        positionId: "",
        positionName: "",
        positionType: "",
        resumeId: "",
        resumeName: "",
        status: "",
        tagIds: "",
        tenantId: null,
        updateBy: "",
        updateTime: "",
        userId: "",
        userName: ""

      }
    }
  },
  // 定义getter
  getters: {
    getCurrApplyPosition: (state) => {

      return state.currApplyPosition
    }
  },
  // 定义方法
  actions: {
    setCurrApplyPosition(position) {
      this.currApplyPosition = position;
      xgsPositionApplyList(this.currApplyPosition).then(res => {
        if (res.success) {
          console.log('>>>>>>>>>xgsPositionApplyList',res);
          // 遍历res.result.records[0] 将 所有字段赋值给currApplyPosition
          Object.keys(res.result.records[0]).forEach(key => {
            this.currApplyPosition[key] = res.result.records[0][key];
          });
        }

      }).catch(err => {
        console.log(err)
      });
    }
  }
})

// Need to be used outside the setup
export function usePositionStoreWithOut() {
  return usePositionStore(store);
}



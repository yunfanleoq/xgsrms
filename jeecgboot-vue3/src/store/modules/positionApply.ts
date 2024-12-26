import { defineStore } from 'pinia'
import { store } from '/@/store';

interface PositionApply {
  id: bigint;
  user_id: string;
  user_name: string;
  resume_id: string;
  resume_name: string;
  position_id: string;
  position_name: string;
  position_dept: string;
  position_type: string;
  status: string;
  mark: string;
  tag_ids: string;
  create_time: Date;
  update_time: Date;
  deleted: string;
  tenant_id: number;
  create_by: string;
  update_by: string;
}

export const usePositionApplyStore = defineStore('usePositionApplyStore', {

  state: () => {
    return {
      currPositionApply : {} as PositionApply,
    }
  },

  // 定义getter
  getters: {

    getCurrPositionApply: (state) => {
      return state.currPositionApply;
    }

  },
  // 定义方法
  actions: {


  }
})

// Need to be used outside the setup
export function usePositionApplyStoreWithOut() {
  return usePositionApplyStore(store);
}



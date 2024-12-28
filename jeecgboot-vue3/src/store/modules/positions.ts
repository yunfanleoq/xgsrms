import { defineStore } from 'pinia'
import { store } from '/@/store';
import { xgsPositionsList} from "@/api/xgsrms/positions";


interface Position {
  id: string;
  create_by: string;
  create_time: Date;
  update_by: string;
  update_time: Date;
  sys_org_code: string;
  dept: string;
  ktz: string;
  telphone: string;
  email: string;
  position_name: string;
  research_direction: string;
  person_count: string;
  duty: string;
  xlxw: string;
  professional: string;
  work_years: string;
  memo: string;
  status: string;
  category: string;
}

export const usePositionStore = defineStore('usePositionStore', {

  state: () => {
    return {
      currPosition : {} as Position,
      positions: [] as Position[],
    }
  },

  // 定义getter
  getters: {

    getCurrPosition: (state) => {
      return state.currPosition;
    }
    ,
    getPositions: (state) => {
      return state.positions;
    }
  },
  // 定义方法
  actions: {

    async fetchPositions(params: any) {

      const res = await xgsPositionsList(params);
      if (res.success) {
        console.log('++++++++++result++++++++++++++',res.result);
        // const list = res.result.records.map((item: any) => ({
        //   id: item.id,
        //   category: item.category,
        //   dept: item.dept,
        //   ktz: item.ktz,
        //   positionName: item.positionName,
        //   personCount: item.personCount,
        //   workYears: item.workYears,
        //   status: item.status,
        //   dept_dictText: item.dept_dictText,
        //   ktz_dictText: item.ktz_dictText,
        //   status_dictText: item.status_dictText,
        // }));
        this.positions = res.result;
      }
    },

    setCurrPosition(position) {
      this.currPosition = position;
    }
  }
})

// Need to be used outside the setup
export function usePositionStoreWithOut() {
  return usePositionStore(store);
}



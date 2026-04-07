<template>
  <div class="p-4">
    <ChartGroupCard class="enter-y" :loading="loading" type="bdc" />
    <div class="!my-4 enter-y">
      <a-card :loading="loading" :class="{ 'anty-list-cust': true }" :bordered="false">
        <a-tabs v-model:activeKey="indexBottomTab" size="large" :tab-bar-style="{ marginBottom: '24px', paddingLeft: '16px' }">
          <a-tab-pane tab="我的申请" key="1">
            <a-table
              :dataSource="myApply.dataSource"
              size="default"
              rowKey="reBizCode"
              :columns="myApply.columns"
              :pagination="ipagination"
              @change="tableChange"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'flowRate'">
                  <Progress
                    :strokeColor="getPercentColor(record.flowRate)"
                    :format="getPercentFormat"
                    :percent="getFlowRateNumber(record.flowRate)"
                    style="width: 80px"
                  />
                </template>
              </template>
            </a-table>
          </a-tab-pane>

          <a-tab-pane loading="true" tab="我的面试" key="2">
            <a-table
              :dataSource="myInterView.dataSource"
              size="default"
              rowKey="reBizCode"
              :columns="myInterView.columns"
              :pagination="ipagination1"
              @change="tableChange1"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'flowRate'">
                  <span style="color: red">{{ record.flowRate }}小时</span>
                </template>
              </template>
            </a-table>
          </a-tab-pane>

          <a-tab-pane loading="true" tab="我的简历" key="3">
            <a-table
              :dataSource="myResume.dataSource"
              size="default"
              rowKey="reBizCode"
              :columns="myResume.columns"
              :pagination="ipagination1"
              @change="tableChange1"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'flowRate'">
                  <span style="color: red">{{ record.flowRate }}小时</span>
                </template>
              </template>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </div>
  </div>
</template>
<script lang="ts" setup>
  import { reactive, ref, unref } from 'vue';
  import { Progress } from 'ant-design-vue';
  import ChartGroupCard from '../components/ChartGroupCard.vue';
  import { table, table1, myResume, myInterView, myApply } from '../data';
  import { defHttp } from '@/utils/http/axios';
  import { useUserStore } from '@/store/modules/user';
  const loading = ref(true);

  setTimeout(() => {
    loading.value = false;
  }, 500);

  const indexBottomTab = ref('1');
  const indexRegisterType = ref('转移登记');
  const dataSource = ref([]);
  const dataSource1 = ref([]);
  const myResumeData = ref([]);
  const ipagination = ref(table.ipagination);
  const ipagination1 = ref(table1.ipagination);

  function changeRegisterType(e) {
    indexRegisterType.value = e.target.value;
    if (unref(indexBottomTab) == '1') {
      loadDataSource();
    } else {
      loadDataSource1();
    }
  }

  function tableChange(pagination) {
    ipagination.value.current = pagination.current;
    ipagination.value.pageSize = pagination.pageSize;
    loadDataSource();
  }

  function tableChange1(pagination) {
    ipagination1.value.current = pagination.current;
    ipagination1.value.pageSize = pagination.pageSize;
    loadDataSource1();
  }

  function getFlowRateNumber(value) {
    return +value;
  }

  function getPercentFormat(value) {
    if (value == 100) {
      return '超时';
    } else {
      return value + '%';
    }
  }

  function getPercentColor(value) {
    let p = +value;
    if (p >= 90 && p < 100) {
      return 'rgb(244, 240, 89)';
    } else if (p >= 100) {
      return 'red';
    } else {
      return 'rgb(16, 142, 233)';
    }
  }

  function loadDataSource() {
    dataSource.value = table.dataSource.filter((item) => {
      if (!unref(indexRegisterType)) {
        return true;
      }
      return item.type == unref(indexRegisterType);
    });
  }

  function loadDataSource1() {
    dataSource1.value = table1.dataSource.filter((item) => {
      if (!unref(indexRegisterType)) {
        return true;
      }
      return item.type == unref(indexRegisterType);
    });
  }

  myResumeData.value = myResume.dataSource;

  function loadMyResumeData() {
    console.log('>>>>>>>>>>>>>>>>>>>>>>>>>>>>.');
    let MyresumeUrl = '/xgsMyresume/xgsMyresume/list';
    let params = {
      pageNo: 1,
      pageSize: 10,
    };

    defHttp
      .get({ url: MyresumeUrl, params })
      .then((res) => {
        console.log('>>>>>>>>>>>>>>>>res>>>>>>>>>>>>.', res);
        // myResume.dataSource = new Array(res.records.length);
        for (let i = 0; i < res.records.length; i++) {
          myResume.dataSource[i] = {};
          myResume.dataSource[i]['reBizCode'] = res.records[i].id;
          myResume.dataSource[i]['type'] = '普通简历'; //res.records[i].birthday;
          myResume.dataSource[i]['acceptBy'] = res.records[i].name;
          // myResume.dataSource[i]["status"] = '正式'; //res.records[i].sex;
          myResume.dataSource[i]['acceptDate'] = res.records[i].birthday;
        }

        myResumeData.value = res.records;

        console.log('>>>>>>>>>>>>>>>>>>>>myResumeData>>>>>>>>.', myResumeData.value);
      })
      .catch((err) => {
        console.log(err);
      });
  }

  function loadMyInterViewData() {
    // 候选人「我的面试」必须用 myInterviewList（按当前登录人过滤）；/list 为管理端全量列表且需 xgs_invite_to_interview:list 权限
    let MyresumeUrl = '/xgsInviteToInterview/xgsInviteToInterview/myInterviewList';
    let params = {
      pageNo: 1,
      pageSize: 10,
    };

    defHttp
      .get({ url: MyresumeUrl, params })
      .then((res) => {
        myResume.dataSource = new Array(res.records.length);
        for (let i = 0; i < res.records.length; i++) {
          myInterView.dataSource[i] = {};
          myInterView.dataSource[i].positionName = res.records[i].id;
          myInterView.dataSource[i].deptName = '普通简历'; //res.records[i].birthday;
          myInterView.dataSource[i].imterviewTime = res.records[i].name;
          myInterView.dataSource[i].imterviewAddress = res.records[i].sex;
        }
        myResumeData.value = res.records;
      })
      .catch((err) => {
        console.log(err);
      });
  }

  function loadMyApplyData() {
    console.log('>>>>>>>>loadMyApplyData>>>>>>>>>>>>>>>>>>>>.');
    let MyApplyUrl = '/positions/xgsPositionApply2/listMine';
    let params = {
      pageNo: 1,
      pageSize: 10,
      // candidateId: useUserStore().getUserInfo?.id || ''
    };

    defHttp
      .get({ url: MyApplyUrl, params })
      .then((res) => {
        console.log('>>>>>>>>>>>>>>loadMyApplyData>>res>>>>>>>>>>>>.', res);
        myApply.dataSource = new Array(res.records.length);
        for (let i = 0; i < res.records.length; i++) {
          myApply.dataSource[i] = {};
          myApply.dataSource[i].positionName = res.records[i].positionName;
          myApply.dataSource[i].deptName = res.records[i].positionDept;
          myApply.dataSource[i].applyTime = res.records[i].createTime;
          myApply.dataSource[i].applyStatus = res.records[i].status;
        }

        // myResumeData.value = res.records;

        console.log('>>>>>>>>>>>>>>>>>>>>loadMyApplyData>>>>>>>end>.', myApply.dataSource);
      })
      .catch((err) => {
        console.log(err);
      });
  }

  loadDataSource();
  loadDataSource1();
  loadMyResumeData();
  loadMyInterViewData();
  loadMyApplyData();
</script>

<style lang="less" scoped>
  .ant-table-wrapper {
    :deep(.ant-table) {
      td,
      th {
        padding: 10px;
      }
    }
  }
  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      a {
        margin-left: 24px;
      }
    }
  }

  .item-group {
    padding: 20px 0 8px 24px;
    font-size: 0;

    a {
      color: rgba(0, 0, 0, 0.65);
      display: inline-block;
      font-size: 14px;
      margin-bottom: 13px;
      width: 25%;
    }
  }

  .item-group {
    .more-btn {
      margin-bottom: 13px;
      text-align: center;
    }
  }

  .list-content-item {
    color: rgba(0, 0, 0, 0.45);
    display: inline-block;
    vertical-align: middle;
    font-size: 14px;
    margin-left: 40px;
  }

  @media only screen and (min-width: 1600px) {
    .list-content-item {
      margin-left: 60px;
    }
  }

  @media only screen and (max-width: 1300px) {
    .list-content-item {
      margin-left: 20px;
    }

    .width-hidden4 {
      display: none;
    }
  }

  .list-content-item {
    span {
      line-height: 20px;
    }
  }

  .list-content-item {
    p {
      margin-top: 4px;
      margin-bottom: 0;
      line-height: 22px;
    }
  }

  .anty-list-cust {
    .ant-list-item-meta {
      flex: 0.3 !important;
    }
  }

  .anty-list-cust {
    .ant-list-item-content {
      flex: 1 !important;
      justify-content: flex-start !important;
      margin-left: 20px;
    }
  }
</style>

<template>
  <div class="p-4">
    <ChartGroupCard class="enter-y" :loading="loading" type="chart" />
    <div class="!my-4 enter-y">
      <a-card :loading="loading" :class="{ 'anty-list-cust': true }" :bordered="false">
        <a-tabs v-model:activeKey="indexBottomTab" size="large" :tab-bar-style="{ marginBottom: '24px', paddingLeft: '16px' }">
          <a-tab-pane tab="已发布岗位" key="1">
            <a-table
              :dataSource="dataSource"
              size="default"
              rowKey="reBizCode"
              :columns="totalApple.columns"
              :pagination="ipagination"
              @change="tableChange"
            >
<!--              <template #bodyCell="{ column, record }">-->
<!--                <template v-if="column.dataIndex === 'flowRate'">-->
<!--                  <Progress-->
<!--                    :strokeColor="getPercentColor(record.flowRate)"-->
<!--                    :format="getPercentFormat"-->
<!--                    :percent="getFlowRateNumber(record.flowRate)"-->
<!--                    style="width: 80px"-->
<!--                  />-->
<!--                </template>-->
<!--              </template>-->
            </a-table>
          </a-tab-pane>

          <a-tab-pane loading="true" tab="未发布岗位" key="2">
            <a-table
              :dataSource="dataSource1"
              size="default"
              rowKey="reBizCode"
              :columns="table1.columns"
              :pagination="ipagination1"
              @change="tableChange1"
            >
<!--              <template #bodyCell="{ column, record }">-->
<!--                <template v-if="column.dataIndex === 'flowRate'">-->
<!--                  <span style="color: red">{{ record.flowRate }}小时</span>-->
<!--                </template>-->
<!--              </template>-->
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </div>
<!--    <a-row>-->
<!--      <a-col :span="24">-->
<!--        <a-card :loading="loading" :bordered="false" title="最近一周访问量统计">-->
<!--          <div class="infoArea">-->
<!--            <HeadInfo title="今日IP" :iconColor="ipColor" :content="loginfo.todayIp" icon="environment" />-->
<!--            <HeadInfo title="今日访问" :iconColor="visitColor" :content="loginfo.todayVisitCount" icon="team" />-->
<!--            <HeadInfo title="总访问量" :iconColor="seriesColor" :content="loginfo.totalVisitCount" icon="rise" />-->
<!--          </div>-->
<!--          <LineMulti :chartData="lineMultiData" height="33vh" type="line" :option="{ legend: { top: 'bottom' } }" />-->
<!--        </a-card>-->
<!--      </a-col>-->
<!--    </a-row>-->
  </div>
</template>
<script lang="ts" setup>
import {defineProps, onMounted, ref, unref, watch} from 'vue';
  import ChartGroupCard from '../components/ChartGroupCard.vue';
  import LineMulti from '/@/components/chart/LineMulti.vue';
  import HeadInfo from '/@/components/chart/HeadInfo.vue';
  import { getLoginfo, getVisitInfo ,getPositionsList} from '../api.ts';
  import { useRootSetting } from '/@/hooks/setting/useRootSetting';
  import { totalApple, table1 ,chartCardList, tableY} from '@/views/dashboard/Analysis/data';
  import { Progress } from 'ant-design-vue';

  const loading = ref(true);
  const { getThemeColor } = useRootSetting();

  setTimeout(() => {
    loading.value = false;
  }, 500);

  const loginfo = ref({});
  const lineMultiData = ref([]);

  function initLogInfo() {
    getLoginfo(null).then((res) => {
      if (res.success) {
        Object.keys(res.result).forEach((key) => {
          res.result[key] = res.result[key] + '';
        });
        loginfo.value = res.result;
      }
    });
    getVisitInfo(null).then((res) => {
      if (res.success) {
        lineMultiData.value = [];
        res.result.forEach((item) => {
          lineMultiData.value.push({ name: item.type, type: 'ip', value: item.ip, color: ipColor.value });
          lineMultiData.value.push({ name: item.type, type: 'visit', value: item.visit, color: visitColor.value });
        });
      }
    });

  }

  const ipColor = ref();
  const visitColor = ref();
  const seriesColor = ref();
  watch(
    () => getThemeColor.value,
    () => {
      seriesColor.value = getThemeColor.value;
      visitColor.value = '#67B962';
      ipColor.value = getThemeColor.value;
      initLogInfo();
    },
    { immediate: true }
  );
  function tableChange(pagination) {
    ipagination.value.current = pagination.current;
    ipagination.value.pageSize = pagination.pageSize;
    // loadDataSource();
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
  const indexBottomTab = ref('1');
  const indexRegisterType = ref('转移登记');
  const dataSource = ref([]);
  const dataSource1 = ref([]);
  const ipagination = ref(totalApple.ipagination);
  const ipagination1 = ref(table1.ipagination);
  // function loadDataSource() {
  //   updateTable();
  //   // dataSource.value = table.dataSource.filter((item) => {
  //     // if (!unref(indexRegisterType)) {
  //       return true;
  //     // }
  //     // return item.sysOrgCode == unref(indexRegisterType);
  //   // });
  //   console.log('table.dataSou8', table.dataSource)
  // }

  function loadDataSource1() {
    // updateTable();
    dataSource1.value = totalApple.dataSource.filter((item) => {
      // if (!unref(indexRegisterType)) {
        return true;
      // }
      // return item.type == unref(indexRegisterType);
    });
  }

  // loadDataSource();
  loadDataSource1();

function updateyfbPositionsTable() {
  getPositionsList(null).then((res) => {
    // console.log('po==============sitionsList', res)
    if (res.success) {
      // console.log('po==============si---tionsList', res.result.records)
      // console.log('table.dataSource,,,,,,,&&&,,,,', table.dataSource)
      // table.dataSource = res.result.records;
      dataSource.value = res.result.records;
      // console.log('table.dataSource,,,,,,,,,,,,,,', table.dataSource)

      ipagination.value = totalApple.ipagination;
    }
  });
}

// 使用 onMounted 钩子在页面刷新时调用 updateTable
onMounted(() => {
  updateyfbPositionsTable();
});

</script>

<style lang="less" scoped>
  .infoArea {
    display: flex;
    justify-content: space-between;
    padding: 0 10%;
    .head-info.center {
      padding: 0;
    }
    .head-info {
      min-width: 0;
    }
  }
  .circle-cust {
    position: relative;
    top: 28px;
    left: -100%;
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

  /* 首页访问量统计 */
  .head-info {
    position: relative;
    text-align: left;
    padding: 0 32px 0 0;
    min-width: 125px;

    &.center {
      text-align: center;
      padding: 0 32px;
    }

    span {
      color: rgba(0, 0, 0, 0.45);
      display: inline-block;
      font-size: 0.95rem;
      line-height: 42px;
      margin-bottom: 4px;
    }

    p {
      line-height: 42px;
      margin: 0;

      a {
        font-weight: 600;
        font-size: 1rem;
      }
    }
  }
  .ant-card {
    ::v-deep(.ant-card-head-title) {
      font-weight: normal;
    }
  }
</style>

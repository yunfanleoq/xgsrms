<template>
  <div class="job-detail">
    <h2>职位详情</h2>
    <div class="button-container">
      <button @click="goBack" class="back-button">返回</button>
      <button @click="positionApply" class="apply-button">在线申请</button>
      <div>
        <button v-if="!isCollected" class="favorite-button" @click="markFavoriteJob">收藏职位</button>
        <button v-else class="marked-favorite-button" @click="delFavoriteJob"   >
          已收藏</button>
      </div>
    </div>

    <div v-if="job" class="job-info">
      <div class="job-field">
        <strong>职位名称:</strong> {{ job.positionName }}
      </div>
      <div class="job-field">
        <strong>部门:</strong> {{ job.dept_dictText }}
      </div>
      <div class="job-field">
        <strong>研究方向:</strong> {{ job.researchDirection }}
      </div>
      <div class="job-field">
        <strong>专业要求:</strong>
        <pre> {{ job.professional }} </pre>
      </div>
      <div class="job-field">
        <strong>工作年限:</strong> {{ job.workYears }}
      </div>
      <div class="job-field">
        <strong>学历要求:</strong> {{ job.xlxw }}
      </div>
      <div class="job-field">
        <strong>招聘状态:</strong> {{ job.status_dictText }}
      </div>
      <div class="job-field">
        <strong>职责:</strong>
        <pre>{{ job.duty }}</pre>
      </div>
      <div class="job-field">
        <strong>联系人:</strong> {{ job.ktz_dictText }}
      </div>
      <div class="job-field">
        <strong>联系电话:</strong> {{ job.telphone || '无' }}
      </div>
      <div class="job-field">
        <strong>电子邮箱:</strong> {{ job.email }}
      </div>
      <div class="job-field">
        <strong>备注:</strong>
        <pre> {{ job.memo || '无' }} </pre>
      </div>
    </div>
    <div v-else>
      <p>职位信息加载中...</p>
    </div>
  </div>
<!--  <XgsPositionApplyForm v-if="XgsPositionApplyFormShow" :formData="job" :formBpm="false" />-->
  <XgsPositionApplyModal ref="registerModal" v-model:visible="XgsPositionApplyFormShow" :formData="record" :formBpm="false" />


</template>

<script setup lang="ts">
import {ref, onMounted, computed} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getJobById } from '@/api/xgsrms/home';
import {xgsFavoriteJobAdd, xgsFavoriteJobDel, xgsFavoriteJobList} from "@/api/xgsrms/positions";
import {useUserStore} from "@/store/modules/user";
import {usePositionApplyStoreWithOut} from "@/store/modules/positionApply"
import {useMessage} from "@/hooks/web/useMessage"; // 假设你有一个 API 来获取职位信息
import XgsPositionApplyModal from '@/views/positions/components/XgsPositionApplyModal.vue'
// const registerModal = ref();

const positionApplyStore = usePositionApplyStoreWithOut();

const record = positionApplyStore.currPositionApply;
const route = useRoute();
const router = useRouter();
const jobId = route.params.id as string;
const job = ref(null);
// const favoriteButtonHover = ref(true);
//
// const buttonText = computed(() =>{
//   return favoriteButtonHover ? '取消收藏' : '收藏';
// });
// const buttonTitle = computed(() =>{
//   return favoriteButtonHover ? '取消收藏' : '收藏';
// });

const userStore = useUserStore();
const goBack = () => {
  // router.go(-1); // 返回上一页
  router.push({ name: 'homePositions' });
};

console.log(jobId);
const createMessage = useMessage();

const isCollected = ref(false);
const favoriteJob = ref({});
const userId = ref('');

// const record = ref({
//   userId: userStore.getUserInfo.username,
//   userName: userStore.getUserInfo.realname,
//   positionId: job.value.id,
//   positionName: job.value.positionName,
//   positionDept: job.value.dept_dictText,
//   positionType: job.value.category,
//   // resumeName:userStore.getUserInfo.realname+userStore.getUserInfo.username+'_'+job.value.+job.value.positionName,
//
// });

const fetchFavoriteJob = () => {
  // 判断 userStore.userInfo 是否为 null，为null则赋值为 false，不为 null 则赋值 true
  let curUserId = '';
  if (userStore.userInfo === null) {
    curUserId = '';
  } else {
    curUserId = userStore.userInfo.username;
  }


  xgsFavoriteJobList({userId:curUserId, positionId: jobId}).then(res => {
      console.log('xgsFavoriteJobList', res);
      if (res.result.records.length > 0) {
        isCollected.value = true;
        favoriteJob.value = res.result.records[0];
        console.log('favoriteJob', favoriteJob.value);
      }

  });
};

onMounted(fetchFavoriteJob);

import { message } from 'ant-design-vue';

const markFavoriteJob = () => {

  // 判断 userStore.userInfo 是否为 null，为null则提示用户登录，不为 null 则赋值 true
  if (userStore.userInfo === null) {
    // 使用 message.warning
    message.warning('请先登录');
    console.log('请先登录');
    return;
  }
  // TODO: 实现收藏职位的功能
  let params = {
    id: jobId,
    userId: userStore.userInfo.username ,
    userName: userStore.userInfo.realname ,
    positionId: jobId,
    positionName: job.value.positionName,
    positionDept: job.value.dept_dictText,
    positionKtz: job.value.ktz_dictText,
    positionCount: job.value.personCount,

  };



  xgsFavoriteJobAdd(params).then(res => {
    if (res.code == 200) {

      isCollected.value = true;
      // 使用 message.warning
      message.success(`收藏职位成功`);
      console.log(`${res.message}`);
    } else {
      message.error(`${res.message}`);
      console.log(`${res.message}`);
    }
  });

}

// 取消收藏职位的功能
const delFavoriteJob = () => {
  // TODO: 实现取消收藏职位的功能
  let params = {
    id: favoriteJob.value.id,
  };
  console.log('delFavoriteJob', params);

  xgsFavoriteJobDel(params).then(res => {
    if (res.code === 200) {

      isCollected.value = false;

      message.success(`取消收藏职位成功`);
      console.log(`${res.message}`,isCollected.value);
    } else {
      message.error(`取消收藏职位失败`);
      console.log(`${res.message}`);
    }
  });
};

const XgsPositionApplyFormShow = ref(false);

const positionApply = () => {
  if (userStore.userInfo === null) {
    // 使用 message.warning
    message.warning('请先登录');
    console.log('请先登录');
    return;
  } else {
    XgsPositionApplyFormShow.value = true;

    // const record = ref({
    //   userId: userStore.getUserInfo.username,
    //   userName: userStore.getUserInfo.realname,
    //   positionId: job.value.id,
    //   positionName: job.value.positionName,
    //   positionDept: job.value.dept_dictText,
    //   positionType: job.value.category,
    //   resumeName:userStore.getUserInfo.realname+userStore.getUserInfo.username+'_'+job.value.+job.value.positionName,
    //
    // });

    // 页面加载完成后，等待3秒钟 点击 新增按钮 并将 job数据传给 新增窗口。
    // setTimeout(() => {
    //   //
    //   registerModal.value.add(record.value  );
    // }, 1000);

    // let queryObj = job.value|| {applyStatus: '申请中'};
    // queryObj.applyStatus = '申请中';
    //
    // message.success('正在跳转至申请页面');
    // console.log('>>>>>>job.value', queryObj);

    // router.push({
    //   name: 'positions-XgsPositionApplyList',
    //   query: queryObj
    // });
  };

}

const fetchCurrApplyPosition = async () => {
  try {
    let params = {
      id: jobId,
    };
    const response = await getJobById(params);
    job.value = response.result.records[0];
    // 将job存pinia
    // 获取 Pinia store 实例


    console.log('>>>>>>fetchCurrApplyPosition', positionApplyStore.currPositionApply);
    positionApplyStore.currPositionApply = JSON.parse(JSON.stringify(response.result.records[0]));
    console.log('>>>>>>fetchCurrApplyPosition', positionApplyStore.currPositionApply);
  } catch (error) {
    console.error('获取职位信息失败:', error);
  }
};

onMounted(() => {
  fetchCurrApplyPosition();
});
</script>

<style scoped>
.job-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.job-info {
  margin-top: 20px;
}

.job-field {
  margin-bottom: 10px;
}

.job-field strong {
  display: inline-block;
  width: 120px;
  color: #333;
}

.job-field span {
  color: #555;
}

h2 {
  text-align: center;
}

h3 {
  margin-top: 20px;
}

p {
  margin: 10px 0;
}

strong {
  font-weight: bold;
}

.button-container {
  display: flex;
  justify-content: space-between; /* 将按钮分别放置在左右两边 */
  margin-top: 20px; /* 可选：添加顶部间距 */
}


.back-button, .apply-button {
  margin-bottom: 20px;
  padding: 10px 20px;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}
.favorite-button {
  margin-bottom: 20px;
  padding: 10px 20px;
  background-color: #c47e6e;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;

}

.marked-favorite-button {
  margin-bottom: 20px;
  padding: 10px 20px;
  background-color: #b84035;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;

}

.back-button:hover, .apply-button:hover{
  background-color: #357ab8;
}

.favorite-button:hover {
  background-color: #ba372a;
}
.marked-favorite-button:hover {
  background-color: #52130d;

}

pre {
  margin: 0;
  white-space: pre-wrap; /* 保留换行符并自动换行 */
  word-wrap: break-word; /* 防止长单词溢出 */
  background-color: #f0f0f0;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ddd;
}
</style>

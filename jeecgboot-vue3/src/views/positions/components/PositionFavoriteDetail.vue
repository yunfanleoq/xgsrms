<template>
  <BasicModal v-bind="$attrs" @register="registerModalWin" destroyOnClose :title="title" :width="896" :height="720">
    <div class="job-detail">
      <h2>职位详情</h2>
      <div class="button-container">
        <button @click="positionApply" class="apply-button">在线申请</button>
        <div>
          <button v-if="!isCollected" class="favorite-button" @click="markFavoriteJob">收藏职位</button>
          <button v-else class="marked-favorite-button" @click="delFavoriteJob"> 已收藏</button>
        </div>
      </div>

      <div v-if="job" class="job-info">
        <div class="job-field"> <strong>职位名称:</strong> {{ job.positionName }} </div>
        <div class="job-field"> <strong>部门:</strong> {{ job.dept_dictText }} </div>
        <div class="job-field"> <strong>研究方向:</strong> {{ job.researchDirection }} </div>
        <div class="job-field">
          <strong>专业要求:</strong>
          <pre> {{ job.professional }} </pre>
        </div>
        <div class="job-field"> <strong>工作年限:</strong> {{ job.workYears }} </div>
        <div class="job-field"> <strong>学历要求:</strong> {{ job.xlxw }} </div>
        <div class="job-field"> <strong>招聘状态:</strong> {{ job.status_dictText }} </div>
        <div class="job-field">
          <strong>职责:</strong>
          <pre>{{ job.duty }}</pre>
        </div>
        <div class="job-field"> <strong>联系人:</strong> {{ job.ktz_dictText }} </div>
        <div class="job-field"> <strong>联系电话:</strong> {{ job.telphone || '无' }} </div>
        <div class="job-field"> <strong>电子邮箱:</strong> {{ job.email }} </div>
        <div class="job-field">
          <strong>备注:</strong>
          <pre> {{ job.memo || '无' }} </pre>
        </div>
      </div>
      <div v-else>
        <p>职位信息加载中...</p>
      </div>
    </div>
<!--    <XgsPositionApplyModal @register="registerModal" :form-data="record" :formBpm="true" />-->
    <XgsResumeBSHModal v-if="job.category === '博士后岗位'" @register="registerModal" :form-data="record" :formBpm="false" />
    <XgsResumeFGModal v-else-if="job.category === '副高级以上岗位'" @register="registerModal" :form-data="record" :formBpm="true" />
    <XgsResumeTJModal v-else-if="job.category === '人才派遣岗位'" @register="registerModal" :form-data="record" :formBpm="true" />
    <XgsResumeBaseModal v-else @register="registerModal" :form-data="record" :formBpm="false" />

  </BasicModal>
</template>

<script setup lang="ts">
  import { ref, onMounted, computed } from 'vue';
  import { useRoute } from 'vue-router';
  import { getJobById } from '@/api/xgsrms/home';
  import { xgsFavoriteJobAdd, xgsFavoriteJobDel, xgsFavoriteJobList } from '@/api/xgsrms/positions';
  import { useUserStore } from '@/store/modules/user';
  import { usePositionApplyStoreWithOut } from '@/store/modules/positionApply';
  import { useMessage } from '@/hooks/web/useMessage'; // 假设你有一个 API 来获取职位信息
  // import XgsPositionApplyModal from '@/views/home/position/XgsPositionApplyModal.vue';
  import XgsPositionApplyModal from '@/views/home/position/components/XgsResumeBaseModal.vue';


  import {saveOrUpdate} from '../XgsFavoriteJob.api';
  import data from "emoji-mart-vue-fast/data/apple.json";
  import XgsResumeBSHModal
    from "@/views/userPositions/xgsResumeFavoriteBase/xgsResumeBSH/components/XgsResumeBSHModal.vue";
  import XgsResumeFGModal
    from "@/views/userPositions/xgsResumeFavoriteBase/xgsResumeFG/components/XgsResumeFGModal.vue";
  import XgsResumeTJModal
    from "@/views/userPositions/xgsResumeFavoriteBase/xgsResumeTJ/components/XgsResumeTJModal.vue";
  import XgsResumeBaseModal
    from "@/views/userPositions/xgsResumeFavoriteBase/xgsResumePT/components/XgsResumeBaseModal.vue";
  import XgsUserPositionApplyModal
    from "@/views/userPositions/userPositions/components/XgsUserPositionApplyModal.vue";
  import JUpload from "@/components/Form/src/jeecg/components/JUpload/JUpload.vue";

  import { message } from 'ant-design-vue';
  import {BasicModal, useModal, useModalInner} from '@/components/Modal';

  import {BasicForm, useForm} from '/@/components/Form/index';
  import {formSchema} from '../XgsFavoriteJob.data';

  const positionApplyStore = usePositionApplyStoreWithOut();

  const route = useRoute();
  // const jobId = route.params.id as string;
  let jobId = '';
  const job = ref({});

  const userStore = useUserStore();

  const createMessage = useMessage();

  const isCollected = ref(false);
  const favoriteJob = ref({});
  const userId = ref('');

  const record = ref({});

  const fetchFavoriteJob = () => {
    // 判断 userStore.userInfo 是否为 null，为null则赋值为 false，不为 null 则赋值 true
    let curUserId = '';
    if (userStore.userInfo === null) {
      curUserId = '';
    } else {
      curUserId = userStore.userInfo.username;
    }
    xgsFavoriteJobList({ userId: curUserId, positionId: jobId }).then((res) => {
      if (res.result.records.length > 0) {
        isCollected.value = true;
        favoriteJob.value = res.result.records[0];
      }
    });
  };

  onMounted(fetchFavoriteJob);

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
      // id: jobId,
      userId: userStore.userInfo.username,
      userName: userStore.userInfo.realname,
      positionId: jobId,
      positionName: job.value.positionName,
      positionDept: job.value.dept_dictText,
      positionKtz: job.value.ktz_dictText,
      positionCount: job.value.personCount,
    };

    xgsFavoriteJobAdd(params).then((res) => {
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
  };

  // 取消收藏职位的功能
  const delFavoriteJob = () => {
    // TODO: 实现取消收藏职位的功能
    let params = {
      id: favoriteJob.value.id,
    };
    console.log('delFavoriteJob', params);

    xgsFavoriteJobDel(params).then((res) => {
      if (res.code === 200) {
        isCollected.value = false;

        message.success(`取消收藏职位成功`);
        console.log(`${res.message}`, isCollected.value);
      } else {
        message.error(`取消收藏职位失败`);
        console.log(`${res.message}`);
      }
    });
  };
  const positionApplyFormData = ref({});
  const XgsPositionApplyFormShow = ref(false);

  const [registerModal, { openModal }] = useModal();
  function positionApply() {
    if (userStore.userInfo === null) {
      // 使用 message.warning
      message.warning('请先登录');
      return;
    } else {
      let jobDefault = {
        applyId: '',
        disabled: false,
        mark: '',
        applyPositionDept: job.value.dept_dictText,
        positionId: job.value.id,
        applyPositionName: job.value.positionName,
        applyPositionType: job.value.category,
        resumeId: '',
        resumeName: userStore.getUserInfo.realname + userStore.getUserInfo.username + '_' + job.value.positionName,
        status: '申请中',
        applyUserName: userStore.getUserInfo.realname,
        email: '',
        researchDirection: '',
      };
      let jobDetail = Object.assign({}, positionApplyStore.currPositionApply, jobDefault);
      openModal(true, {
        isUpdate: false,
        showFooter: true,
        jobDetail: jobDetail,
      });
    }
  }

  const positionApply2 = () => {
    if (userStore.userInfo === null) {
      // 使用 message.warning
      message.warning('请先登录');
      console.log('请先登录');
      return;
    } else {
      XgsPositionApplyFormShow.value = true;
      registerModal.value.addJob(positionApplyStore.currPositionApply);
      // record.value = positionApplyStore.currPositionApply;
      record.value.applyId = '';
      record.value.disabled = false;
      record.value.mark = '';
      record.value.positionDept = job.value.dept_dictText;
      record.value.positionId = job.value.id;
      record.value.positionName = job.value.positionName;
      record.value.positionType = job.value.category;
      record.value.resumeId = '';
      record.value.resumeName = userStore.getUserInfo.realname + userStore.getUserInfo.username + '_' + job.value.positionName;
      record.value.status = '申请中';
      record.value.userName = userStore.getUserInfo.realname;
    }
  };

  const fetchCurrApplyPosition = async () => {
    try {
      let params = {
        id: jobId,
      };
      console.log('id======================', jobId);
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

  //
  const isReady = ref(false);

  const favoriteJobId = ref('')
  // Emits声明
  const emit = defineEmits(['register','success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  //表单配置
  const [registerForm, { setProps,resetFields, setFieldsValue, validate, scrollToField }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: {span: 12}
  });
  //表单赋值
  const [registerModalWin, {setModalProps, closeModal}] = useModalInner(async (data) => {
    console.log('data', data)
    //重置表单
    await resetFields();
    setModalProps({confirmLoading: false,showCancelBtn:!!data?.showFooter,showOkBtn:!!data?.showFooter});
    // isUpdate.value = !!data?.isUpdate;
    // isDetail.value = !!data?.showFooter;
    isUpdate.value = true;
    isDetail.value = true;
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter })
    isReady.value = true;
    jobId = data.record
    favoriteJobId.value = data.record
    fetchCurrApplyPosition();
  });
  //设置标题
  // const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(isDetail) ? '详情' : '编辑'));
  const title = ref('岗位详情')
  // onMounted(() => {
  //   fetchCurrApplyPosition();
  // });
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

  .back-button,
  .apply-button {
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

  .back-button:hover,
  .apply-button:hover {
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

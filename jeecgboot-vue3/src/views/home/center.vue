<template>
  <div class="portal-page">
    <MineNav v-if="!manager" />
    <header class="page-head">
      <h1>个人中心</h1>
    </header>
    <div class="page-card">
      <dl class="profile">
        <div class="profile-row">
          <dt>姓名</dt>
          <dd>{{ display(user?.realname) }}</dd>
        </div>
        <div class="profile-row">
          <dt>登录账号</dt>
          <dd>{{ display(user?.username) }}</dd>
        </div>
        <div class="profile-row">
          <dt>手机</dt>
          <dd>{{ display(user?.phone) }}</dd>
        </div>
        <div class="profile-row">
          <dt>邮箱</dt>
          <dd>{{ display(user?.email) }}</dd>
        </div>
      </dl>

      <h2>修改密码</h2>
      <p class="hint">密码至少 12 位，且须同时包含大写字母、小写字母、数字和特殊字符。</p>
      <form class="pwd-form" @submit.prevent="changePassword">
        <label>
          <span>原密码</span>
          <input v-model="form.oldpassword" type="password" autocomplete="current-password" />
        </label>
        <label>
          <span>新密码</span>
          <input v-model="form.password" type="password" autocomplete="new-password" />
        </label>
        <label>
          <span>确认新密码</span>
          <input v-model="form.confirmpassword" type="password" autocomplete="new-password" />
        </label>
        <button type="submit" :disabled="submitting">{{ submitting ? '保存中…' : '保存' }}</button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts" name="homeCenter">
  import { computed, onMounted, reactive, ref } from 'vue';
  import MineNav from './mine/MineNav.vue';
  import { message } from 'ant-design-vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useUserStore } from '@/store/modules/user';
  import { validatePasswordPolicy } from '/@/utils/helper/passwordPolicy';
  import { ROLE_DEPARTMENT, ROLE_HR } from './mine/workPages';

  const userStore = useUserStore();
  const manager = computed(() => {
    const roles = (userStore.getRoleList || []) as string[];
    return roles.includes(ROLE_DEPARTMENT) || roles.includes(ROLE_HR);
  });
  const user = computed(() => userStore.getUserInfo);
  const submitting = ref(false);
  const form = reactive({
    oldpassword: '',
    password: '',
    confirmpassword: '',
  });

  onMounted(async () => {
    try {
      await userStore.getUserInfoAction();
    } catch {
      // 未登录时留在当前页，路由守卫会送到登录
    }
  });

  function display(value?: string) {
    return value || '—';
  }

  async function changePassword() {
    if (!user.value?.username) {
      message.warning('请先登录');
      return;
    }
    if (!form.oldpassword) {
      message.warning('请输入原密码');
      return;
    }
    const policyError = validatePasswordPolicy(form.password);
    if (policyError) {
      message.warning(policyError);
      return;
    }
    if (form.password !== form.confirmpassword) {
      message.warning('两次输入的新密码不一致');
      return;
    }
    submitting.value = true;
    try {
      const res = await defHttp.put(
        {
          url: '/sys/user/updatePassword',
          params: {
            username: user.value.username,
            oldpassword: form.oldpassword,
            password: form.password,
            confirmpassword: form.confirmpassword,
          },
        },
        { isTransformResponse: false },
      );
      if (res?.success) {
        message.success(res.message || '密码已更新');
        form.oldpassword = '';
        form.password = '';
        form.confirmpassword = '';
      } else {
        message.warning(res?.message || '修改失败');
      }
    } finally {
      submitting.value = false;
    }
  }
</script>

<style scoped>
  .profile {
    margin: 0 0 28px;
  }

  .profile-row {
    display: grid;
    grid-template-columns: 5em 1fr;
    column-gap: 12px;
    margin: 0 0 8px;
    font-size: 15px;
    line-height: 1.8;
  }

  .profile-row:last-child {
    margin-bottom: 0;
  }

  dt,
  dd {
    margin: 0;
  }

  dt {
    font-weight: 700;
  }

  h2 {
    margin: 0 0 8px;
    font-size: 18px;
    font-weight: 700;
  }

  .hint {
    margin: 0 0 16px;
    color: #666666;
    font-size: 14px;
  }

  .pwd-form {
    display: grid;
    gap: 12px;
    max-width: 420px;
  }

  label {
    display: grid;
    gap: 4px;
    font-size: 14px;
  }

  input {
    height: 36px;
    padding: 0 10px;
    border: 1px solid #e0e0e0;
    background: #fff;
    font-size: 14px;
  }

  button {
    justify-self: start;
    margin-top: 4px;
    height: 36px;
    padding: 0 18px;
    border: 0;
    background: #004098;
    color: #fff;
    font-size: 14px;
    cursor: pointer;
  }

  button:disabled {
    opacity: 0.6;
    cursor: default;
  }
</style>

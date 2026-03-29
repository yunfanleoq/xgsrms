<template>
  <template v-if="getShow">
    <LoginFormTitle class="enter-x" />
    <Form class="p-4 enter-x" :model="formData" :rules="getFormRules" ref="formRef">
      <!-- 图形验证码：checkOnlyUser 须带 captcha/checkKey，防止匿名枚举用户名/手机号 -->
      <ARow class="enter-x">
        <ACol :span="12">
          <FormItem name="inputCode" class="enter-x">
            <Input
              size="large"
              v-model:value="formData.inputCode"
              :placeholder="t('sys.login.inputCodePlaceholder')"
              class="fix-auto-fill"
            />
          </FormItem>
        </ACol>
        <ACol :span="8">
          <FormItem :style="{ 'text-align': 'right', 'margin-left': '20px' }" class="enter-x">
            <img
              v-if="randCodeData.requestCodeSuccess"
              style="margin-top: 2px; max-width: initial; cursor: pointer"
              :src="randCodeData.randCodeImage"
              @click="handleChangeCheckCode"
            />
            <img
              v-else
              style="margin-top: 2px; max-width: initial; cursor: pointer"
              src="../../../assets/images/checkcode.png"
              @click="handleChangeCheckCode"
            />
          </FormItem>
        </ACol>
      </ARow>
      <FormItem name="account" class="enter-x">
        <Input class="fix-auto-fill" size="large" v-model:value="formData.account" :placeholder="t('sys.login.userName')" />
      </FormItem>
      <FormItem name="mobile" class="enter-x">
        <Input size="large" v-model:value="formData.mobile" :placeholder="t('sys.login.mobile')" class="fix-auto-fill" />
      </FormItem>
      <FormItem name="sms" class="enter-x">
        <CountdownInput
          size="large"
          class="fix-auto-fill"
          v-model:value="formData.sms"
          :placeholder="t('sys.login.smsCode')"
          :sendCodeApi="sendCodeApi"
        />
      </FormItem>
      <FormItem name="password" class="enter-x">
        <StrengthMeter size="large" v-model:value="formData.password" :placeholder="t('sys.login.password')" />
      </FormItem>
      <FormItem name="confirmPassword" class="enter-x">
        <InputPassword size="large" visibilityToggle v-model:value="formData.confirmPassword" :placeholder="t('sys.login.confirmPassword')" />
      </FormItem>

      <FormItem class="enter-x" name="policy">
        <!-- No logic, you need to deal with it yourself -->
        <Checkbox v-model:checked="formData.policy" size="small">
          {{ t('sys.login.policy') }}
        </Checkbox>
      </FormItem>

      <Button type="primary" class="enter-x" size="large" block @click="handleRegister" :loading="loading">
        {{ t('sys.login.registerButton') }}
      </Button>
      <Button size="large" block class="mt-4 enter-x" @click="handleBackLogin">
        {{ t('sys.login.backSignIn') }}
      </Button>
    </Form>
  </template>
</template>
<script lang="ts" setup>
  import { reactive, ref, unref, computed, toRaw, onMounted } from 'vue';
  import LoginFormTitle from './LoginFormTitle.vue';
  import { Form, Input, Button, Checkbox, Row, Col } from 'ant-design-vue';
  import { StrengthMeter } from '/@/components/StrengthMeter';
  import { CountdownInput } from '/@/components/CountDown';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useLoginState, useFormRules, useFormValid, LoginStateEnum, SmsEnum } from './useLogin';
  import { register, getCaptcha, getCodeInfo } from '/@/api/sys/user';
  const FormItem = Form.Item;
  const ARow = Row;
  const ACol = Col;
  const InputPassword = Input.Password;
  const { t } = useI18n();
  const { handleBackLogin, getLoginState } = useLoginState();
  const { notification, createErrorModal } = useMessage();
  const formRef = ref();
  const loading = ref(false);
  const formData = reactive({
    inputCode: '',
    checkKey: null as number | null,
    account: '',
    password: '',
    confirmPassword: '',
    mobile: '',
    sms: '',
    policy: false,
  });
  const randCodeData = reactive({
    randCodeImage: '',
    requestCodeSuccess: false,
  });
  const { getFormRules } = useFormRules(formData);

  function handleChangeCheckCode() {
    formData.inputCode = '';
    formData.checkKey = new Date().getTime();
    getCodeInfo(formData.checkKey).then((res) => {
      randCodeData.randCodeImage = res;
      randCodeData.requestCodeSuccess = true;
    });
  }
  const { validForm } = useFormValid(formRef);
  const getShow = computed(() => unref(getLoginState) === LoginStateEnum.REGISTER);
  /**
   * 注册
   */
  async function handleRegister() {
    const data = await validForm();
    if (!data) return;
    try {
      loading.value = true;
      const resultInfo = await register(
        toRaw({
          username: data.account,
          password: data.password,
          phone: data.mobile,
          smscode: data.sms,
        })
      );
      if (resultInfo && resultInfo.data.success) {
        notification.success({
          description: resultInfo.data.message || t('sys.api.registerMsg'),
          duration: 3,
        });
        handleBackLogin();
      } else {
        notification.warning({
          message: t('sys.api.errorTip'),
          description: resultInfo.data.message || t('sys.api.networkExceptionMsg'),
          duration: 3,
        });
      }
    } catch (error) {
      notification.error({
        message: t('sys.api.errorTip'),
        description: error.message || t('sys.api.networkExceptionMsg'),
        duration: 3,
      });
    } finally {
      loading.value = false;
    }
  }
  //发送验证码的函数
  function sendCodeApi() {
    return getCaptcha({ mobile: formData.mobile, smsmode: SmsEnum.REGISTER });
  }

  onMounted(() => {
    handleChangeCheckCode();
  });
</script>

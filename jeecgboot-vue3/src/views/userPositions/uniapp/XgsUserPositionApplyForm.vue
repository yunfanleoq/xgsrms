<template>
  <view>
    <!--标题和返回-->
    <cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
      <template #backText>
        <block>返回</block>
      </template>
      <template #content>
        <block>岗位申请</block>
      </template>
    </cu-custom>
    <!--表单区域-->
    <view>
      <form>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">申请人ID：</text></view>
            <input placeholder="请输入申请人ID" v-model="model.userId" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">申请人姓名：</text></view>
            <input placeholder="请输入申请人姓名" v-model="model.userName" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">简历ID：</text></view>
            <input placeholder="请输入简历ID" v-model="model.resumeId" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">简历名称：</text></view>
            <input placeholder="请输入简历名称" v-model="model.resumeName" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">岗位ID：</text></view>
            <input placeholder="请输入岗位ID" v-model="model.positionId" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">岗位名称：</text></view>
            <input placeholder="请输入岗位名称" v-model="model.positionName" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">岗位部门：</text></view>
            <input placeholder="请输入岗位部门" v-model="model.positionDept" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">岗位类型：</text></view>
            <input placeholder="请输入岗位类型" v-model="model.positionType" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">申请状态：</text></view>
            <input placeholder="请输入申请状态" v-model="model.status" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">申请备注：</text></view>
            <input placeholder="请输入申请备注" v-model="model.mark" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">申请标签：</text></view>
            <input placeholder="请输入申请标签" v-model="model.tagIds" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">创建者：</text></view>
            <input placeholder="请输入创建者" v-model="model.creator" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">更新者：</text></view>
            <input placeholder="请输入更新者" v-model="model.updater" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">是否删除：</text></view>
            <input placeholder="请输入是否删除" v-model="model.deleted" />
          </view>
        </view>
        <view class="cu-form-group">
          <view class="flex align-center">
            <view class="title"><text space="ensp">租户编号：</text></view>
            <input type="number" placeholder="请输入租户编号" v-model="model.tenantId" />
          </view>
        </view>
        <view class="padding">
          <button class="cu-btn block bg-blue margin-tb-sm lg" @click="onSubmit">
            <text v-if="loading" class="cuIcon-loading2 cuIconfont-spin" />提交
          </button>
        </view>
      </form>
    </view>
  </view>
</template>

<script>
  export default {
    name: 'XgsUserPositionApplyForm',
    components: { },
    props: {
      formData: {
        type: Object,
        default: () => {},
        required: false,
      },
    },
    data() {
      return {
        CustomBar: this.CustomBar,
        NavBarColor: this.NavBarColor,
        loading: false,
        model: {},
        backRouteName: 'index',
        url: {
          queryById: '/xgsUserResume/xgsUserPositionApply/queryById',
          add: '/xgsUserResume/xgsUserPositionApply/add',
          edit: '/xgsUserResume/xgsUserPositionApply/edit',
        },
      };
    },
    created() {
      this.initFormData();
    },
    methods: {
      initFormData() {
        if (this.formData) {
          let dataId = this.formData.dataId;
          this.$http.get(this.url.queryById, { params: { id: dataId } }).then((res) => {
            if (res.data.success) {
              console.log('表单数据', res);
              this.model = res.data.result;
            }
          });
        }
      },
      onSubmit() {
        let myForm = { ...this.model };
        this.loading = true;
        let url = myForm.id ? this.url.edit : this.url.add;
        this.$http
          .post(url, myForm)
          .then((res) => {
            console.log('res', res);
            this.loading = false;
            this.$Router.push({ name: this.backRouteName });
          })
          .catch(() => {
            this.loading = false;
          });
      },
    },
  };
</script>

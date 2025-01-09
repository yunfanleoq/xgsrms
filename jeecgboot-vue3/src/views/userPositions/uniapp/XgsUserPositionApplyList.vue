<template>
  <view>
    <!--标题和返回-->
    <cu-custom :bgColor="NavBarColor" isBack>
      <template #backText>
        <block>返回</block>
      </template>
      <template #content>
        <block>岗位申请</block>
      </template>
    </cu-custom>
    <!--滚动加载列表-->
    <mescroll-body ref="mescrollRef" bottom="88" @init="mescrollInit" :up="upOption" :down="downOption" @down="downCallback" @up="upCallback">
      <view class="cu-list menu">
        <view class="cu-item" v-for="(item, index) in list" :key="index" @click="goHome">
          <view class="flex" style="width: 100%">
            <text class="text-lg" style="color: #000">
              {{ item.createBy }}
            </text>
          </view>
        </view>
      </view>
    </mescroll-body>
  </view>
</template>

<script>

  export default {
    name: 'XgsUserPositionApplyList',
    mixins: [],
    data() {
      return {
        CustomBar: this.CustomBar,
        NavBarColor: this.NavBarColor,
        url: '/xgsUserResume/xgsUserPositionApply/list',
      };
    },
    methods: {
      goHome() {
        this.$Router.push({ name: 'index' });
      },
    },
  };
</script>

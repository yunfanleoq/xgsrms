import Vue from 'vue'
import App from './App'
import store from './store'
import MinCache from'./common/util/MinCache.js'
import tip from'./common/util/tip.js'
import configService from'./common/service/config.service.js'

import router from './common/router'
import {RouterMount} from './plugin/uni-simple-router/index.js'

//引入mescroll插件
import MescrollBody from "@/components/mescroll-uni/mescroll-body.vue"
import MescrollUni from "@/components/mescroll-uni/mescroll-uni.vue"
Vue.component('mescroll-body', MescrollBody);
Vue.component('mescroll-uni', MescrollUni);

// 注册缓存器
Vue.use(MinCache,{timeout: 6})

// store
Vue.prototype.$store=store;
// tip
Vue.prototype.$tip=tip;
// config
Vue.prototype.$config=configService;

// request请求
import { http } from '@/common/service/service.js' 
Vue.prototype.$http = http

import home from './pages/home/home.vue'
Vue.component('home',home)

// xgs pages begin
import xgsPositionsList from './pages/positions/xgsPositionsList.vue'
Vue.component('xgsPositionsList',xgsPositionsList)

import XgsJournalismForm from './pages/home/XgsJournalismForm.vue'
Vue.component('XgsJournalismForm',XgsJournalismForm)

import xgsPositionsForm from './pages/positions/xgsPositionsForm.vue'
Vue.component('xgsPositionsForm',xgsPositionsForm)

import XgsMyresumeList from './pages/xgs/myresume/XgsMyresumeList.vue'
Vue.component('XgsMyresumeList',XgsMyresumeList)

import resumeApplyForm from './pages/resume/resumeApplyForm.vue'
Vue.component('resumeApplyForm',resumeApplyForm)

import resumeBaseForm from './pages/resume/resumeBaseForm.vue'
Vue.component('resumeBaseForm',resumeBaseForm)

import resumeWorkForm from './pages/resume/resumeWorkForm.vue'
Vue.component('resumeWorkForm',resumeWorkForm)

import resumeEduForm from './pages/resume/resumeEduForm.vue'
Vue.component('resumeEduForm',resumeEduForm)

import resumeHomeForm from './pages/resume/resumeHomeForm.vue'
Vue.component('resumeHomeForm',resumeHomeForm)

// xgs pages end

import people from './pages/user/people.vue'
Vue.component('people',people)

// 自定义组件
import mySelect from './components/my-componets/my-select.vue'
Vue.component('mySelect',mySelect)

import myImageUpload from './components/my-componets/my-image-upload.vue'
Vue.component('myImageUpload',myImageUpload)

import myPage from './components/my-componets/my-page.vue'
Vue.component('myPage',myPage)

import basics from './pages/basics/home.vue'
Vue.component('basics',basics)

import components from './pages/component/home.vue'
Vue.component('components',components)

import plugin from './pages/plugin/home.vue'
Vue.component('plugin',plugin)

import cuCustom from './plugin/colorui/components/cu-custom.vue'
Vue.component('cu-custom',cuCustom)

// import VConsole from './js_sdk/vconsole.min'

//   var vConsole = new VConsole();

Vue.config.productionTip = false

App.mpType = 'app'

const app = new Vue({
	store,
	MinCache,
    ...App
})
//v1.3.5起 H5端 你应该去除原有的app.$mount();使用路由自带的渲染方式
// #ifdef H5
	RouterMount(app,'#app');
// #endif

// #ifndef H5
	app.$mount(); //为了兼容小程序及app端必须这样写才有效果
// #endif



 




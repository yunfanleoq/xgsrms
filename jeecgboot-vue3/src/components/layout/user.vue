<template>
  <div class="user">
    <el-dropdown>
      <span class="el-dropdown-link">
        <div class="nav-avatar"><img :src="user.avatar || defaultAvatar" alt="头像" /></div>
      </span>
      <template #dropdown>
        <el-dropdown-menu class="user-box">
          <div class="user-info">
            <div class="avatar" title="点击修改头像">
              <img :src="user.avatar || defaultAvatar" alt="头像" />
              <label class="mask">
                <i class="iconfont icon-icon-test" style="font-size: 20px;"></i>
                <input ref="avatarInput" type="file" accept="image/*" @change="fileChange" />
              </label>
            </div>
            <div class="text">
              <div class="username" @click="changeNickname" v-if="!nicknameChanged">{{ nickname }}</div>
              <el-input placeholder="请输入内容" size="small" v-else v-model="nickname" ref="input" @blur="blur"></el-input>
            </div>
            <img src="../../assets/image/user/corner.png" class="corner" />
          </div>
          <ul class="dropdown-box">
            <li class="password" @click="goToCenter">
              <i class="iconfont icon-weibaoxitongshangchuanlogo-"></i> <span>个人中心</span>
            </li>
            <li class="account" @click="outLogin"><i class="iconfont icon-tuichu"></i> <span>退出账户</span></li>
          </ul>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <!-- 修改头像 -->
    <el-dialog title="裁剪" v-model="cropVisible" width="300px" :append-to-body="true" :close-on-click-modal="false"
      custom-class="croppa-dialog" center>
      <div style="text-align: center;">
        <div class="avatar-croppa-container">
          <croppa ref="croppa" :width="cropRule.width" :height="cropRule.height" :placeholder="'loading'"
            :zoom-speed="30" :disable-drag-and-drop="false" :show-remove-button="false" :prevent-white-space="true"
            :disable-click-to-choose="false" :disable-scroll-to-zoom="false" :show-loading="true" :quality="quality"
            :initial-image="cropImg">
          </croppa>
        </div>
        <div style="margin-top: 1em;">通过鼠标滚轮调节头像大小</div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cropVisible = false" size="small">取 消</el-button>
          <el-button type="primary" @click="handleCrop" size="small">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog title="修改密码" :append-to-body="true" :before-close="handleClose" v-model="dialogFormVisible"
      class="user-dialog">
      <el-form :model="form" status-icon :rules="rules" label-position="left" ref="form" label-width="90px"
        @submit.prevent>
        <el-form-item label="原始密码" prop="old_password">
          <el-input type="password" v-model="form.old_password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="new_password">
          <el-input type="password" v-model="form.new_password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirm_password" label-position="top">
          <el-input type="password" v-model="form.confirm_password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')">保存</el-button>
          <el-button @click="resetForm('form')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import Croppa from 'vue-croppa'
import User from '@/lin/model/user'
import 'vue-croppa/dist/vue-croppa.css'
import defaultAvatar from '@/assets/image/user/user.png'

const store = useStore()

Vue.use(Croppa)

const width = 150
const height = 150

const username = ref(null)
const dialogFormVisible = ref(false)
const nicknameChanged = ref(false)
const nickname = ref(null)
const groupName = ref(null)
const form = reactive({
  old_password: '',
  new_password: '',
  confirm_password: '',
})
const rules = reactive({
  old_password: [
    { validator: (rule, value, callback) => {
      if (!value) {
        return callback(new Error('原始密码不能为空'))
      }
      callback()
    }, trigger: 'blur', required: true },
  ],
  new_password: [
    { validator: (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位数'))
      } else {
        if (form.confirm_password !== '') {
          store.refs.form.validateField('confirm_password')
        }
        callback()
      }
    }, trigger: 'blur', required: true },
  ],
  confirm_password: [
    { validator: (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== form.new_password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }, trigger: 'blur', required: true },
  ],
})
const cropRule = reactive({
  width,
  height,
})
const imgRule = reactive({
  minWidth: width,
  minHeight: height,
})
const cropVisible = ref(false)
const cropImg = ref('')
const croppa = ref({})
const imgInfo = ref(null)
const quality = ref(1)

watch(cropVisible, val => {
  if (!val) {
    croppa.value.remove()
    cropImg.value = ''
    imgInfo.value = null
  }
})

onMounted(() => {
  init()
})

function init() {
  const user = store.state.user
  username.value = user ? user.username : '未登录'
  groupName.value = user.groupName ? user.groupName : '超级管理员'
  nickname.value = user && user.nickname ? user.nickname : '佚名'
}

function fileChange(evt) {
  if (evt.target.files.length !== 1) {
    return
  }
  const imgFile = evt.target.files[0]
  if (imgFile.size > 1024 * 1024 * 5) {
    store.commit('setMessage', { type: 'error', message: '文件过大超过5M' })
    clearFileInput(store.refs.avatarInput)
    return
  }

  const imgSrc = window.URL.createObjectURL(imgFile)
  const image = new Image()
  image.src = imgSrc
  image.onload = () => {
    const w = image.width
    const h = image.height
    if (w < 50) {
      store.commit('setMessage', { type: 'error', message: '图像宽度过小, 请选择大于50px的图像' })
      clearFileInput(store.refs.avatarInput)
      return
    }
    if (h < 50) {
      store.commit('setMessage', { type: 'error', message: '图像高度过小, 请选择大于50px的图像' })
      clearFileInput(store.refs.avatarInput)
      return
    }
    cropImg.value = imgSrc
    cropVisible.value = true
    if (croppa.value) {
      croppa.value.refresh()
    }
  }
  image.onerror = () => {
    store.commit('setMessage', { type: 'error', message: '获取本地图片出现错误, 请重试' })
    clearFileInput(store.refs.avatarInput)
  }
}

async function handleCrop() {
  const blob = await croppa.value.promisedBlob('image/jpeg', 0.8)
  const file = new File([blob], 'avatar.jpg', {
    type: 'image/jpeg',
  })

  try {
    const res = await store.dispatch('axiosRequest', {
      method: 'post',
      url: '/recruit/file',
      data: {
        file,
      },
    })
    clearFileInput(store.refs.avatarInput)
    if (!Array.isArray(res) || res.length !== 1) {
      store.commit('setMessage', { type: 'error', message: '头像上传失败, 请重试' })
      return false
    }
    const putRes = await store.dispatch('axiosRequest', {
      method: 'put',
      url: '/recruit/user',
      data: {
        avatar: res[0].path,
      },
    })
    if (putRes.code < window.MAX_SUCCESS_CODE) {
      store.commit('setMessage', { type: 'success', message: '更新头像成功' })
      cropVisible.value = false
      const infoRes = await User.getInformation()
      store.commit('setUserAndState', infoRes)
    } else {
      throw new Error('更新头像失败')
    }
  } catch (error) {
    console.error(error)
  }
}

function changeNickname() {
  nicknameChanged.value = true
  setTimeout(() => {
    store.refs.input.focus()
  }, 200)
}

async function blur() {
  if (nickname.value) {
    const user = store.state.user
    if (nickname.value !== user.nickname && nickname.value !== '佚名') {
      try {
        const res = await store.dispatch('axiosRequest', {
          method: 'put',
          url: '/recruit/user',
          data: {
            nickname: nickname.value,
          },
          showBackend: true,
        })
        if (res.code < window.MAX_SUCCESS_CODE) {
          store.commit('setMessage', { type: 'success', message: '更新昵称成功' })
          const infoRes = await User.getInformation()
          store.commit('setUserAndState', infoRes)
          nickname.value = infoRes.nickname
        }
      } catch (error) {
        console.error(error)
      }
    }
  }
  nicknameChanged.value = false
}

function goToCenter() {
  store.router.push('/center')
}

function handleClose(done) {
  dialogFormVisible.value = false
  done()
}

function outLogin() {
  window.location.reload(true)
  store.dispatch('loginOut')
}

function submitForm(formName) {
  if (form.old_password === '' && form.new_password === '' && form.confirm_password === '') {
    dialogFormVisible.value = false
    return
  }
  if (form.old_password === form.new_password) {
    store.commit('setMessage', { type: 'error', message: '新密码不能与原始密码一样' })
    return
  }
  store.refs[formName].validate(async valid => {
    if (valid) {
      try {
        const res = await User.updatePassword(form)
        if (res.code < window.MAX_SUCCESS_CODE) {
          store.commit('setMessage', { type: 'success', message: `${res.message}` })
          resetForm(formName)
          dialogFormVisible.value = false
          setTimeout(() => {
            store.dispatch('loginOut')
            const { origin } = window.location
            window.location.href = origin
          }, 1000)
        }
      } catch (error) {
        console.error(error)
        store.commit('setMessage', { type: 'error', message: '请填写正确的信息' })
      }
    } else {
      console.log('error submit!!')
      store.commit('setMessage', { type: 'error', message: '请填写正确的信息' })
      return false
    }
  })
}

function resetForm(formName) {
  store.refs[formName].resetFields()
}

function clearFileInput(ele) {
  ele.value = ''
}
</script>

<style lang="scss" scoped>
.user-dialog ::v-deep(.el-dialog .el-dialog__header) {
  border-bottom: 1px solid #dae1ed;
  padding-bottom: 20px;
}

.user-dialog ::v-deep(.el-dialog .el-dialog__body) {
  padding-bottom: 0;
}

.user {
  height: 40px;

  .el-dropdown-link {
    cursor: pointer;

    .nav-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      margin-right: 10px;
    }
  }
}

.user-box {
  width: 326px;
  background-color: none;
  background: transparent;
  margin-bottom: 0;
  padding-bottom: 0;
  border: none;

  .user-info {
    background-image: url('../../assets/image/user/user-bg.png');
    background-size: 100% 100%;
    transform: translateY(-10px);
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
    display: flex;
    flex-direction: row;
    padding: 35px 20px 25px 30px;
    z-index: 100;
    position: relative;

    .corner {
      position: absolute;
      right: 18px;
      top: -9px;
      width: 27px;
      height: 10px;
    }

    .avatar {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      cursor: pointer;
      overflow: hidden;
      position: relative;

      .mask {
        opacity: 0;
        transition: all 0.2s;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.3);
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        color: white;

        input {
          display: none;
        }
      }

      &:hover {
        .mask {
          opacity: 1;
        }
      }
    }

    .text {
      margin-left: 20px;
      color: #fff;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .username {
        margin-bottom: 10px;
        font-size: 16px;
        height: 32px;
        line-height: 32px;
        cursor: pointer;
      }

      .desc {
        font-size: 14px;
        color: rgba(222, 226, 230, 1);
      }
    }

    .info {
      position: absolute;
      bottom: 10px;
      right: 10px;
      display: flex;
      color: #fff;
      font-size: 14px;
      height: 20px;
      line-height: 20px;

      .mid {
        padding: 0 5px;
      }
    }
  }

  .dropdown-box {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    padding-left: 35px;
    height: 122px;
    color: #596c8e;
    font-size: 14px;
    background: white;
    margin-top: -10px;

    li {
      cursor: pointer;

      &:nth-child(1) {
        margin-top: 20px;
      }

      &:nth-child(2) {
        margin-bottom: 20px;
      }

      i {
        margin-right: 10px;
      }

      &:hover {
        color: $theme !important;

        i {
          color: $theme !important;
        }
      }
    }
  }
}

</style>

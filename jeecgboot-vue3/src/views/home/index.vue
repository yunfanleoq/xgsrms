<template>
  <div id="app">
    <div class="banner">
      <img src="@/assets/images/xgs_logo_.png" alt="Banner Image" class="banner-image" />
      <img src="@/assets/images/xgs_logo01.png" alt="" class="banner-image" />
    </div>

    <!-- 顶部导航栏 -->
    <header>
      <div class="logo">中国科学院信息工程研究所人才招聘管理系统</div>
      <nav class="nav-menu">
        <RouterLink :to="{ name: 'homeHome' }" class="nav-link">首页</RouterLink>
        <span class="nav-divider">|</span>
        <RouterLink :to="{ name: 'homeNews' }" class="nav-link">科研概况</RouterLink>
        <span class="nav-divider">|</span>
        <RouterLink :to="{ name: 'homeYgqk' }" class="nav-link">员工情况</RouterLink>
        <span class="nav-divider">|</span>
        <RouterLink :to="{ name: 'homePositions' }" class="nav-link">招聘信息</RouterLink>
        <span class="nav-divider">|</span>
        <RouterLink :to="{ name: 'contactUs' }" class="nav-link">联系我们</RouterLink>
      </nav>
      <div class="header-actions">
        <div v-if="!isLoggedIn" class="auth-buttons">
          <button @click="goToLoginPage" class="btn btn-login">登录</button>
          <button @click="goToRegisterPage" class="btn btn-register">注册</button>
        </div>
        <div v-else class="user-section">
          <span class="user-info" v-if="loggedInUser">欢迎, {{ loggedInUser.realname }}</span>
          <button @click="goToUserCenter" class="btn btn-user-center">个人中心</button>
          <button @click="logout" class="btn btn-logout">退出</button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <RouterView />
    </main>

    <footer>
      <!-- 友情链接 -->
      <div class="friend-links">
        <a href="https://www.cas.cn" target="_blank" rel="noopener noreferrer">中国科学院</a>
        <span class="link-separator">|</span>
        <a href="http://www.nsfc.gov.cn/" target="_blank" rel="noopener noreferrer">国家自然科学基金</a>
        <span class="link-separator">|</span>
        <a href="https://www.casjob.com/" target="_blank" rel="noopener noreferrer">中科人才网</a>
        <span class="link-separator">|</span>
        <a href="http://www.iie.cas.cn/" target="_blank" rel="noopener noreferrer">中科院信工所</a>
      </div>
      <p class="footer-text">© {{ currentYear }} 中国科学院信息工程研究所人才招聘管理系统. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router';
  import { computed, onMounted } from 'vue';
  import { useUserStore } from '@/store/modules/user';
  import { doLogout } from '@/api/sys/user';
  import { useMultipleTabStore } from '@/store/modules/multipleTab';
  const router = useRouter();
  const userStore = useUserStore();

  const isLoggedIn = computed(() => !!userStore.userInfo?.username);
  const loggedInUser = computed(() => userStore.userInfo);

  onMounted(() => {
    userStore.getUserInfoAction();
  });

  const currentYear = computed(() => new Date().getFullYear());

  const logout = () => {
    doLogout().then(() => {
      router.push({ name: 'Login' });
    });
  };

  const goToUserCenter = () => {

    // useMultipleTabStore.closeAllTabs(router);
    // 正确调用方式：先获取 store 实例
    const tabStore = useMultipleTabStore();
    tabStore.closeAllTab(router);
    router.push({ path: '/dashboard/analysis' });
  };

  const goToLoginPage = () => {
    router.push({ name: 'Login' });
  };

  const goToRegisterPage = () => {
    router.push({ name: 'Login', query: { tab: 'register' } });
  };
</script>

<style scoped>
  /* 全局样式 */
  body {
    margin: 0;
    font-family: 'Poppins', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
    background-color: #f7f9fc;
  }

  #app {
    width: 100%;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  /* Banner 样式 */
  .banner {
    background-image: url('@/assets/images/xgs_topbg.png');
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
  }

  .banner-image {
    height: 100px;
    width: 80%;
    max-width: 1200px;
    border-radius: 7px;
    object-fit: contain;
    margin: 0 5px;
  }

  /* 顶部导航栏 */
  header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(90deg, #3d54a7, #3d54a7);
    color: white;
    padding: 15px 30px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    position: sticky;
    top: 0;
    z-index: 1000;
  }

  .logo {
    font-size: 20px;
    font-weight: bold;
    white-space: nowrap;
  }

  /* 导航菜单 */
  .nav-menu {
    display: flex;
    align-items: center;
    justify-content: center;
    flex: 1;
    gap: 5px;
  }

  .nav-link {
    color: white;
    padding: 8px 16px;
    text-decoration: none;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 4px;
    position: relative;
  }

  .nav-link:hover {
    background-color: rgba(255, 255, 255, 0.1);
    transform: translateY(-2px);
  }

  .nav-link.router-link-active {
    background-color: rgba(255, 255, 255, 0.2);
  }

  .nav-divider {
    color: rgba(255, 255, 255, 0.5);
    margin: 0 4px;
  }

  /* 头部操作区 */
  .header-actions {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .auth-buttons,
  .user-section {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  /* 按钮样式 */
  .btn {
    padding: 8px 20px;
    border-radius: 6px;
    border: none;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    white-space: nowrap;
  }

  .btn-login,
  .btn-register {
    background-color: transparent;
    color: white;
    border: 2px solid white;
  }

  .btn-login:hover,
  .btn-register:hover {
    background-color: white;
    color: #3d54a7;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  }

  .btn-logout {
    background-color: #e74c3c;
    color: white;
  }

  .btn-logout:hover {
    background-color: #c0392b;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(231, 76, 60, 0.3);
  }

  .btn-user-center {
    background-color: #27ae60;
    color: white;
  }

  .btn-user-center:hover {
    background-color: #229954;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(39, 174, 96, 0.3);
  }

  .user-info {
    color: white;
    font-size: 16px;
    padding: 0 10px;
  }

  /* 主内容区 */
  .main-content {
    flex: 1;
    padding: 20px;
    background-color: #f9f9f9;
  }

  /* 页脚 */
  footer {
    text-align: center;
    padding: 20px;
    background-color: #f5f5f5;
    color: #666;
    margin-top: auto;
    border-top: 1px solid #e0e0e0;
  }

  /* 友情链接 */
  .friend-links {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    gap: 5px;
    padding: 15px 0;
    border-bottom: 1px solid #e0e0e0;
    margin-bottom: 15px;
  }

  .friend-links a {
    color: #666;
    text-decoration: none;
    font-size: 14px;
    padding: 5px 10px;
    transition: color 0.3s ease;
  }

  .friend-links a:hover {
    color: #3d54a7;
    text-decoration: underline;
  }

  .link-separator {
    color: #ccc;
    font-size: 14px;
  }

  .footer-text {
    margin: 0;
    font-size: 13px;
    color: #999;
  }

  /* 响应式设计 */
  @media (max-width: 1024px) {
    header {
      flex-wrap: wrap;
      padding: 10px 20px;
    }

    .logo {
      font-size: 16px;
      width: 100%;
      text-align: center;
      margin-bottom: 10px;
    }

    .nav-menu {
      order: 3;
      width: 100%;
      justify-content: center;
      margin-top: 10px;
    }

    .nav-link {
      font-size: 14px;
      padding: 6px 10px;
    }

    .header-actions {
      order: 2;
    }
  }

  @media (max-width: 768px) {
    .banner-image {
      height: 60px;
      width: 90%;
    }

    .nav-link {
      font-size: 12px;
      padding: 5px 8px;
    }

    .btn {
      font-size: 14px;
      padding: 6px 12px;
    }

    .friend-links {
      gap: 3px;
    }

    .friend-links a {
      font-size: 12px;
      padding: 3px 6px;
    }

    .link-separator {
      font-size: 12px;
    }

    .footer-text {
      font-size: 12px;
    }
  }
</style>
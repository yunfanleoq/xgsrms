<template>
  <div id="app" class="portal-shell">
    <header class="mast">
      <div class="mast-bar">
        <RouterLink :to="{ name: 'homeHome' }" class="brand">
          <img src="@/assets/images/xgs_logo_.png" alt="中国科学院信息工程研究所人才招聘" class="brand-word" />
          <img src="@/assets/images/xgs_logo01.png" alt="信工所标识" class="brand-seal" />
        </RouterLink>
        <div class="header-actions">
          <div v-if="!isLoggedIn" class="auth-buttons">
            <button type="button" @click="goToLoginPage" class="text-action">登录</button>
            <span class="action-split" aria-hidden="true"></span>
            <button type="button" @click="goToRegisterPage" class="text-action text-action--strong">注册</button>
          </div>
          <div v-else class="user-section">
            <RouterLink v-if="managerEntry" :to="{ name: 'homeHome' }" class="home-btn" :class="{ 'home-btn--on': isHome }">首页</RouterLink>
            <button
              v-if="managerEntry"
              type="button"
              class="workbench-btn"
              :class="{ 'workbench-btn--on': inWorkbench }"
              @click="goToUserCenter"
            >
              工作台
            </button>
            <div ref="accountRef" class="account" :class="{ open: accountOpen }">
              <button type="button" class="account-trigger" aria-haspopup="menu" :aria-expanded="accountOpen" @click="accountOpen = !accountOpen">
                <span class="account-name">{{ loggedInUser?.realname || loggedInUser?.username }}</span>
                <span class="account-caret" aria-hidden="true"></span>
              </button>
              <div v-if="accountOpen" class="account-menu" role="menu">
                <button type="button" role="menuitem" @click="goCenter">个人中心</button>
                <button type="button" role="menuitem" class="account-menu__leave" @click="logout">退出登录</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <nav v-if="!hidePortalNav" class="portal-nav" aria-label="门户导航">
        <RouterLink :to="{ name: 'homeHome' }" class="nav-link">首页</RouterLink>
        <RouterLink
          :to="{ name: 'homeTalent' }"
          class="nav-link"
          :class="{ 'router-link-active': talentSectionActive && route.name !== 'homeTalent' }"
        >人才引进</RouterLink>
        <RouterLink :to="{ name: 'homeCareer' }" class="nav-link">职业路径</RouterLink>
        <RouterLink :to="{ name: 'homeTraining' }" class="nav-link">培训发展</RouterLink>
        <RouterLink :to="{ name: 'homeSalary' }" class="nav-link">薪酬福利</RouterLink>
        <RouterLink :to="{ name: 'contactUs' }" class="nav-link">联系我们</RouterLink>
      </nav>
    </header>

    <main class="portal-main" :class="{ 'portal-main--home': isHome }">
      <RouterView />
    </main>

    <footer class="portal-foot">
      <div class="friend-links">
        <a href="https://www.cas.cn" target="_blank" rel="noopener noreferrer">中国科学院</a>
        <a href="http://www.nsfc.gov.cn/" target="_blank" rel="noopener noreferrer">国家自然科学基金</a>
        <a href="https://www.casjob.com/" target="_blank" rel="noopener noreferrer">中科人才网</a>
        <a href="http://www.iie.cas.cn/" target="_blank" rel="noopener noreferrer">中科院信工所</a>
      </div>
      <p class="footer-text">© {{ currentYear }} 中国科学院信息工程研究所人才招聘管理系统</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
  import { useRoute, useRouter } from 'vue-router';
  import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
  import { useUserStore } from '@/store/modules/user';
  import { ROLE_DEPARTMENT, ROLE_HR, workPagesForRoles } from './mine/workPages';
  const route = useRoute();
  const router = useRouter();
  const userStore = useUserStore();

  const isHome = computed(() => route.name === 'homeHome');
  const inWorkbench = computed(() => String(route.name || '').startsWith('work'));
  const hidePortalNav = computed(() => inWorkbench.value || route.name === 'homeCenter');

  // 岗位列表和应聘详情从「人才引进」进入，顶栏仍高亮这一项
  const talentSectionActive = computed(() =>
    ['homeTalent', 'homeTalentApply', 'homePositions', 'positionDetail'].includes(String(route.name)),
  );

  const managerEntry = computed(() => {
    const roles = (userStore.getRoleList || []) as string[];
    if (!roles.includes(ROLE_DEPARTMENT) && !roles.includes(ROLE_HR)) {
      return '';
    }
    return workPagesForRoles(roles)[0]?.name || '';
  });

  const accountOpen = ref(false);
  const accountRef = ref<HTMLElement | null>(null);

  const isLoggedIn = computed(() => !!userStore.userInfo?.username);
  const loggedInUser = computed(() => userStore.userInfo);

  function closeAccountOnOutside(event: MouseEvent) {
    if (!accountRef.value?.contains(event.target as Node)) {
      accountOpen.value = false;
    }
  }

  function closeAccountOnEscape(event: KeyboardEvent) {
    if (event.key === 'Escape') {
      accountOpen.value = false;
    }
  }

  onMounted(() => {
    document.addEventListener('click', closeAccountOnOutside);
    document.addEventListener('keydown', closeAccountOnEscape);
    userStore.getUserInfoAction().catch((e) => {
      console.warn('首页获取用户信息失败（可忽略：未登录或 token 失效）', e);
    });
  });

  onBeforeUnmount(() => {
    document.removeEventListener('click', closeAccountOnOutside);
    document.removeEventListener('keydown', closeAccountOnEscape);
  });

  const currentYear = computed(() => new Date().getFullYear());

  const logout = async () => {
    accountOpen.value = false;
    await userStore.logout(false);
    router.push({ name: 'homeHome' });
  };

  const goToUserCenter = () => {
    router.push({ name: managerEntry.value || 'homeCenter' });
  };

  const goCenter = () => {
    accountOpen.value = false;
    router.push({ name: 'homeCenter' });
  };

  const goToLoginPage = () => {
    router.push({ name: 'Login' });
  };

  const goToRegisterPage = () => {
    router.push({ name: 'Login', query: { tab: 'register' } });
  };
</script>

<style scoped>
  .portal-shell {
    width: 100%;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: #f7f7f7;
    color: #333333;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
  }

  .mast {
    background: #fff;
    position: sticky;
    top: 0;
    z-index: 1000;
    border-bottom: 1px solid #e6e6e6;
  }

  .mast-bar {
    width: min(1280px, calc(100% - 80px));
    margin: 0 auto;
    min-height: 52px;
    padding: 6px 0 0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 24px;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 14px;
    min-width: 0;
    text-decoration: none;
  }

  .brand-word {
    height: 40px;
    width: auto;
    max-width: min(420px, 46vw);
    object-fit: contain;
    object-position: left center;
  }

  .brand-seal {
    height: 40px;
    width: auto;
    object-fit: contain;
  }

  .header-actions {
    flex: none;
  }

  .auth-buttons,
  .user-section {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .text-action {
    border: 0;
    background: transparent;
    padding: 0;
    color: #999999;
    font-size: 14px;
    font-weight: 400;
    letter-spacing: 0;
    cursor: pointer;
  }

  .text-action--strong {
    color: #999999;
    font-weight: 400;
  }

  .text-action:hover,
  .text-action--current {
    color: #004098;
  }

  .action-split {
    width: 1px;
    height: 12px;
    background: #e0e0e0;
  }

  .home-btn {
    display: inline-flex;
    align-items: center;
    height: 32px;
    padding: 0 14px;
    border: 1px solid #d0d7e2;
    border-radius: 4px;
    background: #fff;
    color: #004098;
    font-size: 14px;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-decoration: none;
  }

  .home-btn:hover,
  .home-btn--on {
    border-color: #004098;
    background: #f3f6fb;
  }

  .workbench-btn {
    height: 32px;
    padding: 0 16px;
    border: 1px solid #004098;
    border-radius: 4px;
    background: #004098;
    color: #fff;
    font-size: 14px;
    font-weight: 600;
    letter-spacing: 0.06em;
    cursor: pointer;
  }

  .workbench-btn:hover,
  .workbench-btn--on {
    background: #003067;
    border-color: #003067;
  }

  .account {
    position: relative;
    z-index: 3;
  }

  .account-trigger {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    max-width: 9em;
    height: 32px;
    padding: 0 2px 0 4px;
    border: 0;
    background: transparent;
    color: #333333;
    font-size: 14px;
    cursor: pointer;
  }

  .account-name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .account-caret {
    width: 0;
    height: 0;
    border-left: 4px solid transparent;
    border-right: 4px solid transparent;
    border-top: 5px solid #666666;
  }

  .account.open .account-caret {
    transform: rotate(180deg);
  }

  .account-menu {
    position: absolute;
    top: calc(100% + 8px);
    right: 0;
    z-index: 3;
    min-width: 148px;
    padding: 6px;
    background: #fff;
    border: 1px solid #e6e6e6;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0, 32, 64, 0.12);
  }

  .account-menu button {
    display: block;
    width: 100%;
    padding: 8px 12px;
    border: 0;
    border-radius: 4px;
    background: transparent;
    color: #333333;
    font-size: 14px;
    text-align: left;
    cursor: pointer;
  }

  .account-menu button:hover {
    background: #f3f6fa;
    color: #004098;
  }

  .account-menu__leave {
    color: #666666;
  }

  .portal-nav {
    position: relative;
    z-index: 1;
    width: min(1280px, calc(100% - 80px));
    margin: 0 auto;
    min-height: 36px;
    display: flex;
    align-items: stretch;
    justify-content: flex-end;
    gap: 4px;
  }

  .nav-link {
    position: relative;
    color: #444444;
    text-decoration: none;
    font-size: 16px;
    font-weight: 400;
    letter-spacing: 0;
    padding: 8px 16px 10px;
    white-space: nowrap;
  }

  .nav-link:hover {
    color: #004098;
  }

  .nav-link.router-link-active {
    color: #004098;
    font-weight: 400;
  }

  .nav-link.router-link-active::after {
    content: '';
    position: absolute;
    left: 16px;
    right: 16px;
    bottom: 0;
    height: 2px;
    background: #004098;
  }

  .portal-main {
    flex: 1;
    padding: 28px 20px 48px;
    background: #f7f7f7;
  }

  .portal-main--home {
    padding: 0;
    background: #fff;
  }

  .portal-foot {
    background: #10243f;
    color: rgba(255, 255, 255, 0.8);
    min-height: 220px;
    padding: 56px 24px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .friend-links {
    width: min(1280px, calc(100% - 80px));
    margin: 0 auto 20px;
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 12px 36px;
  }

  .friend-links a {
    color: rgba(255, 255, 255, 0.8);
    text-decoration: none;
    font-size: 14px;
    letter-spacing: 0;
  }

  .friend-links a:hover {
    color: #fff;
  }

  .footer-text {
    margin: 0;
    text-align: center;
    font-size: 13px;
    letter-spacing: 0;
    color: rgba(255, 255, 255, 0.8);
  }

  @media (max-width: 1024px) {
    .mast-bar,
    .portal-nav,
    .friend-links {
      width: min(1280px, calc(100% - 32px));
    }

    .brand-word,
    .brand-seal {
      height: 36px;
    }

    .portal-nav {
      justify-content: flex-start;
      overflow-x: auto;
    }

    .nav-link {
      font-size: 15px;
      padding: 8px 12px 10px;
    }
  }

  @media (max-width: 720px) {
    .mast-bar {
      align-items: flex-start;
      padding-top: 10px;
    }

    .brand {
      flex-direction: column;
      align-items: flex-start;
      gap: 6px;
    }

    .brand-seal {
      display: none;
    }
  }
</style>

<style>
  /* 管理端把 html / #app 锁在一屏里。门户页需要整页往下排。 */
  html:has(.portal-shell),
  html:has(.portal-shell) body,
  html:has(.portal-shell) #app {
    height: auto !important;
    min-height: 100%;
    overflow: auto !important;
  }

  /* 内页与首页同一栏宽、同一套标题和纸色卡片 */
  .portal-page {
    width: min(1280px, calc(100% - 40px));
    margin: 0 auto;
    color: #333333;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
  }

  .portal-page .page-head h1 {
    margin: 0 0 18px;
    font-size: 32px;
    font-weight: 700;
    line-height: 1.3;
    color: #333333;
    letter-spacing: 0;
  }

  .portal-page .page-card {
    background: #fff;
    border: 1px solid #e6e6e6;
    padding: 28px 32px 36px;
  }

  .portal-mine {
    overflow-x: auto;
  }

  @media (max-width: 720px) {
    .portal-page .page-head h1 {
      font-size: 26px;
    }

    .portal-page .page-card {
      padding: 20px 16px 28px;
    }
  }
</style>

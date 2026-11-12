<template>
  <nav ref="navRef" class="mine-nav" aria-label="工作台">
    <template v-for="node in nodes" :key="node.key">
      <RouterLink v-if="node.kind === 'link'" :to="{ name: node.name }" class="mine-link">{{ node.label }}</RouterLink>
      <div v-else class="drop-menu" :class="{ open: openKey === node.key }">
        <button
          type="button"
          class="mine-link drop-trigger"
          :class="{ 'router-link-active': node.items.some((item) => item.name === route.name) }"
          aria-haspopup="menu"
          :aria-expanded="openKey === node.key"
          @click="toggle(node.key)"
        >
          {{ node.label }}
          <span class="caret" aria-hidden="true"></span>
        </button>
        <div v-if="openKey === node.key" class="drop-panel" role="menu">
          <RouterLink v-for="item in node.items" :key="item.name" :to="{ name: item.name }" class="drop-item" role="menuitem" @click="openKey = ''">{{ item.label }}</RouterLink>
        </div>
      </div>
    </template>
  </nav>
</template>

<script setup lang="ts">
  import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import { useUserStore } from '@/store/modules/user';
  import { ROLE_APPLICANT, ROLE_DEPARTMENT, ROLE_HR, workPagesForRoles } from './workPages';

  type NavLink = { kind: 'link'; key: string; name: string; label: string };
  type NavMenu = { kind: 'menu'; key: string; label: string; items: { name: string; label: string }[] };

  const userStore = useUserStore();
  const route = useRoute();
  const openKey = ref('');
  const navRef = ref<HTMLElement | null>(null);

  const nodes = computed(() => {
    const roles = (userStore.getRoleList || []) as string[];
    const manager = roles.includes(ROLE_DEPARTMENT) || roles.includes(ROLE_HR);
    const applicant = !manager || roles.includes(ROLE_APPLICANT);
    const flat: { name: string; label: string; menu?: string }[] = [];
    if (!manager) {
      flat.push({ name: 'homeCenter', label: '个人中心' });
    }
    if (applicant) {
      flat.push(
        { name: 'homeResume', label: '我的简历' },
        { name: 'homeApply', label: '我的应聘' },
        { name: 'homeInterview', label: '我的面试' },
        { name: 'homeFavorite', label: '我的收藏' },
      );
    }
    const work = workPagesForRoles(roles);
    for (const item of work.filter((page) => page.group !== 'site')) {
      flat.push({ name: item.name, label: item.label, menu: item.menu });
    }
    const result: (NavLink | NavMenu)[] = [];
    const seen = new Set<string>();
    for (const item of flat) {
      if (!item.menu) {
        result.push({ kind: 'link', key: item.name, name: item.name, label: item.label });
        continue;
      }
      if (seen.has(item.menu)) {
        continue;
      }
      seen.add(item.menu);
      const items = flat.filter((page) => page.menu === item.menu).map((page) => ({ name: page.name, label: page.label }));
      if (items.length < 2) {
        result.push({ kind: 'link', key: items[0].name, name: items[0].name, label: items[0].label });
      } else {
        result.push({ kind: 'menu', key: item.menu, label: item.menu, items });
      }
    }
    const site = work.filter((page) => page.group === 'site').map((page) => ({ name: page.name, label: page.label }));
    if (site.length) {
      result.push({ kind: 'menu', key: 'site', label: '前台维护', items: site });
    }
    return result;
  });

  function toggle(key: string) {
    openKey.value = openKey.value === key ? '' : key;
  }

  function closeOnOutside(event: MouseEvent) {
    if (!navRef.value?.contains(event.target as Node)) {
      openKey.value = '';
    }
  }

  function closeOnEscape(event: KeyboardEvent) {
    if (event.key === 'Escape') {
      openKey.value = '';
    }
  }

  onMounted(() => {
    document.addEventListener('click', closeOnOutside);
    document.addEventListener('keydown', closeOnEscape);
  });

  onBeforeUnmount(() => {
    document.removeEventListener('click', closeOnOutside);
    document.removeEventListener('keydown', closeOnEscape);
  });
</script>

<style scoped>
  .mine-nav {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 4px 0;
    margin: 0 0 18px;
    border-bottom: 1px solid #e6e6e6;
  }

  .mine-link {
    position: relative;
    padding: 8px 16px 10px;
    border: 0;
    background: transparent;
    color: #444444;
    font-size: 15px;
    text-decoration: none;
    cursor: pointer;
  }

  .mine-link:hover,
  .mine-link.router-link-active {
    color: #004098;
  }

  .mine-link.router-link-active::after {
    content: '';
    position: absolute;
    left: 16px;
    right: 16px;
    bottom: 0;
    height: 2px;
    background: #004098;
  }

  .drop-menu {
    position: relative;
  }

  .drop-trigger {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }

  .caret {
    width: 0;
    height: 0;
    border-left: 4px solid transparent;
    border-right: 4px solid transparent;
    border-top: 5px solid currentColor;
  }

  .drop-menu.open .caret {
    transform: rotate(180deg);
  }

  .drop-panel {
    position: absolute;
    top: calc(100% + 6px);
    left: 8px;
    z-index: 20;
    min-width: 148px;
    padding: 6px;
    background: #fff;
    border: 1px solid #e6e6e6;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0, 32, 64, 0.12);
  }

  .drop-item {
    display: block;
    padding: 8px 12px;
    border-radius: 4px;
    color: #333333;
    font-size: 14px;
    text-decoration: none;
  }

  .drop-item:hover,
  .drop-item.router-link-active {
    background: #f3f6fa;
    color: #004098;
  }
</style>

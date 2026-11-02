<template>
  <div id="careerApp">
    <main>
      <aside class="sidebar">
        <ul>
          <li
            v-for="item in menuList"
            :key="item.value"
            :class="{ active: item.value === currentType }"
            @click="handleTypeChange(item.value)"
          >
            {{ item.text }}
          </li>
        </ul>
      </aside>
      <section class="content">
        <h2>{{ currentType }}</h2>
        <div class="content-wrapper top-border">
          <component :is="currentComponent" />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts" name="homeCareer">
  import { ref, onMounted, markRaw, shallowRef } from 'vue';
  import GangweiFazhanlu from '/@/views/home/category/gangweifazhanlu.vue';

  const menuList = ref<{ text: string; value: string; component: any }[]>([
    { text: '职业路径', value: '职业路径', component: markRaw(GangweiFazhanlu) },
  ]);

  const currentType = ref('职业路径');
  const currentComponent = shallowRef(GangweiFazhanlu);

  const handleTypeChange = (newType: string) => {
    currentType.value = newType;
    const selectedMenu = menuList.value.find((item) => item.value === newType);
    if (selectedMenu) {
      currentComponent.value = selectedMenu.component;
    }
  };

  onMounted(() => {
    handleTypeChange(currentType.value);
  });
</script>

<style>
  @import '/@/design/xgs_webstyle.css';
  @import '/@/design/bootstrap.css';
</style>
<style scoped>
  #careerApp {
    display: flex;
    flex-direction: column;
    font-family: 'Source Han Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif;
    background-color: #f4f7fb;
    color: #1f2a44;
  }

  main {
    display: flex;
    flex: 1;
    padding: 24px;
    gap: 20px;
  }

  .sidebar {
    width: 240px;
    background: #fff;
    padding: 20px 16px;
    box-shadow: 0 8px 24px rgba(15, 40, 80, 0.08);
    border-radius: 10px;
    border-top: 3px solid #0b2f6b;
  }

  .sidebar ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .sidebar li {
    margin: 8px 0;
    padding: 14px 16px;
    cursor: pointer;
    transition: all 0.25s ease;
    border-radius: 8px;
    font-weight: 600;
  }

  .sidebar li:hover {
    background: #eef4ff;
    color: #0b2f6b;
  }

  .sidebar li.active {
    background: linear-gradient(90deg, #0b2f6b, #1a4a9a);
    color: #fff;
  }

  .content {
    flex: 1;
    background: #fff;
    padding: 28px 32px;
    border-radius: 10px;
    box-shadow: 0 8px 24px rgba(15, 40, 80, 0.08);
  }

  .content h2 {
    margin: 0 0 16px;
    font-size: 24px;
    color: #0b2f6b;
    font-weight: 700;
  }

  .top-border {
    border-top: 1px solid #e6ebf5;
    padding-top: 18px;
  }
</style>

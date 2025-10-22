<template>
  <div id="ygqkApp">
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

        <!-- 内容区域 -->
        <div class="content-wrapper top-border">
          <!-- 动态组件 -->
          <component :is="currentComponent" />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts" name="homeYgqk">
  import { ref, computed, onMounted, markRaw, shallowRef } from 'vue';
  import RencaiGaikuang from '/@/views/home/category/rencaigaikuang.vue';
  import GangweiFazhanlu from '/@/views/home/category/gangweifazhanlu.vue';
  import XinchouFuli from '/@/views/home/category/xinchoufuli.vue';

  // --- 状态定义 ---
  const menuList = ref<{ text: string; value: string; component: any }[]>([
    { text: '人才概况', value: '人才概况', component: markRaw(RencaiGaikuang) },
    { text: '岗位发展路径', value: '岗位发展路径', component: markRaw(GangweiFazhanlu) },
    { text: '薪酬福利情况', value: '薪酬福利情况', component: markRaw(XinchouFuli) }
  ]);
  
  const currentType = ref('人才概况');
  const currentComponent = shallowRef(RencaiGaikuang);

  // --- 事件处理 ---
  const handleTypeChange = (newType: string) => {
    currentType.value = newType;
    // 根据选择的类型设置当前组件
    const selectedMenu = menuList.value.find(item => item.value === newType);
    if (selectedMenu) {
      currentComponent.value = selectedMenu.component;
    }
  };

  // 初始化
  onMounted(() => {
    handleTypeChange(currentType.value);
  });
</script>

<style>
  @import '/@/design/xgs_webstyle.css';
  @import '/@/design/bootstrap.css';
</style>
<style scoped>
  #ygqkApp {
    display: flex;
    flex-direction: column;
    font-family: Arial, sans-serif;
    background-color: #f4f4f9;
    color: #333;
  }

  main {
    display: flex;
    flex: 1;
    padding: 20px;
  }

  .sidebar {
    width: 250px;
    background-color: #fff;
    padding: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    margin-right: 20px;
  }

  .sidebar ul {
    list-style: none;
    padding: 0;
  }

  .sidebar li {
    margin: 10px 0;
    padding: 10px;
    cursor: pointer;
    transition: background-color 0.3s;
  }

  .sidebar li:hover {
    background-color: #f0f0f0;
  }

  .sidebar li.active {
    background-color: #3d54a7;
    color: #fff;
  }

  .content {
    flex: 1;
    background-color: #fff;
    padding: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  }

  .content h2 {
    margin-bottom: 20px;
    color: #3d54a7;
  }

  .top-border {
    border-top: 1px solid #3d54a7;
    padding: 20px 0;
  }

  .content-wrapper {
    padding: 20px;
  }

  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
  }

  .pagination button {
    margin: 0 10px;
    padding: 5px 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    background-color: #f9f9f9;
    cursor: pointer;
  }

  .pagination button:disabled {
    background-color: #ddd;
    cursor: not-allowed;
  }
</style>

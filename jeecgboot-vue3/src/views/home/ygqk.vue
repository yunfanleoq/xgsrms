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
      border-radius: 8px;
    }
  
    .sidebar ul {
      list-style: none;
      padding: 0;
    }
  
    .sidebar li {
      margin: 12px 0;
      padding: 12px 15px;
      cursor: pointer;
      transition: all 0.3s ease;
      border-radius: 6px;
      border-left: 3px solid transparent;
      font-weight: 500;
      position: relative;
      overflow: hidden;
    }
  
    .sidebar li:hover {
      background-color: #f0f4ff;
      border-left-color: #7fa5e0;
      transform: translateX(5px);
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
    }
  
    .sidebar li.active {
      background-color: #3d54a7;
      color: #fff;
      border-left-color: #fff;
      font-weight: bold;
      box-shadow: 0 3px 8px rgba(61, 84, 167, 0.3);
    }
  
    .sidebar li.active::after {
      content: "";
      position: absolute;
      top: 0;
      right: 0;
      height: 100%;
      width: 5px;
      background-color: #fff;
      animation: pulse 1.5s infinite;
    }
  
    @keyframes pulse {
      0% { opacity: 0.6; }
      50% { opacity: 1; }
      100% { opacity: 0.6; }
    }
  
    .content {
      flex: 1;
      background-color: #fff;
      padding: 20px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      border-radius: 8px;
      transition: all 0.3s ease;
    }
  
    .content h2 {
      margin-bottom: 20px;
      color: #3d54a7;
      padding-bottom: 10px;
      border-bottom: 2px solid #f0f4ff;
      font-weight: bold;
      transition: color 0.3s ease;
    }
  
    .top-border {
      border-top: 1px solid #3d54a7;
      padding: 20px 0;
      animation: fadeIn 0.5s ease;
    }
  
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(10px); }
      to { opacity: 1; transform: translateY(0); }
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
  
<template>
  <div class="category-content">
    <div class="intro">
      <p>
        加入研究所，意味着进入一个持续学习和成长的环境。我们关注职工在不同发展阶段的能力提升，从入职启航、专业精进到管理能力提升，持续提供与岗位职责、重大任务相衔接的学习支持，帮助职工不断完善能力结构、提升综合素养，增强持续发展的能力，促进人力资本升值。
      </p>
      <p>
        研究所鼓励职工持续深造，支持符合条件的在职攻读博士学位及公派出国学习、访学交流，不断拓展学术视野、提升专业能力。
      </p>
      <p>
        我们希望，每一位职工都能在参与科技创新、承担重要任务的过程中持续积累、持续成长，让个人成长与研究所事业发展双向赋能。
      </p>
    </div>

    <div class="module-grid">
      <div v-for="item in modules" :key="item.title" class="module-card">
        <div class="module-title">{{ item.title }}</div>
        <ul>
          <li v-for="tag in item.items" :key="tag">{{ tag }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  const modules = [
    {
      title: '新人成长',
      items: ['入所认知', '岗位融入', '科研素养', '安全规范'],
    },
    {
      title: '专业精进',
      items: ['前沿技术研修', '科研项目管理', '业务能力提升', '安全合规培训'],
    },
    {
      title: '管理赋能',
      items: ['科研团队管理', '干部履职提升', '科研支撑能力', '综合管理素养'],
    },
    {
      title: '继续深造',
      items: ['在职学历提升', '公派出国学习', '访学交流', '学术视野拓展'],
    },
  ];
</script>

<style scoped>
  .category-content {
    color: #333333;
    line-height: 1.8;
  }

  .intro p {
    margin: 0 0 14px;
    font-size: 15px;
    text-align: justify;
  }

  .module-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin-top: 24px;
  }

  .module-card {
    border: 1px solid #e6e6e6;
    border-radius: 0;
    padding: 18px 20px;
    background: #fff;
  }

  .module-card:nth-child(even) {
    background: #f7f7f7;
  }

  .module-title {
    font-size: 18px;
    font-weight: 700;
    color: #333333;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #e6e6e6;
  }

  ul {
    margin: 0;
    padding-left: 18px;
  }

  li {
    margin: 6px 0;
  }

  @media (max-width: 900px) {
    .module-grid {
      grid-template-columns: 1fr;
    }
  }
</style>

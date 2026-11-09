<template>
  <div class="training">
    <div class="prose">
      <p class="lead">
        加入研究所，意味着进入一个持续学习和成长的环境。我们关注职工在不同发展阶段的能力提升，从入职启航、专业精进到管理能力提升，持续提供与岗位职责、重大任务相衔接的学习支持，帮助职工不断完善能力结构、提升综合素养，增强持续发展的能力，促进人力资本升值。
      </p>
      <p>
        研究所鼓励职工持续深造，支持符合条件的在职攻读博士学位及公派出国学习、访学交流，不断拓展学术视野、提升专业能力。
      </p>
      <p>
        我们希望，每一位职工都能在参与科技创新、承担重要任务的过程中持续积累、持续成长，让个人成长与研究所事业发展双向赋能。
      </p>
    </div>

    <ol class="path">
      <li v-for="(item, index) in modules" :key="item.title" class="paper">
        <p class="step" aria-hidden="true">{{ String(index + 1).padStart(2, '0') }}</p>
        <h2>{{ item.title }}</h2>
        <ul>
          <li v-for="tag in item.items" :key="tag">{{ tag }}</li>
        </ul>
      </li>
    </ol>
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
  .training {
    --brand: #004098;
    --brand-deep: #003067;
    --ink: #1a1a1a;
    --body: #333333;
    --paper: #fffcf7;
    --edge: #e4ddd2;
    --rule: #efeae3;
    color: var(--body);
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
  }

  .prose {
    max-width: none;
  }

  .prose p {
    margin: 0 0 14px;
    font-size: 16px;
    line-height: 1.75;
    text-align: justify;
    text-justify: inter-character;
  }

  .prose .lead {
    margin-bottom: 18px;
    font-family: 'Songti SC', 'STSong', 'Songti TC', 'PingFang SC', serif;
    font-size: 18px;
    line-height: 1.8;
    color: var(--ink);
  }

  .path {
    list-style: none;
    margin: 40px 0 0;
    padding: 0;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 22px 24px;
  }

  .paper {
    min-width: 0;
    background: var(--paper);
    border: 1px solid var(--edge);
    border-top: 3px solid var(--brand);
    padding: 22px 26px 8px;
    transition: border-top-color 200ms ease;
  }

  .paper:hover {
    border-top-color: var(--brand-deep);
  }

  .step {
    position: relative;
    margin: 0 0 8px;
    font-family: 'Songti SC', 'STSong', 'Songti TC', 'PingFang SC', serif;
    font-size: 13px;
    line-height: 1;
    letter-spacing: 0.16em;
    color: var(--brand);
  }

  .step::before {
    content: '';
    position: absolute;
    top: 50%;
    right: calc(100% + 16px);
    width: 10px;
    height: 1px;
    background: var(--brand);
    transform: translateY(-50%);
  }

  h2 {
    margin: 0 0 16px;
    font-family: 'Songti SC', 'STSong', 'Songti TC', 'PingFang SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
    color: var(--ink);
  }

  ul {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  ul li {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0;
    padding: 12px 0;
    border-top: 1px solid var(--rule);
    font-size: 15px;
    line-height: 1.5;
    color: var(--body);
  }

  ul li::before {
    content: '';
    flex: none;
    width: 10px;
    height: 1px;
    background: var(--brand);
  }

  @media (max-width: 800px) {
    .path {
      grid-template-columns: 1fr;
      margin-top: 28px;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .paper {
      transition: none;
    }
  }
</style>

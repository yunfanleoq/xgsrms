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
        <div class="paper-head">
          <div class="paper-copy">
            <p class="step" aria-hidden="true">{{ String(index + 1).padStart(2, '0') }}</p>
            <h2>{{ item.title }}</h2>
          </div>
          <svg
            v-if="index === 0"
            class="paper-mark"
            viewBox="0 0 64 64"
            fill="none"
            stroke="currentColor"
            stroke-width="1.6"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
          >
            <path d="M14 50h36" />
            <path d="M32 50V31" />
            <path d="M32 40c-8-.4-13.2-5.6-15.2-12.4 7.6.7 12 4.6 15.2 12.4z" />
            <path d="M32 35.5c7.6-.6 13.4-6 15.6-13.2-7.8.9-12.6 5.2-15.6 13.2z" />
            <path d="M32 31c1.4-3.4 1.1-6.6-.2-9.2" />
          </svg>
          <svg
            v-else-if="index === 1"
            class="paper-mark"
            viewBox="0 0 64 64"
            fill="none"
            stroke="currentColor"
            stroke-width="1.6"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
          >
            <path d="M10 50h44" />
            <path d="M14 50V38h10" />
            <path d="M24 50V30h10" />
            <path d="M34 50V20h12" />
            <path d="M50 12v7M46.5 15.5h7" />
            <path d="M48 13.5 52.5 18M52.5 13.5 48 18" />
          </svg>
          <svg
            v-else-if="index === 2"
            class="paper-mark"
            viewBox="0 0 64 64"
            fill="none"
            stroke="currentColor"
            stroke-width="1.6"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
          >
            <circle cx="32" cy="15" r="6" />
            <path d="M32 21v6" />
            <path d="M15 27h34" />
            <path d="M15 27v6M32 27v6M49 27v6" />
            <circle cx="15" cy="41" r="5.5" />
            <circle cx="32" cy="41" r="5.5" />
            <circle cx="49" cy="41" r="5.5" />
          </svg>
          <svg
            v-else
            class="paper-mark"
            viewBox="0 0 64 64"
            fill="none"
            stroke="currentColor"
            stroke-width="1.6"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
          >
            <path d="M40 13.5a11 11 0 1 0 3.2 7.6" />
            <path d="M40 9.2V15h5.8" />
            <path d="M12 32h14.5c3 1.5 5 4.2 5.5 7.6V54c-1.5-2.5-4-3.8-7.4-3.8H12V32z" />
            <path d="M52 32H37.5c-3 1.5-5 4.2-5.5 7.6V54c1.5-2.5 4-3.8 7.4-3.8H52V32z" />
            <path d="M32 40.2V54" />
          </svg>
        </div>
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
      title: '终身学习',
      items: ['在职学历提升', '公派出国学习', '访学交流', '学术视野拓展'],
    },
  ];
</script>

<style scoped>
  .training {
    --ink: #1c2430;
    --brand: #004098;
    --brand-deep: #003067;
    --muted: #3d4a5c;
    --line: #d7dee8;
    --paper: #f4f7fb;
    --paper-warm: #f6f4ef;
    color: var(--ink);
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 16px;
    line-height: 1.75;
  }

  .prose {
    max-width: none;
  }

  .prose p {
    margin: 0 0 14px;
    color: #243040;
    font-size: 16px;
    line-height: 1.75;
  }

  .prose p:last-child {
    margin-bottom: 0;
  }

  .prose .lead {
    margin-bottom: 14px;
    color: #243040;
    font-size: 18px;
    line-height: 1.85;
  }

  .path {
    list-style: none;
    margin: 48px 0 0;
    padding: 0;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
  }

  .paper {
    min-width: 0;
    background: #fff;
    border: 1px solid #e1e6ee;
    border-top: 3px solid var(--brand);
    padding: 22px 20px 18px;
    transition: border-color 200ms ease, background-color 200ms ease;
  }

  .paper:nth-child(2) {
    background: var(--paper);
  }

  .paper:nth-child(3) {
    background: var(--paper-warm);
    border-top-color: var(--brand-deep);
  }

  .paper:hover {
    border-color: var(--brand-deep);
    background: #fff;
  }

  .paper-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px 18px;
    margin-bottom: 12px;
  }

  .paper-copy {
    min-width: 0;
  }

  .paper-mark {
    flex: none;
    width: 56px;
    height: 56px;
    color: var(--brand);
  }

  .paper:hover .paper-mark {
    color: var(--brand-deep);
  }

  .step {
    margin: 0 0 12px;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 14px;
    line-height: 1;
    letter-spacing: 0.16em;
    color: var(--brand);
  }

  h2 {
    margin: 0;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
    color: #1a1a1a;
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
    border-top: 1px solid #e6ebf2;
    font-size: 16px;
    line-height: 1.7;
    color: #243040;
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
      margin-top: 32px;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .paper {
      transition: none;
    }
  }
</style>

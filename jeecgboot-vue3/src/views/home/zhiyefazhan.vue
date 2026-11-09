<template>
  <div class="portal-page career-page">
    <header class="page-head">
      <h1>职业路径</h1>
    </header>
    <div class="page-card">
      <p class="principle">
        <span class="principle-label">原则与导向：</span>
        <span class="principle-point">横向分类</span>
        <span class="principle-sep">·</span>
        <span class="principle-point">纵向分层</span>
        <span class="principle-sep">·</span>
        <span class="principle-point">贡献导向</span>
      </p>

      <section class="overview" aria-labelledby="career-overview">
        <h2 id="career-overview">发展路径总揽</h2>
        <div class="overview-body">
          <p class="lead">
            研究所坚持“以事定岗、以岗定人”，构建“横向分类、纵向分层”的岗位管理体系，为职工铺设清晰、多元的成长通道。
          </p>
          <p>
            横向看，岗位分为科技类、支撑类、管理类三大类别；科技类细分为基础科学研究、技术科学研究、教学与研究、工程技术研究四种类型，职工可结合专业所长选择发展方向。
          </p>
          <p>
            纵向看，专业技术岗位由初级、中级、副高级到正高级逐级设置，管理岗位设三级至九级职员，形成“横向分类—纵向分层”的岗位分类评价标准，晋升公开透明，以创新价值、能力、贡献为导向。
          </p>
          <p>
            研究所持续完善“使命导向”的科研成果（科研贡献）分类认定，破除“四唯”，让科研产出与贡献认定名实相符。在这里，每一步成长都有阶梯。
          </p>
        </div>
      </section>

      <section class="block" aria-labelledby="career-families">
        <h2 id="career-families">一、岗位体系总览</h2>
        <div class="family-grid">
          <article v-for="(item, index) in categories" :key="item.title" class="family-card">
            <span class="family-index" aria-hidden="true">{{ pad(index + 1) }}</span>
            <h3>{{ item.title }}</h3>
            <p class="family-desc">{{ item.desc }}</p>
            <p class="series">执行系列：{{ item.series }}</p>
          </article>
        </div>
      </section>

      <section class="block" aria-labelledby="career-science">
        <h2 id="career-science">二、科技与支撑岗位发展阶梯</h2>

        <h3 id="career-types">（一）四种类型</h3>
        <ol class="type-steps">
          <li v-for="(item, index) in scienceTypes" :key="item.name">
            <span class="step-no" aria-hidden="true">{{ pad(index + 1) }}</span>
            <p class="type-line">
              <span class="type-name">{{ item.name }}</span
              ><span class="type-join">——</span><span class="type-desc">{{ item.desc }}</span>
            </p>
          </li>
        </ol>

        <h3 id="career-tech-ladder">（二）纵向发展阶梯</h3>
        <div class="ladder-scroll">
          <table class="ladder" aria-labelledby="career-tech-ladder">
            <thead>
              <tr>
                <th scope="col">层级</th>
                <th scope="col">专业技术等级</th>
                <th scope="col">岗位名称</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in techLadder" :key="row.level">
                <th scope="row" class="ladder-level" data-label="层级">{{ row.level }}</th>
                <td data-label="专业技术等级">{{ row.grade }}</td>
                <td data-label="岗位名称">{{ row.name }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="block" aria-labelledby="career-manage">
        <h2 id="career-manage">三、管理岗位发展阶梯</h2>
        <div class="ladder-scroll">
          <table class="ladder" aria-labelledby="career-manage">
            <thead>
              <tr>
                <th scope="col">层级</th>
                <th scope="col">职员等级</th>
                <th scope="col">任职条件 / 说明</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in manageLadder" :key="row.grade">
                <th scope="row" class="ladder-level" data-label="层级">{{ row.level }}</th>
                <td data-label="职员等级">{{ row.grade }}</td>
                <td data-label="任职条件 / 说明">{{ row.cond }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <p class="note">说明：管理岗位职员层级以院现行职级表为准，后续如有调整将同步更新。</p>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts" name="homeCareer">
  const categories = [
    {
      title: '科技类岗位',
      desc: '面向科研主责主业，聚焦网络空间安全领域的基础理论、前沿技术与重大工程攻关。',
      series: '自然科学研究系列、工程技术系列',
    },
    {
      title: '支撑类岗位',
      desc: '围绕国家和行业重大需求，开展工程技术项目集成建设、重要系统业务支撑和运维支撑；以及战略研究、科学传播、图书资料、学报编辑等工作。',
      series: '工程技术系列、图书资料系列、编辑出版系列等；兼有管理职责的执行职员系列',
    },
    {
      title: '管理类岗位',
      desc: '面向科研、人员、财务等职能管理，以管理促提升，向管理要效益。',
      series: '职员系列（三级至九级职员）',
    },
  ];

  const scienceTypes = [
    { name: '基础科学研究', desc: '面向世界科技前沿，探索原始创新' },
    { name: '技术科学研究', desc: '面向国家战略需求，推动技术创新' },
    { name: '教学与研究', desc: '落实科教融合，承担教育教学' },
    { name: '工程技术研究', desc: '突破关键核心技术，牵头重大工程' },
  ];

  const techLadder = [
    { level: '初级', grade: '十一级', name: '助理工程师一级' },
    { level: '中级', grade: '八 ~ 十级', name: '助理研究员 / 工程师' },
    { level: '副高级', grade: '五 ~ 七级', name: '副研究员 / 高级工程师' },
    { level: '正高级', grade: '一 ~ 四级', name: '研究员 / 正高级工程师' },
  ];

  const manageLadder = [
    { level: '初级', grade: '九级职员', cond: '硕士研究生毕业（入职起点）' },
    { level: '中级', grade: '八级职员', cond: '任九级职员满 1 年' },
    { level: '中级', grade: '七级职员', cond: '任八级职员满 3 年，或博士研究生毕业' },
    { level: '高级', grade: '六级职员', cond: '任七级职员满 3 年' },
    { level: '高级', grade: '五级职员', cond: '任六级职员满 3 年' },
    { level: '高级', grade: '四级职员', cond: '任五级职员累计满 10 年且任处长累计满 5 年' },
  ];

  function pad(n: number) {
    return String(n).padStart(2, '0');
  }
</script>

<style scoped>
  .career-page {
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

  .career-page .page-head h1,
  .career-page h2,
  .family-card h3,
  .type-name,
  .ladder-level,
  .step-no,
  .family-index,
  .principle-label {
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
  }

  .career-page .page-head h1 {
    margin-bottom: 20px;
    color: #1a1a1a;
    font-size: 36px;
    font-weight: 600;
    letter-spacing: 0.02em;
    line-height: 1.3;
  }

  .career-page .page-card {
    padding: 36px 40px 48px;
    border-color: #e2e6ec;
  }

  .principle {
    display: flex;
    flex-wrap: wrap;
    align-items: baseline;
    gap: 0 2px;
    margin: 0 0 36px;
    padding: 14px 18px;
    background: var(--paper);
    border-left: 3px solid var(--brand);
    color: var(--ink);
    font-size: 15px;
    letter-spacing: 0.04em;
    line-height: 1.6;
  }

  .principle-label {
    margin-right: 8px;
    color: var(--brand);
    font-size: 16px;
    font-weight: 600;
  }

  .principle-point {
    padding: 0 2px;
  }

  .principle-sep {
    color: var(--brand);
    padding: 0 6px;
  }

  .overview {
    display: block;
  }

  .overview h2,
  .block > h2 {
    margin: 0;
    color: #1a1a1a;
    font-size: 26px;
    font-weight: 600;
    line-height: 1.35;
  }

  .overview h2 {
    margin-bottom: 16px;
  }

  .overview-body p {
    margin: 0 0 14px;
    color: #243040;
  }

  .overview-body p:last-child {
    margin-bottom: 0;
  }

  .lead {
    color: var(--ink);
    font-size: 18px;
    line-height: 1.85;
  }

  .block {
    margin-top: 48px;
  }

  .block > h2 {
    position: relative;
    margin-bottom: 22px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--line);
  }

  .block > h2::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -1px;
    width: 56px;
    height: 2px;
    background: var(--brand);
  }

  .block > h3 {
    margin: 28px 0 14px;
    color: var(--brand);
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 18px;
    font-weight: 600;
    line-height: 1.4;
  }

  .block > h3:first-of-type {
    margin-top: 0;
  }

  .family-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
  }

  .family-card {
    display: flex;
    flex-direction: column;
    min-width: 0;
    min-height: 100%;
    padding: 22px 20px 18px;
    border: 1px solid #e1e6ee;
    border-top: 3px solid var(--brand);
    background: #fff;
    transition: border-color 200ms ease, background-color 200ms ease;
  }

  .family-card:nth-child(2) {
    background: var(--paper);
  }

  .family-card:nth-child(3) {
    background: var(--paper-warm);
    border-top-color: var(--brand-deep);
  }

  .family-card:hover {
    border-color: var(--brand-deep);
    background: #fff;
  }

  .family-index {
    display: block;
    margin-bottom: 12px;
    color: var(--brand);
    font-size: 14px;
    letter-spacing: 0.16em;
    line-height: 1;
  }

  .family-card h3 {
    margin: 0 0 12px;
    color: #1a1a1a;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
  }

  .family-desc {
    margin: 0;
    flex: 1;
    color: #243040;
  }

  .series {
    margin: 18px 0 0;
    padding-top: 12px;
    border-top: 1px solid #e1e6ee;
    color: var(--muted);
    font-size: 14px;
    line-height: 1.65;
  }

  .type-steps {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px 16px;
    margin: 0 0 8px;
    padding: 0;
    list-style: none;
  }

  .type-steps li {
    display: grid;
    grid-template-columns: 44px minmax(0, 1fr);
    gap: 8px 12px;
    align-items: start;
    min-width: 0;
    padding: 16px 18px;
    border: 1px solid #e6ebf2;
    background: #f7f8fa;
  }

  .step-no {
    color: var(--brand);
    font-size: 20px;
    line-height: 1.35;
  }

  .type-line {
    margin: 0;
    min-width: 0;
    color: #243040;
    line-height: 1.7;
  }

  .type-name {
    color: #1a1a1a;
    font-size: 18px;
    font-weight: 600;
  }

  .type-join {
    color: var(--brand);
    padding: 0 0.15em;
  }

  .ladder-scroll {
    min-width: 0;
  }

  .ladder {
    width: 100%;
    border-collapse: collapse;
    margin: 0;
  }

  .ladder th,
  .ladder td {
    padding: 16px 14px;
    text-align: left;
    vertical-align: middle;
    font-weight: 400;
    line-height: 1.6;
  }

  .ladder thead th {
    padding-bottom: 10px;
    border-bottom: 1px solid var(--line);
    color: var(--brand);
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', sans-serif;
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.06em;
  }

  .ladder tbody th,
  .ladder tbody td {
    border-bottom: 1px solid #e6ebf2;
    background: transparent;
    transition: background-color 200ms ease;
  }

  .ladder tbody tr:hover th,
  .ladder tbody tr:hover td {
    background: var(--paper);
  }

  .ladder thead th:first-child,
  .ladder tbody th.ladder-level {
    padding-left: 40px;
  }

  .ladder-level {
    position: relative;
    width: 18%;
    color: #1a1a1a;
    font-size: 17px;
    font-weight: 600;
  }

  .ladder-level::before {
    content: '';
    position: absolute;
    left: 13px;
    top: 0;
    bottom: 0;
    width: 1px;
    background: #c5d2e4;
  }

  .ladder tbody tr:first-child .ladder-level::before {
    top: 50%;
  }

  .ladder tbody tr:last-child .ladder-level::before {
    bottom: 50%;
  }

  .ladder-level::after {
    content: '';
    position: absolute;
    left: 9px;
    top: 50%;
    width: 9px;
    height: 9px;
    margin-top: -5px;
    border: 2px solid var(--brand);
    border-radius: 50%;
    background: #fff;
  }

  .ladder td:nth-child(2) {
    width: 24%;
    color: var(--ink);
  }

  .ladder td:nth-child(3) {
    color: #243040;
  }

  .note {
    margin: 16px 0 0;
    padding: 12px 16px;
    border-left: 2px solid #c5d2e4;
    background: var(--paper);
    color: var(--muted);
    font-size: 14px;
    line-height: 1.7;
  }

  @media (max-width: 960px) {
    .family-grid,
    .type-steps {
      grid-template-columns: 1fr;
    }

    .career-page .page-card {
      padding: 24px 18px 32px;
    }
  }

  @media (max-width: 720px) {
    .career-page .page-head h1 {
      font-size: 28px;
      letter-spacing: 0.04em;
    }

    .overview h2,
    .block > h2 {
      font-size: 22px;
    }

    .ladder-scroll {
      position: relative;
    }

    .ladder thead {
      position: absolute;
      width: 1px;
      height: 1px;
      padding: 0;
      margin: -1px;
      overflow: hidden;
      clip: rect(0, 0, 0, 0);
      white-space: nowrap;
      border: 0;
    }

    .ladder,
    .ladder tbody,
    .ladder tr,
    .ladder th,
    .ladder td {
      display: block;
      width: 100%;
    }

    .ladder tbody tr {
      padding: 8px 0 12px;
      border-bottom: 1px solid #e6ebf2;
    }

    .ladder tbody th,
    .ladder tbody td {
      padding: 6px 0;
      border-bottom: 0;
    }

    .ladder tbody td::before {
      content: attr(data-label);
      display: block;
      margin-bottom: 2px;
      color: var(--brand);
      font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', sans-serif;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.06em;
    }

    .ladder tbody th.ladder-level,
    .ladder tbody tr:first-child .ladder-level,
    .ladder tbody tr:last-child .ladder-level {
      padding-left: 0;
      font-size: 17px;
    }

    .ladder-level::before,
    .ladder tbody tr:first-child .ladder-level::before,
    .ladder tbody tr:last-child .ladder-level::before {
      content: attr(data-label);
      position: static;
      display: block;
      width: auto;
      height: auto;
      margin: 0 0 2px;
      background: none;
      color: var(--brand);
      font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', sans-serif;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.06em;
    }

    .ladder-level::after {
      display: none;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .family-card,
    .ladder tbody th,
    .ladder tbody td {
      transition: none;
    }
  }
</style>

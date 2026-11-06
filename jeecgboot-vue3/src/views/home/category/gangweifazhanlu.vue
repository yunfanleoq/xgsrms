<template>
  <div class="category-content">
    <div class="hero-line">原则与导向：横向分类 · 纵向分层 · 贡献导向</div>

    <section class="section">
      <h3>发展路径总揽</h3>
      <p>
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
    </section>

    <section class="section">
      <h3>一、岗位体系总览</h3>
      <div class="card-grid">
        <div class="info-card" v-for="item in categories" :key="item.title">
          <div class="card-title">{{ item.title }}</div>
          <p>{{ item.desc }}</p>
          <div class="card-meta">执行系列：{{ item.series }}</div>
        </div>
      </div>
    </section>

    <section class="section">
      <h3>二、科技与支撑岗位发展阶梯</h3>
      <h4>（一）四种类型</h4>
      <ul class="type-list">
        <li v-for="t in scienceTypes" :key="t.name"><strong>{{ t.name }}</strong>——{{ t.desc }}</li>
      </ul>

      <h4>（二）纵向发展阶梯</h4>
      <table class="ladder-table">
        <thead>
          <tr>
            <th>层级</th>
            <th>专业技术等级</th>
            <th>岗位名称</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in techLadder" :key="row.level">
            <td>{{ row.level }}</td>
            <td>{{ row.grade }}</td>
            <td>{{ row.name }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="section">
      <h3>三、管理岗位发展阶梯</h3>
      <table class="ladder-table">
        <thead>
          <tr>
            <th>层级</th>
            <th>职员等级</th>
            <th>任职条件 / 说明</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, idx) in manageLadder" :key="idx">
            <td>{{ row.level }}</td>
            <td>{{ row.grade }}</td>
            <td>{{ row.cond }}</td>
          </tr>
        </tbody>
      </table>
      <p class="note">说明：管理岗位职员层级以院现行职级表为准，后续如有调整将同步更新。</p>
    </section>
  </div>
</template>

<script setup lang="ts">
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
</script>

<style scoped>
  .category-content {
    color: #333333;
    line-height: 1.8;
  }

  .hero-line {
    display: block;
    color: #004098;
    font-size: 14px;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e6e6e6;
  }

  .section {
    margin-bottom: 28px;
  }

  h3 {
    margin: 0 0 12px;
    color: #333333;
    font-size: 20px;
  }

  h4 {
    margin: 16px 0 8px;
    color: #004098;
    font-size: 16px;
  }

  p {
    margin: 0 0 10px;
    text-align: justify;
  }

  .card-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 14px;
  }

  .info-card {
    border: 1px solid #e6e6e6;
    border-radius: 0;
    padding: 16px;
    background: #fff;
  }

  .info-card:nth-child(even) {
    background: #f7f7f7;
  }

  .card-title {
    font-weight: 700;
    color: #333333;
    margin-bottom: 8px;
  }

  .card-meta {
    margin-top: 10px;
    font-size: 13px;
    color: #8a8a8a;
  }

  .type-list {
    margin: 0;
    padding-left: 18px;
  }

  .ladder-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 8px;
  }

  .ladder-table th,
  .ladder-table td {
    border: 1px solid #e6e6e6;
    padding: 10px 12px;
    text-align: left;
  }

  .ladder-table th {
    background: #10243f;
    color: #fff;
    font-weight: 400;
  }

  .ladder-table tr:nth-child(even) td {
    background: #f7f7f7;
  }

  .note {
    margin-top: 10px;
    color: #8a8a8a;
    font-size: 13px;
  }

  @media (max-width: 960px) {
    .card-grid {
      grid-template-columns: 1fr;
    }
  }
</style>

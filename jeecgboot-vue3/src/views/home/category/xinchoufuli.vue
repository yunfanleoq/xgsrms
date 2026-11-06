<template>
  <div class="category-content">
    <div class="text-content">
      <p>
        研究所实行事业单位岗位绩效工资制度，基本工资、津补贴按国家政策发放；绩效工资分配突出贡献导向，实施动态调整与统筹调控，激励承担重大国家任务。职工享受“五险两金”、补充医疗保险、北京普惠健康保、年度体检、子女团险优惠等职工福利，除此之外还享有职工疗养、工会重疾互助等工会福利。
      </p>
    </div>

    <div class="section">
      <h3>研究所岗位绩效工资结构</h3>
      <div class="image-container">
        <img src="@/assets/images/xc_structure_20261029.png" alt="研究所岗位绩效工资结构" class="content-image" />
        <p class="image-caption">研究所岗位绩效工资结构</p>
      </div>
    </div>

    <div class="section">
      <h3>福利保障要点</h3>
      <div class="welfare-grid">
        <div class="welfare-item" v-for="item in welfare" :key="item">{{ item }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  const welfare = [
    '五险两金',
    '补充医疗保险',
    '北京普惠健康保',
    '年度体检',
    '子女团险优惠',
    '职工疗养',
    '工会重疾互助',
  ];
</script>

<style scoped>
  .category-content {
    color: #333333;
    line-height: 1.8;
  }

  h3 {
    margin: 0 0 12px;
    color: #333333;
    font-size: 18px;
    font-weight: 700;
  }

  .text-content p {
    margin: 0 0 12px;
    text-align: justify;
    font-size: 15px;
  }

  .section {
    margin-top: 28px;
  }

  .image-container {
    text-align: center;
    background: #f7f7f7;
    border: 1px solid #e6e6e6;
    border-radius: 0;
    padding: 20px;
  }

  .content-image {
    max-width: 100%;
    height: auto;
  }

  .image-caption {
    margin: 10px 0 0;
    color: #8a8a8a;
    font-size: 13px;
  }

  .welfare-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 12px;
  }

  .welfare-item {
    background: #fff;
    border: 1px solid #e6e6e6;
    border-radius: 0;
    padding: 14px 12px;
    text-align: center;
    font-weight: 400;
    color: #333333;
  }

  @media (max-width: 900px) {
    .welfare-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
</style>

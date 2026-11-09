<template>
  <div class="category-content">
    <p class="lead">
      研究所实行事业单位岗位绩效工资制度，基本工资、津补贴按国家政策发放；绩效工资分配突出贡献导向，实施动态调整与统筹调控，激励承担重大国家任务。职工享受“五险两金”、补充医疗保险、北京普惠健康保、年度体检、子女团险优惠等职工福利，除此之外还享有职工疗养、工会重疾互助等工会福利。
    </p>

    <section class="block" aria-labelledby="wage-heading">
      <h2 id="wage-heading" class="section-title">研究所岗位绩效工资结构</h2>
      <div class="wage-stage">
        <div class="paper-stack">
          <div class="paper-back" aria-hidden="true"></div>
          <div class="paper-sheet">
            <div class="formula-row">
              <div class="formula-lead">
                <span class="formula-name">年度工资</span>
                <span class="formula-eq">=</span>
              </div>

              <div class="term-group term-group--base">
                <div class="term" aria-describedby="wage-note-base">基本工资+津补贴</div>
                <span class="stem" aria-hidden="true"></span>
                <p id="wage-note-base" class="callout">根据国家、中科院、研究所<br />相关标准确定</p>
              </div>

              <span class="joiner">+</span>

              <div class="term-group term-group--perf">
                <div class="term" aria-describedby="wage-note-perf">基础性绩效+月奖励性绩效+年奖励性绩效</div>
                <span class="stem" aria-hidden="true"></span>
                <p id="wage-note-perf" class="callout">
                  依据研究所《收入分配管理办法》<br />《岗位绩效工资实施办法》相关标准
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="block" aria-labelledby="benefit-heading">
      <h2 id="benefit-heading" class="section-title">福利保障要点</h2>
      <ul class="benefit-grid">
        <li v-for="item in welfare" :key="item">{{ item }}</li>
      </ul>
    </section>
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
    color: #1c1c1c;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
  }

  .lead {
    margin: 0;
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 17px;
    font-weight: 400;
    line-height: 1.9;
    letter-spacing: 0.01em;
    text-align: left;
    text-wrap: pretty;
    color: #1c1c1c;
  }

  .block {
    margin-top: 40px;
  }

  .section-title {
    display: inline-block;
    margin: 0 0 18px;
    padding-bottom: 8px;
    border-bottom: 2px solid #004098;
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
    letter-spacing: 0.04em;
    color: #141414;
  }

  .wage-stage {
    padding: 18px 8px 8px;
    perspective: 1400px;
    container-type: inline-size;
    container-name: wage;
  }

  .paper-stack {
    position: relative;
    transform: rotateX(2.4deg) rotateY(-1.6deg);
    transform-style: preserve-3d;
    animation: wage-settle 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .paper-back {
    position: absolute;
    z-index: 0;
    inset: 16px -14px -16px 18px;
    background: #e6eef7;
    border: 1px solid #d3dfec;
    box-shadow: 0 16px 28px rgba(0, 36, 84, 0.07);
  }

  .paper-sheet {
    position: relative;
    z-index: 1;
    padding: 32px 36px 26px;
    background: linear-gradient(180deg, #ffffff 0%, #fbfcfe 100%);
    border: 1px solid #e2e8f0;
    border-top: 3px solid #004098;
    box-shadow:
      0 1px 0 rgba(255, 255, 255, 0.9) inset,
      0 22px 40px rgba(0, 32, 76, 0.1),
      0 3px 8px rgba(0, 32, 76, 0.05);
  }

  .formula-row {
    display: grid;
    width: max-content;
    max-width: 100%;
    margin-inline: auto;
    grid-template-columns: auto auto auto auto;
    grid-template-rows: auto auto auto;
    column-gap: 14px;
    align-items: center;
  }

  .formula-lead {
    grid-column: 1;
    grid-row: 1;
    display: flex;
    align-items: baseline;
    gap: 0.35em;
    padding-right: 4px;
    white-space: nowrap;
  }

  .formula-name {
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 22px;
    font-weight: 700;
    line-height: 1.2;
    color: #8e1b1b;
  }

  .formula-eq {
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 28px;
    font-weight: 700;
    line-height: 1;
    color: #8e1b1b;
    animation: wage-rise 0.34s cubic-bezier(0.22, 1, 0.36, 1) 0.08s both;
  }

  .term-group {
    display: grid;
    grid-template-rows: subgrid;
    grid-row: 1 / span 3;
    justify-items: center;
    align-items: center;
    min-width: 0;
  }

  .term-group--base {
    grid-column: 2;
    color: #004098;
  }

  .term-group--perf {
    grid-column: 4;
    color: #8e1b1b;
  }

  .joiner {
    grid-column: 3;
    grid-row: 1;
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1;
    color: #1c1c1c;
    animation: wage-rise 0.34s cubic-bezier(0.22, 1, 0.36, 1) 0.4s both;
  }

  .term {
    box-sizing: border-box;
    margin: 0;
    padding: 12px 18px;
    border: 1.5px dashed transparent;
    background: #fff;
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 18px;
    font-weight: 600;
    line-height: 1.45;
    text-align: center;
    white-space: nowrap;
    color: #1a1a1a;
    user-select: text;
  }

  .term-group--base .term {
    border-color: #004098;
    background: #f4f7fb;
    animation: wage-rise 0.4s cubic-bezier(0.22, 1, 0.36, 1) 0.3s both;
  }

  .term-group--perf .term {
    border-color: #8e1b1b;
    background: #fbf6f6;
    animation: wage-rise 0.4s cubic-bezier(0.22, 1, 0.36, 1) 0.46s both;
  }

  .stem {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 10px;
    height: 26px;
  }

  .stem::before {
    content: '';
    width: 1.5px;
    flex: 1;
    background: currentColor;
  }

  .stem::after {
    content: '';
    width: 0;
    height: 0;
    border-left: 4px solid transparent;
    border-right: 4px solid transparent;
    border-top: 6px solid currentColor;
  }

  .term-group--base .stem,
  .term-group--base .callout {
    animation: wage-rise 0.4s cubic-bezier(0.22, 1, 0.36, 1) 0.7s both;
  }

  .term-group--perf .stem,
  .term-group--perf .callout {
    animation: wage-rise 0.4s cubic-bezier(0.22, 1, 0.36, 1) 0.84s both;
  }

  .callout {
    margin: 8px 0 0;
    max-width: 22em;
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 15px;
    font-weight: 700;
    line-height: 1.55;
    text-align: center;
    color: inherit;
  }

  .benefit-grid {
    list-style: none;
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 14px 16px;
    margin: 4px 0 0;
    padding: 0;
  }

  .benefit-grid li {
    margin: 0;
    padding: 20px 18px 18px;
    background: #f4f6f8;
    color: #1c2836;
    font-size: 15px;
    font-weight: 500;
    line-height: 1.5;
    letter-spacing: 0.02em;
  }

  .benefit-grid li::before {
    content: '';
    display: block;
    width: 22px;
    height: 2px;
    margin-bottom: 12px;
    background: #004098;
  }

  @keyframes wage-settle {
    from {
      opacity: 0.65;
      transform: rotateX(7deg) rotateY(-4.5deg) translateY(12px);
    }
    to {
      opacity: 1;
      transform: rotateX(2.4deg) rotateY(-1.6deg) translateY(0);
    }
  }

  @keyframes wage-rise {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @media (max-width: 900px) {
    .benefit-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @container wage (max-width: 840px) {
    .wage-stage {
      perspective: none;
    }

    .paper-stack {
      transform: none;
      animation: none;
    }

    .formula-row {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      width: 100%;
      gap: 0;
    }

    .formula-lead,
    .joiner {
      justify-content: center;
      padding: 4px 0;
    }

    .term-group {
      display: flex;
      flex-direction: column;
      grid-row: auto;
      grid-column: auto;
    }

    .term {
      width: 100%;
      white-space: normal;
    }

    .joiner {
      margin: 8px 0;
    }
  }

  @media (max-width: 880px) {
    .wage-stage {
      perspective: none;
      padding-inline: 0;
    }

    .paper-stack {
      transform: none;
      animation: none;
    }

    .paper-back {
      inset: 8px -4px -8px 8px;
    }

    .paper-sheet {
      padding: 22px 16px 18px;
    }

    .formula-row {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      width: 100%;
      gap: 0;
    }

    .formula-lead,
    .joiner {
      justify-content: center;
      padding: 4px 0;
    }

    .term-group {
      display: flex;
      flex-direction: column;
      grid-row: auto;
      grid-column: auto;
    }

    .term {
      width: 100%;
      white-space: normal;
    }

    .joiner {
      margin: 8px 0;
    }
  }

  @media (max-width: 520px) {
    .lead {
      font-size: 16px;
    }

    .section-title {
      font-size: 20px;
    }

    .formula-name {
      font-size: 20px;
    }

    .term {
      font-size: 16px;
    }

    .benefit-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .paper-stack,
    .formula-eq,
    .joiner,
    .term,
    .stem,
    .callout {
      animation: none !important;
    }
  }
</style>

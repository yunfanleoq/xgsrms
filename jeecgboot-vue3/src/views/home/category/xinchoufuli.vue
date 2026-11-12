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
        <li v-for="item in welfare" :key="item">
          <svg
            class="benefit-icon"
            viewBox="0 0 64 64"
            fill="none"
            stroke="currentColor"
            stroke-width="1.7"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
          >
            <g v-if="item === '五险两金'">
              <path d="M32 8 50 16v15c0 13-8 21-18 25-10-4-18-12-18-25V16L32 8z" />
              <circle cx="32" cy="28" r="4.5" />
              <path d="M23.5 43c1.4-6.2 4.4-9.2 8.5-9.2s7.1 3 8.5 9.2" />
            </g>
            <g v-else-if="item === '补充医疗保险'">
              <rect x="8" y="18" width="38" height="34" rx="3" />
              <path d="M27 28v16M19 36h16" />
              <circle cx="48" cy="16" r="8" />
              <path d="M48 12.5v7M44.5 16h7" />
            </g>
            <g v-else-if="item === '北京普惠健康保'">
              <path d="M12 26c0-10 8.5-16 20-16s20 6 20 16" />
              <path d="M12 26h40" />
              <path d="M22 14.2V26M32 10v16M42 14.2V26" />
              <path
                d="M32 56C32 56 20 46 20 37c0-5.5 4-9 8.2-9 2.2 0 3.6 1.2 3.8 2.8.2-1.6 1.6-2.8 3.8-2.8 4.2 0 8.2 3.5 8.2 9 0 9-12 19-12 19z"
              />
            </g>
            <g v-else-if="item === '年度体检'">
              <rect x="16" y="16" width="32" height="40" rx="3" />
              <rect x="26" y="10" width="12" height="10" rx="2" />
              <path d="M22 36h5l4-7 5 14 4-7h6" />
            </g>
            <g v-else-if="item === '子女团险优惠'">
              <path d="M32 6 44 11v7c0 6.2-4.4 10.6-12 13.2C24.4 28.6 20 24.2 20 18v-7L32 6z" />
              <circle cx="22" cy="40" r="4.5" />
              <path d="M13 58c.8-6.4 3.4-9.6 9-9.6s8.2 3.2 9 9.6" />
              <circle cx="45" cy="45" r="3.2" />
              <path d="M39 58c.5-4.4 2.3-6.6 6-6.6s5.5 2.2 6 6.6" />
            </g>
            <g v-else-if="item === '职工疗养'">
              <circle cx="32" cy="18" r="6" />
              <path d="M32 6v3.2M32 26.8V30M20 18h-3.2M47.2 18H44" />
              <path d="M23.2 9.2 25.6 11.6M40.8 9.2 38.4 11.6M23.2 26.8 25.6 24.4M40.8 26.8 38.4 24.4" />
              <path d="M4 52Q18 34 32 50Q46 34 60 50" />
              <path d="M10 58q10-8 18 0" />
            </g>
            <g v-else-if="item === '工会重疾互助'">
              <circle cx="14" cy="18" r="5" />
              <path d="M6 42c1-9 3.8-13 8-13s7 4 8 13" />
              <circle cx="50" cy="18" r="5" />
              <path d="M42 42c1-9 3.8-13 8-13s7 4 8 13" />
              <path
                d="M32 46C32 46 22 38 22 31.5 22 27.2 25.2 24 29 24c1.8 0 3 1.2 3 2.6 0-1.4 1.2-2.6 3-2.6 3.8 0 7 3.2 7 7.5C42 38 32 46 32 46z"
              />
            </g>
            <g v-else>
              <path d="M32 17Q23 6 20 16Q24 22 32 17" />
              <path d="M32 17Q41 6 44 16Q40 22 32 17" />
              <rect x="12" y="22" width="40" height="8" rx="1" />
              <rect x="14" y="30" width="36" height="24" rx="1.5" />
              <path d="M32 17v37" />
            </g>
          </svg>
          <span>{{ item }}</span>
        </li>
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
    '各种工会福利',
  ];
</script>

<style scoped>
  .category-content {
    color: #1c2430;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 16px;
    line-height: 1.75;
  }

  .lead {
    margin: 0;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 1.85;
    letter-spacing: 0;
    text-align: left;
    text-wrap: pretty;
    color: #243040;
  }

  .block {
    margin-top: 40px;
  }

  .section-title {
    display: inline-block;
    margin: 0 0 18px;
    padding-bottom: 8px;
    border-bottom: 2px solid #004098;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 26px;
    font-weight: 600;
    line-height: 1.35;
    letter-spacing: 0;
    color: #1a1a1a;
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
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
    color: #1a1a1a;
  }

  .formula-eq {
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
    color: #1a1a1a;
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
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
    color: #1a1a1a;
    animation: wage-rise 0.34s cubic-bezier(0.22, 1, 0.36, 1) 0.4s both;
  }

  .term {
    box-sizing: border-box;
    margin: 0;
    padding: 12px 18px;
    border: 1.5px dashed transparent;
    background: #fff;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 18px;
    font-weight: 600;
    line-height: 1.4;
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
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 14px;
    font-weight: 400;
    line-height: 1.7;
    text-align: center;
    color: #3d4a5c;
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
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin: 0;
    padding: 16px 16px 18px;
    background: #f4f6f8;
    color: #1a1a1a;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.35;
    letter-spacing: 0;
  }

  .benefit-icon {
    display: block;
    width: 44px;
    height: 44px;
    margin-bottom: 10px;
    color: #004098;
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

  @media (max-width: 720px) {
    .section-title {
      font-size: 22px;
    }
  }

  @media (max-width: 520px) {
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

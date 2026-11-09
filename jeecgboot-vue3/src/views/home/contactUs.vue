<template>
  <div class="portal-page contact-page">
    <article class="contact-sheet">
      <header class="contact-mast">
        <h1>联系我们</h1>
        <p class="contact-kicker">机构</p>
        <p class="contact-office">中国科学院信息工程研究所人事处</p>
      </header>

      <dl class="contact-facts">
        <div class="fact">
          <dt>地址</dt>
          <dd>北京市海淀区树村路19号</dd>
        </div>
        <div class="fact">
          <dt>邮编</dt>
          <dd class="fact-code">100085</dd>
        </div>
        <div class="fact fact--wide">
          <dt>电话</dt>
          <dd>
            <span class="fact-inline">
              <a class="contact-link" href="tel:+861082345076">+86-10-82345076</a><span class="contact-note">（谢绝任何形式的业务推销）</span>
            </span>
          </dd>
        </div>
        <div class="fact fact--wide">
          <dt>邮箱</dt>
          <dd>
            <a class="contact-link" href="mailto:zhaopin@iie.ac.cn">zhaopin@iie.ac.cn</a>
          </dd>
        </div>
      </dl>
    </article>
  </div>
</template>

<script setup lang="ts" name="contactUs"></script>

<style scoped>
  .contact-page {
    color: #1c2430;
  }

  .contact-sheet {
    background: #ffffff;
    border: 1px solid #e2e6ec;
    border-top: 4px solid #004098;
  }

  .contact-mast {
    padding: 48px 56px 12px;
  }

  .contact-page .contact-mast h1 {
    margin: 0;
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 40px;
    font-weight: 700;
    line-height: 1.25;
    letter-spacing: 0.12em;
    color: #142033;
  }

  .contact-kicker {
    margin: 36px 0 8px;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 15px;
    font-weight: 600;
    line-height: 1.5;
    letter-spacing: 0.18em;
    color: #004098;
  }

  .contact-office {
    margin: 0;
    max-width: 18em;
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-size: 28px;
    font-weight: 700;
    line-height: 1.45;
    color: #142033;
  }

  .contact-facts {
    display: grid;
    grid-template-columns: minmax(0, 1.5fr) minmax(220px, 0.7fr);
    gap: 0 48px;
    margin: 28px 0 0;
    padding: 0 56px 20px;
  }

  .fact {
    margin: 0;
    padding: 26px 0 28px;
    border-top: 1px solid #e4e8ee;
  }

  .fact--wide {
    grid-column: 1 / -1;
  }

  .fact dt {
    margin: 0 0 8px;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 15px;
    font-weight: 600;
    line-height: 1.5;
    letter-spacing: 0.14em;
    color: #3d4d63;
  }

  .fact dd {
    margin: 0;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 1.6;
    color: #1c2430;
  }

  .fact-code {
    font-variant-numeric: tabular-nums;
    letter-spacing: 0.04em;
  }

  .fact-inline {
    display: inline;
  }

  .contact-link {
    display: inline-flex;
    align-items: center;
    min-height: 44px;
    color: #004098;
    text-decoration: underline;
    text-underline-offset: 4px;
    text-decoration-thickness: 1px;
    cursor: pointer;
  }

  .contact-link:hover {
    color: #003067;
  }

  .contact-link:focus-visible {
    outline: 2px solid #004098;
    outline-offset: 3px;
  }

  .contact-note {
    color: #1c2430;
    font-size: 18px;
    line-height: 1.6;
  }

  @media (prefers-reduced-motion: no-preference) {
    .contact-link {
      transition: color 180ms ease;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .contact-link {
      transition: none;
    }
  }

  @media (max-width: 720px) {
    .contact-mast {
      padding: 32px 20px 4px;
    }

    .contact-page .contact-mast h1 {
      font-size: 32px;
      letter-spacing: 0.08em;
    }

    .contact-kicker {
      margin-top: 28px;
    }

    .contact-office {
      font-size: 22px;
    }

    .contact-facts {
      grid-template-columns: 1fr;
      gap: 0;
      margin-top: 20px;
      padding: 0 20px 8px;
    }

    .fact dd,
    .contact-note {
      font-size: 17px;
    }
  }
</style>

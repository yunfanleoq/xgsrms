<template>
  <div class="portal-page">
    <header class="page-head">
      <h1>联系我们</h1>
    </header>
    <div class="page-card contact-body">
      <dl class="contact-list">
        <div class="contact-row">
          <dt>机构</dt>
          <dd>中国科学院信息工程研究所人事处</dd>
        </div>
        <div class="contact-row">
          <dt>地址</dt>
          <dd>北京市海淀区树村路19号</dd>
        </div>
        <div class="contact-row">
          <dt>邮编</dt>
          <dd>100085</dd>
        </div>
        <div class="contact-row">
          <dt>电话</dt>
          <dd>
            <a href="tel:+861082345076">+86-10-82345076</a>
            <span class="contact-note">（谢绝任何形式的业务推销）</span>
          </dd>
        </div>
        <div class="contact-row">
          <dt>邮箱</dt>
          <dd>
            <a href="mailto:zhaopin@iie.ac.cn">zhaopin@iie.ac.cn</a>
          </dd>
        </div>
      </dl>
    </div>
  </div>
</template>

<script setup lang="ts" name="contactUs"></script>

<style scoped>
  .contact-body {
    color: #333333;
    font-size: 15px;
    line-height: 1.8;
  }

  .contact-list {
    margin: 0;
  }

  .contact-row {
    display: grid;
    grid-template-columns: 4em 1fr;
    column-gap: 12px;
    margin: 0 0 8px;
  }

  .contact-row:last-child {
    margin-bottom: 0;
  }

  dt,
  dd {
    margin: 0;
  }

  dt {
    font-weight: 700;
  }

  .contact-body a {
    color: #004098;
  }

  .contact-note {
    color: #666666;
  }
</style>

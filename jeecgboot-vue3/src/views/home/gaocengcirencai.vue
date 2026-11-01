<template>
  <div class="talent-page">
    <section class="hero">
      <img src="@/assets/images/talent_hero_20261029.png" alt="信息工程研究所诚邀海内外英才" class="hero-bg" />
      <div class="hero-overlay">
        <h1>信息工程研究所</h1>
        <p>诚邀海内外英才</p>
      </div>
    </section>

    <section class="tracks">
      <div v-for="track in tracks" :key="track.key" class="track-card">
        <h3>{{ track.title }}</h3>
        <div class="track-actions">
          <button class="btn-view" @click="openTrack(track)">查看</button>
          <button class="btn-apply" @click="goApply">投递</button>
        </div>
      </div>
    </section>

    <div class="main-grid">
      <section class="team-intro">
        <h2>信工所人才队伍</h2>
        <p>
          中国科学院信息工程研究所（以下简称信工所）是网络空间安全领域的“龙头所、核心所、总体所”，是从基础研究、高技术研发到工程化开发的长链条综合性研究所。信工所围绕国家发展战略和研究所科研布局，突出“高精尖缺”和干事导向，面向海内外长期招聘引进各类高水平科技创新人才。
        </p>
        <p>
          截至2025年12月，研究所共有职工1521人，其中正式聘用职工929人、在站博士后44人；正式职工中，硕士及以上学位的占93.22%，其中博士学位占41.01%。共有2人获杰青和国家级领军人才计划支持、有12人获国家“四小青”人才项目支持、29人获院级人才项目支持、13人获国务院特殊津贴等。2024年信工所荣获“网安人才培养基地”称号。
        </p>

        <h3>人才风采展示</h3>
        <div class="gallery">
          <img v-for="(img, idx) in gallery" :key="idx" :src="img" alt="人才风采" />
        </div>
      </section>

      <aside class="policy-side">
        <h2>人才政策资讯</h2>
        <div class="policy-group" v-for="group in policies" :key="group.title">
          <h4>{{ group.title }}</h4>
          <ul>
            <li v-for="item in group.items" :key="item.label">
              <a v-if="item.url" :href="item.url" target="_blank" rel="noopener noreferrer">{{ item.label }}</a>
              <span v-else>{{ item.label }}</span>
            </li>
          </ul>
        </div>
      </aside>
    </div>

    <section class="contact">
      <h2>联系我们</h2>
      <p>联系人：人力资源处，李老师</p>
      <p>联系方式：010-82345079，邮箱：lilin@iie.ac.cn</p>
    </section>

    <div v-if="activeTrack" class="modal-mask" @click.self="activeTrack = null">
      <div class="modal">
        <h3>{{ activeTrack.title }}</h3>
        <div class="modal-block">
          <h4>岗位职责</h4>
          <p>{{ activeTrack.duty }}</p>
        </div>
        <div class="modal-block">
          <h4>任职要求</h4>
          <p>{{ activeTrack.requirement }}</p>
        </div>
        <div class="modal-block">
          <h4>支持条件</h4>
          <p>{{ activeTrack.support }}</p>
        </div>
        <div class="modal-actions">
          <button class="btn-apply" @click="goApply">投递简历</button>
          <button class="btn-view" @click="activeTrack = null">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import gallery2 from '@/assets/images/talent_gallery_2.png';
  import gallery3 from '@/assets/images/talent_gallery_3.png';
  import gallery4 from '@/assets/images/talent_gallery_4.png';
  import gallery5 from '@/assets/images/talent_gallery_5.png';
  import gallery6 from '@/assets/images/talent_gallery_6.png';
  import gallery7 from '@/assets/images/talent_gallery_7.png';
  import gallery8 from '@/assets/images/talent_gallery_8.png';

  const router = useRouter();
  const gallery = [gallery2, gallery3, gallery4, gallery5, gallery6, gallery7, gallery8];

  type Track = {
    key: string;
    title: string;
    duty: string;
    requirement: string;
    support: string;
  };

  const tracks: Track[] = [
    {
      key: 'youth',
      title: '高层次青年人才',
      duty: '面向网络空间安全及相关交叉方向开展高水平研究，牵头或参与重大科研任务，推动成果转化与人才培养。',
      requirement: '一般应具有博士学位及相关领域研究经历，具备独立开展科研工作的能力，具体条件以正式发布岗位为准。',
      support: '提供具有竞争力的薪酬待遇、科研启动支持及相关人才政策配套，具体以研究所正式引进政策为准。',
    },
    {
      key: 'leader',
      title: '高层次领军人才',
      duty: '引领学科方向发展，组建并带领高水平创新团队，承担国家重大任务，产出标志性成果。',
      requirement: '在本领域具有显著学术影响力或重大工程贡献，具备团队组织和科研攻关能力，具体条件以正式发布岗位为准。',
      support: '在薪酬、科研经费、团队建设、住房安家等方面按高层次人才政策予以重点支持。',
    },
    {
      key: 'overseas',
      title: '海外优青',
      duty: '面向海外优秀青年学者，回国（来华）开展独立研究，服务国家战略与研究所科研布局。',
      requirement: '具有海外学习或工作经历，科研潜力突出，具体申报条件按国家/院相关计划及研究所要求执行。',
      support: '协助申报相关人才计划，并按政策给予相应配套支持。',
    },
  ];

  const activeTrack = ref<Track | null>(null);

  const openTrack = (track: Track) => {
    activeTrack.value = track;
  };

  const goApply = () => {
    router.push({ name: 'homePositions' });
  };

  const policies = [
    {
      title: '国家相关政策',
      items: [
        {
          label: '中共中央办公厅 国务院办公厅印发《关于进一步加强青年科技人才培养和使用的若干措施》',
          url: 'https://www.gov.cn/yaowen/liebiao/202308/content_6900452.htm',
        },
        {
          label: '中共中央办公厅 国务院办公厅印发《关于加强新时代高技能人才队伍建设的意见》',
          url: 'http://www.gov.cn/gongbao/content/2022/content_5719981.htm',
        },
        {
          label: '科技部等八部门印发《关于开展科技人才评价改革试点的工作方案》的通知',
          url: 'http://www.gov.cn/zhengce/zhengceku/2022-11/10/content_5725957.htm',
        },
        {
          label: '习近平出席中央人才工作会议并发表重要讲话',
          url: 'https://www.gov.cn/xinwen/2021-09/28/content_5639868.htm',
        },
      ],
    },
    {
      title: '院相关政策',
      items: [
        { label: '中国科学院召开2025年人才工作部署会议', url: 'https://www.cas.cn/' },
        { label: '2026年度中国科学院高层次人才国情院情研讨培训班在京举办', url: 'https://www.cas.cn/' },
        { label: '侯建国：一体推进教育科技人才发展', url: 'https://www.cas.cn/' },
      ],
    },
    {
      title: '研究所相关政策',
      items: [
        { label: '《高层次人才管理办法》（附件待上传）', url: '' },
        { label: '《领军人才及团队建设管理办法》（附件待上传）', url: '' },
      ],
    },
  ];
</script>

<style scoped>
  .talent-page {
    color: #24324d;
  }

  .hero {
    position: relative;
    height: 280px;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 20px;
  }

  .hero-bg {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .hero-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: linear-gradient(180deg, rgba(11, 47, 107, 0.35), rgba(11, 47, 107, 0.65));
    color: #fff;
    text-align: center;
  }

  .hero-overlay h1 {
    margin: 0;
    font-size: 36px;
    letter-spacing: 2px;
  }

  .hero-overlay p {
    margin: 10px 0 0;
    font-size: 22px;
  }

  .tracks {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 24px;
  }

  .track-card {
    background: #fff;
    border-radius: 10px;
    border: 1px solid #e4ebf7;
    padding: 22px 18px;
    text-align: center;
    box-shadow: 0 8px 20px rgba(15, 40, 80, 0.06);
  }

  .track-card h3 {
    margin: 0 0 16px;
    color: #0b2f6b;
  }

  .track-actions {
    display: flex;
    justify-content: center;
    gap: 10px;
  }

  .btn-view,
  .btn-apply {
    border: none;
    border-radius: 6px;
    padding: 8px 18px;
    cursor: pointer;
    font-weight: 600;
  }

  .btn-view {
    background: #eef4ff;
    color: #0b2f6b;
  }

  .btn-apply {
    background: #0b2f6b;
    color: #fff;
  }

  .main-grid {
    display: grid;
    grid-template-columns: 1.7fr 1fr;
    gap: 18px;
  }

  .team-intro,
  .policy-side,
  .contact {
    background: #fff;
    border-radius: 10px;
    border: 1px solid #e4ebf7;
    padding: 22px;
  }

  h2 {
    margin: 0 0 12px;
    color: #0b2f6b;
    font-size: 22px;
  }

  h3 {
    margin: 20px 0 10px;
    color: #0b2f6b;
  }

  h4 {
    margin: 0 0 8px;
    color: #1a4a9a;
  }

  p {
    margin: 0 0 12px;
    line-height: 1.8;
    text-align: justify;
  }

  .gallery {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 10px;
  }

  .gallery img {
    width: 100%;
    height: 110px;
    object-fit: cover;
    border-radius: 8px;
  }

  .policy-group {
    margin-bottom: 16px;
  }

  .policy-group ul {
    margin: 0;
    padding-left: 18px;
  }

  .policy-group li {
    margin: 6px 0;
    line-height: 1.6;
  }

  .policy-group a {
    color: #1a4a9a;
    text-decoration: none;
  }

  .policy-group a:hover {
    text-decoration: underline;
  }

  .contact {
    margin-top: 18px;
  }

  .modal-mask {
    position: fixed;
    inset: 0;
    background: rgba(10, 24, 48, 0.45);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2000;
  }

  .modal {
    width: min(640px, 92vw);
    background: #fff;
    border-radius: 12px;
    padding: 24px;
  }

  .modal-block {
    margin: 14px 0;
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 18px;
  }

  @media (max-width: 960px) {
    .tracks,
    .main-grid,
    .gallery {
      grid-template-columns: 1fr;
    }

    .gallery img {
      height: 160px;
    }
  }
</style>

<template>
  <div class="portal-page talent-page">
    <section class="hero">
      <img src="@/assets/images/talent_hero_campus.jpg" alt="中国科学院信息工程研究所" class="hero-bg" />
      <div class="hero-overlay">
        <h1>信息工程研究所</h1>
        <p>诚邀海内外英才</p>
      </div>
    </section>

    <section class="tracks">
      <div v-for="track in tracks" :key="track.key" class="track-card">
        <h3>{{ track.title }}</h3>
        <p class="track-official">{{ track.official }}</p>
        <div class="track-actions">
          <button class="btn-view" @click="openTrack(track)">查看</button>
          <button class="btn-apply" @click="goApply">投递</button>
        </div>
      </div>
    </section>

    <p class="source-note">
      任职要求与支持条件引自研究所官网
      <a :href="officialSource" target="_blank" rel="noopener noreferrer">《中国科学院信息工程研究所（国家级/院级）人才项目及岗位招聘启事（长期有效）》</a>。
      官网按人才项目分列，未单列岗位职责；本页三类栏目与官网栏目的对应关系写在各卡片标题下方。
    </p>

    <section class="benefit">
      <h2>岗位待遇</h2>
      <p>{{ benefitNote }}</p>
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
        <p class="gallery-note">人员尚待确认，照片与名单确定后再行展示。</p>
        <div class="gallery-placeholder" role="status">
          <span>人才风采</span>
          <span>待补充</span>
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
        <p class="modal-official">官网栏目：{{ activeTrack.official }}</p>
        <div class="modal-block">
          <h4>岗位职责</h4>
          <p>招聘学科与研究领域：{{ discipline }}</p>
          <p class="fine">官网启事未按项目单列岗位职责，以上为启事「一、招聘学科与研究领域」，三类岗位相同。</p>
        </div>
        <div class="modal-block">
          <h4>任职要求</h4>
          <ul>
            <li v-for="(line, idx) in activeTrack.requirement" :key="idx">{{ line }}</li>
          </ul>
        </div>
        <div class="modal-block">
          <h4>支持条件</h4>
          <ul>
            <li v-for="(line, idx) in activeTrack.support" :key="idx">{{ line }}</li>
          </ul>
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

  const router = useRouter();

  const officialSource = 'https://iie.cas.cn/yjdw/rczp/202512/t20251203_8021360.html';

  const discipline =
    '网络空间安全类，包括但不限于：密码理论与安全协议、信息智能处理、数据安全、通信与电磁安全、网络与系统技术、信息系统测评等。';

  const benefitNote =
    '工资福利待遇及合同管理按国家、中国科学院和研究所有关规定执行。所有人才项目均可解决北京市居民户口。';

  type Track = {
    key: string;
    title: string;
    official: string;
    requirement: string[];
    support: string[];
  };

  const tracks: Track[] = [
    {
      key: 'leader',
      title: '高层次领军人才',
      official: '国家级人才项目（正高三级专业技术岗位）',
      requirement: [
        '在国外著名高校、科研机构担任相当于副教授及以上职务、具有较高科研水平和较强科技创新能力，或者在国际知名企业担任高级职务的专业技术人才；',
        '原则上副教授及相当水平人员不超过45周岁，教授及相当水平人员不超过55周岁；',
        '具有博士学位；',
        '尚未全职回国（来华）工作，或者在国内工作不超过1年；引进后须辞去海外工作或在海外无工作，并全职回国或来华工作不少于3年。',
      ],
      support: [
        '正高三级岗位；',
        '研究所正高级平均工资2-4倍的薪酬；',
        '400-600万安居经费；',
        '一年4次往返探亲费用；',
        '最高可达1900万的科研启动经费；',
        '通过导师遴选，每年至少给予博（硕）士招生指标各1个；',
        '协助解决子女入托入学问题。',
      ],
    },
    {
      key: 'overseas',
      title: '海外优青',
      official: '海外优青（正高四级专业技术岗位）',
      requirement: [
        '不超过40周岁；',
        '具有博士学位；',
        '一般应在海外高校、科研机构、企业研发机构获得正式教学或者科研职位，且具有连续36个月以上工作经历；在海外取得博士学位且业绩特别突出的，可适当放宽工作年限要求；',
        '尚未全职回国（来华）工作，或者在国内工作不超过1年；获资助通知后须辞去海外工作或在海外无工作，并全职回国或来华工作不少于3年。',
      ],
      support: [
        '正高四级岗位；',
        '研究所正高级专业技术人员平均工资；',
        '200万元安居经费；',
        '最高可达900万的科研启动经费；',
        '通过导师遴选，每年至少给予博（硕）士招生指标各1个；',
        '协助解决子女入托入学问题。',
      ],
    },
    {
      key: 'youth',
      title: '高层次青年人才',
      official: '中国科学院人才项目（正高四级或副高一级专业技术岗位）',
      requirement: [
        '应达到研究所副高级（含）以上专业技术岗位优秀学术技术骨干水平',
        '不超过40周岁；',
        '具有博士学位；',
        '一般应在海外高校、科研机构、企业研发机构获得正式教学或者科研职位，且具有连续36个月以上工作经历；在海外取得博士学位且业绩特别突出的，可适当放宽工作年限要求；',
        '尚未全职回国（来华）工作，或者在国内工作不超过1年；获资助通知后须辞去海外工作或在海外无工作，并全职回国或来华工作不少于3年。',
      ],
      support: [
        '正高四级或副高一级岗位',
        '研究所正（副）高级专业技术人员平均工资',
        '200万元安居经费',
        '最高可达800万的科研启动经费',
        '通过导师遴选，每年至少给予博（硕）士招生指标各1个',
        '协助解决子女入托入学问题',
      ],
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
        { label: '《高层次人才管理办法》' },
        { label: '《领军人才及团队建设管理办法》' },
      ],
    },
  ];
</script>

<style scoped>
  .talent-page {
    color: #333333;
  }

  .hero {
    position: relative;
    height: 340px;
    border-radius: 0;
    overflow: hidden;
    margin-bottom: 20px;
    background: #10243f;
  }

  .hero-bg {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center 68%;
  }

  .hero-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: linear-gradient(to top, rgba(8, 16, 32, 0.55), rgba(8, 16, 32, 0.22));
    color: #fff;
    text-align: center;
  }

  .hero-overlay h1 {
    margin: 0;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 36px;
    font-weight: 700;
    letter-spacing: 0.08em;
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
    border-radius: 0;
    border: 1px solid #e6e6e6;
    padding: 22px 18px;
    text-align: center;
  }

  .track-card h3 {
    margin: 0 0 8px;
    color: #333333;
  }

  .track-official {
    margin: 0 0 16px;
    color: #666666;
    font-size: 13px;
    line-height: 1.5;
    text-align: center;
  }

  .source-note {
    margin: 0 0 18px;
    color: #666666;
    font-size: 13px;
    line-height: 1.7;
    text-align: left;
  }

  .source-note a {
    color: #004098;
    text-decoration: none;
  }

  .source-note a:hover {
    text-decoration: underline;
  }

  .track-actions {
    display: flex;
    justify-content: center;
    gap: 10px;
  }

  .btn-view,
  .btn-apply {
    border-radius: 0;
    padding: 8px 18px;
    cursor: pointer;
    font-weight: 400;
    font-size: 14px;
  }

  .btn-view {
    background: #fff;
    color: #004098;
    border: 1px solid #004098;
  }

  .btn-apply {
    background: #004098;
    color: #fff;
    border: 1px solid #004098;
  }

  .main-grid {
    display: grid;
    grid-template-columns: 1.7fr 1fr;
    gap: 18px;
  }

  .team-intro,
  .policy-side,
  .contact,
  .benefit {
    background: #fff;
    border-radius: 0;
    border: 1px solid #e6e6e6;
    padding: 22px;
  }

  .benefit {
    margin-bottom: 18px;
  }

  h2 {
    margin: 0 0 12px;
    color: #333333;
    font-size: 24px;
    font-weight: 700;
  }

  h3 {
    margin: 20px 0 10px;
    color: #333333;
  }

  h4 {
    margin: 0 0 8px;
    color: #004098;
  }

  p {
    margin: 0 0 12px;
    line-height: 1.8;
    text-align: justify;
  }

  .gallery-note {
    color: #666666;
    font-size: 14px;
  }

  .gallery-placeholder {
    min-height: 148px;
    border: 1px dashed #c8c8c8;
    background: #f7f7f7;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    color: #888888;
    letter-spacing: 0.08em;
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
    color: #004098;
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
    width: min(720px, 92vw);
    max-height: min(86vh, 820px);
    overflow: auto;
    background: #fff;
    border-radius: 0;
    border: 1px solid #e6e6e6;
    padding: 24px;
  }

  .modal-official,
  .fine {
    color: #666666;
    font-size: 13px;
    line-height: 1.6;
    text-align: left;
  }

  .modal ul {
    margin: 0;
    padding-left: 1.2em;
  }

  .modal li {
    margin: 6px 0;
    line-height: 1.7;
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
    .main-grid {
      grid-template-columns: 1fr;
    }

    .hero {
      height: 220px;
    }
  }
</style>

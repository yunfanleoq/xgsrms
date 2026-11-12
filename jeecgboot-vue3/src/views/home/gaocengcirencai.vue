<template>
  <div class="portal-page talent-page">
    <div class="talent-layout">
      <nav class="cat-nav" aria-label="岗位分类">
        <p class="cat-nav-title">岗位分类</p>
        <ul>
          <li v-for="item in navItems" :key="item.key">
            <div class="cat-row">
              <button
                type="button"
                class="cat-btn"
                :class="{
                  'is-current': selected === item.key,
                  'is-ancestor': item.key === 'talent' && talentChildActive,
                }"
                :aria-current="selected === item.key ? 'page' : undefined"
                @click="selectNav(item.key)"
              >
                {{ item.label }}
              </button>
              <button
                v-if="item.children"
                type="button"
                class="cat-toggle"
                :aria-expanded="talentOpen"
                aria-label="展开或收起引进高层次人才"
                @click="talentOpen = !talentOpen"
              >
                <span class="chevron" :class="{ open: talentOpen }" aria-hidden="true"></span>
              </button>
            </div>
            <ul v-if="item.children && talentOpen" class="cat-sub">
              <li v-for="child in item.children" :key="child.key">
                <button
                  type="button"
                  class="cat-btn cat-btn--sub"
                  :class="{ 'is-current': selected === child.key }"
                  :aria-current="selected === child.key ? 'page' : undefined"
                  @click="selectNav(child.key)"
                >
                  {{ child.label }}
                </button>
              </li>
            </ul>
          </li>
        </ul>
      </nav>

      <div class="talent-main">
        <template v-if="showOverview">
    <section class="hero">
      <img src="@/assets/images/talent_hero_campus.jpg" alt="中国科学院信息工程研究所" class="hero-bg" />
      <div class="hero-overlay">
        <div class="hero-copy">
          <h1>信息工程研究所</h1>
          <p>诚邀海内外英才</p>
        </div>
      </div>
    </section>

    <section class="offers">
      <article
        v-for="(track, index) in tracks"
        :id="'talent-track-' + track.key"
        :key="track.key"
        class="offer"
        :class="['offer--' + track.key, { 'offer--current': selected === track.key }]"
      >
        <p v-if="selected === track.key" class="offer-current-tag">当前</p>
        <p class="offer-index" aria-hidden="true">{{ ['壹', '贰', '叁'][index] }}</p>
        <h3>{{ track.title }}</h3>
        <p class="track-official">{{ track.official }}</p>
        <div class="track-actions">
          <button type="button" class="btn-view" :aria-label="'查看' + track.title" @click="openTrack(track)">查看</button>
          <button type="button" class="btn-apply" :aria-label="'投递' + track.title" @click="goApply(track)">投递</button>
        </div>
      </article>
    </section>

    <section class="benefit sheet">
      <h2>岗位待遇</h2>
      <p>{{ benefitNote }}</p>
    </section>

    <div class="main-grid">
      <section class="team-intro sheet">
        <h2>信工所人才队伍</h2>
        <p>
          中国科学院信息工程研究所（以下简称信工所）是网络空间安全领域的“龙头所、核心所、总体所”，是从基础研究、高技术研发到工程化开发的长链条综合性研究所。信工所围绕国家发展战略和研究所科研布局，突出“高精尖缺”和干事导向，面向海内外长期招聘引进各类高水平科技创新人才。
        </p>
        <p>
          截至2025年12月，研究所共有职工1521人，其中正式聘用职工929人、在站博士后44人；正式职工中，硕士及以上学位的占93.22%，其中博士学位占41.01%。共有2人获杰青和国家级领军人才计划支持、有12人获国家“四小青”人才项目支持、29人获院级人才项目支持、13人获国务院特殊津贴等。2024年信工所荣获“网安人才培养基地”称号。
        </p>

        <figure class="talent-gallery">
          <img src="@/assets/images/talent_gallery_scene.jpg" alt="研究所阅览与研究场景" />
        </figure>
      </section>

      <aside class="policy-side sheet">
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

    <section class="contact sheet">
      <h2>联系我们</h2>
      <div class="contact-lines">
        <p>联系人：人力资源处，李老师</p>
        <p>联系方式：010-82345079，邮箱：lilin@iie.ac.cn</p>
      </div>
    </section>
        </template>

        <section v-else class="job-board">
          <h2>{{ listTitle }}</h2>
          <p v-if="jobsLoading" class="empty-jobs">正在加载在招岗位</p>
          <p v-else-if="jobsFailed" class="empty-jobs">在招岗位暂时无法加载</p>
          <p v-else-if="!listedJobs.length" class="empty-jobs">暂无在招岗位</p>
          <ul v-else class="open-jobs">
            <li v-for="job in listedJobs" :key="job.id" class="open-job">
              <div class="open-job-copy">
                <h3>{{ job.positionName }}</h3>
                <p>{{ job.dept || '未标明部门' }}</p>
              </div>
              <div class="open-job-actions">
                <button type="button" class="btn-view" :aria-label="'查看' + job.positionName" @click="goDetail(job.id)">查看</button>
                <button type="button" class="btn-apply" :aria-label="'投递' + job.positionName" @click="goDetail(job.id)">投递</button>
              </div>
            </li>
          </ul>
        </section>
      </div>
    </div>

    <div v-if="activeTrack" class="modal-mask" @click.self="activeTrack = null">
      <div class="modal" role="dialog" aria-modal="true" :aria-labelledby="'track-dialog-title'">
        <h3 id="track-dialog-title">{{ activeTrack.title }}</h3>
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
          <button type="button" class="btn-apply" @click="goApply(activeTrack)">投递简历</button>
          <button type="button" class="btn-view" @click="activeTrack = null">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed, nextTick, onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { message } from 'ant-design-vue';
  import { getPositionList } from '/@/api/xgsrms/home';

  const route = useRoute();
  const router = useRouter();

  type NavKey = 'talent' | 'leader' | 'overseas' | 'youth' | 'senior' | 'campus' | 'social' | 'postdoc' | 'dispatch';

  type NavItem = {
    key: NavKey;
    label: string;
    children?: { key: NavKey; label: string }[];
  };

  const navItems: NavItem[] = [
    {
      key: 'talent',
      label: '引进高层次人才',
      children: [
        { key: 'leader', label: '高层次领军人才' },
        { key: 'overseas', label: '海外优青' },
        { key: 'youth', label: '高层次青年人才' },
        { key: 'senior', label: '其他副高及以上岗位' },
      ],
    },
    { key: 'campus', label: '校园招聘' },
    { key: 'social', label: '社会招聘' },
    { key: 'postdoc', label: '博士后' },
    { key: 'dispatch', label: '劳务派遣' },
  ];

  const TALENT_PARENT = '引进高层次人才';
  const TRACK_LABELS: Record<'leader' | 'overseas' | 'youth', string> = {
    leader: '高层次领军人才',
    overseas: '海外优青',
    youth: '高层次青年人才',
  };
  const SENIOR_ALIASES = ['其他副高及以上岗位', '副高级以上岗位', '副高级岗位', '副高及以上岗位'];
  const GROUP_ALIASES: Record<'campus' | 'social' | 'postdoc' | 'dispatch', string[]> = {
    campus: ['校园招聘', '校园招聘岗位', '校招'],
    social: ['社会招聘', '社会招聘岗位', '社招'],
    postdoc: ['博士后招聘', '博士后岗位', '博士后'],
    dispatch: ['劳务派遣招聘', '劳务派遣', '人才派遣岗位', '人才派遣', '派遣岗位'],
  };
  const LIST_TITLES: Record<'senior' | 'campus' | 'social' | 'postdoc' | 'dispatch', string> = {
    senior: '其他副高及以上岗位',
    campus: '校园招聘',
    social: '社会招聘',
    postdoc: '博士后',
    dispatch: '劳务派遣',
  };
  const CLOSED_STATUS = new Set(['草稿', '审核未通过', '审核通过', '已结束', '停止招聘', '已关闭']);

  const selected = ref<NavKey>('talent');
  const talentOpen = ref(true);
  const talentChildActive = computed(() => ['leader', 'overseas', 'youth', 'senior'].includes(selected.value));
  const showOverview = computed(() => ['talent', 'leader', 'overseas', 'youth'].includes(selected.value));
  const listTitle = computed(() => LIST_TITLES[selected.value as keyof typeof LIST_TITLES] || '');

  const selectNav = async (key: NavKey) => {
    selected.value = key;
    if (key === 'talent' || key === 'leader' || key === 'overseas' || key === 'youth' || key === 'senior') {
      talentOpen.value = true;
    }
    await nextTick();
    const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const behavior = reduce ? 'auto' : 'smooth';
    if (key === 'leader' || key === 'overseas' || key === 'youth') {
      document.getElementById('talent-track-' + key)?.scrollIntoView({ behavior, block: 'center' });
      return;
    }
    document.querySelector('.talent-main')?.scrollIntoView({ behavior, block: 'start' });
  };

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

  const applying = ref(false);

  const talentPositionIds: Record<string, string> = {
    高层次领军人才: '202611092350020001',
    海外优青: '202611092350020002',
    高层次青年人才: '202611092350020003',
  };

  const goApply = async (track: Track | null) => {
    if (!track || applying.value) return;
    applying.value = true;
    activeTrack.value = null;
    try {
      const knownId = talentPositionIds[track.title];
      const res = await getPositionList({
        pageNo: 1,
        pageSize: 20,
        status: '招聘中',
        positionType: '引进高层次人才',
        positionName: track.title,
        positionSubtype: track.title,
      });
      const records = res && res.success && res.result ? res.result.records || [] : [];
      const job = records.find((item: { id?: string }) => String(item?.id) === knownId)
        || records.find(
          (item: { positionName?: string; positionSubtype?: string; positionType?: string }) =>
            item?.positionName === track.title
            && (item?.positionSubtype === track.title || item?.positionType === '引进高层次人才'),
        );
      const id = job?.id || knownId;
      if (!id) {
        message.warning('该岗位暂未开放投递');
        return;
      }
      await router.push({
        name: 'positionDetail',
        params: { id: String(id) },
        query: { from: route.fullPath },
      });
    } catch (error) {
      const knownId = track ? talentPositionIds[track.title] : '';
      if (knownId) {
        await router.push({
          name: 'positionDetail',
          params: { id: knownId },
          query: { from: route.fullPath },
        });
        return;
      }
      console.error('打开岗位投递失败', error);
      message.error('打开岗位失败，请稍后再试');
    } finally {
      applying.value = false;
    }
  };

  type OpenJob = {
    id: string;
    positionName: string;
    dept: string;
    positionType: string;
    positionSubtype: string;
    category: string;
    status: string;
  };

  const openJobs = ref<OpenJob[]>([]);
  const jobsLoading = ref(false);
  const jobsFailed = ref(false);

  const fieldText = (value?: string) => (value || '').trim();

  const matchesNamedTrack = (job: OpenJob, title: string) => {
    const type = fieldText(job.positionType);
    const subtype = fieldText(job.positionSubtype);
    const category = fieldText(job.category);
    const name = fieldText(job.positionName);
    if (subtype === title || type === title || category === title || name === title) return true;
    if (type === TALENT_PARENT && (subtype === title || category === title || name === title)) return true;
    if (category === TALENT_PARENT && (type === title || subtype === title || name === title)) return true;
    return false;
  };

  const matchesAlias = (job: OpenJob, aliases: string[]) => {
    const type = fieldText(job.positionType);
    const subtype = fieldText(job.positionSubtype);
    const category = fieldText(job.category);
    if (aliases.includes(type) || aliases.includes(subtype) || aliases.includes(category)) return true;
    if (type === TALENT_PARENT && (aliases.includes(subtype) || aliases.includes(category))) return true;
    if (category === TALENT_PARENT && (aliases.includes(type) || aliases.includes(subtype))) return true;
    return false;
  };

  const classifyJob = (job: OpenJob): NavKey | null => {
    if (matchesNamedTrack(job, TRACK_LABELS.leader)) return 'leader';
    if (matchesNamedTrack(job, TRACK_LABELS.overseas)) return 'overseas';
    if (matchesNamedTrack(job, TRACK_LABELS.youth)) return 'youth';
    if (matchesAlias(job, SENIOR_ALIASES)) return 'senior';
    if (fieldText(job.positionType) === TALENT_PARENT || fieldText(job.category) === TALENT_PARENT) return 'senior';
    const blob = `${fieldText(job.positionType)}|${fieldText(job.positionSubtype)}|${fieldText(job.category)}`;
    if (matchesAlias(job, GROUP_ALIASES.postdoc) || blob.includes('博士后')) return 'postdoc';
    if (matchesAlias(job, GROUP_ALIASES.campus) || blob.includes('校园招聘') || blob.includes('校招')) return 'campus';
    if (matchesAlias(job, GROUP_ALIASES.social) || blob.includes('社会招聘') || blob.includes('社招')) return 'social';
    if (matchesAlias(job, GROUP_ALIASES.dispatch) || blob.includes('劳务派遣') || blob.includes('人才派遣')) return 'dispatch';
    if (blob.includes('副高')) return 'senior';
    return null;
  };

  const listedJobs = computed(() => {
    const key = selected.value;
    if (!(key in LIST_TITLES)) return [];
    return openJobs.value.filter((job) => classifyJob(job) === key);
  });

  const toOpenJob = (item: {
    id?: string;
    positionName?: string;
    dept?: string;
    dept_dictText?: string;
    positionType?: string;
    positionSubtype?: string;
    category?: string;
    category_dictText?: string;
    status?: string;
    deleted?: string;
  }): OpenJob | null => {
    if (!item?.id || item.deleted === 'Y' || item.deleted === '1') return null;
    const status = fieldText(item.status);
    if (status && CLOSED_STATUS.has(status)) return null;
    const deptName = fieldText(item.dept_dictText);
    const rawDept = fieldText(item.dept);
    return {
      id: String(item.id),
      positionName: item.positionName || '',
      dept: deptName || (/^\d{6,}$/.test(rawDept) ? '' : rawDept),
      positionType: item.positionType || '',
      positionSubtype: item.positionSubtype || '',
      category: item.category_dictText || item.category || '',
      status,
    };
  };

  const loadOpenJobs = async () => {
    jobsLoading.value = true;
    jobsFailed.value = false;
    try {
      const pageSize = 100;
      const first = await getPositionList({ pageNo: 1, pageSize, status: '招聘中' });
      const result = first && first.success ? first.result : null;
      const records = [...((result && result.records) || [])];
      const total = Number((result && result.total) || records.length);
      const pages = Math.min(Math.ceil(total / pageSize) || 1, 5);
      for (let page = 2; page <= pages; page += 1) {
        const more = await getPositionList({ pageNo: page, pageSize, status: '招聘中' });
        const extra = more && more.success && more.result ? more.result.records || [] : [];
        records.push(...extra);
      }
      openJobs.value = records.map(toOpenJob).filter((job): job is OpenJob => !!job);
    } catch (error) {
      console.error('加载在招岗位失败', error);
      jobsFailed.value = true;
      openJobs.value = [];
    } finally {
      jobsLoading.value = false;
    }
  };

  const goDetail = (id: string) => {
    router.push({
      name: 'positionDetail',
      params: { id },
      query: { from: route.fullPath },
    });
  };

  onMounted(() => {
    loadOpenJobs();
  });

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
    color: #243044;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Songti SC', sans-serif;
  }

  .talent-layout {
    display: grid;
    grid-template-columns: 220px minmax(0, 1fr);
    gap: 24px;
    align-items: start;
  }

  .talent-main {
    display: flex;
    flex-direction: column;
    gap: 24px;
    min-width: 0;
    scroll-margin-top: 112px;
  }

  .cat-nav {
    position: sticky;
    top: 108px;
    z-index: 2;
    width: 220px;
    background: #fff;
    border: 1px solid #d5deea;
    border-top: 3px solid #004098;
    padding: 16px 12px 14px;
  }

  .cat-nav-title {
    margin: 0 8px 12px;
    padding-bottom: 10px;
    border-bottom: 1px solid #e4e9f0;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 18px;
    font-weight: 700;
    letter-spacing: 0.12em;
    color: #142033;
  }

  .cat-nav ul {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .cat-row {
    display: flex;
    align-items: stretch;
    gap: 4px;
  }

  .cat-btn {
    flex: 1;
    min-height: 42px;
    padding: 8px 10px;
    border: 0;
    background: transparent;
    color: #243044;
    font-family: inherit;
    font-size: 15px;
    font-weight: 500;
    line-height: 1.45;
    text-align: left;
    cursor: pointer;
  }

  .cat-btn:hover {
    background: #f3f6fb;
    color: #004098;
  }

  .cat-btn.is-ancestor {
    color: #004098;
    font-weight: 600;
    box-shadow: inset 3px 0 0 #004098;
  }

  .cat-btn.is-current {
    background: #004098;
    color: #fff;
    font-weight: 600;
  }

  .cat-btn.is-current:hover {
    background: #003067;
    color: #fff;
  }

  .cat-btn--sub {
    min-height: 38px;
    padding-left: 22px;
    font-size: 14px;
    font-weight: 400;
  }

  .cat-sub {
    margin: 2px 0 6px;
  }

  .cat-toggle {
    flex: none;
    width: 36px;
    border: 0;
    background: transparent;
    cursor: pointer;
  }

  .cat-toggle:hover {
    background: #f3f6fb;
  }

  .chevron {
    display: block;
    width: 8px;
    height: 8px;
    margin: 0 auto;
    border-right: 2px solid #004098;
    border-bottom: 2px solid #004098;
    transform: rotate(45deg);
  }

  .chevron.open {
    transform: rotate(225deg);
    margin-top: 4px;
  }

  .cat-btn:focus-visible,
  .cat-toggle:focus-visible {
    outline: 2px solid #004098;
    outline-offset: 2px;
  }

  .cat-btn.is-current:focus-visible {
    outline-color: #fff;
  }

  .hero {
    position: relative;
    height: 420px;
    overflow: hidden;
    background: #071428;
  }

  .hero-bg {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center 68%;
  }

  .hero-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(
      180deg,
      rgba(4, 16, 40, 0.58) 0%,
      rgba(0, 28, 72, 0.84) 34%,
      rgba(0, 28, 72, 0.84) 66%,
      rgba(4, 16, 40, 0.52) 100%
    );
  }

  .hero-copy {
    padding: 0 24px;
    text-align: center;
    color: #fff;
  }

  .hero-copy h1 {
    margin: 0;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: clamp(34px, 4.2vw, 52px);
    font-weight: 700;
    letter-spacing: 0.16em;
    line-height: 1.25;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.45);
  }

  .hero-copy p {
    margin: 18px 0 0;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: clamp(18px, 2vw, 24px);
    font-weight: 600;
    letter-spacing: 0.42em;
    line-height: 1.4;
    text-indent: 0.42em;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.45);
  }

  .hero-copy p::before {
    content: '';
    display: block;
    width: 48px;
    height: 2px;
    margin: 0 auto 16px;
    background: #fff;
  }

  .offers {
    display: grid;
    grid-template-columns: minmax(0, 1.02fr) minmax(0, 0.88fr) minmax(0, 1.22fr);
    border: 1px solid #d5deea;
    border-top: 3px solid #004098;
    background: #fff;
  }

  .offer {
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 0;
    padding: 28px 26px 24px;
    scroll-margin-top: 128px;
  }

  .offer--current {
    outline: 2px solid #004098;
    outline-offset: -2px;
  }

  .offer--leader.offer--current {
    outline-color: #fff;
  }

  .offer-current-tag {
    position: absolute;
    top: 12px;
    right: 12px;
    margin: 0;
    padding: 2px 8px;
    background: #004098;
    color: #fff;
    font-size: 12px;
    letter-spacing: 0.16em;
    line-height: 1.6;
  }

  .offer--leader .offer-current-tag {
    background: #fff;
    color: #004098;
  }

  .offer--leader {
    background: #004098;
    color: #fff;
  }

  .offer--overseas {
    background: #fff;
    border-left: 1px solid #d5deea;
  }

  .offer--youth {
    background: #f4f7fb;
    border-left: 1px solid #d5deea;
  }

  .offer-index {
    margin: 0;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 15px;
    letter-spacing: 0.18em;
    line-height: 1;
    color: #004098;
  }

  .offer--leader .offer-index {
    color: rgba(255, 255, 255, 0.78);
  }

  .offer h3 {
    margin: 14px 0 10px;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 26px;
    font-weight: 700;
    letter-spacing: 0.06em;
    line-height: 1.35;
    color: #142033;
  }

  .offer--leader h3 {
    color: #fff;
  }

  .track-official {
    margin: 0;
    font-size: 15px;
    line-height: 1.65;
    color: #3e4c5f;
    text-wrap: balance;
  }

  .offer--leader .track-official {
    color: rgba(255, 255, 255, 0.92);
  }

  .track-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: auto;
    padding-top: 24px;
  }

  .btn-view,
  .btn-apply {
    min-height: 44px;
    min-width: 88px;
    padding: 0 18px;
    border-radius: 0;
    font-family: inherit;
    font-size: 15px;
    font-weight: 500;
    letter-spacing: 0.08em;
    line-height: 1;
    white-space: nowrap;
    cursor: pointer;
    transition: background-color 160ms ease, color 160ms ease, border-color 160ms ease;
  }

  .btn-view {
    background: #fff;
    color: #004098;
    border: 1px solid #004098;
  }

  .btn-view:hover {
    color: #003067;
    border-color: #003067;
    background: #f3f6fb;
  }

  .btn-apply {
    background: #004098;
    color: #fff;
    border: 1px solid #004098;
  }

  .btn-apply:hover {
    background: #003067;
    border-color: #003067;
  }

  .offer--leader .btn-view {
    background: transparent;
    color: #fff;
    border-color: rgba(255, 255, 255, 0.88);
  }

  .offer--leader .btn-view:hover {
    background: rgba(255, 255, 255, 0.14);
    color: #fff;
    border-color: #fff;
  }

  .offer--leader .btn-apply {
    background: #fff;
    color: #004098;
    border-color: #fff;
  }

  .offer--leader .btn-apply:hover {
    background: #e7eef8;
    color: #003067;
    border-color: #e7eef8;
  }

  .btn-view:focus-visible,
  .btn-apply:focus-visible,
  .policy-group a:focus-visible {
    outline: 2px solid #004098;
    outline-offset: 3px;
  }

  .offer--leader .btn-view:focus-visible,
  .offer--leader .btn-apply:focus-visible {
    outline-color: #fff;
  }

  .sheet {
    background: #fff;
    border: 1px solid #d5deea;
    padding: 28px 32px 32px;
  }

  .talent-page h2 {
    position: relative;
    margin: 0 0 18px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e4e9f0;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 28px;
    font-weight: 700;
    letter-spacing: 0.08em;
    line-height: 1.3;
    color: #142033;
  }

  .talent-page h2::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -1px;
    width: 56px;
    height: 3px;
    background: #004098;
  }

  .benefit p,
  .team-intro p,
  .contact p,
  .modal p {
    margin: 0 0 14px;
    font-size: 16px;
    line-height: 1.9;
    text-align: justify;
    color: #243044;
  }

  .benefit p:last-child,
  .contact p {
    margin-bottom: 0;
  }

  .main-grid {
    display: grid;
    grid-template-columns: minmax(0, 1.55fr) minmax(280px, 0.9fr);
    gap: 24px;
    align-items: stretch;
  }

  .team-intro h3 {
    margin: 28px 0 10px;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 20px;
    font-weight: 700;
    letter-spacing: 0.08em;
    color: #142033;
  }

  .talent-gallery {
    margin: 28px 0 0;
    border: 1px solid #004098;
    background: #fff;
  }

  .talent-gallery img {
    display: block;
    width: 100%;
    height: 280px;
    object-fit: cover;
  }

  .policy-group {
    margin: 0 0 18px;
  }

  .policy-group:last-child {
    margin-bottom: 0;
  }

  .policy-group h4 {
    margin: 0 0 8px;
    font-family: 'PingFang SC', 'Hiragino Sans GB', sans-serif;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 0.04em;
    color: #004098;
  }

  .policy-group ul {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .policy-group li {
    position: relative;
    margin: 0;
    padding: 7px 0 7px 14px;
    line-height: 1.65;
  }

  .policy-group li::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0.85em;
    width: 5px;
    height: 5px;
    background: #004098;
  }

  .policy-group a {
    color: #004098;
    text-decoration: underline;
    text-underline-offset: 3px;
    transition: color 160ms ease;
  }

  .policy-group a:hover {
    color: #003067;
  }

  .policy-group span {
    color: #243044;
  }

  .contact-lines {
    display: grid;
    grid-template-columns: minmax(0, 0.8fr) minmax(0, 1.2fr);
    gap: 8px 32px;
  }

  .job-board h2 {
    margin-bottom: 20px;
  }

  .open-jobs {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .open-job {
    display: flex;
    flex-direction: column;
    min-width: 0;
    min-height: 168px;
    padding: 22px 22px 18px;
    background: #fff;
    border: 1px solid #d5deea;
    border-top: 3px solid #004098;
  }

  .open-job:nth-child(even) {
    background: #f4f7fb;
  }

  .open-job-copy {
    min-width: 0;
  }

  .open-job-copy h3 {
    margin: 0 0 8px;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 0.04em;
    line-height: 1.4;
    color: #142033;
  }

  .open-job-copy p {
    margin: 0;
    font-size: 15px;
    line-height: 1.6;
    color: #3e4c5f;
    text-align: left;
  }

  .open-job-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: auto;
    padding-top: 20px;
  }

  .empty-jobs {
    margin: 8px 0 0;
    padding: 28px 16px;
    border: 1px dashed #d5deea;
    background: #f7f7f7;
    color: #3e4c5f;
    font-size: 16px;
    letter-spacing: 0.08em;
    text-align: center;
  }

  .modal-mask {
    position: fixed;
    inset: 0;
    z-index: 2000;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px 16px;
    background: rgba(8, 18, 36, 0.62);
  }

  .modal {
    width: min(720px, 100%);
    max-height: min(86vh, 820px);
    overflow: auto;
    padding: 28px 28px 24px;
    background: #fff;
    border: 1px solid #d5deea;
    border-top: 3px solid #004098;
    box-shadow: 0 16px 48px rgba(8, 22, 48, 0.28);
  }

  .modal h3 {
    position: relative;
    margin: 0 0 12px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e4e9f0;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 28px;
    font-weight: 700;
    letter-spacing: 0.06em;
    color: #142033;
  }

  .modal h3::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -1px;
    width: 56px;
    height: 3px;
    background: #004098;
  }

  .modal h4 {
    margin: 0 0 8px;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 18px;
    font-weight: 700;
    letter-spacing: 0.06em;
    color: #004098;
  }

  .modal-official,
  .fine {
    color: #3e4c5f;
    font-size: 14px;
    line-height: 1.7;
    text-align: left;
  }

  .modal ul {
    margin: 0;
    padding-left: 1.2em;
  }

  .modal li {
    margin: 8px 0;
    line-height: 1.75;
  }

  .modal li::marker {
    color: #004098;
  }

  .modal-block {
    margin: 18px 0;
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 8px;
  }

  @media (max-width: 1080px) {
    .talent-layout {
      grid-template-columns: 1fr;
    }

    .cat-nav {
      position: static;
      width: 100%;
    }

    .offers,
    .main-grid,
    .contact-lines {
      grid-template-columns: 1fr;
    }

    .offer--overseas,
    .offer--youth {
      border-left: 0;
      border-top: 1px solid #d5deea;
    }
  }

  @media (max-width: 720px) {
    .hero {
      height: 280px;
    }

    .hero-copy h1 {
      letter-spacing: 0.08em;
    }

    .hero-copy p {
      letter-spacing: 0.22em;
      text-indent: 0.22em;
    }

    .sheet {
      padding: 22px 16px 24px;
    }

    .offer {
      padding: 22px 16px 18px;
    }

    .offer h3,
    .talent-page h2,
    .modal h3 {
      font-size: 24px;
    }

    .open-jobs {
      grid-template-columns: 1fr;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .talent-page *,
    .talent-page *::before,
    .talent-page *::after {
      animation: none !important;
      transition: none !important;
      scroll-behavior: auto !important;
    }
  }
</style>

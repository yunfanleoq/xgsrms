<template>
  <div class="portal-home">
    <section class="hero" aria-label="首页横幅" @mouseenter="isHovered = true" @mouseleave="isHovered = false">
      <video
        v-if="heroVideoSrc"
        class="hero-media"
        :src="heroVideoSrc"
        autoplay
        muted
        loop
        playsinline
      ></video>
      <img v-else-if="currentImgSrc" class="hero-media" :src="currentImgSrc" alt="" @error="handleImageError" />
      <div v-else class="hero-field" aria-hidden="true"></div>
      <div class="hero-shade"></div>
      <div class="hero-copy">
        <p class="hero-kicker">中国科学院信息工程研究所</p>
        <h1>人才招聘</h1>
      </div>
      <template v-if="!heroVideoSrc && carouselImages.length > 1">
        <button type="button" class="hero-nav hero-nav--prev" aria-label="上一张" @click="showPrevImage">‹</button>
        <button type="button" class="hero-nav hero-nav--next" aria-label="下一张" @click="showNextImage">›</button>
        <div class="hero-dots">
          <button
            v-for="(_image, index) in carouselImages"
            :key="index"
            type="button"
            :class="['dot', { active: index === currentIndex }]"
            :aria-label="`第 ${index + 1} 张`"
            @click="currentIndex = index"
          ></button>
        </div>
      </template>
    </section>

    <section class="recruit" aria-label="招聘信息与通知公告">
      <div class="recruit-wrap">
        <div class="jobs-col">
          <header class="col-head">
            <h2>招聘信息</h2>
          </header>
          <div v-if="categoriesLoading" class="col-state">
            <a-spin />
          </div>
          <p v-else-if="!categoryGroups.length" class="col-state">暂无岗位分类</p>
          <article v-for="group in categoryGroups" :key="group.name" class="category">
            <header class="category-head">
              <h3>{{ group.name }}</h3>
              <router-link :to="{ name: 'homePositions', query: { positionType: group.name } }">更多</router-link>
            </header>
            <ul v-if="group.jobs.length" class="job-list">
              <li v-for="job in group.jobs" :key="job.id" @click="goToPosition(job.id)">
                <span class="job-name">{{ job.positionName }}</span>
                <span class="job-side">
                  <span v-if="job.dept">{{ job.dept }}</span>
                  <time v-if="job.date">{{ job.date }}</time>
                </span>
              </li>
            </ul>
            <p v-else class="job-quiet">暂无在招岗位</p>
          </article>
        </div>

        <aside class="notice-col" aria-label="通知公告">
          <header class="col-head notice-head">
            <h2>通知公告</h2>
            <router-link :to="{ name: 'homeNews' }">更多</router-link>
          </header>
          <ul v-if="notices.length" class="notice-list">
            <li v-for="item in notices" :key="item.id" @click="goToNotice(item.id)">
              <span class="notice-text" v-html="item.title"></span>
              <time v-if="formatDate(item.createTime)">{{ formatDate(item.createTime) }}</time>
            </li>
          </ul>
          <p v-else class="col-state">暂无通知</p>
        </aside>
      </div>
    </section>

    <section class="campus" aria-label="园区风貌" @mouseenter="campusHovered = true" @mouseleave="campusHovered = false">
      <h2>园区风貌</h2>
      <div class="campus-carousel">
        <div class="campus-stage">
          <img
            v-for="(photo, index) in campusPhotos"
            :key="photo.src"
            :src="photo.src"
            :alt="photo.alt"
            :class="['campus-slide', { active: index === campusIndex }]"
            :style="{ objectPosition: photo.position }"
          />
        </div>
        <button type="button" class="campus-nav campus-nav--prev" aria-label="上一张" @click="showPrevCampus">‹</button>
        <button type="button" class="campus-nav campus-nav--next" aria-label="下一张" @click="showNextCampus">›</button>
        <div class="campus-dots">
          <button
            v-for="(photo, index) in campusPhotos"
            :key="`${photo.alt}-dot`"
            type="button"
            :class="['campus-dot', { active: index === campusIndex }]"
            :aria-label="`第 ${index + 1} 张，${photo.alt}`"
            @click="campusIndex = index"
          ></button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount, watchEffect } from 'vue';
  import { defHttp } from '@/utils/http/axios';
  import { router } from '@/router';
  import { getDictItems, getPositionList } from '@/api/xgsrms/home';
  import campusGate from '@/assets/images/campus_gate.jpg';
  import campusFacade from '@/assets/images/campus_facade.jpg';
  import campusLawn from '@/assets/images/campus_lawn.jpg';

  const campusPhotos = [
    { src: campusGate, alt: '中国科学院信息工程研究所正门', position: 'center 68%' },
    { src: campusFacade, alt: '中国科学院信息工程研究所主楼', position: 'center 62%' },
    { src: campusLawn, alt: '中国科学院信息工程研究所园区', position: 'center 42%' },
  ];

  const campusIndex = ref(0);
  const campusHovered = ref(false);
  let campusTimer: ReturnType<typeof setInterval> | null = null;

  const showPrevCampus = () => {
    campusIndex.value = (campusIndex.value - 1 + campusPhotos.length) % campusPhotos.length;
  };

  const showNextCampus = () => {
    campusIndex.value = (campusIndex.value + 1) % campusPhotos.length;
  };

  const startCampusCarousel = () => {
    if (campusTimer) return;
    campusTimer = setInterval(() => {
      if (!campusHovered.value && campusPhotos.length > 1) {
        showNextCampus();
      }
    }, 5000);
  };

  const stopCampusCarousel = () => {
    if (campusTimer) {
      clearInterval(campusTimer);
      campusTimer = null;
    }
  };

  interface CarouselImage {
    image: string;
  }

  interface NoticeItem {
    id: string;
    title: string;
    createTime?: string;
  }

  interface JobRow {
    id: string;
    positionName: string;
    dept: string;
    date: string;
  }

  interface CategoryGroup {
    name: string;
    jobs: JobRow[];
  }

  const listUrl = '/xgsJournalism/xgsJournalism/listForHomeNews';
  const imgListUrl = '/xgsHome/xgsHome/listForHome';

  /**
   * 可替换的首屏视频。只接受本所自有片源的本地或站内地址。
   * 留空时使用首页轮播图；轮播也没有时，显示深蓝静帧。
   */
  const heroVideoSrc = '';

  const carouselImages = ref<CarouselImage[]>([]);
  const currentIndex = ref(0);
  const currentImgSrc = ref('');
  const isHovered = ref(false);

  watchEffect(() => {
    currentImgSrc.value = carouselImages.value[currentIndex.value]?.image || '';
  });

  const fetchCarouselImages = async () => {
    try {
      const res = await defHttp.get(
        {
          url: imgListUrl,
          params: { newsType: 'homeImages', pageNo: 1, pageSize: 10 },
        },
        { isTransformResponse: false },
      );
      const response = res && res.success ? res.result : res;
      if (response && response.records) {
        carouselImages.value = response.records
          .filter((item) => item.images)
          .map((item) => {
            let imageUrl = item.images;
            if (imageUrl && !imageUrl.startsWith('data:image') && !imageUrl.startsWith('http') && !imageUrl.startsWith('/')) {
              imageUrl = `data:image/jpeg;base64,${imageUrl}`;
            }
            return { image: imageUrl };
          })
          .slice(0, 5);
      }
    } catch (error) {
      console.error('请求轮播图数据失败:', error);
    }
  };

  let carouselTimer: ReturnType<typeof setInterval> | null = null;

  const startCarousel = () => {
    if (carouselTimer) return;
    carouselTimer = setInterval(() => {
      if (!isHovered.value && carouselImages.value.length > 1) {
        currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length;
      }
    }, 5000);
  };

  const stopCarousel = () => {
    if (carouselTimer) {
      clearInterval(carouselTimer);
      carouselTimer = null;
    }
  };

  const showPrevImage = () => {
    if (carouselImages.value.length < 2) return;
    currentIndex.value = (currentIndex.value - 1 + carouselImages.value.length) % carouselImages.value.length;
  };

  const showNextImage = () => {
    if (carouselImages.value.length < 2) return;
    currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length;
  };

  const handleImageError = () => {
    console.error('轮播图加载失败');
    const failed = currentIndex.value;
    carouselImages.value = carouselImages.value.filter((_, index) => index !== failed);
    if (currentIndex.value >= carouselImages.value.length) {
      currentIndex.value = 0;
    }
  };

  const notices = ref<NoticeItem[]>([]);

  const fetchNotices = async () => {
    try {
      const response = await defHttp.get({
        url: listUrl,
        params: { typeCode: 'rczp', pageNo: 1, pageSize: 5 },
      });
      if (response && response.records) {
        notices.value = response.records.map((item: any) => ({
          id: item.id,
          title: item.journalismHead,
          createTime: item.createTime,
        }));
      }
    } catch (error) {
      console.error('请求通知公告失败:', error);
    }
  };

  const goToNotice = (itemId: string) => {
    router.push({ name: 'newsDetail', params: { id: itemId } });
  };

  const categoryGroups = ref<CategoryGroup[]>([]);
  const categoriesLoading = ref(true);
  const POSITION_TYPE_ORDER = ['科研', '管理', '支撑'];

  const orderPositionTypes = (names: string[]) =>
    [...names].sort((a, b) => {
      const rank = (name: string) => {
        const index = POSITION_TYPE_ORDER.indexOf(name);
        return index === -1 ? POSITION_TYPE_ORDER.length : index;
      };
      return rank(a) - rank(b);
    });

  const fetchCategoryGroups = async () => {
    categoriesLoading.value = true;
    try {
      const res = await getDictItems({ dictCode: '岗位类型' });
      const list = res && res.success && Array.isArray(res.result) ? res.result : [];
      const names = orderPositionTypes(
        list
          .map((item: any) => String(item?.title || item?.text || item?.label || '').trim())
          .filter((name: string) => !!name),
      );

      const groups = await Promise.all(
        names.map(async (name: string) => {
          try {
            const jobRes = await getPositionList({
              pageNo: 1,
              pageSize: 5,
              status: '招聘中',
              positionType: name,
            });
            const records = jobRes && jobRes.success && jobRes.result ? jobRes.result.records || [] : [];
            const jobs: JobRow[] = records.map((job: any) => ({
              id: String(job.id),
              positionName: job.positionName || '',
              dept: job.dept_dictText || '',
              date: formatDate(job.createTime || job.updateTime),
            }));
            return { name, jobs };
          } catch (error) {
            console.error('加载分类岗位失败:', name, error);
            return { name, jobs: [] as JobRow[] };
          }
        }),
      );
      categoryGroups.value = groups;
    } catch (error) {
      console.error('getDictItems(岗位类型) failed', error);
      categoryGroups.value = [];
    } finally {
      categoriesLoading.value = false;
    }
  };

  const goToPosition = (id: string) => {
    router.push({ name: 'positionDetail', params: { id } });
  };

  const formatDate = (value?: string) => {
    if (!value) return '';
    const matched = String(value).match(/\d{4}-\d{2}-\d{2}/);
    return matched ? matched[0] : String(value).slice(0, 10);
  };

  onMounted(() => {
    fetchCarouselImages();
    fetchNotices();
    fetchCategoryGroups();
    startCarousel();
    startCampusCarousel();
  });

  onBeforeUnmount(() => {
    stopCarousel();
    stopCampusCarousel();
  });
</script>

<style scoped>
  .portal-home {
    background: #fff;
    color: #333333;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
  }

  .hero {
    position: relative;
    height: 540px;
    background: #10243f;
    overflow: hidden;
  }

  .hero-media,
  .hero-field {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
  }

  .hero-media {
    object-fit: cover;
    display: block;
  }

  .hero-field {
    background:
      radial-gradient(ellipse at 78% 18%, rgba(214, 204, 184, 0.18), transparent 42%),
      radial-gradient(ellipse at 8% 88%, rgba(0, 64, 152, 0.38), transparent 48%),
      linear-gradient(165deg, #243e66 0%, #10243f 52%, #0b182c 100%);
  }

  .hero-field::after {
    content: '';
    position: absolute;
    inset: 0;
    background-image: linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px);
    background-size: 100% 80px;
    mask-image: linear-gradient(to bottom, transparent, #000 24%, #000 76%, transparent);
  }

  .hero-shade {
    position: absolute;
    inset: 0;
    background: linear-gradient(to top, rgba(8, 16, 32, 0.42), rgba(8, 16, 32, 0.16) 46%, rgba(8, 16, 32, 0.22));
    pointer-events: none;
  }

  .hero-copy {
    position: absolute;
    inset: 0;
    z-index: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #fff;
    text-align: center;
    pointer-events: none;
  }

  .hero-kicker {
    display: flex;
    align-items: center;
    gap: 16px;
    margin: 0 0 18px;
    font-size: 16px;
    font-weight: 400;
    letter-spacing: 0.18em;
    color: rgba(255, 255, 255, 0.92);
  }

  .hero-kicker::before,
  .hero-kicker::after {
    content: '';
    width: 36px;
    height: 1px;
    background: rgba(255, 255, 255, 0.75);
  }

  .hero-copy h1 {
    margin: 0;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', 'Songti SC', serif;
    font-size: 48px;
    font-weight: 700;
    letter-spacing: 0.08em;
    line-height: 1.2;
  }

  .hero-nav {
    position: absolute;
    top: 50%;
    z-index: 2;
    width: 40px;
    height: 40px;
    margin-top: -20px;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.45);
    background: rgba(16, 36, 63, 0.28);
    color: #fff;
    font-size: 26px;
    line-height: 1;
    cursor: pointer;
  }

  .hero-nav--prev {
    left: 28px;
  }

  .hero-nav--next {
    right: 28px;
  }

  .hero-dots {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 22px;
    z-index: 2;
    display: flex;
    justify-content: center;
    gap: 8px;
  }

  .dot {
    width: 22px;
    height: 3px;
    border: 0;
    padding: 0;
    background: rgba(255, 255, 255, 0.45);
    cursor: pointer;
  }

  .dot.active {
    background: #fff;
  }

  .recruit {
    background: #fff;
    padding: 80px 0 28px;
  }

  .recruit-wrap {
    width: min(1280px, calc(100% - 80px));
    margin: 0 auto;
    display: grid;
    grid-template-columns: minmax(0, 7fr) minmax(260px, 3fr);
    column-gap: 40px;
    align-items: start;
  }

  .col-head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    min-height: 44px;
    margin-bottom: 8px;
  }

  .col-head h2 {
    margin: 0;
    font-size: 32px;
    font-weight: 700;
    line-height: 1.3;
    color: #333333;
    letter-spacing: 0;
  }

  .notice-head h2 {
    font-size: 24px;
    color: #004098;
  }

  .col-head a,
  .category-head a {
    color: #004098;
    font-size: 14px;
    text-decoration: none;
  }

  .col-head a:hover,
  .category-head a:hover {
    text-decoration: underline;
  }

  .col-state,
  .job-quiet {
    margin: 8px 0 0;
    color: #8a8a8a;
    font-size: 14px;
  }

  .category + .category {
    margin-top: 32px;
  }

  .category-head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 8px;
  }

  .category-head h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    color: #333333;
  }

  .job-list,
  .notice-list {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .job-list li {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 24px;
    padding: 14px 16px;
    border-bottom: 1px solid #e6e6e6;
    cursor: pointer;
    background: #fff;
  }

  .job-list li:nth-child(even) {
    background: #f7f7f7;
  }

  .job-list li:hover .job-name {
    color: #004098;
  }

  .job-name {
    min-width: 0;
    font-size: 16px;
    font-weight: 400;
    color: #444444;
    line-height: 1.6;
  }

  .job-side {
    flex: none;
    display: flex;
    align-items: baseline;
    gap: 16px;
    color: #8a8a8a;
    font-size: 14px;
  }

  .notice-list li {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 16px;
    padding: 14px 0;
    border-bottom: 1px solid #e6e6e6;
    cursor: pointer;
  }

  .notice-list li:hover .notice-text {
    color: #004098;
  }

  .notice-text {
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 14px;
    line-height: 1.6;
    color: #333333;
  }

  .notice-list time {
    flex: none;
    color: #8a8a8a;
    font-size: 14px;
    font-variant-numeric: tabular-nums;
  }

  .campus {
    background: #f7f7f7;
    padding: 60px 0 72px;
    margin-top: 36px;
  }

  .campus h2 {
    margin: 0;
    text-align: center;
    font-size: 32px;
    font-weight: 700;
    color: #333333;
    letter-spacing: 0;
  }

  .campus-carousel {
    position: relative;
    width: min(1280px, calc(100% - 80px));
    margin: 28px auto 0;
  }

  .campus-stage {
    position: relative;
    height: 460px;
    background: #e6e8ec;
    overflow: hidden;
  }

  .campus-slide {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    opacity: 0;
    z-index: 0;
    transition: opacity 0.8s ease;
  }

  .campus-slide.active {
    opacity: 1;
    z-index: 1;
  }

  .campus-nav {
    position: absolute;
    top: 50%;
    z-index: 2;
    width: 44px;
    height: 44px;
    margin-top: -22px;
    border-radius: 50%;
    border: 0;
    background: rgba(16, 36, 63, 0.45);
    color: #fff;
    font-size: 28px;
    line-height: 1;
    cursor: pointer;
  }

  .campus-nav:hover {
    background: rgba(16, 36, 63, 0.72);
  }

  .campus-nav--prev {
    left: 18px;
  }

  .campus-nav--next {
    right: 18px;
  }

  .campus-dots {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 18px;
    z-index: 2;
    display: flex;
    justify-content: center;
    gap: 8px;
  }

  .campus-dot {
    width: 28px;
    height: 4px;
    border: 0;
    padding: 0;
    background: rgba(255, 255, 255, 0.55);
    cursor: pointer;
  }

  .campus-dot.active {
    background: #fff;
  }

  @media (max-width: 900px) {
    .hero {
      height: 420px;
    }

    .hero-copy h1 {
      font-size: 36px;
    }

    .recruit {
      padding: 48px 0 16px;
    }

    .recruit-wrap,
    .campus-carousel {
      width: min(1280px, calc(100% - 32px));
    }

    .campus-stage {
      height: 240px;
    }

    .recruit-wrap {
      grid-template-columns: 1fr;
      row-gap: 48px;
    }

    .job-list li {
      flex-direction: column;
      gap: 4px;
    }
  }
</style>

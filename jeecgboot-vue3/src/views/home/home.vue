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
          <form class="job-search" role="search" @submit.prevent="searchJobs">
            <label class="visually-hidden" for="portal-job-search">搜索岗位</label>
            <input
              id="portal-job-search"
              v-model="jobKeyword"
              type="search"
              name="keyword"
              placeholder="搜索岗位"
              autocomplete="off"
              enterkeyhint="search"
            />
            <button type="submit">搜索</button>
          </form>
          <header class="col-head">
            <h2>招聘信息</h2>
          </header>
          <nav class="job-cards" aria-label="招聘分类">
            <router-link
              v-for="card in recruitCards"
              :key="card.title"
              class="job-card"
              :class="'job-card--' + card.place"
              :to="{ name: 'homePositions', query: { positionType: card.positionType } }"
            >
              <span class="job-card-body">
              <span class="job-card-icon" aria-hidden="true">
                <svg v-if="card.icon === 'talent'" viewBox="0 0 32 32" fill="none">
                  <circle cx="15" cy="13.2" r="3.3" stroke="currentColor" stroke-width="1.6" />
                  <path d="M8 25.6c.9-3.9 3.3-5.8 7-5.8s6.1 1.9 7 5.8" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M23.6 4.6l.8 1.6 1.8.3-1.3 1.2.3 1.8-1.6-.9-1.6.9.3-1.8-1.3-1.2 1.8-.3.8-1.6z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round" />
                </svg>
                <svg v-else-if="card.icon === 'campus'" viewBox="0 0 32 32" fill="none">
                  <path d="M5 14.2 16 6.4l11 7.8" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
                  <path d="M8 14.2V25.6h16V14.2" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round" />
                  <path d="M13.4 25.6v-5.2h5.2v5.2" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round" />
                  <path d="M5 25.6h22" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M12 17.2h2.3M17.7 17.2H20" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                </svg>
                <svg v-else-if="card.icon === 'society'" viewBox="0 0 32 32" fill="none">
                  <circle cx="16" cy="8.4" r="2.5" stroke="currentColor" stroke-width="1.6" />
                  <circle cx="7.2" cy="11.2" r="2.15" stroke="currentColor" stroke-width="1.6" />
                  <circle cx="24.8" cy="11.2" r="2.15" stroke="currentColor" stroke-width="1.6" />
                  <path d="M11.3 25c.6-2.9 2.3-4.5 4.7-4.5s4.1 1.6 4.7 4.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M3.6 25c.5-2.3 1.8-3.5 3.5-3.5 1.1 0 2 .5 2.6 1.3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M28.4 25c-.5-2.3-1.8-3.5-3.5-3.5-1.1 0-2 .5-2.6 1.3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                </svg>
                <svg v-else-if="card.icon === 'postdoc'" viewBox="0 0 32 32" fill="none">
                  <path d="M4 14 16 8.2 28 14 16 19.8 4 14z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round" />
                  <path d="M10 16.5V22c0 1.5 2.6 3.1 6 3.1s6-1.6 6-3.1v-5.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
                  <path d="M28 14v7.4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <circle cx="28" cy="22.8" r="1.15" stroke="currentColor" stroke-width="1.4" />
                </svg>
                <svg v-else viewBox="0 0 32 32" fill="none">
                  <circle cx="11" cy="11" r="3" stroke="currentColor" stroke-width="1.6" />
                  <path d="M5.1 25c.7-3.6 2.8-5.4 5.9-5.4 1.9 0 3.4.7 4.4 1.9" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M17.6 16.4H27" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                  <path d="M23.2 12.5 27.2 16.4 23.2 20.3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              </span>
              <span class="job-card-title">{{ card.title }}</span>
              </span>
            </router-link>
          </nav>
        </div>

        <aside class="notice-col" aria-label="通知公告">
          <header class="col-head notice-head">
            <h2>通知公告</h2>
            <router-link :to="{ name: 'homeNews' }">更多</router-link>
          </header>
          <ul v-if="notices.length" class="notice-list">
            <li v-for="item in notices" :key="item.id" tabindex="0" @click="openOfficialNotice(item)" @keyup.enter="openOfficialNotice(item)">
              <span class="notice-text">{{ item.title }}</span>
              <time v-if="formatDate(item.newsDate)">{{ formatDate(item.newsDate) }}</time>
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
      <p class="campus-caption">{{ campusPhotos[campusIndex].alt }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount, watchEffect } from 'vue';
  import { defHttp } from '@/utils/http/axios';
  import { router } from '@/router';
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
    newsDate?: string;
    href?: string;
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
          newsDate: item.newsDate,
          href: item.href,
        }));
      }
    } catch (error) {
      console.error('请求通知公告失败:', error);
    }
  };

  const openOfficialNotice = (item: NoticeItem) => {
    const href = (item.href || '').trim();
    if (/^https?:\/\//i.test(href)) {
      window.open(href, '_blank', 'noopener,noreferrer');
    }
  };

  const recruitCards = [
    { title: '校园招聘', positionType: '校园招聘', icon: 'campus', place: 'campus' },
    { title: '社会招聘', positionType: '社会招聘', icon: 'society', place: 'society' },
    { title: '高层次人才', positionType: '引进高层次人才', icon: 'talent', place: 'talent' },
    { title: '博士后', positionType: '博士后招聘', icon: 'postdoc', place: 'postdoc' },
    { title: '劳务派遣', positionType: '劳务派遣招聘', icon: 'dispatch', place: 'dispatch' },
  ];

  const jobKeyword = ref('');

  const searchJobs = () => {
    const keyword = jobKeyword.value.trim();
    router.push({
      name: 'homePositions',
      query: keyword ? { keyword } : {},
    });
  };

  const formatDate = (value?: string) => {
    if (!value) return '';
    const matched = String(value).match(/\d{4}-\d{2}-\d{2}/);
    return matched ? matched[0] : String(value).slice(0, 10);
  };

  onMounted(() => {
    fetchCarouselImages();
    fetchNotices();
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
    transform: scale(1.04);
    animation: hero-settle 16s cubic-bezier(0.22, 1, 0.36, 1) both;
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
    background:
      radial-gradient(ellipse at 50% 46%, rgba(8, 16, 32, 0.62), transparent 42%),
      linear-gradient(to top, rgba(8, 16, 32, 0.58), rgba(8, 16, 32, 0.28) 46%, rgba(8, 16, 32, 0.34));
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
    text-shadow: 0 2px 18px rgba(8, 16, 32, 0.62);
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
    width: 44px;
    height: 44px;
    margin-top: -22px;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.45);
    background: rgba(16, 36, 63, 0.28);
    color: #fff;
    font-size: 26px;
    line-height: 1;
    cursor: pointer;
    transition: background-color 200ms ease;
  }

  .hero-nav:hover {
    background: rgba(16, 36, 63, 0.62);
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
    align-items: stretch;
  }

  .col-head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    min-height: 44px;
    margin-bottom: 8px;
  }

  .col-head h2 {
    position: relative;
    margin: 0;
    padding-bottom: 10px;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 32px;
    font-weight: 700;
    line-height: 1.3;
    color: #1c1917;
    letter-spacing: 0.04em;
  }

  .col-head h2::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 36px;
    height: 2px;
    background: #004098;
  }

  .col-head a {
    color: #004098;
    font-size: 14px;
    letter-spacing: 0.08em;
    text-decoration: none;
    transition: color 200ms ease;
  }

  .col-head a::after {
    content: '›';
    margin-left: 4px;
  }

  .col-head a:hover {
    color: #003067;
  }

  .visually-hidden {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
  }

  .job-search {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0 0 22px;
    padding: 6px 6px 6px 22px;
    background: #f7f6f3;
    border: 1px solid #d5deea;
    border-radius: 999px;
    box-shadow: 0 10px 28px rgba(16, 36, 63, 0.06);
  }

  .job-search:focus-within {
    border-color: #004098;
    box-shadow: 0 0 0 4px rgba(0, 64, 152, 0.12);
  }

  .job-search input {
    flex: 1;
    min-width: 0;
    height: 48px;
    padding: 0;
    border: 0;
    background: transparent;
    color: #1c1917;
    font: inherit;
    font-size: 16px;
    letter-spacing: 0.04em;
    outline: none;
  }

  .job-search input::placeholder {
    color: #8a8178;
  }

  .job-search input::-webkit-search-cancel-button,
  .job-search input::-webkit-search-decoration {
    display: none;
  }

  .job-search button {
    flex: none;
    height: 48px;
    min-width: 108px;
    padding: 0 28px;
    border: 0;
    border-radius: 999px;
    background: #004098;
    color: #fff;
    font: inherit;
    font-size: 16px;
    letter-spacing: 0.16em;
    cursor: pointer;
    transition: background-color 200ms ease, transform 200ms ease;
  }

  .job-search button:hover {
    background: #003067;
  }

  .job-search button:active {
    transform: translateY(1px);
  }

  .col-state {
    margin: 0;
    padding: 12px 16px;
    border-bottom: 1px solid #eee;
    color: #8a8a8a;
    font-size: 14px;
  }

  .jobs-col {
    container-type: inline-size;
  }

  .job-cards {
    display: grid;
    grid-template-columns: minmax(0, 1fr) minmax(0, 1.22fr) minmax(0, 1fr);
    grid-template-rows: 1fr 1fr;
    gap: 12px;
    margin-top: 20px;
  }

  .job-card--campus { grid-column: 1; grid-row: 1; }
  .job-card--society { grid-column: 3; grid-row: 1; }
  .job-card--talent { grid-column: 2; grid-row: 1 / span 2; }
  .job-card--postdoc { grid-column: 1; grid-row: 2; }
  .job-card--dispatch { grid-column: 3; grid-row: 2; }

  .job-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-width: 0;
    min-height: 148px;
    padding: 22px 16px;
    border: 1px solid #d5deea;
    border-radius: 0;
    background: #fff;
    color: #142033;
    text-align: center;
    text-decoration: none;
    cursor: pointer;
    transition: border-color 160ms ease, background-color 160ms ease, transform 160ms ease, box-shadow 160ms ease;
  }

  .job-card--society,
  .job-card--postdoc {
    background: #f4f7fb;
  }

  .job-card--talent {
    min-height: 0;
    padding: 28px 20px;
    border-color: #004098;
    background: #004098;
    color: #fff;
  }

  .job-card-body {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .job-card-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 52px;
    height: 52px;
    background: rgba(0, 64, 152, 0.16);
    color: #004098;
  }

  .job-card-icon svg {
    width: 30px;
    height: 30px;
  }

  .job-card--talent .job-card-icon {
    width: 60px;
    height: 60px;
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
  }

  .job-card--talent .job-card-icon svg {
    width: 34px;
    height: 34px;
  }

  .job-card-title {
    margin-top: 14px;
    font-family: 'Songti SC', 'STSong', 'SimSun', serif;
    font-size: 22px;
    font-weight: 700;
    line-height: 1.35;
    letter-spacing: 0.06em;
    color: #142033;
  }

  .job-card--talent .job-card-title {
    font-size: 28px;
    color: #fff;
  }

  .job-card:hover,
  .job-card:focus-visible {
    border-color: #003067;
    transform: translateY(-3px);
    box-shadow: 0 10px 22px rgba(0, 64, 152, 0.14);
  }

  .job-card--talent:hover,
  .job-card--talent:focus-visible {
    border-color: #003067;
    background: #003067;
  }

  @container (max-width: 680px) {
    .job-cards {
      grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
      gap: 12px;
    }

    .job-card--talent { grid-column: 1 / -1; grid-row: 1; min-height: 148px; }
    .job-card--campus { grid-column: 1; grid-row: 2; }
    .job-card--society { grid-column: 2; grid-row: 2; }
    .job-card--postdoc { grid-column: 1; grid-row: 3; }
    .job-card--dispatch { grid-column: 2; grid-row: 3; }

    .job-card--talent .job-card-title {
      font-size: 24px;
    }
  }

  .notice-list {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .notice-col {
    background: #f7f8fa;
    padding: 4px 22px 8px;
    border-top: 3px solid #004098;
  }

  .notice-list li {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 16px;
    padding: 14px 0;
    border-bottom: 1px solid #e6e6e6;
    cursor: pointer;
    animation: rise 420ms cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .notice-list li:nth-child(2) { animation-delay: 60ms; }
  .notice-list li:nth-child(3) { animation-delay: 120ms; }
  .notice-list li:nth-child(4) { animation-delay: 180ms; }
  .notice-list li:nth-child(5) { animation-delay: 240ms; }

  .notice-list li:hover .notice-text {
    color: #004098;
  }

  .notice-text {
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 14px;
    line-height: 1.7;
    color: #333333;
    transition: color 200ms ease;
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
    position: relative;
    margin: 0;
    padding-bottom: 12px;
    text-align: center;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 32px;
    font-weight: 700;
    color: #1c1917;
    letter-spacing: 0.06em;
  }

  .campus h2::after {
    content: '';
    position: absolute;
    left: 50%;
    bottom: 0;
    width: 36px;
    height: 2px;
    margin-left: -18px;
    background: #004098;
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
    box-shadow: 0 0 0 1px rgba(16, 36, 63, 0.08);
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

  .campus-caption {
    width: min(1280px, calc(100% - 80px));
    margin: 12px auto 0;
    color: #57534e;
    font-size: 14px;
    letter-spacing: 0.04em;
    text-align: center;
  }

  .hero-nav:focus-visible,
  .dot:focus-visible,
  .campus-nav:focus-visible,
  .campus-dot:focus-visible {
    outline: 2px solid #fff;
    outline-offset: 3px;
  }

  .job-search button:focus-visible,
  .job-card:focus-visible,
  .notice-list li:focus-visible,
  .col-head a:focus-visible {
    outline: 2px solid #004098;
    outline-offset: 3px;
  }

  @keyframes hero-settle {
    from { transform: scale(1.08); }
    to { transform: scale(1.02); }
  }

  @keyframes rise {
    from { opacity: 0; transform: translateY(12px); }
    to { opacity: 1; transform: none; }
  }

  @media (prefers-reduced-motion: reduce) {
    .hero-media,
    .job-card,
    .notice-list li,
    .campus-slide {
      animation: none;
      transform: none;
      transition: none;
    }
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
    .campus-carousel,
    .campus-caption {
      width: min(1280px, calc(100% - 32px));
    }

    .campus-stage {
      height: 240px;
    }

    .recruit-wrap {
      grid-template-columns: 1fr;
      row-gap: 48px;
    }

    .job-search {
      padding-left: 16px;
    }

    .job-search button {
      min-width: 84px;
      padding: 0 18px;
      letter-spacing: 0.08em;
    }
  }
</style>

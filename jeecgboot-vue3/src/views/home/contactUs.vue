<template>
  <div class="portal-page contact-page">
    <article class="contact-sheet">
      <header class="contact-mast">
        <h1>联系我们</h1>
      </header>

      <div class="contact-columns">
        <div class="contact-info">
          <dl class="contact-facts">
            <div class="fact">
              <dt>机构</dt>
              <dd>
                <p class="contact-office">人力资源处</p>
              </dd>
            </div>
            <div class="fact">
              <dt>地址</dt>
              <dd>北京市海淀区树村路19号</dd>
            </div>
            <div class="fact">
              <dt>电话</dt>
              <dd>+86-10-82345076</dd>
            </div>
            <div class="fact">
              <dt>邮箱</dt>
              <dd>zhaopin@iie.ac.cn</dd>
            </div>
            <div class="fact">
              <dt>邮编</dt>
              <dd class="fact-code">100085</dd>
            </div>
          </dl>

          <section class="contact-wechat" aria-labelledby="contact-wechat-title">
            <h2 id="contact-wechat-title">研究所微信公众号</h2>
            <img
              class="contact-wechat-qr"
              src="@/assets/images/iie-wechat-qr.png"
              alt="中国科学院信息工程研究所微信公众号二维码"
              width="160"
              height="160"
            />
          </section>
        </div>

        <figure class="contact-map">
          <div
            v-if="!mapFallback"
            ref="mapEl"
            class="contact-map-canvas"
            role="application"
            aria-label="中国科学院信息工程研究所位置地图"
            @pointerdown="onPointerDown"
            @pointermove="onPointerMove"
            @pointerup="onPointerUp"
            @pointercancel="onPointerUp"
          >
            <img
              v-for="tile in tiles"
              :key="tile.key"
              class="contact-map-tile"
              :src="tile.src"
              :style="{ left: tile.left + 'px', top: tile.top + 'px' }"
              alt=""
              draggable="false"
              @load="onTileLoad"
              @error="onTileError"
            />
            <div class="contact-map-pin" :style="{ left: pin.x + 'px', top: pin.y + 'px' }">
              <span class="contact-map-label">中国科学院信息工程研究所</span>
              <span class="contact-map-dot" aria-hidden="true"></span>
            </div>
            <div class="contact-map-zoom">
              <button type="button" aria-label="放大" @click="zoomBy(1)">+</button>
              <button type="button" aria-label="缩小" @click="zoomBy(-1)">−</button>
            </div>
          </div>
          <img
            v-else
            class="contact-map-fallback"
            src="@/assets/images/iie-shucun-map.png"
            alt="中国科学院信息工程研究所位置地图，标记位于北京市海淀区树村路19号"
            width="1400"
            height="680"
          />
        </figure>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts" name="contactUs">
  import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue';

  /**
   * 标记对应百度 BD-09 北纬 40.026012、东经 116.307279。
   * 底图用 GCJ-02：北纬 40.019851、东经 116.300834（WGS84 北纬 40.018594、东经 116.294756）。
   * 仓库里的百度演示 AK 能加载脚本，瓦片接口返回 403，因此不用带广告的百度页面。
   */
  const MARKER_LNG = 116.30083436;
  const MARKER_LAT = 40.01985081;
  const TILE_SIZE = 256;

  const mapEl = ref<HTMLDivElement | null>(null);
  const mapFallback = ref(!navigator.onLine);
  const view = reactive({
    w: 0,
    h: 0,
    lng: MARKER_LNG,
    lat: MARKER_LAT,
    zoom: 16,
  });
  let resizeObserver: ResizeObserver | null = null;
  let tileLoads = 0;
  let tileErrors = 0;
  let drag: { x: number; y: number; lng: number; lat: number; id: number } | null = null;

  function showStaticMap() {
    mapFallback.value = true;
  }

  function onOffline() {
    showStaticMap();
  }

  function project(lng: number, lat: number, zoom: number) {
    const scale = TILE_SIZE * 2 ** zoom;
    const x = ((lng + 180) / 360) * scale;
    const sine = Math.sin((lat * Math.PI) / 180);
    const y = (0.5 - Math.log((1 + sine) / (1 - sine)) / (4 * Math.PI)) * scale;
    return { x, y };
  }

  function unproject(x: number, y: number, zoom: number) {
    const scale = TILE_SIZE * 2 ** zoom;
    const lng = (x / scale) * 360 - 180;
    const n = Math.PI - (2 * Math.PI * y) / scale;
    const lat = (180 / Math.PI) * Math.atan(Math.sinh(n));
    return { lng, lat };
  }

  const tiles = computed(() => {
    if (mapFallback.value || view.w < 16 || view.h < 16) return [];
    const center = project(view.lng, view.lat, view.zoom);
    const left = center.x - view.w / 2;
    const top = center.y - view.h / 2;
    const x0 = Math.floor(left / TILE_SIZE);
    const y0 = Math.floor(top / TILE_SIZE);
    const x1 = Math.floor((left + view.w) / TILE_SIZE);
    const y1 = Math.floor((top + view.h) / TILE_SIZE);
    const max = 2 ** view.zoom;
    const list: { key: string; src: string; left: number; top: number }[] = [];
    for (let x = x0; x <= x1; x += 1) {
      if (x < 0 || x >= max) continue;
      for (let y = y0; y <= y1; y += 1) {
        if (y < 0 || y >= max) continue;
        const host = 1 + ((x + y) % 4);
        list.push({
          key: `${view.zoom}-${x}-${y}`,
          src: `https://webrd0${host}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x=${x}&y=${y}&z=${view.zoom}`,
          left: x * TILE_SIZE - left,
          top: y * TILE_SIZE - top,
        });
      }
    }
    return list;
  });

  const pin = computed(() => {
    const center = project(view.lng, view.lat, view.zoom);
    const point = project(MARKER_LNG, MARKER_LAT, view.zoom);
    return {
      x: view.w / 2 + (point.x - center.x),
      y: view.h / 2 + (point.y - center.y),
    };
  });

  function measure() {
    const el = mapEl.value;
    if (!el) return;
    view.w = el.clientWidth;
    view.h = el.clientHeight;
  }

  function zoomBy(delta: number) {
    const next = Math.min(18, Math.max(14, view.zoom + delta));
    if (next === view.zoom) return;
    view.zoom = next;
    tileLoads = 0;
    tileErrors = 0;
  }

  function onWheel(event: WheelEvent) {
    event.preventDefault();
    zoomBy(event.deltaY < 0 ? 1 : -1);
  }

  function onPointerDown(event: PointerEvent) {
    if ((event.target as HTMLElement).closest('button')) return;
    const el = event.currentTarget as HTMLElement;
    drag = { x: event.clientX, y: event.clientY, lng: view.lng, lat: view.lat, id: event.pointerId };
    el.setPointerCapture(event.pointerId);
  }

  function onPointerMove(event: PointerEvent) {
    if (!drag || drag.id !== event.pointerId) return;
    const origin = project(drag.lng, drag.lat, view.zoom);
    const next = unproject(origin.x - (event.clientX - drag.x), origin.y - (event.clientY - drag.y), view.zoom);
    view.lng = next.lng;
    view.lat = next.lat;
  }

  function onPointerUp(event: PointerEvent) {
    if (!drag || drag.id !== event.pointerId) return;
    drag = null;
  }

  function onTileLoad() {
    tileLoads += 1;
  }

  function onTileError() {
    tileErrors += 1;
    if (tileLoads === 0 && tileErrors > 0) showStaticMap();
  }

  onMounted(() => {
    window.addEventListener('offline', onOffline);
    if (mapFallback.value || !mapEl.value) return;
    measure();
    resizeObserver = new ResizeObserver(measure);
    resizeObserver.observe(mapEl.value);
    mapEl.value.addEventListener('wheel', onWheel, { passive: false });
  });

  onBeforeUnmount(() => {
    window.removeEventListener('offline', onOffline);
    resizeObserver?.disconnect();
    mapEl.value?.removeEventListener('wheel', onWheel);
  });
</script>

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
    padding: 48px 56px 8px;
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

  .contact-columns {
    display: grid;
    grid-template-columns: minmax(0, 0.72fr) minmax(0, 1.28fr);
    column-gap: 36px;
    align-items: stretch;
    padding: 12px 56px 48px;
  }

  .contact-info {
    min-width: 0;
  }

  .contact-facts {
    display: flex;
    flex-direction: column;
    margin: 8px 0 0;
    padding: 0;
  }

  .fact {
    display: flex;
    align-items: center;
    gap: 16px;
    margin: 0;
    padding: 12px 0;
    border-top: 1px solid #e4e8ee;
  }

  .fact dt {
    flex: 0 0 3.2em;
    margin: 0;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 1.6;
    color: #3d4d63;
  }

  .fact dd {
    margin: 0;
    min-width: 0;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 1.6;
    color: #1c2430;
  }

  .contact-office {
    margin: 0;
    font: inherit;
    line-height: inherit;
    color: inherit;
  }

  .fact-code {
    font-variant-numeric: tabular-nums;
    letter-spacing: 0.04em;
  }

  .contact-wechat {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 16px;
    margin: 0;
    padding: 12px 0 0;
    border-top: 1px solid #e4e8ee;
  }

  .contact-wechat h2 {
    margin: 0;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 1.6;
    color: #3d4d63;
  }

  .contact-wechat-qr {
    display: block;
    flex: 0 0 auto;
    width: 160px;
    height: 160px;
    padding: 8px;
    border: 1px solid #d5deea;
    background: #ffffff;
    image-rendering: pixelated;
  }

  .contact-map {
    display: flex;
    margin: 8px 0 0;
    min-width: 0;
    min-height: 360px;
  }

  .contact-map-canvas,
  .contact-map-fallback {
    display: block;
    width: 100%;
    height: 100%;
    min-height: 360px;
    border: 1px solid #d5deea;
    background: #f4f7fb;
  }

  .contact-map-canvas {
    position: relative;
    overflow: hidden;
    cursor: grab;
    touch-action: none;
  }

  .contact-map-canvas:active {
    cursor: grabbing;
  }

  .contact-map-tile {
    position: absolute;
    width: 256px;
    height: 256px;
    pointer-events: none;
    user-select: none;
  }

  .contact-map-pin {
    position: absolute;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
    transform: translate(-50%, -100%);
    pointer-events: none;
  }

  .contact-map-label {
    margin-bottom: 6px;
    padding: 4px 8px;
    background: #004098;
    color: #ffffff;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 13px;
    line-height: 18px;
    white-space: nowrap;
  }

  .contact-map-dot {
    width: 14px;
    height: 14px;
    border: 2px solid #ffffff;
    border-radius: 50%;
    background: #004098;
    box-shadow: 0 1px 3px rgba(20, 32, 51, 0.35);
  }

  .contact-map-zoom {
    position: absolute;
    top: 12px;
    right: 12px;
    z-index: 3;
    display: flex;
    flex-direction: column;
    border: 1px solid #d5deea;
    background: #ffffff;
  }

  .contact-map-zoom button {
    width: 36px;
    height: 36px;
    margin: 0;
    padding: 0;
    border: 0;
    background: #ffffff;
    color: #142033;
    font-size: 20px;
    line-height: 1;
    cursor: pointer;
  }

  .contact-map-zoom button + button {
    border-top: 1px solid #e4e8ee;
  }

  .contact-map-zoom button:hover {
    background: #f4f7fb;
  }

  .contact-map-zoom button:focus-visible {
    outline: 2px solid #004098;
    outline-offset: -2px;
  }

  .contact-map-fallback {
    object-fit: cover;
    object-position: center 42%;
  }

  @media (max-width: 800px) {
    .contact-mast {
      padding: 32px 20px 4px;
    }

    .contact-page .contact-mast h1 {
      font-size: 32px;
      letter-spacing: 0.08em;
    }

    .contact-columns {
      grid-template-columns: 1fr;
      row-gap: 20px;
      padding: 8px 20px 32px;
    }

    .fact dd,
    .fact dt,
    .contact-wechat h2 {
      font-size: 17px;
    }

    .contact-map {
      margin: 0;
      min-height: 0;
    }

    .contact-map-canvas,
    .contact-map-fallback {
      height: 280px;
      min-height: 280px;
    }
  }
</style>

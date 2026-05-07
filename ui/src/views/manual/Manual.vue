<!--
  @author       LiuRunYu 2026-05-05
  @description  手册页，可以分为电影、音乐、图书、电视剧、人物、游戏等类别
-->

<template>
  <div>
    <div class="manual-page relative mx-auto min-h-screen max-w-7xl px-6 py-24 lg:px-12">
      <!-- 顶部标题 -->
      <section class="my-12 ml-6 rounded-3xl">
        <div class="self-start text-3xl font-semibold tracking-wide">手册</div>
        <div class="mt-3 text-sm dark:text-slate-400">记录观影、阅读与生活轨迹</div>
      </section>

      <!-- 手册类别 -->
      <div class="grid items-start gap-6 md:grid-cols-2">
        <div v-for="(cate, cateIndex) in vo" :key="cate.id">
          <div class="overflow-hidden rounded-2xl" :class="getCateBgClass(cate.type)">
            <!-- 类别信息 -->
            <div class="flex items-center justify-between border-b px-6 pb-4 pt-6" :class="getCateBorder(cate.type)">
              <div class="flex items-center gap-3">
                <div>
                  <!-- 标题 -->
                  <div class="flex items-center gap-2">
                    <h2 class="text-lg font-semibold text-white" style="font-family: Rajdhani, sans-serif">{{ cate.name }}</h2>
                    <span class="rounded-full border px-2 py-0.5 text-xs" :class="getTypeBadgeClass(cate.type)">{{ getTypeLabel(cate.type) }}</span>
                  </div>
                  <!-- 描述 -->
                  <p class="mt-0.5 text-xs text-slate-400">{{ cate.memo }}</p>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <span class="text-xs text-slate-500">{{ cate.items.length }} 条目</span>
                <button class="add-btn flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-medium transition-all" :class="getAddBtnClass(cate.type)">
                  <plus class="h-3 w-3" />添加
                </button>
              </div>
            </div>

            <!-- 条目网格 -->
            <div class="p-6">
              <!-- 电影条目 -->
              <div v-if="cate.type === 'movie' || cate.type === 'tv'" class="movie-grid flex flex-wrap gap-5">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, scale: 0.88, y: 20 }"
                  :animate="motionReady ? { opacity: 1, scale: 1, y: 0 } : { opacity: 0, scale: 0.88, y: 20 }"
                  :transition="{ duration: 0.45, delay: cateIndex * 0.15 + itemIndex * 0.1 + 0.2 }"
                  class="movie-card-wrapper"
                >
                  <div class="movie-card group relative cursor-pointer" @mouseenter="hoveredId = item.id" @mouseleave="hoveredId = null">
                    <!-- Poster -->
                    <div class="poster-shell relative overflow-hidden rounded-xl" style="height: 220px; width: 150px">
                      <!-- Gradient placeholder -->
                      <div class="poster-bg absolute inset-0" :style="getPosterGradient(item.title, itemIndex)"></div>
                      <!-- Actual cover if available -->
                      <img v-if="item.cover && !item.cover.includes('example.com')" :src="item.cover" :alt="item.title" class="absolute inset-0 h-full w-full object-cover" />
                      <!-- Overlay lines (cinematic) -->
                      <div class="scan-lines absolute inset-0 opacity-10"></div>
                      <!-- Bottom gradient -->
                      <div class="absolute bottom-0 left-0 right-0 h-2/3 bg-gradient-to-t from-black/90 via-black/40 to-transparent"></div>
                      <!-- Rating Badge -->
                      <div
                        class="absolute right-2.5 top-2.5 flex items-center gap-0.5 rounded-md px-1.5 py-0.5 text-xs font-bold"
                        style="backdrop-filter: blur(4px); background: rgb(0 0 0 / 75%); border: 1px solid rgb(240 192 64 / 30%); color: #f0c040"
                      >
                        ★ {{ (item!.rating! / 10).toFixed(1) }}
                      </div>
                      <!-- Status -->
                      <div class="absolute left-2.5 top-2.5">
                        <span class="status-dot block h-2 w-2 rounded-full" :class="getStatusDotClass(item.status)"></span>
                      </div>
                      <!-- Title on poster -->
                      <div class="absolute bottom-0 left-0 right-0 p-3">
                        <p class="text-sm font-semibold leading-tight text-white drop-shadow-lg">{{ item.title }}</p>
                        <p class="mt-0.5 text-xs text-slate-300 opacity-80">{{ getParsedMeta(item.meta) }}</p>
                      </div>
                      <!-- Hover overlay -->
                      <div class="hover-overlay absolute inset-0 flex items-center justify-center rounded-xl bg-blue-600/0 transition-all duration-300 group-hover:bg-blue-600/10">
                        <div class="opacity-0 transition-opacity duration-300 group-hover:opacity-100">
                          <div class="flex h-10 w-10 items-center justify-center rounded-full border border-white/30 bg-white/20 backdrop-blur-sm">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="white"><polygon points="5,3 19,12 5,21" /></svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!-- Tags -->
                    <div class="mt-2.5 w-full" style="width: 150px">
                      <div class="flex flex-wrap gap-1">
                        <span
                          v-for="tag in parseTags(item.tags).slice(0, 2)"
                          :key="tag"
                          class="rounded px-1.5 py-0.5 text-xs text-slate-400"
                          style="background: rgb(255 255 255 / 6%); border: 1px solid rgb(255 255 255 / 8%)"
                          >{{ tag }}</span
                        >
                      </div>
                    </div>
                  </div>
                </motion.div>
              </div>

              <!-- Book Cards -->
              <div v-else-if="cate.type === 'book'" class="book-shelf flex flex-wrap items-end gap-4">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, x: -20, rotateY: -15 }"
                  :animate="motionReady ? { opacity: 1, x: 0, rotateY: 0 } : { opacity: 0, x: -20, rotateY: -15 }"
                  :transition="{ duration: 0.5, delay: cateIndex * 0.15 + itemIndex * 0.12 + 0.2 }"
                  class="book-card-wrapper"
                  style="perspective: 800px"
                >
                  <div class="book-card group relative cursor-pointer" @mouseenter="hoveredId = item.id" @mouseleave="hoveredId = null">
                    <!-- Book 3D body -->
                    <div class="book-body relative" style="height: 170px; width: 120px">
                      <!-- Spine (left) -->
                      <div
                        class="book-spine absolute bottom-0 left-0 top-0 flex items-center justify-center rounded-l-sm"
                        :style="getBookSpineStyle(item.title, itemIndex)"
                        style="width: 16px"
                      >
                        <p
                          class="rotate-0 text-xs font-bold text-white opacity-80"
                          style="font-size: 9px; letter-spacing: 0.1em; max-height: 130px; overflow: hidden; text-orientation: mixed; writing-mode: vertical-rl"
                        >
                          {{ item.title }}
                        </p>
                      </div>
                      <!-- Cover -->
                      <div class="book-cover absolute bottom-0 right-0 top-0 overflow-hidden rounded-r-sm" :style="getBookCoverStyle(item.title, itemIndex)" style="left: 16px">
                        <!-- Cover art pattern -->
                        <div class="absolute inset-0 opacity-30" :style="getBookPattern(itemIndex)"></div>
                        <!-- Cover gradient overlay -->
                        <div class="absolute inset-0 bg-gradient-to-br from-white/10 to-transparent"></div>
                        <!-- Bottom info -->
                        <div class="absolute inset-0 flex flex-col justify-between p-2.5">
                          <div class="flex justify-end">
                            <span class="rounded px-1.5 py-0.5 text-xs font-bold text-white" style="background: rgb(0 0 0 / 40%); font-size: 9px"
                              >★ {{ (item!.rating! / 10).toFixed(1) }}</span
                            >
                          </div>
                          <div>
                            <p class="text-xs font-bold leading-tight text-white drop-shadow" style="font-size: 11px">{{ item.title }}</p>
                            <p class="mt-0.5 text-white/70" style="font-size: 9px">{{ getBookAuthor(item.meta) }}</p>
                          </div>
                        </div>
                        <!-- Progress bar -->
                        <div v-if="item!.progress! < 100" class="absolute bottom-0 left-0 right-0 h-0.5 bg-black/30">
                          <div class="h-full bg-emerald-400/80 transition-all" :style="`width: ${item.progress}%`"></div>
                        </div>
                        <!-- Hover glow -->
                        <div class="absolute inset-0 rounded-r-sm bg-white/5 opacity-0 transition-opacity duration-300 group-hover:opacity-100"></div>
                      </div>
                      <!-- Page edge (right side shadow) -->
                      <div class="page-edge absolute bottom-1 right-0 top-1 w-px bg-white/10"></div>
                      <!-- 3D shadow below -->
                      <div class="absolute -bottom-1.5 left-4 right-0 h-3 rounded-b-sm opacity-40 blur-sm" :style="`background: ${getBookShadowColor(itemIndex)}`"></div>
                    </div>
                    <!-- Book info below -->
                    <div class="mt-3" style="width: 120px">
                      <p class="truncate text-xs font-medium text-slate-300">{{ item.title }}</p>
                      <div class="mt-1 flex items-center justify-between">
                        <span class="text-xs" :class="getStatusTextClass(item.status)">{{ getStatusLabel(item.status) }}</span>
                        <span v-if="item!.progress! < 100" class="text-xs text-slate-500">{{ item.progress }}%</span>
                      </div>
                    </div>
                  </div>
                </motion.div>
              </div>

              <!-- Generic Cards (music, game, person, etc.) -->
              <!-- <div v-else-if="cate.type === 'book'" class="generic-grid grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, scale: 0.9 }"
                  :animate="motionReady ? { opacity: 1, scale: 1 } : { opacity: 0, scale: 0.9 }"
                  :transition="{ duration: 0.4, delay: cateIndex * 0.15 + itemIndex * 0.08 + 0.2 }"
                >
                  <div class="generic-card group relative cursor-pointer overflow-hidden rounded-xl" style="border: 1px solid rgb(255 255 255 / 6%)">
                    <div class="aspect-square" :style="getPosterGradient(item.title, itemIndex + 4)"></div>
                    <div class="p-3" style="background: rgb(255 255 255 / 3%)">
                      <p class="truncate text-sm font-medium text-white">{{ item.title }}</p>
                      <p class="mt-0.5 text-xs text-slate-500">★ {{ (item.rating / 10).toFixed(1) }}</p>
                    </div>
                  </div>
                </motion.div>
              </div> -->
              <!-- ─── Comic 漫画卡片 ─── -->
              <div v-else-if="cate.type === 'comic'" class="comic-grid flex flex-wrap gap-5">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, y: 24, rotate: -3 }"
                  :animate="motionReady ? { opacity: 1, y: 0, rotate: 0 } : { opacity: 0, y: 24, rotate: -3 }"
                  :transition="{ duration: 0.45, delay: cateIndex * 0.15 + itemIndex * 0.1 + 0.2 }"
                >
                  <div class="comic-card group relative cursor-pointer" style="width: 140px">
                    <!-- Cover with manga-panel border -->
                    <div class="comic-shell relative overflow-hidden" style="border-radius: 4px 12px 12px 4px; height: 200px; width: 140px">
                      <div class="absolute inset-0" :style="getPosterGradient(item.title, itemIndex + 10)"></div>
                      <!-- Manga screentone dots -->
                      <div class="screentone absolute inset-0 opacity-20"></div>
                      <!-- Panel border accent -->
                      <div class="absolute inset-0 rounded-r-xl border-2" :style="`border-color: ${getAccent(cate.type)}40`"></div>
                      <!-- Left spine accent stripe -->
                      <div class="absolute bottom-0 left-0 top-0 w-1.5" :style="`background: ${getAccent(cate.type)}`"></div>
                      <!-- Bottom overlay -->
                      <div class="absolute bottom-0 left-0 right-0 h-1/2 bg-gradient-to-t from-black/90 to-transparent"></div>
                      <!-- Rating -->
                      <div
                        class="absolute right-2 top-2 rounded px-1.5 py-0.5 text-xs font-black"
                        :style="`background: ${getAccent(cate.type)}33; color: ${getAccent(cate.type)}; border: 1px solid ${getAccent(cate.type)}66`"
                      >
                        ★ {{ fmtRating(item.rating) }}
                      </div>
                      <!-- Status dot -->
                      <div class="absolute left-3 top-2.5">
                        <span class="status-dot block h-2 w-2 rounded-full" :class="getStatusDotClass(item.status)"></span>
                      </div>
                      <!-- Title -->
                      <div class="absolute bottom-0 left-0 right-0 p-2.5">
                        <p class="text-xs font-bold leading-snug text-white">{{ item.title }}</p>
                        <p class="mt-0.5 text-slate-400" style="font-size: 10px">{{ getMetaSubtitle(item.meta) }}</p>
                      </div>
                      <!-- Hover overlay -->
                      <div class="absolute inset-0 opacity-0 transition-opacity duration-300 group-hover:opacity-100" :style="`background: ${getAccent(cate.type)}18`"></div>
                    </div>
                    <!-- Tags -->
                    <div class="mt-2 flex flex-wrap gap-1">
                      <span
                        v-for="tag in parseTags(item.tags).slice(0, 2)"
                        :key="tag"
                        class="rounded px-1.5 py-0.5 text-xs"
                        :style="`background: ${getAccent(cate.type)}18; color: ${getAccent(cate.type)}cc; border: 1px solid ${getAccent(cate.type)}33`"
                      >
                        {{ tag }}
                      </span>
                    </div>
                  </div>
                </motion.div>
              </div>

              <!-- ─── Music 唱片卡片 ─── -->
              <div v-else-if="cate.type === 'music'" class="music-grid flex flex-wrap gap-6">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, scale: 0.8, rotate: -8 }"
                  :animate="motionReady ? { opacity: 1, scale: 1, rotate: 0 } : { opacity: 0, scale: 0.8, rotate: -8 }"
                  :transition="{ duration: 0.5, delay: cateIndex * 0.15 + itemIndex * 0.12 + 0.2, type: 'spring', bounce: 0.35 }"
                >
                  <div class="vinyl-card group cursor-pointer">
                    <!-- Vinyl record disc -->
                    <div class="vinyl-disc relative mx-auto" style="height: 130px; width: 130px">
                      <!-- Outer groove ring -->
                      <div
                        class="vinyl-grooves absolute inset-0 rounded-full"
                        :style="`background: conic-gradient(from 0deg, #1a1a1a, #2a2a2a 5%, #111 10%, #222 15%, #111); box-shadow: 0 8px 32px rgba(0,0,0,0.7), 0 2px 8px rgba(0,0,0,0.5), inset 0 0 20px rgba(0,0,0,0.5)`"
                      ></div>
                      <!-- Gradient shimmer ring -->
                      <div
                        class="absolute inset-2 rounded-full opacity-20"
                        :style="`background: conic-gradient(from 45deg, ${getAccent(cate.type)}, transparent 40%, ${getAccent(cate.type)} 60%, transparent)`"
                      ></div>
                      <!-- Album art center label -->
                      <div class="absolute inset-0 flex items-center justify-center">
                        <div
                          class="album-label flex flex-col items-center justify-center rounded-full text-center"
                          style="height: 52px; width: 52px"
                          :style="`background: radial-gradient(circle, ${getAccent(cate.type)}33, ${getAccent(cate.type)}11); border: 2px solid ${getAccent(cate.type)}55`"
                        >
                          <span
                            style="
                              -webkit-box-orient: vertical;
                              color: rgb(255 255 255 / 80%);
                              display: -webkit-box;
                              font-size: 7px;
                              font-weight: 700;
                              -webkit-line-clamp: 3;
                              line-height: 1.2;
                              max-height: 36px;
                              overflow: hidden;
                              padding: 0 4px;
                            "
                            >{{ item.title }}</span
                          >
                        </div>
                      </div>
                      <!-- Center hole -->
                      <div class="pointer-events-none absolute inset-0 flex items-center justify-center">
                        <div class="h-2 w-2 rounded-full border border-slate-700 bg-black/80"></div>
                      </div>
                      <!-- Hover spin glow -->
                      <div
                        class="vinyl-spin-glow absolute inset-0 rounded-full opacity-0 transition-opacity duration-500 group-hover:opacity-100"
                        :style="`background: radial-gradient(circle, ${getAccent(cate.type)}22 30%, transparent 70%)`"
                      ></div>
                    </div>
                    <!-- Info below -->
                    <div class="mt-3 text-center" style="width: 130px">
                      <p class="truncate text-sm font-semibold text-white">{{ item.title }}</p>
                      <p class="mt-0.5 text-xs text-slate-400">{{ getMetaSubtitle(item.meta) }}</p>
                      <div class="mt-1.5 flex justify-center">
                        <span class="text-xs font-bold" :style="`color: ${getAccent(cate.type)}`">★ {{ fmtRating(item.rating) }}</span>
                      </div>
                    </div>
                  </div>
                </motion.div>
              </div>

              <!-- ─── Game 游戏卡片 ─── -->
              <div v-else-if="cate.type === 'game'" class="game-grid flex flex-wrap gap-5">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, x: 30 }"
                  :animate="motionReady ? { opacity: 1, x: 0 } : { opacity: 0, x: 30 }"
                  :transition="{ duration: 0.4, delay: cateIndex * 0.15 + itemIndex * 0.1 + 0.2 }"
                >
                  <div class="game-card group relative cursor-pointer" style="width: 200px">
                    <!-- Game box art -->
                    <div class="game-shell relative overflow-hidden rounded-lg" style="height: 120px; width: 200px">
                      <div class="absolute inset-0" :style="getPosterGradient(item.title, itemIndex + 20)"></div>
                      <!-- Hexagonal grid overlay -->
                      <div class="hex-grid absolute inset-0 opacity-10"></div>
                      <!-- Status bar top -->
                      <div class="absolute left-0 right-0 top-0 h-0.5" :style="`background: ${getAccent(cate.type)}`"></div>
                      <!-- Rating badge -->
                      <div
                        class="absolute right-2.5 top-2.5 rounded px-2 py-0.5 text-xs font-black"
                        :style="`background: rgba(0,0,0,0.7); color: ${getAccent(cate.type)}; border: 1px solid ${getAccent(cate.type)}55`"
                      >
                        {{ fmtRating(item.rating) }}
                      </div>
                      <!-- Hover overlay -->
                      <div
                        class="absolute inset-0 flex items-center justify-center opacity-0 transition-opacity duration-300 group-hover:opacity-100"
                        :style="`background: ${getAccent(cate.type)}18`"
                      >
                        <div
                          class="flex h-9 w-9 items-center justify-center rounded-lg"
                          :style="`background: ${getAccent(cate.type)}44; border: 1px solid ${getAccent(cate.type)}88`"
                        >
                          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><polygon points="5,3 19,12 5,21" /></svg>
                        </div>
                      </div>
                    </div>
                    <!-- Info strip below art -->
                    <div
                      class="game-info mt-0 rounded-b-lg p-2.5"
                      style="background: rgb(255 255 255 / 4%); border: 1px solid rgb(255 255 255 / 7%); border-top: none; margin-top: -2px"
                    >
                      <p class="truncate text-sm font-semibold text-white">{{ item.title }}</p>
                      <p class="mt-0.5 text-xs text-slate-400">{{ getMetaSubtitle(item.meta) }}</p>
                      <!-- Progress bar -->
                      <div v-if="item.progress != null" class="mt-2">
                        <div class="mb-1 flex items-center justify-between">
                          <span class="text-xs" :class="getStatusTextClass(item.status)">{{ getStatusLabel(item.status) }}</span>
                          <span class="text-xs text-slate-500">{{ item.progress }}%</span>
                        </div>
                        <div class="h-1 overflow-hidden rounded-full bg-slate-800">
                          <div class="h-full rounded-full transition-all duration-700" :style="`width: ${item.progress}%; background: ${getAccent(cate.type)}`"></div>
                        </div>
                      </div>
                      <!-- Tags row -->
                      <div class="mt-2 flex flex-wrap gap-1">
                        <span
                          v-for="tag in parseTags(item.tags).slice(0, 2)"
                          :key="tag"
                          class="rounded px-1 py-0.5 text-xs"
                          :style="`color: ${getAccent(cate.type)}bb; background: ${getAccent(cate.type)}15; border: 1px solid ${getAccent(cate.type)}25`"
                        >
                          {{ tag }}
                        </span>
                      </div>
                    </div>
                  </div>
                </motion.div>
              </div>

              <!-- ─── OC 原创角色卡片 ─── -->
              <div v-else-if="cate.type === 'oc'" class="oc-grid flex flex-wrap gap-5">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, y: 20, scale: 0.92 }"
                  :animate="motionReady ? { opacity: 1, y: 0, scale: 1 } : { opacity: 0, y: 20, scale: 0.92 }"
                  :transition="{ duration: 0.5, delay: cateIndex * 0.15 + itemIndex * 0.13 + 0.2 }"
                >
                  <div class="oc-card group relative cursor-pointer overflow-hidden rounded-xl" style="border: 1px solid rgb(167 139 250 / 15%); width: 160px">
                    <!-- Portrait area -->
                    <div class="oc-portrait relative" style="height: 160px">
                      <div class="absolute inset-0" :style="getPosterGradient(item.title, itemIndex + 30)"></div>
                      <!-- Star particles bg -->
                      <div class="star-field absolute inset-0 opacity-40"></div>
                      <!-- Glow ring -->
                      <div class="absolute inset-0 flex items-center justify-center">
                        <div class="rounded-full opacity-30 blur-2xl" style="height: 80px; width: 80px" :style="`background: ${getAccent(cate.type)}`"></div>
                      </div>
                      <!-- Initials avatar -->
                      <div class="absolute inset-0 flex items-center justify-center">
                        <div
                          class="flex h-16 w-16 items-center justify-center rounded-full border-2 text-xl font-black"
                          :style="`background: ${getAccent(cate.type)}22; border-color: ${getAccent(cate.type)}66; color: ${getAccent(cate.type)}`"
                        >
                          {{ item.title.charAt(0) }}
                        </div>
                      </div>
                      <!-- Status chip top-right -->
                      <div class="absolute right-2 top-2">
                        <span
                          class="rounded-full px-1.5 py-0.5 text-xs"
                          :class="getStatusTextClass(item.status)"
                          style="background: rgb(0 0 0 / 60%); border: 1px solid rgb(255 255 255 / 10%)"
                        >
                          {{ getStatusLabel(item.status) }}
                        </span>
                      </div>
                    </div>
                    <!-- Character info -->
                    <div class="p-3" style="background: rgb(10 8 20 / 90%)">
                      <p class="text-sm font-semibold leading-snug text-white">{{ item.title }}</p>
                      <!-- Meta attributes -->
                      <div class="mt-2 space-y-1">
                        <div v-for="(val, key) in getOcMeta(item.meta)" :key="key" class="flex items-center justify-between">
                          <span class="text-slate-500" style="font-size: 10px">{{ key }}</span>
                          <span class="max-w-24 truncate font-medium text-slate-300" style="font-size: 10px">{{ val }}</span>
                        </div>
                      </div>
                      <!-- Progress bar if applicable -->
                      <div v-if="item.progress != null && item.progress < 100" class="mt-2">
                        <div class="h-0.5 rounded-full bg-slate-800">
                          <div class="h-full rounded-full" :style="`width: ${item.progress}%; background: ${getAccent(cate.type)}`"></div>
                        </div>
                      </div>
                      <!-- Tags -->
                      <div class="mt-2 flex flex-wrap gap-1">
                        <span
                          v-for="tag in parseTags(item.tags).slice(0, 2)"
                          :key="tag"
                          class="rounded-full px-1.5 py-0.5 text-xs"
                          :style="`color: ${getAccent(cate.type)}cc; background: ${getAccent(cate.type)}15; border: 1px solid ${getAccent(cate.type)}30`"
                        >
                          {{ tag }}
                        </span>
                      </div>
                    </div>
                  </div>
                </motion.div>
              </div>

              <!-- ─── Generic fallback ─── -->
              <div v-else class="generic-grid grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
                <motion.div
                  v-for="(item, itemIndex) in cate.items"
                  :key="item.id"
                  :initial="{ opacity: 0, scale: 0.9 }"
                  :animate="motionReady ? { opacity: 1, scale: 1 } : { opacity: 0, scale: 0.9 }"
                  :transition="{ duration: 0.4, delay: cateIndex * 0.15 + itemIndex * 0.08 + 0.2 }"
                >
                  <div class="generic-card group relative cursor-pointer overflow-hidden rounded-xl" style="border: 1px solid rgb(255 255 255 / 6%)">
                    <div class="aspect-square" :style="getPosterGradient(item.title, itemIndex + 4)"></div>
                    <div class="p-3" style="background: rgb(255 255 255 / 3%)">
                      <p class="truncate text-sm font-medium text-white">{{ item.title }}</p>
                      <p class="mt-0.5 text-xs text-slate-500">★ {{ fmtRating(item.rating) }}</p>
                    </div>
                  </div>
                </motion.div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { motion } from 'motion-v';
import { useDarkOnce, useGradientOnce } from '@/utils/responsive';
import { getModel } from '@/utils/cache';
import { useMyStore } from '@/config/my-store';
import { useAppStore } from '@/config/app-store';
import { Plus } from 'lucide-vue-next';

// 定义VO数据类型
interface Category {
  [key: string]: any;
  items: any[];
}
const vo = getModel() as Category[];
const myStore = useMyStore();
const appStore = useAppStore();
useGradientOnce();

const isDark = computed(() => appStore.theme !== 'light');

console.log(vo);
// useDarkOnce();

const hoveredId = ref<string | null>(null);

/** 页面 layout 动画结束后再触发卡片入场 */
const motionReady = ref(false);
onMounted(() => {
  setTimeout(() => {
    motionReady.value = true;
  }, 300);
});

// ─── Helpers ────────────────────────────────────────────────────────────────

function getTypeLabel(type: string): string {
  const map: Record<string, string> = {
    movie: '电影',
    book: '图书',
    tv: '剧集',
    music: '音乐',
    game: '游戏',
    person: '人物',
  };
  return map[type] ?? '其他';
}

function getCateBgClass(type: string): string {
  const map: Record<string, string> = {
    movie: 'cate-movie',
    book: 'cate-book',
    tv: 'cate-tv',
    music: 'cate-music',
    game: 'cate-game',
  };
  return map[type] ?? 'cate-default';
}

function getCateBorder(type: string): string {
  const map: Record<string, string> = {
    movie: 'border-blue-900/40',
    book: 'border-amber-900/40',
    tv: 'border-purple-900/40',
    music: 'border-pink-900/40',
    game: 'border-emerald-900/40',
  };
  return map[type] ?? 'border-slate-700/40';
}

function getTypeBadgeClass(type: string): string {
  const map: Record<string, string> = {
    movie: 'text-blue-400 border-blue-700/50 bg-blue-900/20',
    book: 'text-amber-400 border-amber-700/50 bg-amber-900/20',
    tv: 'text-purple-400 border-purple-700/50 bg-purple-900/20',
    music: 'text-pink-400 border-pink-700/50 bg-pink-900/20',
    game: 'text-emerald-400 border-emerald-700/50 bg-emerald-900/20',
  };
  return map[type] ?? 'text-slate-400 border-slate-600 bg-slate-800/20';
}

function getAddBtnClass(type: string): string {
  const map: Record<string, string> = {
    movie: 'text-blue-300 bg-blue-900/30 hover:bg-blue-800/50 border border-blue-700/30',
    book: 'text-amber-300 bg-amber-900/30 hover:bg-amber-800/50 border border-amber-700/30',
    tv: 'text-purple-300 bg-purple-900/30 hover:bg-purple-800/50 border border-purple-700/30',
    music: 'text-pink-300 bg-pink-900/30 hover:bg-pink-800/50 border border-pink-700/30',
    game: 'text-emerald-300 bg-emerald-900/30 hover:bg-emerald-800/50 border border-emerald-700/30',
  };
  return map[type] ?? 'text-slate-300 bg-slate-800/50 hover:bg-slate-700/50 border border-slate-600/30';
}

const POSTER_GRADIENTS = [
  'linear-gradient(135deg, #0f2027, #203a43, #2c5364)',
  'linear-gradient(135deg, #1a1a2e, #16213e, #0f3460)',
  'linear-gradient(135deg, #2d1b69, #11998e, #38ef7d)',
  'linear-gradient(135deg, #360033, #0b8793)',
  'linear-gradient(135deg, #141e30, #243b55)',
  'linear-gradient(135deg, #0a0a0a, #1a1a2e, #16213e)',
  'linear-gradient(135deg, #1e3c72, #2a5298)',
  'linear-gradient(135deg, #200122, #6f0000)',
];

function getPosterGradient(title: string, index: number): string {
  const idx = (index + title.charCodeAt(0)) % POSTER_GRADIENTS.length;
  return `background: ${POSTER_GRADIENTS[idx]};`;
}

const BOOK_COLORS = [
  { spine: '#1a3a5c', cover: '#234b73', shadow: '#1a3a5c' },
  { spine: '#4a1942', cover: '#6b2d5e', shadow: '#4a1942' },
  { spine: '#1a4a2e', cover: '#2d6e46', shadow: '#1a4a2e' },
  { spine: '#4a3800', cover: '#7a5c00', shadow: '#4a3800' },
  { spine: '#2d1b0a', cover: '#5c3a1e', shadow: '#2d1b0a' },
];

function getBookSpineStyle(title: string, index: number): string {
  const c = BOOK_COLORS[index % BOOK_COLORS.length];
  return `background: ${c.spine};`;
}

function getBookCoverStyle(title: string, index: number): string {
  const c = BOOK_COLORS[index % BOOK_COLORS.length];
  return `background: linear-gradient(160deg, ${c.cover} 0%, ${c.spine} 100%);`;
}

function getBookShadowColor(index: number): string {
  return BOOK_COLORS[index % BOOK_COLORS.length].shadow;
}

const BOOK_PATTERNS = [
  `radial-gradient(circle at 30% 30%, rgba(255,255,255,0.15) 0%, transparent 60%), repeating-linear-gradient(45deg, transparent, transparent 4px, rgba(255,255,255,0.05) 4px, rgba(255,255,255,0.05) 8px)`,
  `radial-gradient(ellipse at top, rgba(255,255,255,0.1) 0%, transparent 70%)`,
  `repeating-linear-gradient(0deg, transparent, transparent 8px, rgba(255,255,255,0.04) 8px, rgba(255,255,255,0.04) 9px)`,
  `radial-gradient(circle at 70% 20%, rgba(255,255,255,0.12) 0%, transparent 50%)`,
  `linear-gradient(45deg, rgba(255,255,255,0.05) 25%, transparent 25%, transparent 75%, rgba(255,255,255,0.05) 75%)`,
];

function getBookPattern(index: number): string {
  return `background: ${BOOK_PATTERNS[index % BOOK_PATTERNS.length]};`;
}

function parseTags(tags: string): string[] {
  return tags.split(',').filter(t => t.trim().length > 0);
}

function getParsedMeta(meta: string): string {
  try {
    const m = JSON.parse(meta);
    return m.director ? `${m.year} · ${m.director}` : `${m.year}`;
  } catch {
    return '';
  }
}

function getBookAuthor(meta: string): string {
  try {
    const m = JSON.parse(meta);
    return m.author ?? '';
  } catch {
    return '';
  }
}

function getStatusDotClass(status: number): string {
  const map: Record<number, string> = {
    0: 'bg-slate-500',
    1: 'bg-yellow-400 shadow-yellow-400/50 shadow-sm',
    2: 'bg-emerald-400 shadow-emerald-400/50 shadow-sm',
  };
  return map[status] ?? 'bg-slate-500';
}

function getStatusTextClass(status: number): string {
  const map: Record<number, string> = {
    0: 'text-slate-500',
    1: 'text-yellow-400',
    2: 'text-emerald-400',
  };
  return map[status] ?? 'text-slate-500';
}

function getStatusLabel(status: number): string {
  const map: Record<number, string> = {
    0: '未开始',
    1: '进行中',
    2: '已完成',
  };
  return map[status] ?? '未知';
}

/** 从 meta 解析 artist / developer 等通用字段 */
function getMetaSubtitle(meta: string): string {
  try {
    const m = JSON.parse(meta);
    const v = m.artist ?? m.developer ?? m.author ?? m.ability ?? m.function ?? '';
    return m.year ? `${m.year}${v ? ' · ' + v : ''}` : v;
  } catch {
    return '';
  }
}

/** OC 角色属性解析 */
function getOcMeta(meta: string): Record<string, string> {
  try {
    return JSON.parse(meta);
  } catch {
    return {};
  }
}

/** 漫画/OC/游戏/音乐 各自的 accent 色 */
const TYPE_ACCENT: Record<string, string> = {
  comic: '#f97316', // orange
  music: '#ec4899', // pink
  game: '#10b981', // emerald
  oc: '#a78bfa', // violet
};

function getAccent(type: string) {
  return TYPE_ACCENT[type] ?? '#64748b';
}

/** 星级显示（rating 可能为 null） */
function fmtRating(rating: number | null): string {
  if (rating == null) return '—';
  return (rating / 10).toFixed(1);
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Rajdhani:wght@400;500;600;700&display=swap');

/* ── Category boxes ── */
.cate-movie {
  background: linear-gradient(160deg, rgb(15 25 50 / 90%) 0%, rgb(10 15 35 / 95%) 100%);
  border: 1px solid rgb(59 130 246 / 15%);
  box-shadow:
    0 4px 32px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 4%);
}

.cate-book {
  background: linear-gradient(160deg, rgb(40 28 10 / 90%) 0%, rgb(25 18 5 / 95%) 100%);
  border: 1px solid rgb(217 119 6 / 15%);
  box-shadow:
    0 4px 32px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 4%);
}

.cate-tv {
  background: linear-gradient(160deg, rgb(30 15 50 / 90%) 0%, rgb(20 10 35 / 95%) 100%);
  border: 1px solid rgb(147 51 234 / 15%);
  box-shadow:
    0 4px 32px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 4%);
}

.cate-default {
  background: rgb(15 20 30 / 90%);
  border: 1px solid rgb(255 255 255 / 7%);
  box-shadow: 0 4px 32px rgb(0 0 0 / 40%);
}

/* ── Movie card ── */
.movie-card {
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.movie-card:hover {
  transform: translateY(-8px) scale(1.02);
}

.poster-shell {
  box-shadow:
    0 10px 40px rgb(0 0 0 / 60%),
    0 4px 12px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 8%);
}

/* CRT scan lines effect */
.scan-lines {
  background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgb(0 0 0 / 15%) 2px, rgb(0 0 0 / 15%) 4px);
}

/* ── Book card ── */
.book-card {
  transform-style: preserve-3d;
  transition: transform 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.book-card:hover {
  transform: translateY(-10px) rotateY(-8deg) rotateX(3deg);
}

.book-body {
  box-shadow:
    4px 6px 20px rgb(0 0 0 / 70%),
    1px 2px 6px rgb(0 0 0 / 50%),
    inset -1px 0 0 rgb(255 255 255 / 4%);
  transform-style: preserve-3d;
}

.book-cover {
  box-shadow: inset -3px 0 8px rgb(0 0 0 / 30%);
}

/* ── Add button ── */
.add-btn {
  transition: all 0.2s;
}

/* ── Shelf indicator ── */
.book-shelf::after {
  background: linear-gradient(90deg, rgb(139 90 43 / 40%) 0%, rgb(80 50 20 / 20%) 100%);
  border-radius: 2px;
  box-shadow: 0 2px 8px rgb(0 0 0 / 40%);
  content: '';
  display: block;
  height: 4px;
  margin-top: 8px;
  width: 100%;
}
/* ─── Category theme boxes ─── */
.cate-comic {
  background: linear-gradient(160deg, rgb(40 20 8 / 90%) 0%, rgb(25 12 5 / 95%) 100%);
  border: 1px solid rgb(249 115 22 / 15%);
  box-shadow:
    0 4px 32px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 4%);
}

.cate-music {
  background: linear-gradient(160deg, rgb(40 8 25 / 90%) 0%, rgb(25 5 15 / 95%) 100%);
  border: 1px solid rgb(236 72 153 / 15%);
  box-shadow:
    0 4px 32px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 4%);
}

.cate-game {
  background: linear-gradient(160deg, rgb(5 30 20 / 90%) 0%, rgb(3 18 12 / 95%) 100%);
  border: 1px solid rgb(16 185 129 / 15%);
  box-shadow:
    0 4px 32px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 4%);
}

.cate-oc {
  background: linear-gradient(160deg, rgb(20 10 40 / 90%) 0%, rgb(12 5 25 / 95%) 100%);
  border: 1px solid rgb(167 139 250 / 15%);
  box-shadow:
    0 4px 32px rgb(0 0 0 / 40%),
    inset 0 1px 0 rgb(255 255 255 / 4%);
}

/* ─── Comic screentone ─── */
.screentone {
  background-image: radial-gradient(circle, rgb(255 255 255 / 80%) 1px, transparent 1px);
  background-size: 6px 6px;
}

.comic-shell {
  box-shadow:
    3px 3px 16px rgb(0 0 0 / 60%),
    inset 0 1px 0 rgb(255 255 255 / 6%);
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.comic-card:hover .comic-shell {
  transform: translateY(-8px) rotate(1deg);
}

/* ─── Vinyl record ─── */
.vinyl-disc {
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.vinyl-card:hover .vinyl-disc {
  transform: rotate(15deg) scale(1.05);
}

.vinyl-grooves {
  /* fine groove rings via repeating radial */
  background: repeating-radial-gradient(circle, #1a1a1a 0, #1a1a1a 3px, #222 3px, #222 6px);
}

.vinyl-spin-glow {
  animation: none;
}

.vinyl-card:hover .vinyl-spin-glow {
  animation: spin-slow 3s linear infinite;
}

@keyframes spin-slow {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

/* ─── Game card ─── */
.game-shell {
  box-shadow: 0 8px 28px rgb(0 0 0 / 60%);
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.game-card:hover .game-shell {
  transform: translateY(-4px);
}

.hex-grid {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='28' height='28'%3E%3Cpath d='M14 2 L26 8 L26 20 L14 26 L2 20 L2 8 Z' fill='none' stroke='white' stroke-width='0.5'/%3E%3C/svg%3E");
  background-size: 28px 28px;
}

/* ─── OC card ─── */
.oc-card {
  box-shadow: 0 8px 32px rgb(0 0 0 / 50%);
  transition: transform 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.oc-card:hover {
  transform: translateY(-8px) scale(1.02);
}

.star-field {
  background-image: radial-gradient(circle, rgb(255 255 255 / 90%) 1px, transparent 1px), radial-gradient(circle, rgb(255 255 255 / 50%) 1px, transparent 1px);
  background-position:
    0 0,
    8px 8px;
  background-size:
    30px 30px,
    17px 17px;
}
</style>

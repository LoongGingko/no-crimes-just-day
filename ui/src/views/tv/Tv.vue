<template>
  <div class="tv-container" ref="containerRef"></div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import * as THREE from 'three';
import { useAppStore } from '@/config/app-store';

const appStore = useAppStore();
const isDark = computed(() => appStore.theme !== 'light');

const loader = new THREE.ObjectLoader();

const containerRef = ref<HTMLDivElement | null>(null);
let scene: THREE.Scene | null = null;
let camera: THREE.PerspectiveCamera | null = null;
let renderer: THREE.WebGLRenderer | null = null;
let animationId: number | null = null;
let targetRotationX = 0;
let targetRotationY = 0;
let currentRotationX = 0;
let currentRotationY = 0;

let targetDistance = 5.5;
let currentDistance = 5.5;
let zoomOffset = 0;
const minDistance = 3;
const maxDistance = 8;

let ambientLight: THREE.AmbientLight | null = null;
let keyLight: THREE.DirectionalLight | null = null;
let rimLight: THREE.PointLight | null = null;
let fillLight: THREE.PointLight | null = null;
let skyPlane: THREE.Mesh<THREE.PlaneGeometry, THREE.Material> | null = null;
let windowGlassMat: THREE.MeshPhysicalMaterial | null = null;
let sunMesh: THREE.Mesh | null = null;
let moonMesh: THREE.Mesh | null = null;
let starGroup: THREE.Group | null = null;
let stageLightLeft: THREE.SpotLight | null = null;
let stageLightRight: THREE.SpotLight | null = null;
let topPurpleLight: THREE.SpotLight | null = null;
let discoLight: THREE.PointLight | null = null;
let strobeLight: THREE.PointLight | null = null;
let stageBeamLeft: THREE.Mesh<THREE.ConeGeometry, THREE.MeshBasicMaterial> | null = null;
let stageBeamRight: THREE.Mesh<THREE.ConeGeometry, THREE.MeshBasicMaterial> | null = null;
let topPurpleBeam: THREE.Mesh<THREE.ConeGeometry, THREE.MeshBasicMaterial> | null = null;
let smokeParticles: THREE.Points | null = null;
let screenGlow: THREE.PointLight | null = null;
let currentVideoIndex = 0;
const videoPlaylist = ['/videos/output1.webm', '/videos/output2.webm', '/videos/output3.webm', '/videos/output4.webm', '/videos/output5.webm', '/videos/output6.webm'];
let powerMat: THREE.MeshStandardMaterial | null = null;
let videoElement: HTMLVideoElement | null = null;
let audioElement: HTMLAudioElement | null = null;
let videoTexture: THREE.VideoTexture | null = null;
let dustParticles: THREE.Points | null = null;

let isDragging = false;
let dragStartX = 0;
let dragStartY = 0;
let dragStartRotX = 0;
let dragStartRotY = 0;
let touchPinch = false;
let pinchStartDistance = 0;

// 计算两指触摸间距，用于捏合缩放手势
const getTouchDistance = (touches: TouchList) => {
  const dx = touches[0].clientX - touches[1].clientX;
  const dy = touches[0].clientY - touches[1].clientY;
  return Math.sqrt(dx * dx + dy * dy);
};

// 生成逼真烟雾渐变贴图，用于粒子效果
const createSmokeTexture = () => {
  const size = 256;
  const canvas = document.createElement('canvas');
  canvas.width = size;
  canvas.height = size;
  const ctx = canvas.getContext('2d');
  if (!ctx) throw new Error('Failed to create smoke texture');

  const gradient = ctx.createRadialGradient(size / 2, size / 2, 8, size / 2, size / 2, size / 2);
  gradient.addColorStop(0, 'rgba(255,255,255,0.95)');
  gradient.addColorStop(0.28, 'rgba(220,230,255,0.35)');
  gradient.addColorStop(0.55, 'rgba(180,190,230,0.12)');
  gradient.addColorStop(1, 'rgba(40,55,90,0)');

  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, size, size);

  const texture = new THREE.CanvasTexture(canvas);
  texture.minFilter = THREE.LinearFilter;
  texture.magFilter = THREE.LinearFilter;
  texture.colorSpace = THREE.SRGBColorSpace;
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.RepeatWrapping;
  return texture;
};

// 生成光束纹理贴图，用于增强舞台灯光的投射质感
const createBeamTexture = (color: string) => {
  const size = 256;
  const canvas = document.createElement('canvas');
  canvas.width = size;
  canvas.height = size;
  const ctx = canvas.getContext('2d');
  if (!ctx) throw new Error('Failed to create beam texture');

  const gradient = ctx.createLinearGradient(0, 0, size, 0);
  gradient.addColorStop(0, 'rgba(255,255,255,0.0)');
  gradient.addColorStop(0.16, 'rgba(255,255,255,0.25)');
  gradient.addColorStop(0.5, color);
  gradient.addColorStop(0.84, 'rgba(255,255,255,0.22)');
  gradient.addColorStop(1, 'rgba(255,255,255,0.0)');

  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, size, size);

  const texture = new THREE.CanvasTexture(canvas);
  texture.minFilter = THREE.LinearFilter;
  texture.magFilter = THREE.LinearFilter;
  texture.colorSpace = THREE.SRGBColorSpace;
  texture.wrapS = THREE.ClampToEdgeWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
};

// 根据主题变化更新场景氛围、灯光与窗外背景
const updateThemeAppearance = () => {
  if (
    !scene ||
    !ambientLight ||
    !keyLight ||
    !rimLight ||
    !fillLight ||
    !skyPlane ||
    !windowGlassMat ||
    !sunMesh ||
    !moonMesh ||
    !starGroup ||
    !stageLightLeft ||
    !stageLightRight ||
    !topPurpleLight ||
    !discoLight ||
    !strobeLight ||
    !stageBeamLeft ||
    !stageBeamRight ||
    !topPurpleBeam ||
    !smokeParticles
  )
    return;

  if (isDark.value) {
    stageLightLeft.visible = true;
    stageLightRight.visible = true;
    topPurpleLight.visible = true;
    strobeLight.visible = true;
    stageBeamLeft.visible = true;
    stageBeamRight.visible = true;
    topPurpleBeam.visible = true;
    smokeParticles.visible = true;
    if (scene.background instanceof THREE.Color) scene.background.set(0x040814);
    scene.fog?.color.set(0x040814);
    ambientLight.color.set(0x3c5a83);
    ambientLight.intensity = 0.45;
    keyLight.color.set(0x6fa2ff);
    keyLight.intensity = 0.5;
    rimLight.color.set(0x5286ff);
    rimLight.intensity = 0.35;
    fillLight.color.set(0x3d74b6);
    fillLight.intensity = 0.25;
    discoLight.color.setHex(0xff40d8);
    discoLight.intensity = 30.8;
    windowGlassMat.color.set(0x728ebf);
    (skyPlane.material as THREE.MeshStandardMaterial).color.set(0x1c2b45);
    sunMesh.visible = false;
    moonMesh.visible = true;
    starGroup.visible = true;
  } else {
    if (scene.background instanceof THREE.Color) scene.background.set(0xa8d4ff);
    scene.fog?.color.set(0xa8d4ff);
    ambientLight.color.set(0xf0f2d8);
    ambientLight.intensity = 0.9;
    keyLight.color.set(0xfff1c2);
    keyLight.intensity = 1.2;
    rimLight.color.set(0x9dd3ff);
    rimLight.intensity = 0.45;
    fillLight.color.set(0xfff7e1);
    fillLight.intensity = 0.45;
    windowGlassMat.color.set(0xd7ecff);
    (skyPlane.material as THREE.MeshStandardMaterial).color.set(0x93cfff);
    sunMesh.visible = true;
    moonMesh.visible = false;
    starGroup.visible = false;
    stageLightLeft.visible = false;
    stageLightRight.visible = false;
    topPurpleLight.visible = false;
    discoLight.visible = false;
    strobeLight.visible = false;
    stageBeamLeft.visible = false;
    topPurpleBeam.visible = false;
    stageBeamRight.visible = false;
    smokeParticles.visible = false;
  }
};

// 解锁浏览器音频策略，在用户交互后开启声音播放
const unlockAudio = () => {
  if (videoElement) {
    videoElement.muted = true;
    videoElement.volume = 0.7;
    videoElement.play().catch(() => undefined);
  }
  if (audioElement) {
    audioElement.muted = false;
    audioElement.volume = 0.55;
    audioElement.play().catch(() => undefined);
  }
};

// 鼠标按下时开始旋转控制
const onPointerDown = (event: PointerEvent) => {
  if (event.pointerType === 'mouse' && event.button !== 0) return;
  if (!renderer?.domElement) return;
  renderer.domElement.setPointerCapture(event.pointerId);
  isDragging = true;
  dragStartX = event.clientX;
  dragStartY = event.clientY;
  dragStartRotX = targetRotationX;
  dragStartRotY = targetRotationY;
};

// 鼠标移动时更新目标旋转角度
const onPointerMove = (event: PointerEvent) => {
  if (!isDragging || touchPinch) return;
  const dx = event.clientX - dragStartX;
  const dy = event.clientY - dragStartY;
  targetRotationY = dragStartRotY + dx * 0.004;
  targetRotationX = dragStartRotX + dy * 0.0024;
  targetRotationX = Math.max(-0.55, Math.min(0.55, targetRotationX));
};

// 鼠标释放时停止拖拽
const onPointerUp = (event: PointerEvent) => {
  isDragging = false;
  touchPinch = false;
  if (renderer?.domElement) renderer.domElement.releasePointerCapture(event.pointerId);
};

// 触摸开始，支持单指拖拽与双指捏合缩放
const onTouchStart = (event: TouchEvent) => {
  if (event.touches.length === 2) {
    touchPinch = true;
    pinchStartDistance = getTouchDistance(event.touches);
  } else if (event.touches.length === 1) {
    isDragging = true;
    dragStartX = event.touches[0].clientX;
    dragStartY = event.touches[0].clientY;
    dragStartRotX = targetRotationX;
    dragStartRotY = targetRotationY;
  }
};

// 触摸移动，处理缩放和旋转
const onTouchMove = (event: TouchEvent) => {
  if (touchPinch && event.touches.length === 2) {
    const newDistance = getTouchDistance(event.touches);
    zoomOffset += (pinchStartDistance - newDistance) * 0.015;
    zoomOffset = Math.max(minDistance - 5.5, Math.min(maxDistance - 5.5, zoomOffset));
    pinchStartDistance = newDistance;
  } else if (isDragging && event.touches.length === 1) {
    const dx = event.touches[0].clientX - dragStartX;
    const dy = event.touches[0].clientY - dragStartY;
    targetRotationY = dragStartRotY + dx * 0.004;
    targetRotationX = dragStartRotX + dy * 0.0024;
    targetRotationX = Math.max(-0.55, Math.min(0.55, targetRotationX));
  }
};

// 触摸结束时重置状态
const onTouchEnd = (event: TouchEvent) => {
  if (event.touches.length < 2) touchPinch = false;
  if (event.touches.length === 0) isDragging = false;
};

// 鼠标滚轮缩放镜头距离
const onWheel = (event: WheelEvent) => {
  zoomOffset += event.deltaY * 0.007;
  zoomOffset = Math.max(minDistance - 5.5, Math.min(maxDistance - 5.5, zoomOffset));
};

// 窗口改变大小时重新调整渲染器与相机
const onResize = () => {
  if (!renderer || !camera) return;
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(window.innerWidth, window.innerHeight);
};

// 场景开始：初始化灯光
const init = () => {
  if (!containerRef.value) return;

  scene = new THREE.Scene();
  scene.background = new THREE.Color(isDark.value ? 0x040814 : 0xa8d4ff);
  scene.fog = new THREE.FogExp2(isDark.value ? 0x040814 : 0xa8d4ff, 0.03);

  camera = new THREE.PerspectiveCamera(42, window.innerWidth / window.innerHeight, 0.1, 1000);
  camera.position.set(0, 1.7, 5.5);
  camera.lookAt(0, 1, 0);

  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(window.innerWidth, window.innerHeight);
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.shadowMap.enabled = true;
  renderer.shadowMap.type = THREE.PCFSoftShadowMap;
  containerRef.value.appendChild(renderer.domElement);

  ambientLight = new THREE.AmbientLight(isDark.value ? 0x3c5a83 : 0xf0f2d8, isDark.value ? 0.45 : 0.9);
  scene.add(ambientLight); // 添加环境光，让场景有基础整体亮度

  const hemiLight = new THREE.HemisphereLight(0x6a88c8, 0x0f1624, isDark.value ? 0.5 : 0.6);
  scene.add(hemiLight); // 添加半球光，模拟天空到地面的柔和光照

  keyLight = new THREE.DirectionalLight(isDark.value ? 0x6fa2ff : 0xfff1c2, isDark.value ? 0.5 : 1.2);
  keyLight.position.set(4.2, 7.5, 4);
  keyLight.castShadow = true;
  keyLight.shadow.mapSize.set(2048, 2048);
  scene.add(keyLight); // 添加主方向光，作为关键光源投射阴影

  rimLight = new THREE.PointLight(isDark.value ? 0x5286ff : 0x9dd3ff, isDark.value ? 0.35 : 0.45, 8);
  rimLight.position.set(-4, 2.5, -3);
  scene.add(rimLight); // 添加边缘光，增强场景轮廓和立体感

  fillLight = new THREE.PointLight(isDark.value ? 0x3d74b6 : 0xfff7e1, isDark.value ? 0.25 : 0.45, 6);
  fillLight.position.set(2, 1.5, 3);
  scene.add(fillLight); // 添加补光，让阴影区域不会过于暗沉

  stageLightLeft = new THREE.SpotLight(0xffa451, 2.2, 10, Math.PI / 6, 0.18, 2);
  stageLightLeft.position.set(-2.2, 3.2, 1.4);
  stageLightLeft.target.position.set(-0.5, 1.1, 0.1);
  stageLightLeft.angle = Math.PI / 7;
  stageLightLeft.penumbra = 0.4;
  stageLightLeft.decay = 2;
  stageLightLeft.castShadow = false;
  scene.add(stageLightLeft); // 添加左侧舞台聚光灯
  scene.add(stageLightLeft.target); // 添加左侧聚光灯目标对象，让投射方向有效

  stageLightRight = new THREE.SpotLight(0x4fa6ff, 1.9, 10, Math.PI / 6, 0.18, 2);
  stageLightRight.position.set(2.2, 3.2, 1.4);
  stageLightRight.target.position.set(0.5, 1.1, 0.1);
  stageLightRight.angle = Math.PI / 7;
  stageLightRight.penumbra = 0.4;
  stageLightRight.decay = 2;
  stageLightRight.castShadow = false;
  scene.add(stageLightRight); // 添加右侧舞台聚光灯
  scene.add(stageLightRight.target); // 添加右侧聚光灯目标对象，让投射方向有效

  topPurpleLight = new THREE.SpotLight(0xc459ff, 1.3, 11, Math.PI / 5, 0.24, 2);
  topPurpleLight.position.set(0, 3.8, 0.4);
  topPurpleLight.target.position.set(0, 1.05, 0.25);
  topPurpleLight.penumbra = 0.5;
  topPurpleLight.decay = 2;
  topPurpleLight.castShadow = false;
  scene.add(topPurpleLight); // 添加顶部紫色聚光灯
  scene.add(topPurpleLight.target); // 添加顶部紫色聚光灯目标对象，让光线能指向场景中心

  discoLight = new THREE.PointLight(0xff40d8, 1.8, 20, 2);
  discoLight.position.set(0, 1.5, 0.4);
  discoLight.castShadow = false;
  discoLight.visible = isDark.value;
  scene.add(discoLight); // 添加夜间 RGB 迪斯科氛围光

  strobeLight = new THREE.PointLight(0xffffff, 0.18, 11, 2);
  strobeLight.position.set(0, 3.4, -0.5);
  strobeLight.visible = isDark.value;
  scene.add(strobeLight); // 添加频闪光源，用于夜晚节奏感效果

  const beamTextureWarm = createBeamTexture('rgba(255,186,108,0.62)');
  const beamMat = new THREE.MeshBasicMaterial({
    map: beamTextureWarm,
    alphaMap: beamTextureWarm,
    color: 0xffa451,
    transparent: true,
    opacity: 0.32,
    side: THREE.DoubleSide,
    depthWrite: false,
    blending: THREE.AdditiveBlending,
  });
  const leftBeamGeometry = new THREE.ConeGeometry(0.72, 4, 24, 1, true);
  leftBeamGeometry.translate(0, -2.1, 0);
  stageBeamLeft = new THREE.Mesh(leftBeamGeometry, beamMat);
  stageBeamLeft.position.set(-1.4, 3.05, 0.95);
  stageBeamLeft.rotation.set(-0.2, 0, -0.05); // ← 无旋转 = 垂直向下
  stageBeamLeft.renderOrder = 1;
  scene.add(stageBeamLeft); // 添加左侧光束，增强舞台氛围

  const beamTextureCool = createBeamTexture('rgba(95,173,255,0.68)');
  const beamMatRight = new THREE.MeshBasicMaterial({
    map: beamTextureCool,
    alphaMap: beamTextureCool,
    color: 0x5bbaff,
    transparent: true,
    opacity: 0.3,
    side: THREE.DoubleSide,
    depthWrite: false,
    blending: THREE.AdditiveBlending,
  });
  const rightBeamGeometry = new THREE.ConeGeometry(0.72, 4, 24, 1, true);
  rightBeamGeometry.translate(0, -2.1, 0);
  stageBeamRight = new THREE.Mesh(rightBeamGeometry, beamMatRight);
  stageBeamRight.position.set(1.4, 3.05, 0.95);
  stageBeamRight.rotation.set(-0.2, 0, 0.05);
  stageBeamRight.renderOrder = 1;
  scene.add(stageBeamRight); // 添加右侧光束，营造对称舞台效果

  const beamTexturePurple = createBeamTexture('rgba(186,71,255,0.78)');
  const beamMatTop = new THREE.MeshBasicMaterial({
    map: beamTexturePurple,
    alphaMap: beamTexturePurple,
    color: 0xba47ff,
    transparent: true,
    opacity: 0.34,
    side: THREE.DoubleSide,
    depthWrite: false,
    blending: THREE.AdditiveBlending,
  });
  const topBeamGeometry = new THREE.ConeGeometry(0.8, 5, 24, 1, true);
  topBeamGeometry.translate(0, -2.1, 0);
  topPurpleBeam = new THREE.Mesh(topBeamGeometry, beamMatTop);
  topPurpleBeam.position.set(0, 3.05, -0.15);
  topPurpleBeam.rotation.set(0.4, 0, 0);
  topPurpleBeam.renderOrder = 1;
  scene.add(topPurpleBeam); // 添加顶部紫色光束，增强舞台氛围层次

  const floorGlowLeft = new THREE.Mesh(
    new THREE.CircleGeometry(0.6, 48),
    new THREE.MeshBasicMaterial({
      color: 0xffbb78,
      transparent: true,
      opacity: 0.16,
      side: THREE.DoubleSide,
      blending: THREE.AdditiveBlending,
    })
  );
  floorGlowLeft.rotation.x = -Math.PI / 2;
  floorGlowLeft.position.set(0, -0.893, 0.35);
  scene.add(floorGlowLeft); // 添加左侧地面光晕，突出舞台区域

  // const floorGlowRight = new THREE.Mesh(
  //   new THREE.CircleGeometry(0.6, 48),
  //   new THREE.MeshBasicMaterial({
  //     color: 0x84c5ff,
  //     transparent: true,
  //     opacity: 0.14,
  //     side: THREE.DoubleSide,
  //     blending: THREE.AdditiveBlending,
  //   })
  // );
  // floorGlowRight.rotation.x = -Math.PI / 2;
  // floorGlowRight.position.set(0.5, -0.893, 0.35);
  // scene.add(floorGlowRight); // 添加右侧地面光晕，增强对称视觉效果

  const smokeTexture = createSmokeTexture();
  const smokeCount = 220;
  const smokePositions = new Float32Array(smokeCount * 3);
  for (let i = 0; i < smokeCount; i++) {
    smokePositions[i * 3] = (Math.random() - 0.5) * 4.8;
    smokePositions[i * 3 + 1] = 0.2 + Math.random() * 1.8;
    smokePositions[i * 3 + 2] = (Math.random() - 0.5) * 2.8 - 0.3;
  }
  const smokeGeometry = new THREE.BufferGeometry();
  smokeGeometry.setAttribute('position', new THREE.BufferAttribute(smokePositions, 3));
  const smokeMaterial = new THREE.PointsMaterial({
    map: smokeTexture,
    transparent: true,
    opacity: 0.14,
    size: 0.16,
    sizeAttenuation: true,
    depthWrite: false,
    blending: THREE.AdditiveBlending,
    vertexColors: false,
  });
  smokeParticles = new THREE.Points(smokeGeometry, smokeMaterial);
  smokeParticles.frustumCulled = false;
  scene.add(smokeParticles); // 添加烟雾粒子，营造朦胧氛围效果

  const bodyMat = new THREE.MeshStandardMaterial({
    color: 0x553d2a,
    roughness: 0.68,
    metalness: 0.12,
    emissive: 0x120904,
    emissiveIntensity: 0.05,
  });

  const edgeMat = new THREE.MeshStandardMaterial({
    color: 0x2a1b11,
    roughness: 0.48,
    metalness: 0.08,
    emissive: 0x070403,
    emissiveIntensity: 0.04,
  });

  const screenFrameMat = new THREE.MeshStandardMaterial({
    color: 0x121212,
    roughness: 0.22,
    metalness: 0.4,
    emissive: 0x080808,
    emissiveIntensity: 0.05,
  });

  const glassMat = new THREE.MeshPhysicalMaterial({
    color: 0xacf0ff,
    roughness: 0.01,
    metalness: 0.05,
    transparent: true,
    opacity: 0.14,
    transmission: 0.98,
    clearcoat: 1,
    clearcoatRoughness: 0.02,
    depthWrite: false,
    side: THREE.DoubleSide,
  });

  const legMat = new THREE.MeshStandardMaterial({
    color: 0x142430,
    roughness: 0.2,
    metalness: 0.85,
  });

  const thinPanelMat = new THREE.MeshStandardMaterial({
    color: 0x375772,
    roughness: 0.16,
    metalness: 0.75,
    emissive: 0x0a1a2a,
    emissiveIntensity: 0.05,
  });

  const mainBody = new THREE.Mesh(new THREE.BoxGeometry(1.8, 1.2, 0.48), bodyMat);
  mainBody.position.y = 0;
  mainBody.castShadow = true;
  mainBody.receiveShadow = true;
  scene.add(mainBody); // 添加电视机机身主体模型

  const shell = new THREE.Mesh(new THREE.BoxGeometry(1.92, 1.3, 0.56), edgeMat);
  shell.position.y = 0.02;
  shell.castShadow = true;
  scene.add(shell); // 添加电视外壳

  const bezel = new THREE.Mesh(new THREE.BoxGeometry(1.28, 0.98, 0.06), screenFrameMat);
  bezel.position.set(0, 0.02, 0.285);
  bezel.castShadow = true;
  scene.add(bezel); // 添加屏幕边框

  const videoEl = document.createElement('video');
  videoEl.src = videoPlaylist[currentVideoIndex];
  videoEl.loop = false;
  videoEl.muted = true;
  videoEl.volume = 0.7;
  videoEl.autoplay = true;
  videoEl.playsInline = true;
  videoEl.preload = 'auto';
  videoEl.crossOrigin = 'anonymous';
  videoEl.style.display = 'none';
  document.body.appendChild(videoEl);
  videoElement = videoEl;
  videoEl.play().catch(() => {
    // 静音自动播放，等待用户交互解锁声音
  });
  videoEl.onended = () => {
    currentVideoIndex = (currentVideoIndex + 1) % videoPlaylist.length;
    videoEl.src = videoPlaylist[currentVideoIndex];
    videoEl.currentTime = 0;
    videoEl.play().catch(() => undefined);
  };

  const audioEl = document.createElement('audio');
  audioEl.src = '/audios/music.mp3';
  audioEl.loop = true;
  audioEl.muted = true;
  audioEl.autoplay = true;
  audioEl.preload = 'metadata'; // 边下边播
  audioEl.style.display = 'none';
  document.body.appendChild(audioEl);
  audioElement = audioEl;
  audioEl.play().catch(() => undefined);

  videoTexture = new THREE.VideoTexture(videoEl);
  videoTexture.minFilter = THREE.LinearFilter;
  videoTexture.magFilter = THREE.LinearFilter;
  videoTexture.format = THREE.RGBAFormat;
  videoTexture.colorSpace = THREE.SRGBColorSpace;

  const screenVideoMat = new THREE.MeshBasicMaterial({ map: videoTexture, toneMapped: false });
  const screen = new THREE.Mesh(new THREE.PlaneGeometry(1.18, 0.86), screenVideoMat);
  screen.position.set(0, 0.03, 0.315);
  scene.add(screen); // 添加视频屏幕显示面

  const glass = new THREE.Mesh(new THREE.PlaneGeometry(1.24, 0.9), glassMat);
  glass.position.set(0, 0.035, 0.33);
  scene.add(glass); // 添加电视屏幕前的玻璃保护层

  // ========== 创建吊灯组 ==========

  const lampWarm = new THREE.PointLight(0xfff1d6, 1.25, 3.4, 2);
  lampWarm.position.set(0, -0.18, 0.68);
  lampWarm.decay = 2;
  scene.add(lampWarm); // 添加电视下方暖光点光源

  // 灯泡（发光球体）
  const lampBulb = new THREE.Mesh(
    new THREE.SphereGeometry(0.08, 20, 20),
    new THREE.MeshStandardMaterial({
      color: 0xfff3d7,
      emissive: 0xffe6c0,
      emissiveIntensity: 0.75,
      roughness: 0.22,
      metalness: 0.08,
    })
  );
  lampBulb.position.set(0, 1.35, 0.22);
  // scene.add(lampBulb); // 添加电视上的灯泡装饰

  // 灯泡电线
  const lampWire = new THREE.Mesh(new THREE.CylinderGeometry(0.01, 0.01, 2, 10), new THREE.MeshStandardMaterial({ color: 0x141414, roughness: 0.35, metalness: 0.15 }));
  lampWire.position.set(0, 2.4, 0.22);
  lampWire.castShadow = true;
  lampWire.rotation.z = 0;
  scene.add(lampWire); // 添加灯泡上方的垂直电线

  // 灯泡顶部的小金属帽（装饰）
  const bulbCap = new THREE.Mesh(new THREE.CylinderGeometry(0.045, 0.05, 0.04, 12), new THREE.MeshStandardMaterial({ color: 0xaaaaaa, metalness: 0.7, roughness: 0.3 }));
  bulbCap.position.y = 0.85;

  const lampGroup = new THREE.Group();
  lampGroup.add(bulbCap);
  lampGroup.add(lampWarm);
  lampGroup.add(lampBulb);
  lampGroup.add(lampWire);

  // 设置吊灯位置（挂在场景某处）
  lampGroup.position.set(-0, 0, 0);
  scene.add(lampGroup);

  const stripCount = 5;
  for (let i = 0; i < stripCount; i++) {
    const strip = new THREE.Mesh(new THREE.BoxGeometry(0.02, 0.16, 0.52), thinPanelMat);
    strip.position.set(-0.9, 0.24 - i * 0.16, -0.01);
    strip.rotation.y = 0.08;
    scene.add(strip); // 添加电视左侧装饰条
  }

  const stripRight = new THREE.Mesh(new THREE.BoxGeometry(0.08, 0.5, 0.08), thinPanelMat);
  stripRight.position.set(0.92, 0.05, 0);
  stripRight.castShadow = true;
  scene.add(stripRight); // 添加电视右侧装饰条

  // const channelKnob = new THREE.Mesh(new THREE.CylinderGeometry(0.085, 0.085, 0.05, 24), legMat);
  // channelKnob.position.set(-0.55, -0.42, 0.37);
  // channelKnob.rotation.x = Math.PI / 2;
  // channelKnob.castShadow = true;
  // scene.add(channelKnob); // 添加频道旋钮

  // const volumeKnob = new THREE.Mesh(new THREE.CylinderGeometry(0.06, 0.06, 0.05, 24), legMat);
  // volumeKnob.position.set(-0.55, -0.55, 0.37);
  // volumeKnob.rotation.x = Math.PI / 2;
  // volumeKnob.castShadow = true;
  // scene.add(volumeKnob); // 添加音量旋钮

  powerMat = new THREE.MeshStandardMaterial({
    color: 0xff6a8d,
    emissive: 0xff4d78,
    emissiveIntensity: 0.85,
    roughness: 0.2,
    metalness: 0.3,
  });
  const powerLight = new THREE.Mesh(new THREE.SphereGeometry(0.045, 16, 16), powerMat);
  powerLight.position.set(0.68, -0.55, 0.36);
  scene.add(powerLight); // 添加电源指示灯

  const antennaMat = new THREE.MeshStandardMaterial({ color: 0x527094, metalness: 0.85, roughness: 0.2 });
  const antennaLeft = new THREE.Mesh(new THREE.CylinderGeometry(0.02, 0.02, 0.7, 6), antennaMat);
  antennaLeft.position.set(-0.2, 0.85, 0.1);
  antennaLeft.rotation.z = 0.28;
  antennaLeft.rotation.x = -0.21;
  scene.add(antennaLeft); // 添加左侧天线

  const antennaRight = antennaLeft.clone();
  antennaRight.position.set(0.2, 0.85, 0.1);
  antennaRight.rotation.z = -0.28;
  scene.add(antennaRight); // 添加右侧天线

  const ballMat = new THREE.MeshStandardMaterial({ color: 0xf2f7ff, emissive: 0x5eaaff, emissiveIntensity: 0.2 });
  const tipLeft = new THREE.Mesh(new THREE.SphereGeometry(0.045, 8, 8), ballMat);
  tipLeft.position.set(-0.31, 1.22, 0.02);
  scene.add(tipLeft); // 添加左侧天线饰球

  const tipRight = tipLeft.clone();
  tipRight.position.set(0.31, 1.22, 0.02);
  scene.add(tipRight); // 添加右侧天线饰球

  const legLeft = new THREE.Mesh(new THREE.BoxGeometry(0.14, 0.08, 0.24), legMat);
  legLeft.position.set(-0.6, -0.7, 0.2);
  legLeft.castShadow = true;
  scene.add(legLeft); // 添加电视左侧支脚

  const legRight = legLeft.clone();
  legRight.position.set(0.6, -0.7, 0.2);
  scene.add(legRight); // 添加电视右侧支脚

  const baseStand = new THREE.Mesh(new THREE.BoxGeometry(0.72, 0.04, 0.26), legMat);
  baseStand.position.set(0, -0.7, 0);
  baseStand.castShadow = true;
  scene.add(baseStand); // 添加电视底座支撑板

  const floorMat = new THREE.MeshStandardMaterial({ color: 0x7f5b35, roughness: 0.75, metalness: 0.05, emissive: 0x120800, emissiveIntensity: 0.08 });
  const floor = new THREE.Mesh(new THREE.PlaneGeometry(12, 12), floorMat);
  floor.rotation.x = -Math.PI / 2;
  floor.position.y = -0.9;
  floor.receiveShadow = true;
  scene.add(floor); // 添加地板

  const plankMat = new THREE.MeshStandardMaterial({ color: 0x5d3f26, roughness: 0.82, metalness: 0.02 });
  for (let i = 0; i < 10; i++) {
    const plank = new THREE.Mesh(new THREE.PlaneGeometry(12, 0.18), plankMat);
    plank.rotation.x = -Math.PI / 2;
    plank.position.set(0, -0.898 + i * 0.0004, -5.0 + i * 1.1);
    plank.receiveShadow = false;
    plank.renderOrder = 1;
    scene.add(plank); // 添加木板装饰，丰富地面细节
  }

  const floorGlow = new THREE.Mesh(
    new THREE.RingGeometry(1.8, 2.9, 64),
    new THREE.MeshBasicMaterial({ color: 0xffd2a7, transparent: true, opacity: 0.12, side: THREE.DoubleSide })
  );
  floorGlow.rotation.x = -Math.PI / 2;
  floorGlow.position.y = -0.892;
  scene.add(floorGlow); // 添加地面环形光晕，增强视觉焦点

  const windowFrameMat = new THREE.MeshStandardMaterial({
    color: 0x15273f,
    roughness: 0.12,
    metalness: 0.8,
    emissive: 0x0a1d2f,
    emissiveIntensity: 0.08,
  });
  const windowGlassMaterial = new THREE.MeshPhysicalMaterial({
    color: 0xbfe8ff,
    transparent: true,
    opacity: 0.12,
    transmission: 0.98,
    roughness: 0.01,
    metalness: 0.0,
    clearcoat: 1,
    clearcoatRoughness: 0.02,
    depthWrite: false,
    side: THREE.DoubleSide,
  });

  const windowFrame = new THREE.Group();
  const frameWidth = 2.3;
  const frameHeight = 1.7;
  const frameDepth = 0.12;
  const frameBorder = 0.12;
  const innerWidth = 2.16;
  const innerHeight = 1.46;

  const topBar = new THREE.Mesh(new THREE.BoxGeometry(frameWidth, frameBorder, frameDepth), windowFrameMat);
  topBar.position.set(0, frameHeight / 2 - frameBorder / 2, 0);
  topBar.castShadow = true;
  windowFrame.add(topBar);

  const bottomBar = new THREE.Mesh(new THREE.BoxGeometry(frameWidth, frameBorder, frameDepth), windowFrameMat);
  bottomBar.position.set(0, -frameHeight / 2 + frameBorder / 2, 0);
  bottomBar.castShadow = true;
  windowFrame.add(bottomBar);

  const leftBar = new THREE.Mesh(new THREE.BoxGeometry(frameBorder, innerHeight, frameDepth), windowFrameMat);
  leftBar.position.set(-frameWidth / 2 + frameBorder / 2, 0, 0);
  leftBar.castShadow = true;
  windowFrame.add(leftBar);

  const rightBar = new THREE.Mesh(new THREE.BoxGeometry(frameBorder, innerHeight, frameDepth), windowFrameMat);
  rightBar.position.set(frameWidth / 2 - frameBorder / 2, 0, 0);
  rightBar.castShadow = true;
  windowFrame.add(rightBar);

  windowFrame.position.set(2.1, 1.25, -1.75);
  scene.add(windowFrame); // 添加窗框模型，保留中间透明区域

  const windowGlass = new THREE.Mesh(new THREE.PlaneGeometry(innerWidth, innerHeight), windowGlassMaterial);
  windowGlass.position.set(2.1, 1.25, -1.7);
  windowGlass.renderOrder = 1;
  scene.add(windowGlass); // 添加窗户玻璃面，位置前置不被其他框体遮挡
  windowGlassMat = windowGlassMaterial;

  const forestGroup = new THREE.Group();
  const skyMaterial = new THREE.MeshStandardMaterial({
    color: isDark.value ? 0x0d1b32 : 0x81c9ff,
    emissive: isDark.value ? 0x122b50 : 0x95dfff,
    emissiveIntensity: 0.55,
    roughness: 0.9,
  });
  const skyPlaneMesh = new THREE.Mesh(new THREE.PlaneGeometry(1.7, 1.1), skyMaterial);
  skyPlaneMesh.position.set(2.1, 1.25, -2.02);
  forestGroup.add(skyPlaneMesh);
  skyPlane = skyPlaneMesh; // 天空背景

  const treeTrunkMat = new THREE.MeshStandardMaterial({ color: 0x4b3220, roughness: 0.8 });
  const treeLeafMat = new THREE.MeshStandardMaterial({ color: 0x1e4f2a, roughness: 0.45, metalness: 0.05 });
  for (let i = 0; i < 5; i++) {
    const trunk = new THREE.Mesh(new THREE.CylinderGeometry(0.05, 0.05, 0.45, 8), treeTrunkMat);
    trunk.position.set(1.2 + i * 0.08, 0.7, -2.04);
    trunk.rotation.y = (i % 2) * 0.15;
    const leaves = new THREE.Mesh(new THREE.ConeGeometry(0.3, 0.7, 12), treeLeafMat);
    leaves.position.set(1.2 + i * 0.08, 1.08, -2.04);
    leaves.rotation.x = Math.PI;
    forestGroup.add(trunk, leaves);
  }

  sunMesh = new THREE.Mesh(new THREE.SphereGeometry(0.16, 28, 28), new THREE.MeshStandardMaterial({ color: 0xffe17f, emissive: 0xfff1ac, emissiveIntensity: 1.2 }));
  sunMesh.position.set(1.7, 1.7, -1.95);
  forestGroup.add(sunMesh); // 添加太阳模型，作为白天背景光源效果

  moonMesh = new THREE.Mesh(new THREE.SphereGeometry(0.14, 24, 24), new THREE.MeshStandardMaterial({ color: 0xdfeaf8, emissive: 0xe7f1ff, emissiveIntensity: 0.9 }));
  moonMesh.position.set(2.55, 1.75, -1.95);
  moonMesh.visible = false;
  forestGroup.add(moonMesh); // 添加月亮模型，夜间主题时显示

  starGroup = new THREE.Group();
  for (let i = 0; i < 35; i++) {
    const star = new THREE.Mesh(new THREE.SphereGeometry(0.012, 6, 6), new THREE.MeshBasicMaterial({ color: 0xe8f8ff, transparent: true, opacity: isDark.value ? 0.8 : 0 }));
    star.position.set(1.35 + Math.random() * 1.1, 1.5 + Math.random() * 0.55, -1.95);
    starGroup.add(star);
  }
  starGroup.visible = isDark.value;
  forestGroup.add(starGroup); // 添加星星组，夜间主题时显示

  scene.add(forestGroup); // 添加森林背景组，包含天空、树木、月亮等元素

  updateThemeAppearance();

  const posterTexture = new THREE.TextureLoader().load('/images/pic.webp');
  const posterMat = new THREE.MeshStandardMaterial({ map: posterTexture, roughness: 0.65, metalness: 0.05, emissive: 0x050505, emissiveIntensity: 0.04 });
  const poster = new THREE.Mesh(new THREE.BoxGeometry(1.4, 1.6, 0.05), posterMat);
  poster.position.set(-2, 1.35, -1.8);
  poster.castShadow = true;
  scene.add(poster); // 添加海报模型到背景墙

  const dustCount = 260;
  const dustGeometry = new THREE.BufferGeometry();
  const dustPositions = new Float32Array(dustCount * 3);
  for (let i = 0; i < dustCount; i++) {
    dustPositions[i * 3] = (Math.random() - 0.5) * 8;
    dustPositions[i * 3 + 1] = (Math.random() - 0.5) * 3;
    dustPositions[i * 3 + 2] = (Math.random() - 0.5) * 5 - 1;
  }
  dustGeometry.setAttribute('position', new THREE.BufferAttribute(dustPositions, 3));
  const dustMat = new THREE.PointsMaterial({
    color: 0xc0d9ff,
    size: 0.01,
    transparent: true,
    opacity: 0.22,
  });
  dustParticles = new THREE.Points(dustGeometry, dustMat);
  dustParticles.position.y = 0.5;
  scene.add(dustParticles); // 添加尘埃粒子，增加场景细节氛围

  screenGlow = new THREE.PointLight(0x4bb8ff, 0.45, 6);
  screenGlow.position.set(0, 0.9, 0.6);
  scene.add(screenGlow); // 添加屏幕光晕，提升电视屏幕亮度效果

  let flickerInterval: number | null = null;
  // 启动屏幕与电源指示灯的闪烁效果
  const startFlicker = () => {
    flickerInterval = window.setInterval(() => {
      const intensity = 0.25 + Math.random() * 0.16;
      if (screenGlow) screenGlow.intensity = intensity;
      if (powerMat) powerMat.emissiveIntensity = 0.5 + Math.sin(Date.now() * 0.01) * 0.28;
      if (Math.random() < 0.015) {
        if (screenGlow) screenGlow.intensity = 1.1;
        setTimeout(() => {
          if (screenGlow) screenGlow.intensity = 0.45;
        }, 80);
      }
    }, 220);
  };
  startFlicker();

  // 清理闪烁效果定时器
  const cleanupFlicker = () => {
    if (flickerInterval) window.clearInterval(flickerInterval);
  };

  const dom = renderer.domElement;
  dom.addEventListener('pointerdown', onPointerDown);
  dom.addEventListener('pointermove', onPointerMove);
  dom.addEventListener('pointerup', onPointerUp);
  dom.addEventListener('touchstart', onTouchStart, { passive: true });
  dom.addEventListener('touchmove', onTouchMove, { passive: true });
  dom.addEventListener('touchend', onTouchEnd, { passive: true });
  window.addEventListener('wheel', onWheel, { passive: true });
  window.addEventListener('resize', onResize);
  window.addEventListener('pointerdown', unlockAudio, { once: true, passive: true });
  window.addEventListener('touchstart', unlockAudio, { once: true, passive: true });

  // 逐帧渲染与灯光动画效果更新
  const animate = () => {
    animationId = window.requestAnimationFrame(animate);

    currentRotationX += (targetRotationX - currentRotationX) * 0.08;
    currentRotationY += (targetRotationY - currentRotationY) * 0.08;

    const elapsed = Date.now() * 0.001;
    const musicTime = (audioElement?.currentTime ?? elapsed) * 1.2;
    const beat = Math.abs(Math.sin(musicTime * 2.5));
    const oscillationDistance = 5.5 + Math.sin(elapsed * 0.4) * 1.3;
    targetDistance = Math.max(minDistance, Math.min(maxDistance, oscillationDistance + zoomOffset));
    currentDistance += (targetDistance - currentDistance) * 0.08;

    if (stageLightLeft) {
      stageLightLeft.intensity = 5 + beat * 1.6;
      stageLightLeft.color.setHex(beat > 0.5 ? 0xff9b33 : 0xffc86c);
      (stageLightLeft.target as THREE.Object3D).position.set(-0.5, 1.1 + beat * 0.16, 0.1);
    }
    if (stageLightRight) {
      stageLightRight.intensity = 5 + (1 - beat) * 1.4;
      stageLightRight.color.setHex(beat > 0.5 ? 0x69d6ff : 0x338dff);
      (stageLightRight.target as THREE.Object3D).position.set(0.5, 1.1 + (1 - beat) * 0.16, 0.1);
    }
    if (topPurpleLight) {
      topPurpleLight.intensity = 5 + beat * 1.1;
      topPurpleLight.color.setHex(beat > 0.3 ? 0xba47ff : 0x8d3cff);
    }
    if (strobeLight) {
      const flash = Math.max(0.14, Math.sin(musicTime * 18) * 0.45);
      strobeLight.intensity = flash;
    }
    if (stageBeamLeft) {
      stageBeamLeft.material.opacity = 0.5 + beat * 0.32;
      stageBeamLeft.material.color.setHex(0xffb564);
    }
    if (stageBeamRight) {
      stageBeamRight.material.opacity = 0.5 + (1 - beat) * 0.32;
      stageBeamRight.material.color.setHex(0x62c5ff);
    }
    if (topPurpleBeam) {
      topPurpleBeam.material.opacity = 0.5 + beat * 0.28;
    }
    if (discoLight && discoLight.visible) {
      const hue = (elapsed * 0.15) % 1;
      discoLight.color.setHSL(hue, 0.85, 0.55);
      discoLight.intensity = 30.8 + Math.sin(elapsed * 6.2) * 0.35;
    }
    if (smokeParticles) {
      (smokeParticles.material as THREE.PointsMaterial).opacity = 0.06 + beat * 0.08;
      smokeParticles.rotation.y += 0.008;
      smokeParticles.position.x = Math.sin(elapsed * 0.15) * 0.05;
    }

    const orbitAngle = elapsed * 0.08;
    const yaw = orbitAngle + currentRotationY;
    if (camera) {
      camera.position.x = Math.cos(yaw) * currentDistance;
      camera.position.z = Math.sin(yaw) * currentDistance;
      camera.position.y = 1.15 + Math.sin(elapsed * 0.35) * 0.08 + currentRotationX * 0.8;
      camera.lookAt(0, 0.9, 0);
    }

    if (dustParticles) {
      dustParticles.rotation.y += 0.0012;
      dustParticles.rotation.x += 0.0005;
    }

    if (moonMesh?.visible) {
      moonMesh.position.y = 1.75 + Math.sin(elapsed * 0.25) * 0.04;
      moonMesh.position.x = 2.55 + Math.cos(elapsed * 0.32) * 0.04;
    }

    if (starGroup) {
      starGroup.children.forEach((child: THREE.Object3D) => {
        if (child instanceof THREE.Mesh) {
          (child.material as THREE.MeshBasicMaterial).opacity = isDark.value ? 0.8 : 0;
        }
      });
    }

    if (renderer && scene && camera) {
      renderer.render(scene, camera);
    }
  };
  animate();

  // 销毁与卸载时清理事件、纹理、媒体与渲染资源
  const cleanup = () => {
    dom.removeEventListener('pointerdown', onPointerDown);
    dom.removeEventListener('pointermove', onPointerMove);
    dom.removeEventListener('pointerup', onPointerUp);
    dom.removeEventListener('touchstart', onTouchStart);
    dom.removeEventListener('touchmove', onTouchMove);
    dom.removeEventListener('touchend', onTouchEnd);
    window.removeEventListener('wheel', onWheel);
    window.removeEventListener('resize', onResize);
    window.removeEventListener('pointerdown', unlockAudio);
    window.removeEventListener('touchstart', unlockAudio);
    if (videoElement) {
      videoElement.pause();
      document.body.removeChild(videoElement);
      videoElement = null;
    }
    if (audioElement) {
      audioElement.pause();
      document.body.removeChild(audioElement);
      audioElement = null;
    }
    if (videoTexture) {
      videoTexture.dispose();
      videoTexture = null;
    }
    if (smokeParticles) {
      smokeParticles.geometry.dispose();
      const smokeMat = smokeParticles.material;
      if (Array.isArray(smokeMat)) {
        smokeMat.forEach(mat => mat.dispose());
      } else {
        smokeMat.dispose();
      }
      smokeParticles = null;
    }
    if (stageBeamLeft) {
      stageBeamLeft.geometry.dispose();
      stageBeamLeft.material.dispose();
      stageBeamLeft = null;
    }
    if (stageBeamRight) {
      stageBeamRight.geometry.dispose();
      stageBeamRight.material.dispose();
      stageBeamRight = null;
    }
    if (topPurpleBeam) {
      topPurpleBeam.geometry.dispose();
      topPurpleBeam.material.dispose();
      topPurpleBeam = null;
    }
    if (dustParticles) {
      dustParticles.geometry.dispose();
      const material = dustParticles.material;
      if (Array.isArray(material)) {
        material.forEach(mat => mat.dispose());
      } else {
        material.dispose();
      }
    }
    cleanupFlicker();
    if (renderer && containerRef.value) {
      containerRef.value.removeChild(renderer.domElement);
    }
  };

  (window as any).__tvComponentCleanup = cleanup;
};

watch(isDark, updateThemeAppearance);

onMounted(() => {
  init();
});

onUnmounted(() => {
  if (animationId) window.cancelAnimationFrame(animationId);
  if ((window as any).__tvComponentCleanup) (window as any).__tvComponentCleanup();
  if (renderer) renderer.dispose();
});
</script>

<style scoped>
.tv-container {
  background: radial-gradient(circle at top, rgb(50 120 255 / 12%), transparent 28%), radial-gradient(circle at bottom right, rgb(56 200 255 / 8%), transparent 20%), #040916;
  height: 100%;
  inset: 0;
  overflow: hidden;
  position: fixed;
  width: 100%;
  z-index: 0;
}

.tv-container :deep(canvas) {
  display: block;
  height: 100%;
  width: 100%;
}
</style>

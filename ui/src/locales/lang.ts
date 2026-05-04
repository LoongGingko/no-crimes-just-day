import { useAppStore } from "@/config/app-store";
import { computed } from "vue";

const appStore = useAppStore();
const currLang = computed(() => appStore.lang);

export const t = (zh: string, en?: string) => {
  if (currLang.value === "zh") return zh;
  return en || zh;
};

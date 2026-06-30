import { invoke } from "@tauri-apps/api/core";
import type { AppData } from "../types";

export async function loadData(): Promise<AppData | null> {
  try {
    const raw = await invoke<string>("load_data");
    const parsed = JSON.parse(raw) as AppData;
    if (parsed && typeof parsed.version === "number") {
      return parsed;
    }
    return null;
  } catch {
    return null;
  }
}

export async function saveData(data: AppData): Promise<boolean> {
  try {
    await invoke("save_data", { data: JSON.stringify(data) });
    return true;
  } catch (e) {
    console.error("save failed:", e);
    return false;
  }
}

export function createDefaultAppData(): AppData {
  return {
    version: 1,
    categories: [
      { id: "work", name: "工作", color: "#3B82F6", order: 0, createdAt: Date.now() },
      { id: "life", name: "生活", color: "#10B981", order: 1, createdAt: Date.now() },
      { id: "entertainment", name: "娱乐", color: "#F59E0B", order: 2, createdAt: Date.now() },
      { id: "other", name: "其他", color: "#6B7280", order: 3, createdAt: Date.now() },
    ],
    todos: [],
    settings: {
      showCompleted: false,
      lastSelectedCategory: null,
    },
  };
}

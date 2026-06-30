import { create } from "zustand";

interface SettingsState {
  showCompleted: boolean;
  toggleShowCompleted: () => void;
  lastSelectedCategory: string | null;
  setLastSelectedCategory: (id: string | null) => void;
}

export const useSettingsStore = create<SettingsState>((set) => ({
  showCompleted: false,

  toggleShowCompleted: () => {
    set((state) => ({ showCompleted: !state.showCompleted }));
  },

  lastSelectedCategory: null,

  setLastSelectedCategory: (id) => {
    set({ lastSelectedCategory: id });
  },
}));

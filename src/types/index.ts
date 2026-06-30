export interface ProgressNote {
  id: string;
  content: string;
  createdAt: number;
}

export interface Category {
  id: string;
  name: string;
  color: string;
  order: number;
  createdAt: number;
}

export interface Todo {
  id: string;
  content: string;
  categoryId: string;
  completed: boolean;
  completedAt: number | null;
  createdAt: number;
  updatedAt: number;
  order: number;
  progressNotes: ProgressNote[];
}

export interface Settings {
  showCompleted: boolean;
  lastSelectedCategory: string | null;
}

export interface AppData {
  version: number;
  categories: Category[];
  todos: Todo[];
  settings: Settings;
}

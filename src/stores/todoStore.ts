import { create } from "zustand";
import type { Todo, Category, Settings } from "../types";
import { generateId } from "../lib/idGenerator";

const DEFAULT_CATEGORIES: Category[] = [
  { id: "work", name: "工作", color: "#3B82F6", order: 0, createdAt: Date.now() },
  { id: "life", name: "生活", color: "#10B981", order: 1, createdAt: Date.now() },
  { id: "entertainment", name: "娱乐", color: "#F59E0B", order: 2, createdAt: Date.now() },
  { id: "other", name: "其他", color: "#6B7280", order: 3, createdAt: Date.now() },
];

const DEFAULT_SETTINGS: Settings = {
  showCompleted: false,
  lastSelectedCategory: null,
};

interface TodoState {
  categories: Category[];
  todos: Todo[];
  selectedCategoryId: string | null;
  settings: Settings;

  getCategories: () => Category[];
  getTodos: (categoryId?: string | null) => Todo[];
  getIncompleteTodos: (categoryId?: string | null) => Todo[];
  getCompletedTodos: () => Todo[];

  addTodo: (content: string, categoryId: string) => void;
  completeTodo: (todoId: string) => void;
  toggleTodo: (todoId: string) => void;
  deleteTodo: (todoId: string) => void;
  updateTodoContent: (todoId: string, content: string) => void;
  addProgressNote: (todoId: string, content: string) => void;
  reorderTodos: (todoIds: string[]) => void;
  setSelectedCategory: (categoryId: string | null) => void;

  addCategory: (name: string, color: string) => void;
  updateCategory: (categoryId: string, updates: Partial<Pick<Category, "name" | "color" | "order">>) => void;
  deleteCategory: (categoryId: string) => void;

  reset: () => void;
}

export const useTodoStore = create<TodoState>((set, get) => ({
  categories: DEFAULT_CATEGORIES,
  todos: [],
  selectedCategoryId: null,
  settings: DEFAULT_SETTINGS,

  getCategories: () => get().categories.sort((a, b) => a.order - b.order),

  getTodos: (categoryId) => {
    const { todos, selectedCategoryId } = get();
    const filterId = categoryId !== undefined ? categoryId : selectedCategoryId;
    if (!filterId) return todos;
    return todos.filter((t) => t.categoryId === filterId);
  },

  getIncompleteTodos: (categoryId) => {
    return get().getTodos(categoryId).filter((t) => !t.completed);
  },

  getCompletedTodos: () => {
    return get().todos.filter((t) => t.completed);
  },

  addTodo: (content, categoryId) => {
    const now = Date.now();
    const todo: Todo = {
      id: generateId(),
      content,
      categoryId,
      completed: false,
      completedAt: null,
      createdAt: now,
      updatedAt: now,
      order: get().todos.length,
      progressNotes: [],
    };
    set((state) => ({ todos: [...state.todos, todo] }));
  },

  completeTodo: (todoId) => {
    set((state) => ({
      todos: state.todos.map((t) =>
        t.id === todoId
          ? { ...t, completed: true, completedAt: Date.now(), updatedAt: Date.now() }
          : t
      ),
    }));
  },

  toggleTodo: (todoId) => {
    set((state) => ({
      todos: state.todos.map((t) =>
        t.id === todoId
          ? {
              ...t,
              completed: !t.completed,
              completedAt: t.completed ? null : Date.now(),
              updatedAt: Date.now(),
            }
          : t
      ),
    }));
  },

  deleteTodo: (todoId) => {
    set((state) => ({
      todos: state.todos.filter((t) => t.id !== todoId),
    }));
  },

  updateTodoContent: (todoId, content) => {
    set((state) => ({
      todos: state.todos.map((t) =>
        t.id === todoId ? { ...t, content, updatedAt: Date.now() } : t
      ),
    }));
  },

  addProgressNote: (todoId, content) => {
    set((state) => ({
      todos: state.todos.map((t) =>
        t.id === todoId
          ? {
              ...t,
              updatedAt: Date.now(),
              progressNotes: [
                ...t.progressNotes,
                { id: generateId(), content, createdAt: Date.now() },
              ],
            }
          : t
      ),
    }));
  },

  reorderTodos: (todoIds) => {
    set((state) => {
      const reordered = todoIds.map((id, index) => {
        const todo = state.todos.find((t) => t.id === id);
        return todo ? { ...todo, order: index } : null;
      }).filter(Boolean) as Todo[];

      const otherTodos = state.todos.filter(
        (t) => !todoIds.includes(t.id)
      );

      return { todos: [...reordered, ...otherTodos] };
    });
  },

  setSelectedCategory: (categoryId) => {
    set({ selectedCategoryId: categoryId });
  },

  addCategory: (name, color) => {
    set((state) => {
      const maxOrder = Math.max(...state.categories.map((c) => c.order), -1);
      return {
        categories: [
          ...state.categories,
          {
            id: generateId(),
            name,
            color,
            order: maxOrder + 1,
            createdAt: Date.now(),
          },
        ],
      };
    });
  },

  updateCategory: (categoryId, updates) => {
    set((state) => ({
      categories: state.categories.map((c) =>
        c.id === categoryId ? { ...c, ...updates } : c
      ),
    }));
  },

  deleteCategory: (categoryId) => {
    const otherCategory = get().categories.find((c) => c.id !== categoryId);
    if (!otherCategory) return;

    set((state) => ({
      categories: state.categories.filter((c) => c.id !== categoryId),
      todos: state.todos.map((t) =>
        t.categoryId === categoryId
          ? { ...t, categoryId: otherCategory.id, updatedAt: Date.now() }
          : t
      ),
    }));
  },

  reset: () => {
    set({
      categories: DEFAULT_CATEGORIES,
      todos: [],
      selectedCategoryId: null,
      settings: DEFAULT_SETTINGS,
    });
  },
}));

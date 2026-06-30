import { describe, it, expect, beforeEach } from "vitest";
import { useTodoStore } from "../../src/stores/todoStore";
import type { Todo } from "../../src/types";

describe("todoStore", () => {
  beforeEach(() => {
    useTodoStore.getState().reset();
  });

  describe("initial state", () => {
    it("should have 4 default categories", () => {
      const categories = useTodoStore.getState().getCategories();
      expect(categories).toHaveLength(4);
      expect(categories.map((c) => c.name)).toEqual([
        "工作",
        "生活",
        "娱乐",
        "其他",
      ]);
    });

    it("should start with empty todos", () => {
      const todos = useTodoStore.getState().todos;
      expect(todos).toHaveLength(0);
    });

    it("should have no category selected", () => {
      expect(useTodoStore.getState().selectedCategoryId).toBeNull();
    });
  });

  describe("addTodo", () => {
    it("should add a todo with correct category", () => {
      useTodoStore.getState().addTodo("买牛奶", "life");
      const todos = useTodoStore.getState().todos;
      expect(todos).toHaveLength(1);
      expect(todos[0].content).toBe("买牛奶");
      expect(todos[0].categoryId).toBe("life");
      expect(todos[0].completed).toBe(false);
    });

    it("should generate unique IDs for each todo", () => {
      useTodoStore.getState().addTodo("任务A", "work");
      useTodoStore.getState().addTodo("任务B", "life");
      const todos = useTodoStore.getState().todos;
      expect(todos[0].id).not.toBe(todos[1].id);
    });
  });

  describe("completeTodo", () => {
    it("should mark a todo as completed", () => {
      useTodoStore.getState().addTodo("测试", "work");
      const todo = useTodoStore.getState().todos[0];
      useTodoStore.getState().completeTodo(todo.id);
      const completed = useTodoStore.getState().todos[0];
      expect(completed.completed).toBe(true);
      expect(completed.completedAt).not.toBeNull();
    });
  });

  describe("deleteTodo", () => {
    it("should remove the todo", () => {
      useTodoStore.getState().addTodo("测试", "work");
      const todo = useTodoStore.getState().todos[0];
      useTodoStore.getState().deleteTodo(todo.id);
      expect(useTodoStore.getState().todos).toHaveLength(0);
    });
  });

  describe("updateTodoContent", () => {
    it("should update content and updatedAt", () => {
      useTodoStore.getState().addTodo("旧内容", "work");
      const todo = useTodoStore.getState().todos[0];
      const oldUpdated = todo.updatedAt;

      useTodoStore.getState().updateTodoContent(todo.id, "新内容");

      const updated = useTodoStore.getState().todos[0];
      expect(updated.content).toBe("新内容");
      expect(updated.updatedAt).toBeGreaterThanOrEqual(oldUpdated);
    });
  });

  describe("addProgressNote", () => {
    it("should add a progress note to the todo", () => {
      useTodoStore.getState().addTodo("项目", "work");
      const todo = useTodoStore.getState().todos[0];

      useTodoStore.getState().addProgressNote(todo.id, "50% 完成");

      const updated = useTodoStore.getState().todos[0];
      expect(updated.progressNotes).toHaveLength(1);
      expect(updated.progressNotes[0].content).toBe("50% 完成");
    });

    it("should append multiple notes", () => {
      useTodoStore.getState().addTodo("项目", "work");
      const todo = useTodoStore.getState().todos[0];

      useTodoStore.getState().addProgressNote(todo.id, "第一版");
      useTodoStore.getState().addProgressNote(todo.id, "第二版");

      const updated = useTodoStore.getState().todos[0];
      expect(updated.progressNotes).toHaveLength(2);
      expect(updated.progressNotes[1].content).toBe("第二版");
    });
  });

  describe("getIncompleteTodos", () => {
    it("should return only incomplete todos", () => {
      useTodoStore.getState().addTodo("任务A", "work");
      useTodoStore.getState().addTodo("任务B", "work");
      const todoA = useTodoStore.getState().todos[0];
      useTodoStore.getState().completeTodo(todoA.id);

      const incomplete = useTodoStore.getState().getIncompleteTodos();
      expect(incomplete).toHaveLength(1);
      expect(incomplete[0].content).toBe("任务B");
    });
  });

  describe("getCompletedTodos", () => {
    it("should return only completed todos", () => {
      useTodoStore.getState().addTodo("任务A", "work");
      useTodoStore.getState().addTodo("任务B", "work");
      const todoA = useTodoStore.getState().todos[0];
      useTodoStore.getState().completeTodo(todoA.id);

      const completed = useTodoStore.getState().getCompletedTodos();
      expect(completed).toHaveLength(1);
      expect(completed[0].content).toBe("任务A");
    });
  });

  describe("category filter", () => {
    it("should filter todos by selected category", () => {
      useTodoStore.getState().addTodo("工作事项", "work");
      useTodoStore.getState().addTodo("生活事项", "life");

      useTodoStore.getState().setSelectedCategory("work");
      const workTodos = useTodoStore.getState().getIncompleteTodos();
      expect(workTodos).toHaveLength(1);
      expect(workTodos[0].content).toBe("工作事项");
    });
  });

  describe("category management", () => {
    it("should add a new category", () => {
      useTodoStore.getState().addCategory("学习", "#8B5CF6");
      const categories = useTodoStore.getState().getCategories();
      expect(categories).toHaveLength(5);
      expect(categories[4].name).toBe("学习");
      expect(categories[4].color).toBe("#8B5CF6");
    });

    it("should update a category", () => {
      const categories = useTodoStore.getState().categories;
      const workCat = categories[0];

      useTodoStore.getState().updateCategory(workCat.id, {
        name: "办公室",
        color: "#EF4444",
      });

      const updated = useTodoStore.getState().categories[0];
      expect(updated.name).toBe("办公室");
      expect(updated.color).toBe("#EF4444");
    });

    it("should reassign todos when deleting a category", () => {
      useTodoStore.getState().addTodo("工作事项", "work");
      useTodoStore.getState().addTodo("工作事项2", "work");

      const workCat = useTodoStore.getState().categories.find(
        (c) => c.id === "work"
      )!;
      useTodoStore.getState().deleteCategory(workCat.id);

      const todos = useTodoStore.getState().todos;
      expect(todos).toHaveLength(2);
      todos.forEach((t) => {
        expect(t.categoryId).not.toBe("work");
      });
    });

    it("should not delete the last category", () => {
      useTodoStore.getState().deleteCategory("work");
      useTodoStore.getState().deleteCategory("life");
      useTodoStore.getState().deleteCategory("entertainment");

      const lastCat = useTodoStore.getState().categories.find(
        (c) => c.id === "other"
      )!;
      useTodoStore.getState().deleteCategory(lastCat.id);

      expect(useTodoStore.getState().categories).not.toHaveLength(0);
    });
  });

  describe("reorderTodos", () => {
    it("should reorder todos by provided id array", () => {
      useTodoStore.getState().addTodo("任务A", "work");
      useTodoStore.getState().addTodo("任务B", "work");
      useTodoStore.getState().addTodo("任务C", "work");

      const todos = useTodoStore.getState().todos;
      const ids = [todos[2].id, todos[1].id, todos[0].id];

      useTodoStore.getState().reorderTodos(ids);

      const reordered = useTodoStore.getState().todos;
      expect(reordered[0].content).toBe("任务C");
      expect(reordered[1].content).toBe("任务B");
      expect(reordered[2].content).toBe("任务A");
    });
  });
});

import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  type DragEndEvent,
} from "@dnd-kit/core";
import {
  SortableContext,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { useTodoStore } from "../stores/todoStore";
import TodoItem from "./TodoItem";
import type { Category, Todo } from "../types";

interface TodoListProps {
  todos: Todo[];
  categories: Category[];
}

function EmptyState() {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-[#C7C7CC]">
      <svg
        className="w-10 h-10 mb-3 opacity-40"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={1.5}
          d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
        />
      </svg>
      <p className="text-sm">还没有 todo</p>
      <p className="text-xs mt-1">在上方输入内容，然后选择一个分类添加</p>
    </div>
  );
}

function TodoList({ todos, categories }: TodoListProps) {
  const reorderTodos = useTodoStore((s) => s.reorderTodos);
  const categoryMap = new Map(categories.map((c) => [c.id, c]));

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 8 },
    })
  );

  if (todos.length === 0) return <EmptyState />;

  const todoIds = todos.map((t) => t.id);

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    if (!over || active.id === over.id) return;

    const oldIndex = todoIds.indexOf(active.id as string);
    const newIndex = todoIds.indexOf(over.id as string);
    if (oldIndex === -1 || newIndex === -1) return;

    const reordered = [...todoIds];
    reordered.splice(oldIndex, 1);
    reordered.splice(newIndex, 0, active.id as string);
    reorderTodos(reordered);
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragEnd={handleDragEnd}
    >
      <SortableContext items={todoIds} strategy={verticalListSortingStrategy}>
        <div className="flex-1 overflow-y-auto">
          {todos.map((todo) => (
            <TodoItem
              key={todo.id}
              todo={todo}
              category={categoryMap.get(todo.categoryId)}
            />
          ))}
        </div>
      </SortableContext>
    </DndContext>
  );
}

export default TodoList;

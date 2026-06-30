import type { Todo, Category } from "../types";

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

function TodoItem({
  todo,
  category,
}: {
  todo: Todo;
  category?: Category;
}) {
  return (
    <div className="flex items-start gap-3 px-4 py-3 group hover:bg-[#F5F5F7] transition-colors">
      <button className="mt-0.5 w-5 h-5 rounded-full border-2 border-[#C7C7CC] flex-shrink-0
                         hover:border-[#3B82F6] transition-colors" />
      <div className="flex-1 min-w-0">
        <p className="text-sm text-[#1C1C1E] truncate">{todo.content}</p>
        {todo.progressNotes.length > 0 && (
          <p className="text-xs text-[#8E8E93] mt-0.5 truncate">
            └ {todo.progressNotes[todo.progressNotes.length - 1].content}
          </p>
        )}
      </div>
      {category && (
        <span
          className="w-2 h-2 rounded-full mt-1.5 flex-shrink-0"
          style={{ backgroundColor: category.color }}
        />
      )}
    </div>
  );
}

function TodoList({ todos, categories }: TodoListProps) {
  if (todos.length === 0) return <EmptyState />;

  const categoryMap = new Map(categories.map((c) => [c.id, c]));

  return (
    <div className="flex-1 overflow-y-auto">
      {todos.map((todo) => (
        <TodoItem
          key={todo.id}
          todo={todo}
          category={categoryMap.get(todo.categoryId)}
        />
      ))}
    </div>
  );
}

export default TodoList;

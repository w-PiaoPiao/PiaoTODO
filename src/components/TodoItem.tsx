import type { Todo, Category } from "../types";

interface TodoItemProps {
  todo: Todo;
  category?: Category;
}

function TodoItem({ todo, category }: TodoItemProps) {
  return (
    <div className="flex items-start gap-3 px-4 py-3 group hover:bg-[#F5F5F7] transition-colors">
      <button
        className="mt-0.5 w-5 h-5 rounded-full border-2 border-[#C7C7CC] flex-shrink-0
                   hover:border-[#3B82F6] transition-colors"
      />
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

export default TodoItem;

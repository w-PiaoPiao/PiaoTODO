import { useState, useRef, useEffect } from "react";
import { X } from "lucide-react";
import { useTodoStore } from "../stores/todoStore";
import type { Todo, Category } from "../types";

interface TodoItemProps {
  todo: Todo;
  category?: Category;
}

function TodoItem({ todo, category }: TodoItemProps) {
  const [editing, setEditing] = useState(false);
  const [editValue, setEditValue] = useState(todo.content);
  const inputRef = useRef<HTMLInputElement>(null);
  const { toggleTodo, deleteTodo, updateTodoContent } = useTodoStore();

  useEffect(() => {
    if (editing && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [editing]);

  const handleComplete = () => {
    toggleTodo(todo.id);
  };

  const handleDelete = () => {
    deleteTodo(todo.id);
  };

  const startEdit = () => {
    setEditValue(todo.content);
    setEditing(true);
  };

  const saveEdit = () => {
    const trimmed = editValue.trim();
    if (trimmed && trimmed !== todo.content) {
      updateTodoContent(todo.id, trimmed);
    }
    setEditing(false);
  };

  const cancelEdit = () => {
    setEditValue(todo.content);
    setEditing(false);
  };

  return (
    <div className="group flex items-start gap-3 px-4 py-2.5 hover:bg-[#F5F5F7] transition-colors">
      <button
        onClick={handleComplete}
        className={`mt-0.5 w-5 h-5 rounded-full border-2 flex-shrink-0 flex items-center justify-center
                    transition-all duration-200
                    ${
                      todo.completed
                        ? "bg-[#3B82F6] border-[#3B82F6]"
                        : "border-[#C7C7CC] hover:border-[#3B82F6]"
                    }`}
      >
        {todo.completed && (
          <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
          </svg>
        )}
      </button>

      <div className="flex-1 min-w-0">
        {editing ? (
          <input
            ref={inputRef}
            type="text"
            value={editValue}
            onChange={(e) => setEditValue(e.target.value)}
            onBlur={saveEdit}
            onKeyDown={(e) => {
              if (e.key === "Enter") saveEdit();
              if (e.key === "Escape") cancelEdit();
            }}
            className="w-full text-sm bg-transparent border-b border-[#3B82F6] outline-none text-[#1C1C1E] py-0.5"
          />
        ) : (
          <p
            onClick={startEdit}
            className={`text-sm transition-colors cursor-text
                        ${todo.completed ? "text-[#C7C7CC] line-through" : "text-[#1C1C1E]"}`}
          >
            {todo.content}
          </p>
        )}

        {!editing && todo.progressNotes.length > 0 && (
          <p className="text-xs text-[#8E8E93] mt-0.5 truncate">
            └ {todo.progressNotes[todo.progressNotes.length - 1].content}
          </p>
        )}
      </div>

      <div className="flex items-center gap-1.5 flex-shrink-0">
        <button
          onClick={handleDelete}
          className="opacity-0 group-hover:opacity-100 p-1 rounded-md text-[#C7C7CC]
                     hover:text-[#EF4444] hover:bg-[#FEE2E2] transition-all duration-200"
        >
          <X className="w-3.5 h-3.5" />
        </button>
        {category && (
          <span
            className="w-2 h-2 rounded-full"
            style={{ backgroundColor: category.color }}
          />
        )}
      </div>
    </div>
  );
}

export default TodoItem;

import { useState, useRef, useEffect } from "react";
import { X, ChevronDown, ChevronUp, Clock, GripVertical } from "lucide-react";
import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { useTodoStore } from "../stores/todoStore";
import { useLongPress } from "../hooks/useLongPress";
import ProgressDialog from "./ProgressDialog";
import type { Todo, Category } from "../types";

interface TodoItemProps {
  todo: Todo;
  category?: Category;
}

function TodoItem({ todo, category }: TodoItemProps) {
  const [editing, setEditing] = useState(false);
  const [editValue, setEditValue] = useState(todo.content);
  const [showHistory, setShowHistory] = useState(false);
  const [showProgressDialog, setShowProgressDialog] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const { toggleTodo, deleteTodo, updateTodoContent, addProgressNote } = useTodoStore();

  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: todo.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : undefined,
    position: "relative" as const,
    zIndex: isDragging ? 10 : undefined,
  };

  useEffect(() => {
    if (editing && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [editing]);

  const handleComplete = (e: React.MouseEvent) => {
    e.stopPropagation();
    toggleTodo(todo.id);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    deleteTodo(todo.id);
  };

  const startEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
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

  const toggleExpand = () => {
    if (!editing) {
      setShowHistory((prev) => !prev);
    }
  };

  const longPress = useLongPress({
    onLongPress: () => setShowProgressDialog(true),
    onClick: toggleExpand,
  });

  const formatTime = (ts: number) => {
    const d = new Date(ts);
    return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, "0")}:${d.getMinutes().toString().padStart(2, "0")}`;
  };

  return (
    <>
      <div
        ref={setNodeRef}
        style={style}
        {...longPress}
        className="group flex flex-col px-4 py-2.5 hover:bg-[#F5F5F7] transition-colors cursor-default"
      >
        <div className="flex items-start gap-3">
          <button
            className="mt-1 opacity-0 group-hover:opacity-100 p-0.5 rounded cursor-grab active:cursor-grabbing
                       text-[#C7C7CC] hover:text-[#8E8E93] transition-all duration-200 flex-shrink-0"
            {...attributes}
            {...listeners}
            onClick={(e) => e.stopPropagation()}
          >
            <GripVertical className="w-3.5 h-3.5" />
          </button>
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
                  e.stopPropagation();
                  if (e.key === "Enter") saveEdit();
                  if (e.key === "Escape") cancelEdit();
                }}
                onClick={(e) => e.stopPropagation()}
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
            {todo.progressNotes.length > 1 && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setShowHistory((prev) => !prev);
                }}
                className="p-1 rounded-md text-[#C7C7CC] hover:text-[#3B82F6] hover:bg-[#EFF6FF] transition-all"
              >
                {showHistory ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
              </button>
            )}
          </div>
        </div>

        {showHistory && todo.progressNotes.length > 0 && (
          <div className="ml-8 mt-2 space-y-1.5 border-l-2 border-[#E5E7EB] pl-3">
            {[...todo.progressNotes].reverse().map((note) => (
              <div key={note.id} className="flex items-start gap-2">
                <Clock className="w-3 h-3 text-[#C7C7CC] mt-0.5 flex-shrink-0" />
                <div>
                  <p className="text-xs text-[#6B7280]">{note.content}</p>
                  <p className="text-[10px] text-[#C7C7CC]">{formatTime(note.createdAt)}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {showProgressDialog && (
        <ProgressDialog
          todoContent={todo.content}
          onSubmit={(content) => addProgressNote(todo.id, content)}
          onClose={() => setShowProgressDialog(false)}
        />
      )}
    </>
  );
}

export default TodoItem;

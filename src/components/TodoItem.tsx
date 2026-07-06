import { useState, useRef, useEffect } from "react";
import { X, ChevronDown, ChevronUp, Clock, GripVertical, Bell, CheckCircle, RotateCcw } from "lucide-react";
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
  const [showPostponeMenu, setShowPostponeMenu] = useState(false);
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

  const catColor = category?.color ?? "#3B82F6";
  const isOverdue = todo.dueDate && todo.dueDate < Date.now() && !todo.completed;

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

  const longPress = useLongPress({
    onLongPress: () => setShowProgressDialog(true),
  });

  const formatDueDate = (ts: number) => {
    const d = new Date(ts);
    const today = new Date();
    const isToday = d.toDateString() === today.toDateString();
    const time = `${d.getHours().toString().padStart(2, "0")}:${d.getMinutes().toString().padStart(2, "0")}`;
    if (isToday) return `今天 ${time}`;
    return `${d.getMonth() + 1}/${d.getDate()} ${time}`;
  };

  const formatTime = (ts: number) => {
    const d = new Date(ts);
    return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, "0")}:${d.getMinutes().toString().padStart(2, "0")}`;
  };

  const postponeOneDay = () => {
    const current = todo.dueDate ?? Date.now();
    useTodoStore.getState().updateTodo(todo.id, { dueDate: current + 86400000 });
  };

  const postponeBy = (ms: number) => {
    const current = todo.dueDate ?? Date.now();
    useTodoStore.getState().updateTodo(todo.id, { dueDate: current + ms });
    setShowPostponeMenu(false);
  };

  return (
    <>
      <div
        ref={setNodeRef}
        style={{ ...style, borderLeft: `3px solid ${catColor}4D` }}
        {...longPress}
        className="group flex flex-col px-4 py-2.5 hover:bg-[#F5F5F7] dark:hover:bg-[#2C2C2E] transition-colors cursor-default relative overflow-hidden"
      >

        <div className="flex items-start gap-3 relative z-10">
          <button
            className="mt-1 opacity-0 group-hover:opacity-100 p-0.5 rounded cursor-grab active:cursor-grabbing
                       text-[#C7C7CC] dark:text-[#636366] hover:text-[#8E8E93] dark:hover:text-[#98989D] transition-all duration-200 flex-shrink-0"
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
                            : "border-[#C7C7CC] dark:border-[#636366] hover:border-[#3B82F6]"
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
                className="w-full text-sm bg-transparent border-b border-[#3B82F6] outline-none text-[#1C1C1E] dark:text-[#F5F5F5] py-0.5"
              />
            ) : (
              <p
                onClick={startEdit}
                className={`text-sm transition-colors cursor-text
                            ${todo.completed ? "text-[#C7C7CC] dark:text-[#636366] line-through" : "text-[#1C1C1E] dark:text-[#F5F5F5]"}`}
              >
                {todo.content}
              </p>
            )}

            {!editing && todo.progressNotes.length > 0 && (
              <p className="text-xs text-[#8E8E93] dark:text-[#98989D] mt-0.5 truncate">
                └ {todo.progressNotes[todo.progressNotes.length - 1].content}
              </p>
            )}
            {todo.dueDate && !editing && (
              <div className="flex items-center gap-1 mt-1">
                <Clock className={`w-3 h-3 ${isOverdue ? "text-[#EF4444]" : "text-[#3B82F6]"}`} />
                <span className={`text-[11px] ${isOverdue ? "text-[#EF4444] font-medium" : "text-[#3B82F6]"}`}>
                  {formatDueDate(todo.dueDate)}
                </span>
                {todo.reminder && <Bell className={`w-3 h-3 ${isOverdue ? "text-[#EF4444]" : "text-[#3B82F6]"}`} />}
                {isOverdue && (
                  <div className="relative inline-flex">
                    <PostponeButton
                      onPostpone1d={postponeOneDay}
                      onLongPress={() => setShowPostponeMenu(true)}
                    />
                    {showPostponeMenu && (
                      <div
                        className="absolute top-full left-0 mt-1 z-50 bg-white dark:bg-[#2C2C2E] rounded-lg shadow-xl border border-[#F0F0F0] dark:border-[#38383A] overflow-hidden"
                        onMouseLeave={() => setShowPostponeMenu(false)}
                      >
                        {[
                          { label: "30 分钟", ms: 30 * 60 * 1000 },
                          { label: "1 小时", ms: 60 * 60 * 1000 },
                          { label: "3 小时", ms: 3 * 60 * 60 * 1000 },
                          { label: "1 天", ms: 24 * 60 * 60 * 1000 },
                          { label: "3 天", ms: 3 * 24 * 60 * 60 * 1000 },
                        ].map((opt) => (
                          <button
                            key={opt.label}
                            onClick={(e) => { e.stopPropagation(); postponeBy(opt.ms); }}
                            className="block w-full text-left px-4 py-1.5 text-xs text-[#1C1C1E] dark:text-[#F5F5F5]
                                       hover:bg-[#F5F5F7] dark:hover:bg-[#3A3A3C] transition-colors whitespace-nowrap"
                          >
                            {opt.label}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="flex items-center gap-1.5 flex-shrink-0 relative z-10">
            <button
              onClick={handleDelete}
              className="opacity-0 group-hover:opacity-100 p-1 rounded-md text-[#C7C7CC] dark:text-[#636366]
                         hover:text-[#EF4444] hover:bg-[#FEE2E2] dark:hover:bg-[#3B1C1C] transition-all duration-200"
            >
              <X className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={(e) => { e.stopPropagation(); toggleTodo(todo.id); }}
              className="p-1 rounded-md text-[#C7C7CC] dark:text-[#636366]
                         hover:text-[#10B981] hover:bg-[#ECFDF5] dark:hover:bg-[#1A3A2A] transition-all duration-200"
              title="快速办结"
            >
              <CheckCircle className="w-4 h-4" />
            </button>
            {todo.progressNotes.length > 0 && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setShowHistory((prev) => !prev);
                }}
                className="p-1 rounded-md text-[#C7C7CC] dark:text-[#636366] hover:text-[#3B82F6] hover:bg-[#EFF6FF] dark:hover:bg-[#1E3A5F] transition-all"
              >
                {showHistory ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
              </button>
            )}
          </div>
        </div>

        {showHistory && todo.progressNotes.length > 0 && (
          <div className="ml-8 mt-2 space-y-1.5 border-l-2 border-[#E5E7EB] dark:border-[#38383A] pl-3 relative z-10">
            {[...todo.progressNotes].reverse().map((note) => (
              <div key={note.id} className="flex items-start gap-2">
                <Clock className="w-3 h-3 text-[#C7C7CC] dark:text-[#636366] mt-0.5 flex-shrink-0" />
                <div>
                  <p className="text-xs text-[#6B7280] dark:text-[#98989D]">{note.content}</p>
                  <p className="text-[10px] text-[#C7C7CC] dark:text-[#636366]">{formatTime(note.createdAt)}</p>
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

function PostponeButton({ onPostpone1d, onLongPress }: { onPostpone1d: () => void; onLongPress: () => void }) {
  const handlers = useLongPress({
    onLongPress,
    onClick: onPostpone1d,
  });
  return (
    <button
      {...handlers}
      className="ml-1 px-1.5 py-0.5 text-[10px] text-[#EF4444] bg-[#FEE2E2] dark:bg-[#3B1C1C] rounded
                 hover:bg-[#FECACA] dark:hover:bg-[#5B1C1C] transition-colors"
    >
      <RotateCcw className="w-3 h-3 inline mr-0.5" />
      顺延
    </button>
  );
}

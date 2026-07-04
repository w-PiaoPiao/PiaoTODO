import { useState } from "react";
import { ChevronDown, ChevronUp, Trash2 } from "lucide-react";
import type { Todo, Category } from "../types";

interface CompletedSectionProps {
  todos: Todo[];
  categories: Category[];
  onClear: () => void;
}

function CompletedSection({ todos, categories, onClear }: CompletedSectionProps) {
  const [expanded, setExpanded] = useState(false);

  if (todos.length === 0) return null;

  const sorted = [...todos].sort(
    (a, b) => (b.completedAt ?? 0) - (a.completedAt ?? 0)
  );
  const categoryMap = new Map(categories.map((c) => [c.id, c]));

  return (
    <div className="border-t border-[#F0F0F0] dark:border-[#38383A]">
      <button
        onClick={() => setExpanded((prev) => !prev)}
        className="flex items-center gap-2 w-full px-4 py-2.5 hover:bg-[#F5F5F7] dark:hover:bg-[#2C2C2E] transition-colors"
      >
        <div className="flex items-center gap-2 flex-1">
          <span className="text-xs font-medium text-[#8E8E93] dark:text-[#98989D]">已完成</span>
          <span className="text-[10px] text-[#C7C7CC] dark:text-[#636366] bg-[#F5F5F7] dark:bg-[#2C2C2E] px-1.5 py-0.5 rounded-full">
            {todos.length}
          </span>
        </div>
        {expanded ? (
          <ChevronDown className="w-3.5 h-3.5 text-[#C7C7CC] dark:text-[#636366]" />
        ) : (
          <ChevronUp className="w-3.5 h-3.5 text-[#C7C7CC] dark:text-[#636366]" />
        )}
      </button>

      {expanded && (
        <div className="pb-2">
          {sorted.map((todo) => (
            <div
              key={todo.id}
              className="flex items-center gap-3 px-4 py-2 hover:bg-[#F5F5F7] dark:hover:bg-[#2C2C2E] transition-colors"
            >
              <div className="w-5 h-5 rounded-full bg-[#3B82F6] flex items-center justify-center flex-shrink-0">
                <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <p className="flex-1 text-sm text-[#C7C7CC] dark:text-[#636366] line-through truncate">
                {todo.content}
              </p>
              {(() => {
                const cat = categoryMap.get(todo.categoryId);
                return cat ? (
                  <span
                    className="w-2 h-2 rounded-full flex-shrink-0"
                    style={{ backgroundColor: cat.color }}
                  />
                ) : null;
              })()}
            </div>
          ))}
          <button
            onClick={onClear}
            className="flex items-center gap-1.5 mx-4 mt-1 px-2 py-1 rounded-md
                       text-xs text-[#8E8E93] dark:text-[#98989D] hover:text-[#EF4444] hover:bg-[#FEE2E2] dark:hover:bg-[#3B1C1C]
                       transition-all duration-200"
          >
            <Trash2 className="w-3 h-3" />
            清空已完成
          </button>
        </div>
      )}
    </div>
  );
}

export default CompletedSection;

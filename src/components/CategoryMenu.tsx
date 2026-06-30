import { useState } from "react";
import { Palette, Pencil, Trash2, Eye } from "lucide-react";
import { useTodoStore } from "../stores/todoStore";

interface CategoryMenuProps {
  categoryId: string;
  categoryName: string;
  categoryColor: string;
  onClose: () => void;
}

const PRESET_COLORS = [
  "#3B82F6", "#10B981", "#F59E0B", "#EF4444",
  "#8B5CF6", "#EC4899", "#06B6D4", "#84CC16",
  "#F97316", "#6B7280", "#1C1C1E", "#A855F7",
];

function CategoryMenu({ categoryId, categoryName, categoryColor, onClose }: CategoryMenuProps) {
  const [mode, setMode] = useState<"menu" | "rename" | "color">("menu");
  const [renameValue, setRenameValue] = useState(categoryName);
  const { setSelectedCategory, updateCategory, deleteCategory } = useTodoStore();

  const handleRename = () => {
    if (renameValue.trim()) {
      updateCategory(categoryId, { name: renameValue.trim() });
    }
    setMode("menu");
  };

  const handleColorChange = (color: string) => {
    updateCategory(categoryId, { color });
    setMode("menu");
  };

  const handleDelete = () => {
    deleteCategory(categoryId);
    onClose();
  };

  return (
    <div
      className="absolute left-4 right-4 top-full mt-1 z-40 bg-white rounded-xl shadow-xl border border-[#F0F0F0] overflow-hidden"
      onClick={(e) => e.stopPropagation()}
    >
      {mode === "menu" && (
        <div className="py-1">
          <button
            onClick={() => { setSelectedCategory(categoryId); onClose(); }}
            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-[#1C1C1E] hover:bg-[#F5F5F7] transition-colors"
          >
            <Eye className="w-3.5 h-3.5 text-[#8E8E93]" />
            只看此类
          </button>
          <button
            onClick={() => setMode("rename")}
            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-[#1C1C1E] hover:bg-[#F5F5F7] transition-colors"
          >
            <Pencil className="w-3.5 h-3.5 text-[#8E8E93]" />
            编辑名称
          </button>
          <button
            onClick={() => setMode("color")}
            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-[#1C1C1E] hover:bg-[#F5F5F7] transition-colors"
          >
            <Palette className="w-3.5 h-3.5 text-[#8E8E93]" />
            更改颜色
          </button>
          <div className="border-t border-[#F0F0F0] mx-2 my-1" />
          <button
            onClick={handleDelete}
            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-[#EF4444] hover:bg-[#FEE2E2] transition-colors"
          >
            <Trash2 className="w-3.5 h-3.5" />
            删除分类
          </button>
        </div>
      )}

      {mode === "rename" && (
        <div className="p-3">
          <p className="text-xs text-[#8E8E93] mb-2">重命名分类</p>
          <div className="flex gap-2">
            <input
              type="text"
              value={renameValue}
              onChange={(e) => setRenameValue(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleRename();
                if (e.key === "Escape") { setMode("menu"); onClose(); }
              }}
              autoFocus
              className="flex-1 h-8 px-2 text-sm bg-[#F5F5F7] rounded-lg border-none outline-none
                         focus:ring-2 focus:ring-[#3B82F6]/30"
            />
            <button
              onClick={handleRename}
              className="px-3 h-8 text-xs text-white bg-[#3B82F6] rounded-lg hover:bg-[#2563EB]"
            >
              保存
            </button>
          </div>
        </div>
      )}

      {mode === "color" && (
        <div className="p-3">
          <p className="text-xs text-[#8E8E93] mb-2">选择颜色</p>
          <div className="flex flex-wrap gap-2">
            {PRESET_COLORS.map((c) => (
              <button
                key={c}
                onClick={() => handleColorChange(c)}
                className={`w-7 h-7 rounded-full transition-all duration-150
                           ${c === categoryColor ? "ring-2 ring-offset-1 ring-[#1C1C1E] scale-110" : "hover:scale-110"}`}
                style={{ backgroundColor: c }}
              />
            ))}
          </div>
        </div>
      )}

      <button
        onClick={onClose}
        className="w-full py-2 text-xs text-[#8E8E93] hover:bg-[#F5F5F7] border-t border-[#F0F0F0] transition-colors"
      >
        取消
      </button>
    </div>
  );
}

export default CategoryMenu;

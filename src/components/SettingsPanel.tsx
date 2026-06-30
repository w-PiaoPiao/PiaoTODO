import { useState } from "react";
import { ArrowLeft, Plus, Trash2 } from "lucide-react";
import { useTodoStore } from "../stores/todoStore";

interface SettingsPanelProps {
  onClose: () => void;
}

const PRESET_COLORS = [
  "#3B82F6", "#10B981", "#F59E0B", "#EF4444",
  "#8B5CF6", "#EC4899", "#06B6D4", "#84CC16",
  "#F97316", "#6B7280", "#1C1C1E", "#A855F7",
];

function SettingsPanel({ onClose }: SettingsPanelProps) {
  const {
    categories,
    addCategory,
    updateCategory,
    deleteCategory,
    getCategories,
  } = useTodoStore();

  const [showAdd, setShowAdd] = useState(false);
  const [newName, setNewName] = useState("");
  const [newColor, setNewColor] = useState(PRESET_COLORS[0]);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editName, setEditName] = useState("");

  const sorted = getCategories();

  const handleAdd = () => {
    if (newName.trim()) {
      addCategory(newName.trim(), newColor);
      setNewName("");
      setNewColor(PRESET_COLORS[0]);
      setShowAdd(false);
    }
  };

  const handleDelete = (id: string) => {
    if (categories.length <= 1) return;
    deleteCategory(id);
  };

  return (
    <div className="flex flex-col h-screen bg-white">
      <header className="flex items-center gap-3 px-4 py-3 border-b border-[#F0F0F0]">
        <button onClick={onClose} className="p-1 rounded-md hover:bg-[#F5F5F7] transition-colors">
          <ArrowLeft className="w-4 h-4 text-[#8E8E93]" />
        </button>
        <h1 className="text-sm font-semibold text-[#1C1C1E]">设置</h1>
      </header>

      <div className="flex-1 overflow-y-auto p-4">
        <h2 className="text-xs font-medium text-[#8E8E93] uppercase tracking-wider mb-3">分类管理</h2>

        <div className="space-y-2">
          {sorted.map((cat) => (
            <div key={cat.id} className="flex items-center gap-3 p-3 rounded-lg hover:bg-[#F5F5F7] transition-colors">
              <span
                className="w-3 h-3 rounded-full flex-shrink-0"
                style={{ backgroundColor: cat.color }}
              />
              {editingId === cat.id ? (
                <input
                  type="text"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  onBlur={() => {
                    if (editName.trim()) updateCategory(cat.id, { name: editName.trim() });
                    setEditingId(null);
                  }}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      if (editName.trim()) updateCategory(cat.id, { name: editName.trim() });
                      setEditingId(null);
                    }
                    if (e.key === "Escape") setEditingId(null);
                  }}
                  autoFocus
                  className="flex-1 text-sm bg-transparent border-b border-[#3B82F6] outline-none text-[#1C1C1E]"
                />
              ) : (
                <span
                  className="flex-1 text-sm text-[#1C1C1E] cursor-pointer"
                  onClick={() => { setEditingId(cat.id); setEditName(cat.name); }}
                >
                  {cat.name}
                </span>
              )}
              <button
                onClick={() => {
                  const current = sorted.find((c) => c.id === cat.id);
                  const colors = PRESET_COLORS;
                  const idx = colors.indexOf(current?.color ?? colors[0]);
                  const nextColor = colors[(idx + 1) % colors.length];
                  updateCategory(cat.id, { color: nextColor });
                }}
                className="w-5 h-5 rounded-full hover:scale-110 transition-transform flex-shrink-0"
                style={{ backgroundColor: cat.color }}
              />
              {categories.length > 1 && (
                <button
                  onClick={() => handleDelete(cat.id)}
                  className="p-1 rounded-md text-[#C7C7CC] hover:text-[#EF4444] hover:bg-[#FEE2E2] transition-all"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
          ))}
        </div>

        {showAdd ? (
          <div className="mt-4 p-3 rounded-lg bg-[#F5F5F7]">
            <p className="text-xs text-[#8E8E93] mb-2">添加分类</p>
            <input
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleAdd();
                if (e.key === "Escape") setShowAdd(false);
              }}
              placeholder="分类名称"
              autoFocus
              className="w-full h-8 px-2 text-sm bg-white rounded-lg border-none outline-none
                         focus:ring-2 focus:ring-[#3B82F6]/30 mb-2"
            />
            <div className="flex flex-wrap gap-1.5 mb-3">
              {PRESET_COLORS.map((c) => (
                <button
                  key={c}
                  onClick={() => setNewColor(c)}
                  className={`w-6 h-6 rounded-full transition-all ${c === newColor ? "ring-2 ring-offset-1 ring-[#1C1C1E] scale-110" : "hover:scale-110"}`}
                  style={{ backgroundColor: c }}
                />
              ))}
            </div>
            <div className="flex justify-end gap-2">
              <button
                onClick={() => setShowAdd(false)}
                className="px-3 py-1.5 text-xs text-[#8E8E93] rounded-lg hover:bg-white transition-colors"
              >
                取消
              </button>
              <button
                onClick={handleAdd}
                className="px-3 py-1.5 text-xs text-white bg-[#3B82F6] rounded-lg hover:bg-[#2563EB] transition-colors"
              >
                添加
              </button>
            </div>
          </div>
        ) : (
          <button
            onClick={() => setShowAdd(true)}
            className="flex items-center gap-2 mt-4 px-3 py-2 text-sm text-[#3B82F6] rounded-lg hover:bg-[#EFF6FF] transition-colors"
          >
            <Plus className="w-4 h-4" />
            添加分类
          </button>
        )}
      </div>
    </div>
  );
}

export default SettingsPanel;

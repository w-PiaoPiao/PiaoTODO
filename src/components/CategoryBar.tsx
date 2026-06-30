import { useTodoStore } from "../stores/todoStore";
import { useLongPress } from "../hooks/useLongPress";

interface CategoryBarProps {
  onSelect: (categoryId: string) => void;
  shaking: boolean;
}

function CategoryButton({
  name,
  color,
  active,
  onLongPress,
  onClick,
}: {
  name: string;
  color: string;
  active: boolean;
  onLongPress: () => void;
  onClick: () => void;
}) {
  const handlers = useLongPress({ onLongPress, onClick });

  return (
    <button
      {...handlers}
      className={`flex-1 h-9 mx-1 first:ml-0 last:mr-0 rounded-lg text-xs font-medium
                 text-white transition-all duration-150 active:scale-95
                 hover:brightness-110
                 ${active ? "ring-2 ring-offset-1 ring-[#1C1C1E] brightness-110" : ""}`}
      style={{ backgroundColor: color }}
    >
      {name}
    </button>
  );
}

function FilterBanner({
  categoryName,
  onCancel,
}: {
  categoryName: string;
  onCancel: () => void;
}) {
  return (
    <div className="px-4 pb-1.5 flex items-center gap-2">
      <span className="text-xs text-[#8E8E93]">
        只看：<span className="font-medium text-[#1C1C1E]">{categoryName}</span>
      </span>
      <button
        onClick={onCancel}
        className="text-xs text-[#3B82F6] hover:underline ml-auto"
      >
        取消筛选
      </button>
    </div>
  );
}

function CategoryBar({ onSelect, shaking }: CategoryBarProps) {
  const { categories, selectedCategoryId, setSelectedCategory } = useTodoStore();
  const sorted = [...categories].sort((a, b) => a.order - b.order);

  const selectedCategory = selectedCategoryId
    ? categories.find((c) => c.id === selectedCategoryId)
    : null;

  return (
    <div>
      <div
        className={`px-4 pb-2 transition-transform duration-200 ${shaking ? "animate-[shake_0.4s_ease]" : ""}`}
      >
        <div className="flex">
          {sorted.map((cat) => (
            <CategoryButton
              key={cat.id}
              name={cat.name}
              color={cat.color}
              active={cat.id === selectedCategoryId}
              onClick={() => onSelect(cat.id)}
              onLongPress={() => setSelectedCategory(cat.id)}
            />
          ))}
        </div>
      </div>
      {selectedCategory && (
        <FilterBanner
          categoryName={selectedCategory.name}
          onCancel={() => setSelectedCategory(null)}
        />
      )}
    </div>
  );
}

export default CategoryBar;

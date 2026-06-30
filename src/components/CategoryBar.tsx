const DEFAULT_CATEGORIES = [
  { id: "work", name: "工作", color: "#3B82F6" },
  { id: "life", name: "生活", color: "#10B981" },
  { id: "entertainment", name: "娱乐", color: "#F59E0B" },
  { id: "other", name: "其他", color: "#6B7280" },
];

interface Category {
  id: string;
  name: string;
  color: string;
}

interface CategoryBarProps {
  onSelect: (categoryId: string) => void;
}

function CategoryButton({
  category,
  onClick,
}: {
  category: Category;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="flex-1 h-9 mx-1 first:ml-0 last:mr-0 rounded-lg text-xs font-medium
                 text-white transition-all duration-150 active:scale-95
                 hover:brightness-110"
      style={{ backgroundColor: category.color }}
    >
      {category.name}
    </button>
  );
}

function CategoryBar({ onSelect }: CategoryBarProps) {
  return (
    <div className="px-4 pb-2">
      <div className="flex">
        {DEFAULT_CATEGORIES.map((cat) => (
          <CategoryButton
            key={cat.id}
            category={cat}
            onClick={() => onSelect(cat.id)}
          />
        ))}
      </div>
    </div>
  );
}

export default CategoryBar;

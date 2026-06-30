import { useTodoStore } from "../stores/todoStore";

interface CategoryBarProps {
  onSelect: (categoryId: string) => void;
  shaking: boolean;
}

function CategoryButton({
  name,
  color,
  onClick,
}: {
  name: string;
  color: string;
  onClick?: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="flex-1 h-9 mx-1 first:ml-0 last:mr-0 rounded-lg text-xs font-medium
                 text-white transition-all duration-150 active:scale-95
                 hover:brightness-110"
      style={{ backgroundColor: color }}
    >
      {name}
    </button>
  );
}

function CategoryBar({ onSelect, shaking }: CategoryBarProps) {
  const { categories } = useTodoStore();
  const sorted = [...categories].sort((a, b) => a.order - b.order);

  return (
    <div className={`px-4 pb-2 transition-transform duration-200 ${shaking ? "animate-[shake_0.4s_ease]" : ""}`}>
      <div className="flex">
        {sorted.map((cat) => (
          <CategoryButton
            key={cat.id}
            name={cat.name}
            color={cat.color}
            onClick={() => onSelect(cat.id)}
          />
        ))}
      </div>
    </div>
  );
}

export default CategoryBar;

import { useState } from "react";
import { Settings, CheckSquare } from "lucide-react";
import InputBox from "./components/InputBox";
import CategoryBar from "./components/CategoryBar";
import TodoList from "./components/TodoList";

function App() {
  const [todoCount] = useState(0);

  const handleAdd = (content: string) => {
    console.log("add:", content);
  };

  const handleSelectCategory = (categoryId: string) => {
    console.log("select category:", categoryId);
  };

  return (
    <div className="flex flex-col h-screen bg-white">
      <header className="flex items-center justify-between px-4 py-3 border-b border-[#F0F0F0]">
        <div className="flex items-center gap-2">
          <CheckSquare className="w-4 h-4 text-[#3B82F6]" />
          <h1 className="text-sm font-semibold text-[#1C1C1E]">LiteToDo</h1>
        </div>
        <button className="p-1 rounded-md hover:bg-[#F5F5F7] transition-colors">
          <Settings className="w-4 h-4 text-[#8E8E93]" />
        </button>
      </header>

      <InputBox onAdd={handleAdd} />
      <CategoryBar onSelect={handleSelectCategory} />
      <TodoList />

      {todoCount > 0 && (
        <footer className="border-t border-[#F0F0F0] px-4 py-2">
          <button className="text-xs text-[#8E8E93] hover:text-[#1C1C1E] transition-colors">
            ▼ 已完成（{todoCount}）
          </button>
        </footer>
      )}
    </div>
  );
}

export default App;

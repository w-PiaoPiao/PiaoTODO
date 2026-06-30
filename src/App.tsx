import { useState, useCallback, useEffect } from "react";
import { Settings, CheckSquare } from "lucide-react";
import { useTodoStore, initAutoSave } from "./stores/todoStore";
import InputBox from "./components/InputBox";
import CategoryBar from "./components/CategoryBar";
import TodoList from "./components/TodoList";
import CompletedSection from "./components/CompletedSection";
import SettingsPanel from "./components/SettingsPanel";

function App() {
  const [inputValue, setInputValue] = useState("");
  const [shakeInput, setShakeInput] = useState(false);
  const [shakeCategory, setShakeCategory] = useState(false);
  const [showSettings, setShowSettings] = useState(false);

  const {
    selectedCategoryId,
    addTodo,
    getCategories,
    getIncompleteTodos,
    getCompletedTodos,
    clearCompleted,
    hydrate,
  } = useTodoStore();

  useEffect(() => {
    hydrate();
    initAutoSave();
  }, [hydrate]);

  const triggerShake = useCallback(() => {
    setShakeInput(true);
    setShakeCategory(true);
    setTimeout(() => {
      setShakeInput(false);
      setShakeCategory(false);
    }, 500);
  }, []);

  const handleAdd = useCallback(
    (content: string) => {
      if (!selectedCategoryId) {
        triggerShake();
        return;
      }
      addTodo(content, selectedCategoryId);
      setInputValue("");
    },
    [selectedCategoryId, addTodo, triggerShake]
  );

  const handleCategorySelect = useCallback(
    (categoryId: string) => {
      if (!inputValue.trim()) {
        triggerShake();
        return;
      }
      addTodo(inputValue.trim(), categoryId);
      setInputValue("");
    },
    [inputValue, addTodo, triggerShake]
  );

  const categories = getCategories();
  const displayTodos = getIncompleteTodos();

  if (showSettings) {
    return <SettingsPanel onClose={() => setShowSettings(false)} />;
  }

  return (
    <div className="flex flex-col h-screen bg-white">
      <header className="flex items-center justify-between px-4 py-3 border-b border-[#F0F0F0]">
        <div className="flex items-center gap-2">
          <CheckSquare className="w-4 h-4 text-[#3B82F6]" />
          <h1 className="text-sm font-semibold text-[#1C1C1E]">LiteToDo</h1>
        </div>
        <button onClick={() => setShowSettings(true)} className="p-1 rounded-md hover:bg-[#F5F5F7] transition-colors">
          <Settings className="w-4 h-4 text-[#8E8E93]" />
        </button>
      </header>

      <InputBox
        value={inputValue}
        onChange={setInputValue}
        onEnter={handleAdd}
        shaking={shakeInput}
      />
      <CategoryBar
        onSelect={handleCategorySelect}
        shaking={shakeCategory}
      />
      <TodoList
        todos={displayTodos}
        categories={categories}
      />
      <CompletedSection
        todos={getCompletedTodos()}
        categories={categories}
        onClear={clearCompleted}
      />
    </div>
  );
}

export default App;

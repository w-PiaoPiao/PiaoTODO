import { useState } from "react";

interface InputBoxProps {
  onAdd: (content: string) => void;
}

function InputBox({ onAdd }: InputBoxProps) {
  const [value, setValue] = useState("");

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && value.trim()) {
      onAdd(value.trim());
      setValue("");
    }
  };

  return (
    <div className="px-4 pt-3 pb-2">
      <div className="relative">
        <input
          type="text"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="输入待办事项..."
          className="w-full h-10 px-4 text-sm bg-[#F5F5F7] rounded-lg border-none outline-none
                     placeholder:text-[#C7C7CC] text-[#1C1C1E]
                     focus:ring-2 focus:ring-[#3B82F6]/30 focus:bg-white
                     transition-all duration-200"
        />
      </div>
    </div>
  );
}

export default InputBox;

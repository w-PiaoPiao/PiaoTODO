interface InputBoxProps {
  value: string;
  onChange: (value: string) => void;
  onEnter: (content: string) => void;
  shaking: boolean;
}

function InputBox({ value, onChange, onEnter, shaking }: InputBoxProps) {
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      if (value.trim()) {
        onEnter(value.trim());
      }
    }
  };

  return (
    <div className="px-4 pt-3 pb-2">
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="输入待办事项..."
        className={`w-full h-10 px-4 text-sm bg-[#F5F5F7] rounded-lg border-none outline-none
                   placeholder:text-[#C7C7CC] text-[#1C1C1E]
                   focus:ring-2 focus:ring-[#3B82F6]/30 focus:bg-white
                   transition-all duration-200
                   ${shaking ? "animate-[shake_0.4s_ease]" : ""}`}
      />
    </div>
  );
}

export default InputBox;

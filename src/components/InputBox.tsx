interface InputBoxProps {
  value: string;
  onChange: (value: string) => void;
  shaking: boolean;
}

function InputBox({ value, onChange, shaking }: InputBoxProps) {
  return (
    <div className="px-4 pt-3 pb-2">
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder="输入待办事项，点击下方分类按钮添加..."
        className={`w-full h-10 px-4 text-sm bg-[#F5F5F7] dark:bg-[#2C2C2E] rounded-lg border-none outline-none
                   placeholder:text-[#C7C7CC] dark:placeholder:text-[#636366] text-[#1C1C1E] dark:text-[#F5F5F5]
                   focus:ring-2 focus:ring-[#3B82F6]/30 focus:bg-white dark:focus:bg-[#2C2C2E]
                   transition-all duration-200
                   ${shaking ? "animate-[shake_0.4s_ease]" : ""}`}
      />
    </div>
  );
}

export default InputBox;

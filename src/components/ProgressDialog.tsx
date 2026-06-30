import { useState, useRef, useEffect } from "react";

interface ProgressDialogProps {
  todoContent: string;
  onSubmit: (content: string) => void;
  onClose: () => void;
}

function ProgressDialog({ todoContent, onSubmit, onClose }: ProgressDialogProps) {
  const [value, setValue] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    setTimeout(() => inputRef.current?.focus(), 50);
  }, []);

  const handleSubmit = () => {
    if (value.trim()) {
      onSubmit(value.trim());
    }
    onClose();
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/20"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-xl shadow-xl p-4 w-72 mx-4"
        onClick={(e) => e.stopPropagation()}
      >
        <p className="text-sm font-medium text-[#1C1C1E] mb-1 truncate">{todoContent}</p>
        <p className="text-xs text-[#8E8E93] mb-3">添加进度记录</p>
        <input
          ref={inputRef}
          type="text"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") handleSubmit();
            if (e.key === "Escape") onClose();
          }}
          placeholder="输入最新进展..."
          className="w-full h-9 px-3 text-sm bg-[#F5F5F7] rounded-lg border-none outline-none
                     placeholder:text-[#C7C7CC] text-[#1C1C1E]
                     focus:ring-2 focus:ring-[#3B82F6]/30 focus:bg-white
                     transition-all duration-200"
        />
        <div className="flex justify-end gap-2 mt-3">
          <button
            onClick={onClose}
            className="px-3 py-1.5 text-xs text-[#8E8E93] rounded-lg hover:bg-[#F5F5F7] transition-colors"
          >
            取消
          </button>
          <button
            onClick={handleSubmit}
            className="px-3 py-1.5 text-xs text-white bg-[#3B82F6] rounded-lg
                       hover:bg-[#2563EB] transition-colors"
          >
            添加
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProgressDialog;

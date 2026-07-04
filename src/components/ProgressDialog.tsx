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
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 dark:bg-black/50"
      onClick={onClose}
    >
      <div
        className="bg-white dark:bg-[#2C2C2E] rounded-xl shadow-xl p-4 w-72 mx-4"
        onClick={(e) => e.stopPropagation()}
      >
        <p className="text-sm font-medium text-[#1C1C1E] dark:text-[#F5F5F5] mb-1 truncate">{todoContent}</p>
        <p className="text-xs text-[#8E8E93] dark:text-[#98989D] mb-3">添加进度记录</p>
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
          className="w-full h-9 px-3 text-sm bg-[#F5F5F7] dark:bg-[#3A3A3C] dark:text-[#F5F5F5] rounded-lg border-none outline-none
                     placeholder:text-[#C7C7CC] dark:placeholder:text-[#636366] text-[#1C1C1E] dark:text-[#F5F5F5]
                     focus:ring-2 focus:ring-[#3B82F6]/30 focus:bg-white dark:focus:bg-[#3A3A3C]
                     transition-all duration-200"
        />
        <div className="flex justify-end gap-2 mt-3">
          <button
            onClick={onClose}
            className="px-3 py-1.5 text-xs text-[#8E8E93] dark:text-[#98989D] rounded-lg hover:bg-[#F5F5F7] dark:hover:bg-[#3A3A3C] transition-colors"
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

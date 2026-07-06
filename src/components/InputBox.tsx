import { useState } from "react";
import { Clock, X } from "lucide-react";
import DueDatePicker from "./DueDatePicker";

interface InputBoxProps {
  value: string;
  onChange: (value: string) => void;
  shaking: boolean;
  dueDate: number | null;
  reminder: boolean;
  onDueDateChange: (dueDate: number | null, reminder: boolean) => void;
}

function InputBox({ value, onChange, shaking, dueDate, reminder, onDueDateChange }: InputBoxProps) {
  const [showPicker, setShowPicker] = useState(false);

  const formatDueDate = (ts: number) => {
    const d = new Date(ts);
    const today = new Date();
    const isToday = d.toDateString() === today.toDateString();
    const time = `${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
    if (isToday) return `今天 ${time}`;
    return `${d.getMonth() + 1}/${d.getDate()} ${time}`;
  };

  return (
    <div className="px-4 pt-3 pb-2">
      <div className="flex gap-2 items-center">
        <div className="relative flex-1">
          <input
            type="text"
            value={value}
            onChange={(e) => onChange(e.target.value)}
            placeholder="输入待办事项..."
            className={`w-full h-10 px-4 text-sm bg-[#F5F5F7] dark:bg-[#2C2C2E] rounded-lg border-none outline-none
                       placeholder:text-[#C7C7CC] dark:placeholder:text-[#636366] text-[#1C1C1E] dark:text-[#F5F5F5]
                       focus:ring-2 focus:ring-[#3B82F6]/30 focus:bg-white dark:focus:bg-[#2C2C2E]
                       transition-all duration-200 pr-10
                       ${shaking ? "animate-[shake_0.4s_ease]" : ""}`}
          />
        </div>
        <button
          onClick={() => setShowPicker(true)}
          className={`flex-shrink-0 w-10 h-10 rounded-lg flex items-center justify-center transition-all
                     ${dueDate
                       ? "bg-[#3B82F6] text-white"
                       : "bg-[#F5F5F7] dark:bg-[#2C2C2E] text-[#8E8E93] hover:text-[#3B82F6]"}`}
        >
          <Clock className="w-4 h-4" />
        </button>
      </div>

      {dueDate && (
        <div className="flex items-center gap-1 mt-1.5 ml-1">
          <span className="text-[11px] text-[#3B82F6] bg-[#EFF6FF] dark:bg-[#1E3A5F] px-2 py-0.5 rounded-full">
            {formatDueDate(dueDate)} {reminder && "🔔"}
          </span>
          <button
            onClick={() => onDueDateChange(null, false)}
            className="p-0.5 rounded-full hover:bg-[#F5F5F7] dark:hover:bg-[#3A3A3C] transition-colors"
          >
            <X className="w-3 h-3 text-[#8E8E93]" />
          </button>
        </div>
      )}

      {showPicker && (
        <DueDatePicker
          dueDate={dueDate}
          reminder={reminder}
          onChange={onDueDateChange}
          onClose={() => setShowPicker(false)}
        />
      )}
    </div>
  );
}

export default InputBox;

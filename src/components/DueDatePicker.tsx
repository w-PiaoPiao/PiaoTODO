import { useState } from "react";
import { Bell, BellOff } from "lucide-react";

interface DueDatePickerProps {
  dueDate: number | null;
  reminder: boolean;
  onChange: (dueDate: number | null, reminder: boolean) => void;
  onClose: () => void;
}

function DueDatePicker({ dueDate, reminder, onChange, onClose }: DueDatePickerProps) {
  const [dateStr, setDateStr] = useState(() => {
    const d = dueDate ? new Date(dueDate) : new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  });
  const [timeStr, setTimeStr] = useState(() => {
    const d = dueDate ? new Date(dueDate) : new Date();
    return `${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
  });
  const [hasReminder, setHasReminder] = useState(reminder);

  const handleConfirm = () => {
    const ts = new Date(`${dateStr}T${timeStr}:00`).getTime();
    if (isNaN(ts)) {
      onChange(Date.now(), hasReminder);
    } else {
      onChange(ts, hasReminder);
    }
    onClose();
  };

  const handleClear = () => {
    onChange(null, false);
    onClose();
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 dark:bg-black/50"
      onClick={onClose}
    >
      <div
        className="bg-white dark:bg-[#2C2C2E] rounded-xl shadow-xl p-5 w-72 mx-4"
        onClick={(e) => e.stopPropagation()}
      >
        <h3 className="text-sm font-medium text-[#1C1C1E] dark:text-[#F5F5F5] mb-4">设置到期时间</h3>

        <label className="text-xs text-[#8E8E93] dark:text-[#98989D] block mb-1">日期</label>
        <input
          type="date"
          value={dateStr}
          onChange={(e) => setDateStr(e.target.value)}
          className="w-full h-9 px-3 text-sm bg-[#F5F5F7] dark:bg-[#3A3A3C] dark:text-[#F5F5F5] rounded-lg border-none outline-none mb-3
                     focus:ring-2 focus:ring-[#3B82F6]/30"
        />

        <label className="text-xs text-[#8E8E93] dark:text-[#98989D] block mb-1">时间</label>
        <input
          type="time"
          value={timeStr}
          onChange={(e) => setTimeStr(e.target.value)}
          className="w-full h-9 px-3 text-sm bg-[#F5F5F7] dark:bg-[#3A3A3C] dark:text-[#F5F5F5] rounded-lg border-none outline-none mb-4
                     focus:ring-2 focus:ring-[#3B82F6]/30"
        />

        <button
          onClick={() => setHasReminder(!hasReminder)}
          className={`flex items-center gap-2 w-full px-3 py-2 rounded-lg text-sm transition-colors mb-4
                     ${hasReminder
                       ? "bg-[#EFF6FF] dark:bg-[#1E3A5F] text-[#3B82F6]"
                       : "bg-[#F5F5F7] dark:bg-[#3A3A3C] text-[#8E8E93] dark:text-[#98989D]"}`}
        >
          {hasReminder ? <Bell className="w-4 h-4" /> : <BellOff className="w-4 h-4" />}
          {hasReminder ? "闹钟提醒（已开启）" : "闹钟提醒（已关闭）"}
        </button>

        <div className="flex justify-between gap-2">
          <button
            onClick={handleClear}
            className="px-3 py-1.5 text-xs text-[#EF4444] rounded-lg hover:bg-[#FEE2E2] dark:hover:bg-[#3B1C1C] transition-colors"
          >
            清除时间
          </button>
          <div className="flex gap-2 ml-auto">
            <button
              onClick={onClose}
              className="px-3 py-1.5 text-xs text-[#8E8E93] dark:text-[#98989D] rounded-lg hover:bg-[#F5F5F7] dark:hover:bg-[#3A3A3C] transition-colors"
            >
              取消
            </button>
            <button
              onClick={handleConfirm}
              className="px-3 py-1.5 text-xs text-white bg-[#3B82F6] rounded-lg hover:bg-[#2563EB] transition-colors"
            >
              确定
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DueDatePicker;

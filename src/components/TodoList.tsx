function EmptyState() {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-[#C7C7CC]">
      <svg
        className="w-10 h-10 mb-3 opacity-40"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={1.5}
          d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
        />
      </svg>
      <p className="text-sm">还没有 todo</p>
      <p className="text-xs mt-1">在上方输入内容，然后选择一个分类添加</p>
    </div>
  );
}

function TodoList() {
  return (
    <div className="flex-1 overflow-y-auto">
      <EmptyState />
    </div>
  );
}

export default TodoList;

# LiteToDo 技术规范

## 技术选型

| 层级 | 技术 | 说明 |
|------|------|------|
| 跨平台框架 | Tauri v2 | Rust 后端 + Webview 前端，体积小、启动快 |
| 前端框架 | React 18 + TypeScript | 组件化开发，类型安全 |
| 样式 | Tailwind CSS | 原子化 CSS，快速构建界面 |
| 状态管理 | Zustand | 轻量状态管理，自动持久化 |
| 拖拽排序 | @dnd-kit/sortable | 现代拖拽库，支持触控 |
| 图标 | Lucide React | 简洁统一的图标 |
| 构建工具 | Vite | 与 Tauri 集成良好，开发体验好 |

## 选择理由

### Tauri

- 安装包体积小（通常 < 10MB）
- 内存占用远低于 Electron
- 原生支持 macOS 菜单栏、全局快捷键、本地文件读写
- 前端代码可高度复用到未来 Windows 和手机版本

### React + TypeScript

- 生态成熟，招聘/维护成本低
- TypeScript 提供类型安全，减少运行时错误
- 组件化便于未来跨平台复用

### Zustand

- API 极简，学习成本低
- 无需 Provider 嵌套
- 可配合中间件实现持久化

## 项目目录结构

```
liteToDo/
├── src/
│   ├── App.tsx                 # 根组件
│   ├── main.tsx                # 入口
│   ├── components/             # 可复用组件
│   │   ├── TodoItem.tsx
│   │   ├── TodoList.tsx
│   │   ├── CategoryBar.tsx
│   │   ├── CategoryButton.tsx
│   │   ├── InputBox.tsx
│   │   ├── CompletedSection.tsx
│   │   ├── SettingsPanel.tsx
│   │   └── ProgressDialog.tsx
│   ├── stores/                 # Zustand 状态管理
│   │   ├── todoStore.ts
│   │   └── settingsStore.ts
│   ├── hooks/                  # 自定义 Hooks
│   │   ├── useLongPress.ts
│   │   └── useAutoSave.ts
│   ├── lib/                    # 工具函数
│   │   ├── storage.ts          # Tauri FS 封装
│   │   ├── idGenerator.ts      # ID 生成
│   │   └── validators.ts       # 数据校验
│   ├── types/                  # TypeScript 类型
│   │   └── index.ts
│   └── styles/
│       └── index.css
├── src-tauri/                  # Tauri Rust 后端
│   ├── src/
│   │   └── main.rs
│   ├── Cargo.toml
│   └── tauri.conf.json
├── tests/                      # 测试
│   ├── unit/
│   └── e2e/
├── docs/                       # 项目文档
├── dev-logs/                   # 开发日志
├── public/                     # 静态资源
├── index.html
├── package.json
├── vite.config.ts
├── tailwind.config.js
└── tsconfig.json
```

## 核心依赖版本约束

- Node.js >= 20
- pnpm >= 9（包管理器）
- tauri-cli >= 2.0
- react >= 18.3
- typescript >= 5.4
- tailwindcss >= 3.4
- zustand >= 4.5

## Tauri 权限配置

需要在 `src-tauri/capabilities/` 中申请以下权限：

- `fs:read` 和 `fs:write`：读写本地数据文件
- `core:menu` 和 `core:tray`：菜单栏图标与托盘
- `core:window`：控制弹窗显示/隐藏
- `global-shortcut`：全局快捷键

## 数据存储策略

- 数据文件路径：`{appDataDir}/data.json`
- 每次状态变更 300ms 后自动保存（防抖）
- 启动时读取数据文件，若不存在则初始化默认数据
- 每次保存前创建 `.data.json.bak` 备份
- 数据格式版本号管理，便于未来迁移

## 性能考虑

- 使用 `useMemo` / `useCallback` 优化列表渲染
- todo 数量较多时采用虚拟列表（未来可扩展）
- 自动保存使用防抖，避免频繁写入磁盘
- 图片/图标使用 SVG，减少资源体积

## 安全考虑

- 不收集用户数据
- 所有数据本地存储
- 禁用前端对文件系统的直接访问，所有文件操作通过 Tauri 命令
- 输入内容做 XSS 过滤（React 默认已转义）

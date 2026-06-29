# LiteToDo macOS 版实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个轻量级 macOS 菜单栏待办清单应用，支持预设分类添加、进度记录、拖拽排序和本地持久化。

**Architecture:** 使用 Tauri v2 作为跨平台壳，React + TypeScript + Tailwind CSS 构建 UI，Zustand 管理状态，Tauri FS API 实现本地 JSON 持久化。菜单栏图标和全局快捷键通过 Tauri 的 tray 和 global-shortcut 插件实现。

**Tech Stack:** Tauri v2, React 18, TypeScript, Tailwind CSS, Zustand, @dnd-kit/sortable, Lucide React, Vite, Vitest.

## Global Constraints

- Node.js >= 20
- pnpm >= 9
- 每次只推进一个 Phase，Phase 结束后必须可运行、可测试
- 核心逻辑必须写单元测试，UI 交互必须手动验证
- 每个 Phase 完成后立即提交 Git
- 所有数据本地存储，本期不做云同步
- 分类按钮必须位于输入框下方、列表上方
- 所有 todo 和进度记录时间戳但不显示

---

## Phase 0: 项目脚手架与文档初始化

**目标：** 搭建 Tauri + React + TypeScript 项目骨架，配置好开发环境。

**验收标准：**
- 项目能 `pnpm install` 成功
- `pnpm tauri dev` 能启动一个空白窗口
- TypeScript 严格模式已开启
- Tailwind CSS 已配置
- Vitest 已配置并能运行一个示例测试

**Files:**
- Create: `package.json`, `vite.config.ts`, `tsconfig.json`, `tailwind.config.js`, `index.html`
- Create: `src/main.tsx`, `src/App.tsx`, `src/styles/index.css`
- Create: `src-tauri/src/main.rs`, `src-tauri/Cargo.toml`, `src-tauri/tauri.conf.json`
- Create: `tests/unit/example.test.ts`

**Steps:**

- [ ] **Step 1: 初始化 Tauri 项目**

```bash
pnpm create tauri-app@latest . --template react-ts --manager pnpm
```

- [ ] **Step 2: 安装额外依赖**

```bash
pnpm add zustand lucide-react @dnd-kit/core @dnd-kit/sortable @dnd-kit/utilities
pnpm add -D vitest @testing-library/react @testing-library/jest-dom jsdom
```

- [ ] **Step 3: 配置 Tailwind CSS 和 Vitest**

修改 `vite.config.ts` 添加 Vitest 配置，修改 `tailwind.config.js` 添加 content 路径。

- [ ] **Step 4: 运行示例测试**

```bash
pnpm test
```

Expected: 测试通过。

- [ ] **Step 5: 启动开发模式**

```bash
pnpm tauri dev
```

Expected: 空白窗口正常显示。

- [ ] **Step 6: 提交**

```bash
git add .
git commit -m "chore: initialize Tauri + React project"
```

---

## Phase 1: 基础 UI 布局

**目标：** 实现菜单栏面板的基础布局结构。

**验收标准：**
- 面板显示标题栏、输入框、分类按钮占位区、列表占位区
- 布局符合设计规范中的线框图
- 分类按钮区位于输入框下方、列表上方

**Files:**
- Create: `src/components/InputBox.tsx`
- Create: `src/components/CategoryBar.tsx`
- Create: `src/components/TodoList.tsx`
- Modify: `src/App.tsx`

**Steps:**

- [ ] **Step 1: 创建 InputBox 组件**

输入框固定高度，带 placeholder。

- [ ] **Step 2: 创建 CategoryBar 组件**

显示四个占位按钮，使用 flex 布局平均分布。

- [ ] **Step 3: 创建 TodoList 组件**

显示占位 todo 项，支持滚动。

- [ ] **Step 4: 组合到 App.tsx**

按设计规范布局组合。

- [ ] **Step 5: 手动验证**

运行 `pnpm tauri dev`，确认布局正确。

- [ ] **Step 6: 提交**

```bash
git commit -m "feat: add basic panel layout"
```

---

## Phase 2: 状态管理与数据类型

**目标：** 定义应用数据类型和 Zustand store 骨架。

**验收标准：**
- TypeScript 类型完整定义 Category, Todo, ProgressNote, AppData
- Zustand store 能创建默认数据
- 能通过 store 读取分类和 todo
- 相关单元测试通过

**Files:**
- Create: `src/types/index.ts`
- Create: `src/stores/todoStore.ts`
- Create: `src/stores/settingsStore.ts`
- Create: `tests/unit/todoStore.test.ts`

**Steps:**

- [ ] **Step 1: 定义类型**

按 `docs/design.md` 中的数据结构定义类型。

- [ ] **Step 2: 创建 todoStore**

包含默认分类和空 todo 列表，提供读取方法。

- [ ] **Step 3: 写单元测试**

测试 store 初始状态和读取方法。

- [ ] **Step 4: 运行测试**

```bash
pnpm test
```

Expected: PASS。

- [ ] **Step 5: 提交**

```bash
git commit -m "feat: add data types and Zustand stores"
```

---

## Phase 3: 分类按钮与添加 todo

**目标：** 实现通过分类按钮添加 todo 的核心交互。

**验收标准：**
- 输入框输入内容后，点击分类按钮能添加 todo
- 新 todo 出现在列表顶部
- 空输入时点击按钮有反馈（按钮抖动或提示）
- todo 正确关联到对应分类

**Files:**
- Modify: `src/components/InputBox.tsx`
- Modify: `src/components/CategoryBar.tsx`
- Create: `src/components/CategoryButton.tsx`
- Modify: `src/stores/todoStore.ts`
- Create: `tests/unit/addTodo.test.ts`

**Steps:**

- [ ] **Step 1: 实现 addTodo action**

参数：`content: string, categoryId: string`。

- [ ] **Step 2: 写添加 todo 测试**

测试正常添加、空内容拒绝、分类正确关联。

- [ ] **Step 3: 更新 CategoryButton**

按钮显示分类名称和颜色，点击调用 addTodo。

- [ ] **Step 4: 更新 InputBox**

维护本地输入状态，添加后清空。

- [ ] **Step 5: 手动验证**

运行应用，输入内容并点击分类按钮，确认 todo 出现。

- [ ] **Step 6: 提交**

```bash
git commit -m "feat: add todo via category buttons"
```

---

## Phase 4: 列表展示与分类筛选

**目标：** 实现 todo 列表展示和长按分类按钮筛选。

**验收标准：**
- 默认显示所有未完成 todo
- 长按分类按钮后只显示该分类 todo
- 再次操作可取消筛选
- 列表显示 todo 内容和最新进度

**Files:**
- Create: `src/components/TodoItem.tsx`
- Modify: `src/components/TodoList.tsx`
- Modify: `src/components/CategoryBar.tsx`
- Create: `src/hooks/useLongPress.ts`
- Modify: `src/stores/todoStore.ts`

**Steps:**

- [ ] **Step 1: 创建 useLongPress hook**

支持鼠标长按和触控长按，触发阈值 500ms。

- [ ] **Step 2: 实现筛选 action**

store 增加 `selectedCategoryId` 状态和 `setSelectedCategoryId` action。

- [ ] **Step 3: 实现 TodoItem 组件**

显示内容、最新进度、左侧完成圆圈。

- [ ] **Step 4: 更新 CategoryBar**

长按分类按钮触发筛选。

- [ ] **Step 5: 手动验证**

添加多个分类的 todo，长按筛选确认只显示对应分类。

- [ ] **Step 6: 提交**

```bash
git commit -m "feat: todo list display and category filter"
```

---

## Phase 5: 完成、删除、编辑 todo

**目标：** 实现 todo 的基础操作。

**验收标准：**
- 点击圆圈标记完成，todo 从当前列表消失
- 右键 todo 可删除
- 单击 todo 文字可编辑，Enter 保存，Esc 取消
- 以上操作都有单元测试覆盖

**Files:**
- Modify: `src/components/TodoItem.tsx`
- Modify: `src/stores/todoStore.ts`
- Create: `tests/unit/todoActions.test.ts`

**Steps:**

- [ ] **Step 1: 实现 completeTodo action**

设置 completed 为 true 并记录 completedAt。

- [ ] **Step 2: 实现 deleteTodo action**

从列表中移除。

- [ ] **Step 3: 实现 updateTodoContent action**

更新内容和 updatedAt。

- [ ] **Step 4: 写单元测试**

覆盖完成、删除、编辑三种操作。

- [ ] **Step 5: 更新 UI**

TodoItem 支持三种交互。

- [ ] **Step 6: 手动验证**

- [ ] **Step 7: 提交**

```bash
git commit -m "feat: complete, delete, and edit todo"
```

---

## Phase 6: 进度记录

**目标：** 实现长按 todo 添加进度和查看历史进度。

**验收标准：**
- 长按 todo 弹出输入框，可输入进度文字
- 最新进度显示在 todo 下方
- 点击 todo 可展开查看全部历史进度
- 进度记录时间戳但不显示

**Files:**
- Create: `src/components/ProgressDialog.tsx`
- Modify: `src/components/TodoItem.tsx`
- Modify: `src/stores/todoStore.ts`
- Create: `tests/unit/progressNotes.test.ts`

**Steps:**

- [ ] **Step 1: 实现 addProgressNote action**

参数：`todoId: string, content: string`。

- [ ] **Step 2: 写进度测试**

测试添加进度、查看最新进度。

- [ ] **Step 3: 创建 ProgressDialog**

简单的输入弹窗。

- [ ] **Step 4: 更新 TodoItem**

长按触发 ProgressDialog，点击展开/收起历史进度。

- [ ] **Step 5: 手动验证**

- [ ] **Step 6: 提交**

```bash
git commit -m "feat: add progress notes to todos"
```

---

## Phase 7: 已完成列表

**目标：** 实现已完成 todo 的折叠区域。

**验收标准：**
- 已完成 todo 移入底部折叠区
- 默认折叠，点击展开
- 按完成时间倒序排列
- 提供「清空已完成」按钮

**Files:**
- Create: `src/components/CompletedSection.tsx`
- Modify: `src/App.tsx`
- Modify: `src/stores/todoStore.ts`

**Steps:**

- [ ] **Step 1: 实现 clearCompleted action**

删除所有 completed=true 的 todo。

- [ ] **Step 2: 创建 CompletedSection**

显示标题和数量，支持展开/收起。

- [ ] **Step 3: 写测试**

测试已完成列表过滤和清空。

- [ ] **Step 4: 手动验证**

- [ ] **Step 5: 提交**

```bash
git commit -m "feat: completed todos section"
```

---

## Phase 8: 拖拽排序

**目标：** 实现 todo 列表的拖拽排序。

**验收标准：**
- 未完成列表支持拖拽排序
- 排序结果持久化保存
- 在分类筛选模式下也支持排序

**Files:**
- Modify: `src/components/TodoList.tsx`
- Modify: `src/components/TodoItem.tsx`
- Modify: `src/stores/todoStore.ts`

**Steps:**

- [ ] **Step 1: 集成 @dnd-kit**

使用 SortableContext 包裹列表。

- [ ] **Step 2: 实现 reorderTodos action**

参数：新的 id 数组，更新 order 字段。

- [ ] **Step 3: 写排序测试**

- [ ] **Step 4: 手动验证拖拽**

- [ ] **Step 5: 提交**

```bash
git commit -m "feat: drag and drop todo reordering"
```

---

## Phase 9: 本地持久化

**目标：** 实现数据的本地 JSON 持久化。

**验收标准：**
- 每次状态变更后自动保存到本地文件
- 重启应用后数据不丢失
- 首次启动时使用默认数据
- 保存失败时显示提示

**Files:**
- Create: `src/lib/storage.ts`
- Create: `src/lib/idGenerator.ts`
- Modify: `src/stores/todoStore.ts`
- Modify: `src-tauri/src/main.rs` 添加 Tauri 命令
- Modify: `src-tauri/capabilities/` 添加权限

**Steps:**

- [ ] **Step 1: 实现 Tauri 读写命令**

在 Rust 端添加 `load_data` 和 `save_data` 命令。

- [ ] **Step 2: 实现 storage.ts**

封装 Tauri invoke 调用。

- [ ] **Step 3: 在 store 中集成自动保存**

使用 Zustand subscribe + 防抖。

- [ ] **Step 4: 启动时加载数据**

在 App.tsx 或 store init 中调用 load。

- [ ] **Step 5: 测试持久化**

添加 todo，关闭应用，重启后确认数据存在。

- [ ] **Step 6: 提交**

```bash
git commit -m "feat: persist data to local JSON file"
```

---

## Phase 10: 菜单栏集成与快捷键

**目标：** 将应用改造为菜单栏小工具，并支持全局快捷键。

**验收标准：**
- 应用启动后只在菜单栏显示图标，不显示 Dock 图标
- 点击菜单栏图标弹出主面板
- 点击面板外区域关闭面板
- `Cmd + Shift + T` 可打开/关闭面板
- `Esc` 关闭面板

**Files:**
- Modify: `src-tauri/src/main.rs`
- Modify: `src-tauri/tauri.conf.json`
- Modify: `src/main.tsx`
- Modify: `src/App.tsx`

**Steps:**

- [ ] **Step 1: 配置 Tauri 菜单栏 Tray**

创建 tray 图标和点击事件。

- [ ] **Step 2: 创建/隐藏主窗口**

点击 tray 图标时显示/隐藏窗口。

- [ ] **Step 3: 注册全局快捷键**

使用 `global-shortcut` 插件。

- [ ] **Step 4: 配置无 Dock 图标**

修改 `tauri.conf.json` 的 `activationPolicy`。

- [ ] **Step 5: 手动验证**

- [ ] **Step 6: 提交**

```bash
git commit -m "feat: menubar tray and global shortcut"
```

---

## Phase 11: 设置与分类管理

**目标：** 实现分类的自定义管理。

**验收标准：**
- 长按分类按钮可弹出管理菜单
- 可修改分类名称和颜色
- 可添加新分类
- 可删除分类（至少保留 1 个）
- 删除分类时，该分类下 todo 自动移入「其他」

**Files:**
- Create: `src/components/SettingsPanel.tsx`
- Create: `src/components/CategoryMenu.tsx`
- Modify: `src/components/CategoryBar.tsx`
- Modify: `src/stores/todoStore.ts`

**Steps:**

- [ ] **Step 1: 实现分类 CRUD actions**

添加、编辑、删除分类。

- [ ] **Step 2: 创建 CategoryMenu**

长按分类按钮弹出的菜单。

- [ ] **Step 3: 创建 SettingsPanel**

分类管理入口。

- [ ] **Step 4: 写分类管理测试**

- [ ] **Step 5: 手动验证**

- [ ] **Step 6: 提交**

```bash
git commit -m "feat: category management and settings"
```

---

## Phase 12: 打包、优化与收尾

**目标：** 完成应用打包和发布准备。

**验收标准：**
- `pnpm tauri build` 能生成 .app 文件
- 应用图标、名称正确
- 无明显 UI 错位或交互 bug
- 文档和开发日志已同步更新

**Files:**
- Modify: `src-tauri/icons/`
- Modify: `src-tauri/tauri.conf.json`
- Modify: `docs/execution-plan.md`
- Modify: `dev-logs/YYYY-MM-DD.md`

**Steps:**

- [ ] **Step 1: 准备应用图标**

替换默认 Tauri 图标。

- [ ] **Step 2: 配置应用信息**

名称、版本、identifier。

- [ ] **Step 3: 运行生产构建**

```bash
pnpm tauri build
```

- [ ] **Step 4: 全面手动测试**

- [ ] **Step 5: 更新文档和日志**

- [ ] **Step 6: 最终提交**

```bash
git commit -m "chore: prepare v0.1.0 release"
```

---

## 执行方式

### 推荐：Subagent-Driven

每个 Phase 作为一个独立任务，由子代理执行。当前 Phase 完成并测试通过后，再进入下一个 Phase。每个 Phase 开始前更新 `dev-logs/YYYY-MM-DD.md`。

### 备选：Inline Execution

在当前会话中按 Phase 顺序执行，每个 Phase 结束后进行人工检查点确认。

## 附录：Spec 覆盖检查

| 需求 | 对应 Phase |
|------|-----------|
| 菜单栏小工具 | Phase 10 |
| 分类按钮添加 todo | Phase 3 |
| 长按分类筛选 | Phase 4 |
| 完成/删除/编辑 | Phase 5 |
| 进度记录 | Phase 6 |
| 已完成列表 | Phase 7 |
| 拖拽排序 | Phase 8 |
| 本地持久化 | Phase 9 |
| 分类管理 | Phase 11 |
| 快捷键 | Phase 10 |

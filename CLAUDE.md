# LiteToDo 项目指引

> 本文件面向所有参与 LiteToDo 开发的 AI 代理和开发者。
> 项目目标：构建一款轻量级、跨平台待办清单应用。首期 macOS 菜单栏版本。

## 项目信息

- **产品名称**：LiteToDo
- **首期平台**：macOS（菜单栏小工具）
- **远期平台**：Windows、HarmonyOS、Android、iOS
- **核心特色**：通过底部预设分类按钮替代传统「+」号按钮，输入内容后直接点击分类添加 todo

## 标准文档路径

所有项目标准和规范都存放在 `docs/` 目录下。做任何开发前，请先阅读相关文档：

| 文档 | 路径 | 说明 |
|------|------|------|
| 产品需求 | `docs/requirements.md` | 功能需求、优先级、非功能性需求 |
| 设计规范 | `docs/design.md` | 界面布局、交互设计、数据结构、视觉风格 |
| 技术规范 | `docs/tech-spec.md` | 技术选型、项目结构、依赖版本、存储策略 |
| 工作流 | `docs/workflow.md` | 开发原则、Git 规范、测试策略、代码规范 |
| 实施计划 | `docs/execution-plan.md` | 分阶段开发计划，每个 Phase 独立可测试 |

## 开发日志路径

每日开发进展记录在 `dev-logs/` 目录：

- `dev-logs/README.md`：日志使用规范
- `dev-logs/YYYY-MM-DD.md`：每日具体开发日志

每次开发会话结束时，必须更新当天的开发日志。

## 工作说明

### 1. 开始前必读

- 如果你是新加入的 AI 代理，先阅读本文件和 `docs/` 下的所有标准文档。
- 重点理解产品的核心交互：分类按钮位置、长按筛选、长按进度、已完成列表。
- 明确当前处于哪个 Phase，查看 `docs/execution-plan.md` 中对应 Phase 的验收标准。

### 2. 开发节奏

- **每次只推进一个 Phase**，不要跨 Phase 一次性做太多。
- 每个 Phase 结束后必须满足验收标准，并通过手动或自动测试。
- 每个 Phase 完成后立即提交 Git。
- 优先写单元测试，UI 交互必须手动验证。

### 3. 与用户沟通

- 项目发起人不懂代码，请使用通俗语言解释技术决策。
- 做 UI 调整前，尽量用文字线框图或简单示意图确认。
- 遇到设计/需求冲突时，优先回到 `docs/design.md` 和 `docs/requirements.md` 确认。

### 4. 文档同步

当发生以下变更时，必须同步更新对应文档：

- 需求变更 → `docs/requirements.md`
- 设计调整 → `docs/design.md`
- 技术选型或结构变更 → `docs/tech-spec.md`
- 阶段推进或计划调整 → `docs/execution-plan.md`
- 工作流程变更 → `docs/workflow.md`
- 每日开发 → `dev-logs/YYYY-MM-DD.md`

### 5. 当前状态

- **Phase 0 已完成**：Tauri + React + TypeScript 项目骨架就绪。
- **Phase 1 已完成**：基础 UI 布局（标题栏、输入框、分类按钮、列表区）。
- **Phase 2 已完成**：状态管理与数据类型定义。
- **Phase 3 已完成**：通过分类按钮添加 todo。
- **Phase 4 已完成**：列表展示与分类筛选。
- **Phase 5 已完成**：完成、删除、编辑 todo。
- **Phase 6 已完成**：进度记录。
- **Phase 7 已完成**：已完成列表。
- **Phase 8 已完成**：拖拽排序。
- **Phase 9 已完成**：本地持久化。
- **Phase 10 已完成**：菜单栏集成与快捷键。
- **Phase 11 已完成**：设置与分类管理。
- 下一步将进入 **Phase 12：打包与收尾**。
- 当前阶段目标参考 `docs/execution-plan.md` 中对应 Phase 的验收标准。

## 快速链接

- [产品需求](./docs/requirements.md)
- [设计规范](./docs/design.md)
- [技术规范](./docs/tech-spec.md)
- [工作流](./docs/workflow.md)
- [实施计划](./docs/execution-plan.md)
- [开发日志](./dev-logs/README.md)

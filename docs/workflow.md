# LiteToDo 开发工作流

## 开发原则

- **小步快跑**：每次只完成一个可独立运行、可测试的功能点
- **先写计划再写代码**：每个阶段开始前更新执行计划，明确当前任务边界
- **频繁提交**：每个小功能完成后立即提交，便于回滚
- **测试驱动**：核心逻辑优先写单元测试，UI 交互优先手动测试
- **保持简单**：不提前做未来才需要的功能（YAGNI）

## 每日开发流程

1. **开始前**：查看 `dev-logs/YYYY-MM-DD.md`，确认前一天遗留的待办
2. **规划中**：明确今天要做的 1-3 个小任务
3. **执行中**：按 `docs/execution-plan.md` 的阶段推进
4. **结束时**：更新开发日志，记录完成事项、遗留问题、下一步计划

## Git 提交规范

提交消息格式：`<type>: <subject>`

| type | 含义 |
|------|------|
| `feat` | 新功能 |
| `fix` | 修复 bug |
| `docs` | 文档更新 |
| `style` | 代码格式调整 |
| `refactor` | 重构 |
| `test` | 测试相关 |
| `chore` | 构建/工具/依赖 |

示例：

- `feat: add category bar with add-todo action`
- `fix: prevent empty todo creation`
- `docs: update design spec for progress notes`

## 代码规范

- 使用 TypeScript 严格模式
- 组件文件使用 PascalCase（如 `TodoItem.tsx`）
- 工具函数使用 camelCase（如 `useLongPress.ts`）
- 每个文件单一职责，超过 200 行考虑拆分
- 优先使用函数组件 + Hooks
- 避免 any 类型，必须使用时加注释说明

## 测试策略

### 单元测试

- 覆盖 stores 中的核心逻辑：添加、完成、删除、筛选、排序
- 覆盖工具函数：ID 生成、数据校验、数据迁移
- 使用 Vitest 作为测试运行器

### 集成测试

- Tauri 命令的读取/写入数据测试
- 数据迁移测试

### 手动测试

- 菜单栏图标点击、面板显示/隐藏
- 长按分类按钮筛选
- 长按 todo 添加进度
- 拖拽排序
- 已完成列表折叠/展开

## 代码审查清单

- [ ] 功能是否符合当前阶段的验收标准
- [ ] 是否有未处理的边界情况
- [ ] 是否引入了不必要的依赖
- [ ] 是否有 console.log 等调试代码残留
- [ ] 测试是否通过
- [ ] 开发日志是否已更新

## 文档同步

当发生以下变更时，必须同步更新对应文档：

- 需求变更 → `docs/requirements.md`
- 设计调整 → `docs/design.md`
- 技术选型或项目结构变更 → `docs/tech-spec.md`
- 阶段推进或计划调整 → `docs/execution-plan.md`
- 工作流程变更 → `docs/workflow.md`

## 问题记录

开发中遇到的坑、决策记录、未解决的问题，统一记录到当天开发日志的「Notes」部分。每周回顾一次，转化为文档或任务。

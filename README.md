# SMS Briefing System

企业内部短信简讯管理平台，目标覆盖：通讯录、群组、模板、简讯录入、发送任务、查询追踪、统计审计。

## 技术栈
- Frontend: React + Vite + Ant Design
- Backend: Spring Boot 3 + MyBatis + MySQL

## 当前进度
- ✅ 阶段1：补齐系统架构设计文档
- ✅ 阶段2：搭建可运行前后端基础工程
- ✅ 阶段3：联系人 / 群组 / 模板 / 发送任务 前后端闭环
- ✅ 阶段4：简讯录入与详情流真实接入
- 🚧 阶段5：全局联调、文档收口、体验完善

## 目录结构
```text
.
├── frontend/    # React + Vite 前端
├── backend/     # Spring Boot 后端
├── docs/        # 架构/计划/决策文档
├── docker-compose.yml
└── TODO.md
```

## 本地开发准备

### 1) 启动 MySQL
推荐直接使用仓库内的 Docker Compose：

```bash
docker compose up -d mysql
```

默认数据库信息：
- host: `127.0.0.1`
- port: `3306`
- database: `sms_briefing`
- username: `sms_user`
- password: `sms_pass`
- root password: `root`

`db/init/01-schema.sql` 会在容器首次启动时自动初始化。

### 2) 启动 Backend
```bash
cd backend
mvn spring-boot:run
```

默认端口：`http://localhost:8080`

支持以下环境变量覆盖：
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`

如需保留一份本地样例配置：

```bash
cd backend
cp src/main/resources/application-local.example.yml src/main/resources/application-local.yml
```

### 3) 启动 Frontend
```bash
cd frontend
npm install
npm run dev
```

默认端口：`http://localhost:5173`

## 已实现能力
- 仪表盘聚合接口 `/api/dashboard`
- 通讯录接口 `/api/contacts`（完整 CRUD + 搜索）
- 群组接口 `/api/groups`（完整 CRUD + 搜索）
- 模板接口 `/api/templates`（完整 CRUD + 搜索）
- 发送任务接口 `/api/tasks`（完整 CRUD + 搜索）
- 简讯接口 `/api/briefings`（完整 CRUD + 搜索）
- React + Ant Design 页面已接入真实后端 API
- Briefing 编辑提交后可进入详情页

## 文档
- `docs/project-plan.md`：阶段计划
- `docs/architecture.md`：架构设计与 API 规划
- `docs/architecture-decisions.md`：架构与本地开发决策记录
- `backend/README.md`：后端配置说明
- `db/init/01-schema.sql`：MySQL 初始化脚本
- `TODO.md`：下一阶段待办

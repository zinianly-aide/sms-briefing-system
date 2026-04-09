# Backend 开发说明

## 配置方式
默认配置在 `src/main/resources/application.yml`，支持通过环境变量覆盖：

- `DB_HOST`，默认 `localhost`
- `DB_PORT`，默认 `3306`
- `DB_NAME`，默认 `sms_briefing`
- `DB_USERNAME`，默认 `root`
- `DB_PASSWORD`，默认 `root`
- `SERVER_PORT`，默认 `8080`

如需保留一份本地样例，可复制：

```bash
cp src/main/resources/application-local.example.yml src/main/resources/application-local.yml
```

然后按本机 MySQL 账号修改。

## 数据初始化
建表脚本位于：

- `../db/init/01-schema.sql`

使用 `docker-compose.yml` 启动 MySQL 时会自动挂载并初始化。

## 本地运行
```bash
mvn spring-boot:run
```

如需使用自定义环境变量：

```bash
DB_USERNAME=sms_user DB_PASSWORD=sms_pass mvn spring-boot:run
```

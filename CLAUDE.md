# CLAUDE.md

本文档为 Claude Code（claude.ai/code）在处理此仓库中的代码时提供指导。

## 构建与运行命令

```bash
# 构建项目（编译 + 测试）
./gradlew build

# 运行应用程序
./gradlew bootRun

# 仅运行测试
./gradlew test

# 运行单个测试类
./gradlew test --tests "cn.jee.Jee2026ExamAApplicationTests"

# 单独启动 HSQLDB 数据库（如果数据库未运行，bootRun 前需要执行此命令）
./gradlew dbStart

# 打开 HSQLDB 数据库管理器 GUI
./gradlew dbManager

# 优雅关闭 HSQLDB 数据库
./gradlew dbShutdown
```

`bootRun` 连接数据库前，数据库必须处于运行状态。请先在另一个终端中使用 `dbStart` 启动数据库，否则应用程序在启动时将无法连接。

## 项目架构

**Spring Boot 4.0.3 + Java 17** Web 应用程序，用于管理电影观看记录。考试项目（jee2026_exam_stu）。

### 分层结构

```
Controller (@Controller) → Service (@Service) → DAO (@Repository) → HSQLDB（嵌入式）
```

- **控制器** — `UserController`（登录）、`MovieController`（增删改查 + 图片上传）。处理 HTTP 请求/响应、Session 操作、参数校验，返回 Thymeleaf 视图名称。
- **Service** — `UserService`、`MovieService`。负责业务编排和数据转换（如将图片文件系统路径转换为 Web URL），中转 Controller 到 DAO 的调用。
- **DAO** — 直接使用 `JdbcTemplate`（无 JPA 仓库）。SQL 语句内联编写，职责仅限于数据访问。
- **配置** — `WebConfig`（实现 `WebMvcConfigurer`，将 `D:/tools/images/` 映射为 `/images/**` 静态资源路径）。
- **实体** — `Movie`（Lombok `@Builder` + `@Data`）、`User`（仅 Lombok `@Data`）。
- **校验** — 对 `Movie` 字段使用 Jakarta Bean Validation。通过 `ValidationMessages.properties` 实现国际化。
- **异常处理** — `@RestControllerAdvice` 全局捕获 `IOException`；`MovieController` 上的 `@ExceptionHandler` 捕获 `SQLException`。

### 数据库

HSQLDB 文件模式，包含两张表：
- `USER_DATA` — `NAME`（主键，varchar）
- `USER_MOVIE` — `WATCH_DATE`（主键，varchar 11）、`USER_NAME`（外键 → USER_DATA）、`MOVIE_NAME`、`TICKET_PRICE`、`IMAGES`（空格分隔的路径列表）、`COMMENT`

通过 `spring.jpa.hibernate.ddl-auto=update` 自动更新 DDL（Hibernate 管理表结构）。

### 关键 URL

| URL | 用途 |
|-----|------|
| `http://localhost:8080/` | 登录页面 |
| `http://localhost:8080/movie/load-all` | 电影列表（登录后） |
| `http://localhost:8080/movie/add-movie` | 添加电影表单 |
| `http://localhost:8080/movie/upload-images` | 上传电影图片 |
| `http://localhost:8080/movie/look-images?id=<watchDate>` | 查看图片（Thymeleaf 页面展示） |
| `http://localhost:8080/movie/delete-movie?id=<watchDate>` | 删除电影记录及关联图片 |
| `http://localhost:8080/movie/delete-image?id=<watchDate>&imageUrl=<url>` | 删除单张图片 |
| `http://localhost:8080/images/<filename>` | 直接访问上传的图片文件（静态资源） |

### 图片处理

图片上传至 `D:/tools/images/`（可通过 `application.properties` 中的 `default.images.path` 配置）。文件路径以空格分隔的字符串形式存储在 `IMAGES` 列中。

- **自动创建目录** — 上传时若 `D:/tools/images/` 不存在则自动创建
- **UUID 命名** — 上传的图片自动使用 UUID + 原扩展名重命名，避免中文/空格导致的 URL 问题
- **自动清理** — 查看图片时检查磁盘文件是否存在，不存在的自动从数据库移除
- **删除联动** — 删除电影时级联删除关联图片文件；删除单张图片时同步从数据库和磁盘删除

## 约定

- **三层架构** — Controller → Service → DAO。Controller 处理 HTTP 相关逻辑，Service 负责业务编排，DAO 负责数据访问。
- **全面使用 Lombok** — DTO 和实体使用 `@Data`，`Movie` 使用 `@Builder`。
- **直接字段访问** — DAO 使用 `JdbcTemplate.query()` 配合行映射器，通过 `Movie.builder()` 构造实体。
- **基于会话的认证** — 用户名存储在 `HttpSession` 属性 `"username"` 中。无密码，无 Spring Security。
- **校验** — 控制器端通过 `@Validated` + `BindingResult` 进行服务端校验，错误信息通过 `errorMessages` 请求属性转发到表单视图。
- **Thymeleaf 模板** — 位于 `src/main/resources/templates/`。静态资源位于 `src/main/resources/static/`。
- **图片资源映射** — 通过 `WebConfig` 将外部目录 `D:/tools/images/` 映射为 `/images/**` URL 路径，浏览器可直接访问。

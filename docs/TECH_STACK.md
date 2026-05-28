# 技术栈版本说明

## 后端（已升级）

| 组件 | 原版本 | 现版本 |
|------|--------|--------|
| Spring Boot | 2.2.1 | **2.7.18** |
| Spring Cloud | Hoxton | **2021.0.9** |
| Spring Cloud Alibaba | 2.2.0 | **2021.0.5.0** |
| MyBatis-Plus | 3.3.1 | **3.5.5** |
| MySQL 驱动 | 5.1.46 (`mysql-connector-java`) | **8.0.33** (`mysql-connector-j`) |
| JSON | fastjson 1.2.29 | **fastjson2 2.0.43** |
| API 文档 | springfox 2.7 | **springdoc-openapi 1.7** |
| Java | 8 | **8**（保持，便于迁移） |

### API 文档地址

- 微服务：`http://localhost:<port>/swagger-ui.html`
- 分组：`webApi`（`/api/**`）、`adminApi`（`/admin/**`）

### 编译

```bash
cd yygh
mvn -DskipTests clean package
```

需 **JDK 8+** 与 **Maven 3.6+**。

## 前端（已升级）

| 项目 | 原版本 | 现版本 |
|------|--------|--------|
| Vue | 2.5.17 | **2.7.16** |
| Nuxt | 2.0 | **2.17.3** |
| axios | 0.18 / 0.19 | **1.7.x** |
| Element UI | 2.12 | **2.15.14** |
| node-sass | 4.x | **sass (dart-sass) 1.77** |
| Node 要求 | >=6 | **>=18** |

### 前端安装

```bash
cd yygh/yygh-site && npm install && npm run dev
cd yygh/hospital_admin && npm install && npm run dev
```

`hospital_admin` 在 Node 18+ 下通过 `NODE_OPTIONS=--openssl-legacy-provider` 兼容 Webpack 4。

## 尚未升级（后续可选）

- **Spring Boot 3.x**：需 Java 17+、`javax` → `jakarta` 全量迁移
- **Nuxt 3 / Vue 3**：需重写前台与后台前端
- **Webpack 5**（管理端）：可进一步去掉 `openssl-legacy-provider`

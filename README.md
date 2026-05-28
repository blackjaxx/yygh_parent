# 尚医通（yygh）— 网上预约挂号系统

尚医通是一个完整的网上预约挂号平台，旨在缓解"看病难、挂号难"的就医难题。患者可以通过线上平台随时随地搜索医院、选择科室和排班、完成预约挂号，无需到医院排长队。系统支持微信扫码登录、微信支付，涵盖医院数据管理、排班管理、订单管理和数据统计等完整业务闭环。

**🆕 近期新增功能：** 医院收藏、就诊评价/评分、智能科室推荐（导诊）、医院对比、体检套餐、医生团队展示、预约记录导出 Excel。

---

## 目录

- [项目架构](#项目架构)
- [目录结构](#目录结构)
- [技术栈](#技术栈)
- [微服务说明](#微服务说明)
- [数据库设计](#数据库设计)
- [快速开始](#快速开始)
  - [1. 环境要求](#1-环境要求)
  - [2. 环境变量配置](#2-环境变量配置)
  - [3. 启动基础设施](#3-启动基础设施)
  - [4. 初始化 MongoDB 数据](#4-初始化-mongodb-数据)
  - [5. 启动后端微服务](#5-启动后端微服务)
  - [6. 启动前端应用](#6-启动前端应用)
- [访问地址一览](#访问地址一览)
- [业务流程](#业务流程)
- [API 接口文档](#api-接口文档)
- [常见问题](#常见问题)
- [安全说明](#安全说明)
- [后续升级规划](#后续升级规划)

---

## 项目架构

```
┌─────────────────────────────────────────────────────────┐
│                     用户端 (Nuxt 2)                       │
│                  http://localhost:3000                   │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                API 网关 (Spring Cloud Gateway)            │
│                  http://localhost:8080                   │
│           路由分发 · 鉴权过滤 · 跨域处理 · 限流           │
└──────┬──────────────┬──────────────┬────────────────────┘
       │              │              │
┌──────▼──────┐ ┌─────▼─────┐ ┌─────▼──────┐
│ service_user│ │service_hosp│ │service_order│  ... 8个微服务
│  用户服务    │ │  医院服务   │ │  订单服务    │
└──────┬──────┘ └─────┬─────┘ └─────┬──────┘
       │              │              │
┌──────▼──────────────▼──────────────▼────────────────────┐
│                     基础设施层                             │
│  Nacos(注册中心) · RabbitMQ(消息队列) · Redis(缓存)       │
│  MySQL(关系型) · MongoDB(文档型)                          │
└─────────────────────────────────────────────────────────┘
```

系统采用 **Spring Cloud 微服务架构**，前后端分离设计。前端包含用户预约端和管理后台，后端由 8 个独立微服务组成，通过 Nacos 实现服务注册与发现，OpenFeign 实现服务间调用，Spring Cloud Gateway 作为统一入口网关。

---

## 目录结构

```
cupr/
├── .env.example              # 环境变量模板（复制为 .env 后填写真实密钥）
├── .nvmrc                    # Node.js 版本要求（18）
├── docker-compose.yml        # Docker Compose 一键启动所有基础设施
├── README.md                 # 本文件
├── docs/
│   └── TECH_STACK.md         # 技术栈版本详细说明
├── scripts/
│   └── export-env.sh         # 环境变量加载脚本
└── yygh/                     # 主项目
    ├── pom.xml               # 父级 Maven POM（统一依赖版本管理）
    ├── README.md             # 项目 API 接口文档
    ├── common/               # 公共模块
    │   ├── common_util/      #   通用工具类（JWT、MD5、异常处理、Result 等）
    │   ├── service_util/     #   服务工具类（HTTP 请求、Redis 配置等）
    │   └── rabbitMQ_util/    #   RabbitMQ 工具（MQ 常量、配置、RabbitService）
    ├── model/                # 数据模型模块（Entity 类、枚举）
    ├── service/              # 业务微服务模块
    │   ├── service_cmn/      #   数据字典服务
    │   ├── service_hosp/     #   医院服务（医院/科室/排班，MongoDB）
    │   ├── service_msm/      #   短信服务（阿里云/腾讯云）
    │   ├── service_order/    #   订单服务（预约/支付）
    │   ├── service_oss/      #   文件服务（阿里云 OSS）
    │   ├── service_statistics/ # 统计服务
    │   ├── service_task/     #   定时任务服务
    │   └── service_user/     #   用户服务（微信登录/就诊人）
    ├── service_client/       # Feign 远程调用客户端模块
    │   ├── service_cmn_client/   #   字典服务客户端
    │   ├── service_hosp_client/  #   医院服务客户端
    │   ├── service_order_client/ #   订单服务客户端
    │   └── service_user_client/  #   用户服务客户端
    ├── service_gateway/      # API 网关（Spring Cloud Gateway）
    ├── hospital-manage/      # 医院对接模拟端（Spring Boot + Thymeleaf）
    ├── hospital_admin/       # 管理后台前端（Vue 2 + Element UI）
│   ├── yygh-site/            # 用户预约端前端（Nuxt 2 + Element UI）
    │   └── sql/                  # SQL 脚本（表结构 + 初始化数据 + 测试数据 + 新功能表）
```

---

## 技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **后端框架** | Spring Boot | 2.7.18 | 微服务基础框架 |
| **微服务治理** | Spring Cloud | 2021.0.9 | 服务治理体系 |
| | Spring Cloud Alibaba | 2021.0.5.0 | Nacos 注册/配置中心 |
| **API 网关** | Spring Cloud Gateway | — | 统一入口、鉴权、路由 |
| **服务调用** | OpenFeign | — | 声明式 HTTP 客户端 |
| **ORM** | MyBatis-Plus | 3.5.5 | 数据库操作框架 |
| **消息队列** | RabbitMQ | 3.x (Docker) | 异步消息、号源更新 |
| **关系型数据库** | MySQL | 5.7 (Docker) | 订单、用户、字典等 |
| **文档型数据库** | MongoDB | 4.4 (Docker) | 医院、科室、排班数据 |
| **缓存** | Redis | 6-alpine (Docker) | 会话缓存、支付二维码 |
| **服务注册** | Nacos | 2.1.0 (Docker) | 服务注册与配置中心 |
| **API 文档** | springdoc-openapi | 1.7.0 | Swagger UI 文档 |
| **JSON** | fastjson2 | 2.0.43 | JSON 序列化/反序列化 |
| **JWT** | JJWT | 0.9.1 | Token 认证 |
| **对象存储** | 阿里云 OSS | 3.17.4 | 文件上传 |
| **短信** | 阿里云短信 SDK | — | 短信通知 |
| **支付** | 微信支付 | — | 扫码支付 |
| **前端框架** | Vue.js | 2.7.16 | 渐进式前端框架 |
| | Nuxt.js | 2.17.3 | SSR 服务端渲染（用户端） |
| **UI 组件** | Element UI | 2.15.14 | 桌面端组件库 |
| **HTTP 客户端** | Axios | 1.7.x | HTTP 请求库 |
| **构建工具** | Webpack 4 / Nuxt Builder | — | 前端打包 |
| **样式** | Sass | 1.77 | CSS 预处理器 |
| **基础设施** | Docker + Docker Compose | — | 容器化部署 |

> 详细版本说明及升级记录见 [docs/TECH_STACK.md](docs/TECH_STACK.md)

---

## 微服务说明

### 业务微服务（8 个）

| 模块 | 端口范围 | 职责 | 主要数据源 |
|------|---------|------|-----------|
| `service_cmn` | 8202 | 数据字典管理（省市、医院类型等基础编码） | MySQL |
| `service_hosp` | 8201 | 医院信息、科室、排班管理 | MongoDB |
| `service_msm` | 8204 | 短信发送服务（阿里云/腾讯云） | — |
| `service_order` | 8206 | 预约下单、微信支付、订单状态管理 | MySQL |
| `service_oss` | 8205 | 阿里云 OSS 文件上传 | — |
| `service_statistics` | 8208 | 订单统计、数据报表 | MySQL |
| `service_task` | 8207 | 定时任务（就诊提醒等） | MySQL |
| `service_user` | 8203 | 微信登录、就诊人管理、医院收藏、就诊评价 | MySQL |

### 公共服务

| 模块 | 职责 |
|------|------|
| `service_gateway` | API 统一网关（端口 8080），处理路由分发、JWT 鉴权、CORS 跨域 |
| `hospital-manage` | 医院对接模拟端（端口 9998），模拟合作医院的接口，用于测试 |
| `common/` | 公共模块（工具类、异常处理、统一返回格式、MQ 工具） |
| `model/` | 实体类和枚举定义 |
| `service_client/` | Feign 声明式服务调用接口 |

### 微服务调用关系

```
用户端 → Gateway → service_user（微信登录/就诊人管理）
                 → service_hosp（医院列表/科室/排班查询）
                 → service_order（预约下单/支付/取消）
                       ├── Feign → service_hosp（查询排班/医院签名）
                       ├── Feign → service_user（查询就诊人）
                       ├── HTTP   → 医院模拟端（提交预约/取消/支付通知）
                       └── MQ    → service_hosp（更新号源）
                                → service_msm（发送短信通知）
```

---

## 数据库设计

### MySQL 数据库

| 数据库 | 用途 | 核心表 |
|--------|------|--------|
| `yygh_cmn` | 数据字典 | `dict`（省市、医院类型、证件类型等基础编码） |
| `yygh_hosp` | 医院设置 | `hospital_set`（医院接口配置、签名密钥） |
| `yygh_order` | 订单支付 | `order_info`、`payment_info`、`refund_info` |
| `yygh_user` | 用户就诊人 | `user_info`、`patient`、`user_login_record`、`user_favorite`（收藏）、`evaluation`（评价）、`checkup_package`（体检套餐） |

### MongoDB 数据库

| 数据库 | 用途 | 集合 |
|--------|------|------|
| `yygh_hosp` | 医院业务数据 | `Hospital`（医院详情+预约规则）、`Department`（科室）、`Schedule`（排班+号源） |

> 初始化脚本在 `yygh/sql/` 目录，Docker 首次启动会自动导入。
> 
> - `yygh表结构.sql` — 全部 MySQL 表结构定义
> - `yygh初始化数据.sql` — 基础字典数据
> - `yygh_test_data.sql` — 测试/演示数据（用户、就诊人、订单、支付记录）
> - `yygh_new_features.sql` — 新功能表（收藏、评价、体检套餐）及性能索引

---

## 快速开始

### 1. 环境要求

| 工具 | 版本要求 | 说明 |
|------|---------|------|
| **JDK** | 8+ | 编译和运行 Java 后端 |
| **Maven** | 3.6+ | 后端构建工具 |
| **Node.js** | 18+ | 前端运行环境（见 .nvmrc） |
| **Docker + Docker Compose** | 最新稳定版 | 一键启动基础设施 |
| **npm** | 9+ | 前端包管理 |

### 2. 环境变量配置

```bash
# 从模板创建环境变量文件
cp .env.example .env

# 编辑 .env，至少配置以下关键项：
#   MYSQL_ROOT_PASSWORD  — 数据库密码
#   RABBITMQ_PASSWORD    — 消息队列密码
#   REDIS_PASSWORD       — Redis 密码（生产必配）
#
# 以下密钥需要自行申请（旧密钥已轮换）：
#   WX_OPEN_APP_ID / WX_OPEN_APP_SECRET  — 微信开放平台
#   WEIXIN_APPID / WEIXIN_PARTNER / WEIXIN_PARTNERKEY  — 微信支付
#   ALIYUN_SMS_ACCESS_KEY_ID / ALIYUN_SMS_SECRET    — 阿里云短信
#   ALIYUN_OSS_ACCESS_KEY_ID / ALIYUN_OSS_SECRET    — 阿里云 OSS
```

### 3. 启动基础设施

```bash
# 在项目根目录执行，一键启动 MySQL、MongoDB、Redis、RabbitMQ、Nacos
docker compose up -d

# 查看各服务启动状态
docker compose ps

# 等待 MySQL 健康检查通过（约 40s），数据库表结构会自动初始化
```

启动后各基础设施端口：

| 服务 | 端口 | 管理界面 |
|------|------|---------|
| MySQL | 3306 | — |
| MongoDB | 27017 | — |
| Redis | 6379 | — |
| RabbitMQ | 5672 | http://localhost:15672 |
| Nacos | 8848 | http://localhost:8848/nacos |

### 4. 初始化 MongoDB 数据

1. 启动 `hospital-manage` 模块（医院对接模拟端）
2. 访问 http://localhost:9998/
3. 在医院管理页面中通过"上传医院"、"上传科室"、"上传排班"功能将数据导入 MongoDB

### 5. 启动后端微服务

```bash
# 方式一：在 IDE 中逐个启动各微服务模块的 Spring Boot 主类

# 方式二：命令行编译并启动
cd yygh

# 加载环境变量
source ../scripts/export-env.sh

# 编译所有模块（跳过测试）
mvn -DskipTests clean package

# 按顺序启动（建议顺序：cmn → hosp → user → order → msm/oss/statistics/task → gateway）
cd service/service_cmn && mvn spring-boot:run &
cd service/service_hosp && mvn spring-boot:run &
cd service/service_user && mvn spring-boot:run &
cd service/service_order && mvn spring-boot:run &
cd service/service_msm && mvn spring-boot:run &
cd service/service_oss && mvn spring-boot:run &
cd service/service_statistics && mvn spring-boot:run &
cd service/service_task && mvn spring-boot:run &
cd service_gateway && mvn spring-boot:run &
```

### 6. 启动前端应用

```bash
# 用户预约端（Nuxt 2 SSR）
cd yygh/yygh-site
npm install
npm run dev
# → http://localhost:3000

# 管理后台（Vue 2 SPA）
cd yygh/hospital_admin
npm install
npm run dev
# → http://localhost:9528
```

> `hospital_admin` 使用 Webpack 4，在 Node 18+ 下会自动使用 `NODE_OPTIONS=--openssl-legacy-provider` 兼容。

---

## 访问地址一览

| 应用 | 地址 | 说明 |
|------|------|------|
| **用户预约端** | http://localhost:3000 | 患者使用的预约挂号前台 |
| **管理后台** | http://localhost:9528 | 平台管理员的后台管理系统 |
| **API 网关** | http://localhost:8080 | 所有 API 的统一入口 |
| **医院模拟端** | http://localhost:9998 | 模拟合作医院的数据上传和接口对接 |
| **Nacos 控制台** | http://localhost:8848/nacos | 服务注册与配置中心 |
| **RabbitMQ 管理** | http://localhost:15672 | 消息队列管理界面 |
| **Swagger 文档** | http://localhost:8080/swagger-ui.html | API 接口文档（通过网关访问） |

---

## 业务流程

### 整体业务流程

```
医院方                              平台方                              用户方
  │                                   │                                   │
  ├─ 上传医院信息 ──────────────────→ │                                   │
  ├─ 上传科室信息 ──────────────────→ │                                   │
  ├─ 上传排班信息 ──────────────────→ │                                   │
  │                                   │                                   │
  │                                   │ ←── 微信扫码登录 ─────────────────┤
  │                                   │ ←── 搜索医院/科室 ────────────────┤
  │                                   │ ←── 查看排班详情 ────────────────┤
  │                                   │ ←── 选择排班预约 ────────────────┤
  │                                   │                                   │
  │ ←── 平台推送预约订单 ──────────── │ ──→ 创建订单 ──────────────────→ │
  │ ──→ 返回预约结果 ──────────────→ │                                   │
  │                                   │ ←── 微信扫码支付 ────────────────┤
  │                                   │ ──→ MQ 更新号源 ────────────────→│
  │ ←── 通知支付成功 ─────────────── │                                   │
  │                                   │ ──→ MQ 发送短信通知 ────────────→│
```

### 用户端核心功能

1. **微信登录** — 微信开放平台扫码授权登录
2. **医院搜索** — 按地区、医院名称、等级筛选医院
3. **科室排班** — 查看医院的科室树和排班日程
4. **预约挂号** — 选择排班时段，填写就诊人信息，确认预约
5. **微信支付** — 扫码支付挂号费用
6. **订单管理** — 查看预约订单、取消预约、导出 Excel
7. **就诊人管理** — 添加/编辑就诊人信息
8. **🆕 医院收藏** — 收藏常用医院，快速访问
9. **🆕 就诊评价** — 就诊后对医院进行五星评分和文字评价
10. **🆕 智能导诊** — 输入症状关键词，智能推荐就诊科室（覆盖 30+ 常见症状）
11. **🆕 医院对比** — 同时选择 2-3 家医院进行横向对比（等级、评分、预约规则等）
12. **🆕 体检套餐** — 查看医院的体检套餐（项目内容、价格对比、适用人群）
13. **🆕 医生团队** — 查看医院专家医生团队及擅长领域

### 管理端核心功能

1. **医院设置管理** — 配置合作医院的接口地址和签名密钥
2. **医院列表** — 查看/审核医院信息、上下线管理
3. **订单管理** — 查看所有预约订单、订单状态
4. **数据统计** — 订单数量趋势、预约统计图表
5. **数据字典** — 管理省市、医院类型等基础编码数据

---

## API 接口文档

完整的 API 接口文档见 [yygh/README.md](yygh/README.md)，包括：

- **平台接口**（医院方调用）：上传医院、上传科室、上传排班、查询/删除等
- **医院接口**（平台调用）：预约下单、更新支付状态、取消预约

接口采用 **MD5 数字签名** 方式进行身份验证和防篡改，签名逻辑为：

```
所有参数按名称升序排列，用 | 连接，末尾拼接签名密钥 signKey → 32 位 MD5 小写
```

Swagger 文档在启动网关后可通过 http://localhost:8080/swagger-ui.html 在线查看。

### 新增功能 API 接口

| 功能 | 路径 | 方法 | 说明 |
|------|------|------|------|
| 医院收藏 | `/api/user/favorite/auth/list` | GET | 获取收藏列表 |
| 医院收藏 | `/api/user/favorite/auth/check/{hoscode}` | GET | 检查是否已收藏 |
| 医院收藏 | `/api/user/favorite/auth/add/{hoscode}/{hosname}` | POST | 收藏医院 |
| 医院收藏 | `/api/user/favorite/auth/cancel/{hoscode}` | DELETE | 取消收藏 |
| 就诊评价 | `/api/user/evaluation/auth/submit` | POST | 提交评价 |
| 就诊评价 | `/api/user/evaluation/list/{hoscode}/{page}/{limit}` | GET | 获取医院评价列表 |
| 就诊评价 | `/api/user/evaluation/rating/{hoscode}` | GET | 获取医院评分统计 |
| 就诊评价 | `/api/user/evaluation/auth/check/{orderId}` | GET | 检查订单是否已评价 |
| 订单导出 | `/api/order/orderInfo/auth/exportExcel` | GET | 导出预约记录为 Excel |
| 统计看板 | `/api/sta/statistics/overview` | GET | 平台概览数据 |
| 统计看板 | `/api/sta/statistics/deptRanking` | GET | 科室预约排行 |
| 统计看板 | `/api/sta/statistics/hospRanking` | GET | 医院预约排行 |

---

## 常见问题

### Q: Docker 容器启动后 MySQL 报错退出？
A: 检查 `.env` 是否已配置 `MYSQL_ROOT_PASSWORD`。若之前用不同密码启动过，需要先清理数据卷：`docker compose down -v && docker compose up -d`

### Q: MongoDB 连接失败？
A: 确保已通过 hospital-manage 导入数据。首次启动 MongoDB 没有数据，需要访问 http://localhost:9998/ 上传医院、科室、排班数据。

### Q: 前端启动报 `ERR_OSSL_EVP_UNSUPPORTED`？
A: hospital_admin 使用 Webpack 4，已配置 `NODE_OPTIONS=--openssl-legacy-provider`。如仍报错，请确认 Node.js 版本 ≥ 18。

### Q: 微信登录/支付不可用？
A: 需要先在微信开放平台和微信支付商户平台申请 AppID 和密钥，填入 `.env` 文件。开发测试时可以跳过微信登录，直接使用其他功能。

### Q: 短信发送失败？
A: 需要在阿里云短信控制台申请签名和模板，获取 AccessKey 填入 `.env`。短信模板编码在 `OrderServiceImpl.java` 中配置。

### Q: API 返回 403 / 认证失败？
A: 网关已恢复 JWT 认证。需要登录或调用的接口属于 `/api/**/auth/**` 路径时，请在请求 Header 中携带 `token` 字段。

### Q: 智能导诊如何工作？
A: 在首页右侧"智能导诊"卡片中输入症状关键词（如"头痛"、"咳嗽"），系统会根据症状-科室映射推荐最匹配的就诊科室。覆盖 30+ 常见症状，也支持按科室名称反向搜索。

### Q: 如何对比多家医院？
A: 点击导航栏"医院对比"，进入对比页面。在搜索框中分别输入 2-3 家医院名称，系统将展示等级、地址、预约周期、放号时间、退号规则、用户评分等维度的横向对比表格。

### Q: 评价系统需要登录吗？
A: 提交评价需要先登录（`/auth/` 路径）。查看评价列表和评分统计无需登录，在医院详情页底部直接可见。

### Q: 套餐数据从哪里来？
A: 体检套餐目前使用前端静态 Mock 数据展示，数据库表 `checkup_package` 已建好，后续可通过管理后台录入真实数据。测试数据在 `yygh_new_features.sql` 中。

### Q: 如何导入评价和套餐的测试数据？
A: 执行 `yygh/sql/yygh_new_features.sql` 脚本即可创建 `evaluation` 和 `checkup_package` 表并导入套餐示例数据。

---

## 安全说明

- **JWT 签名密钥**：从系统属性 `JWT_SIGN_KEY` 或环境变量读取，生产环境务必设置强随机密钥（256 位以上）。开发环境默认值仅用于本地测试。
- **数据库密码**：通过 `.env` 文件和 Docker Compose 环境变量注入，切勿将 `.env` 提交到 Git。
- **API 接口签名**：医院与平台之间通过 MD5 + 签名密钥进行身份验证和防篡改。生产环境建议升级为 HMAC-SHA256。
- **网关认证**：`/api/**/auth/**` 路径的接口需要通过 JWT Token 认证才能访问。`/inner/**` 内部接口已屏蔽外部访问。
- **CORS 跨域**：当前允许所有来源，生产环境建议配置为具体域名白名单。
- **敏感日志**：已对 access_token 等敏感信息做了日志脱敏处理，生产环境应关闭 DEBUG 级别日志。
- **密钥轮换**：若仓库历史中曾包含真实密钥，请在微信开放平台、阿里云等控制台**立即轮换（作废旧 Key）**。
- **生产部署**：请使用密钥管理服务（如 HashiCorp Vault、阿里云 KMS），勿使用本仓库的 `.env.example` 默认值。

---

## 后续升级规划

以下为建议的后续技术升级方向，按优先级排列：

| 优先级 | 升级项 | 说明 |
|--------|--------|------|
| **高** | JJWT 0.9.1 → 0.12.6 | 修复已知 CVE 安全漏洞 |
| **高** | MD5 → HMAC-SHA256 | 接口签名算法升级 |
| **高** | 补充单元测试 | 当前测试覆盖率 ≈ 0%，需补全 |
| **中** | Sentinel 熔断降级 | 当前 Feign 无降级方案，需启用 Sentinel |
| **中** | Prometheus + Grafana | 补充监控和可观测性 |
| **中** | Sleuth + Zipkin | 分布式链路追踪 |
| **中** | Vue 2 → Vue 3 | Vue 2 已于 2023 年 12 月 EOL |
| **中** | Nuxt 2 → Nuxt 3 | Nuxt 2 已于 2024 年 6 月 EOL |
| **中** | Webpack 4 → 5 / Vite | 去掉 openssl-legacy-provider 兼容 |
| **低** | Spring Boot 2.7 → 3.x | 需 Java 17+，javax → jakarta 全量迁移 |
| **低** | Java 8 → 17/21 | 语言特性 + 性能提升 |
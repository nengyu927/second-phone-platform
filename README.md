# Second Phone Platform

二手手機販賣、維修與舊機收購整合平台。後端採 `Java 21`、`Spring Boot`、`Spring Security`、`JWT` 與 `MySQL`；前端採 `Vue 3`、`Vue Router`、`Pinia`、`Axios` 與 `Vite`。

## 目前完成範圍

- 會員註冊、登入、`JWT`、`BCrypt`、角色與帳號狀態驗證。
- 公開前台、會員中心與管理後台共用 `Layout`、巢狀路由與守衛。
- 企業級響應式設計系統、首頁、商品列表與商品詳情。
- 商品、品牌、分類、多圖網址、庫存調整與庫存異動紀錄。
- 第二批購物車與訂單、第三批維修與收購完整流程仍在開發中；介面會明確標示。

## 執行需求

- `Java 21`
- `Maven 3.9+`
- `Node.js 20+` 與 `npm 10+`
- `MySQL 8+`

前端統一使用 `npm`，請勿混用 `pnpm` 或其他套件管理器。

## 換電腦快速啟動

實際專案目錄為 `frontend` 與 `backend`。`scripts` 內的 PowerShell 腳本會從腳本所在位置自動找到專案根目錄，不包含任何使用者名稱、桌面位置或其他電腦絕對路徑。

### 第一次使用新電腦

先安裝 `Git`、`Java 21`、`Node.js 20.19+`、`npm`、`Maven 3.9+` 與 `MySQL 8+`，接著執行：

```powershell
git clone GitHub網址
cd 專案資料夾
powershell -ExecutionPolicy Bypass -File .\scripts\setup.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\start-all.ps1
```

`setup.ps1` 會檢查工具版本、安裝前端套件、安裝後端 Maven 相依套件，並由 `.env.example` 建立本機 `.env`。首次啟動前請確認 `.env` 的 `DB_USERNAME`、`DB_PASSWORD` 與管理員設定符合該電腦的 MySQL 環境。

### 平常開始製作

```powershell
git pull
powershell -ExecutionPolicy Bypass -File .\scripts\start-all.ps1
```

### 製作完成

```powershell
git status
git add .
git commit -m "更新專題功能"
git push
```

### 本機服務資訊

- 前端：`http://localhost:5173`
- 後端：`http://localhost:8080`
- Swagger：`http://localhost:8080/swagger-ui/index.html`
- Java：`21`
- Node.js：`20.19+`
- 資料庫：`second_phone_platform`

`.env`、`application-local.properties`、`node_modules`、`dist`、`target`、上傳檔案與 `*.log` 都是本機資料，不會上傳 GitHub。`.env.example` 與 `application-example.properties` 只能存放欄位名稱或假資料。

## 建立 MySQL 資料庫

```sql
CREATE DATABASE second_phone_platform
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

目前開發環境使用 `spring.jpa.hibernate.ddl-auto=update`，不會主動清除既有資料。正式期末版建議在確認現有資料結構後導入 `Flyway`，本階段不混用其他建表機制。

## 環境變數

請在啟動後端的同一個 PowerShell 視窗設定；以下皆為示意值，不可提交真實密碼或密鑰。

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/second_phone_platform?useSSL=false&serverTimezone=Asia/Taipei&characterEncoding=utf8'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='YOUR_LOCAL_DATABASE_PASSWORD'
$env:APP_JWT_SECRET='CHANGE_TO_AT_LEAST_32_RANDOM_CHARACTERS'
$env:APP_JWT_EXPIRATION_MS='86400000'
$env:APP_ADMIN_USERNAME='admin'
$env:APP_ADMIN_EMAIL='admin@example.com'
$env:APP_ADMIN_PASSWORD='CHANGE_ME_AT_LEAST_8_CHARACTERS'
$env:APP_UPLOAD_DIR='uploads'
```

也可參考 `backend/src/main/resources/application-example.properties` 建立已被 `.gitignore` 排除的 `application-local.properties`。前端環境設定參考 `frontend/.env.example`；開發預設使用 `/api`，由 `Vite proxy` 連接後端。

## 初始管理員

啟動時若資料庫尚無 `ADMIN`，且已設定 `APP_ADMIN_EMAIL` 與 `APP_ADMIN_PASSWORD`，系統才會建立一次初始管理員。既有管理員不會被覆蓋，也沒有公開註冊管理員 API。

## 啟動方式

後端：

```powershell
cd backend
mvn spring-boot:run
```

前端：

```powershell
cd frontend
npm install
npm run dev
```

正式建置與測試：

```powershell
cd backend
mvn test
mvn clean package

cd ..\frontend
npm run build
```

## 測試網址

- 前台：`http://localhost:5173/`
- 商品列表：`http://localhost:5173/products`
- 登入：`http://localhost:5173/login`
- 註冊：`http://localhost:5173/register`
- 會員中心：`http://localhost:5173/member`
- 管理後台：`http://localhost:5173/admin`
- Swagger：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI：`http://localhost:8080/v3/api-docs`

## 第一批主要 API

公開：

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/brands`
- `GET /api/categories`

登入會員：

- `GET /api/auth/me`
- `GET /api/member/profile`
- `PUT /api/member/profile`

`STAFF`、`ADMIN`：

- `POST /api/products`
- `PUT /api/products/{id}`
- `GET|POST|PUT /api/admin/brands/**`
- `GET|POST|PUT /api/admin/categories/**`
- `POST|DELETE /api/admin/products/{productId}/images/**`（含 `multipart` 圖片上傳）
- `GET /api/admin/inventory/movements`
- `POST /api/admin/inventory/products/{productId}/adjustments`

刪除商品限制為 `ADMIN`。呼叫受保護 API 時需使用 `Authorization: Bearer JWT_TOKEN`。

## 建立測試帳號

一般會員可由 `/register` 建立，角色固定為 `CUSTOMER`。管理員使用上述環境變數初始化；本專案不提供預設真實密碼。請勿將 `.env`、本機 properties、資料庫密碼、`JWT` 密鑰或管理員密碼提交到 Git。

## 第二批：購物車、訂單與模擬結帳

登入會員：

- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items/{itemId}`
- `DELETE /api/cart/items/{itemId}`
- `DELETE /api/cart`
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders/{id}/cancel`

`STAFF`、`ADMIN`：

- `GET /api/admin/orders`
- `GET /api/admin/orders/{id}`
- `PATCH /api/admin/orders/{id}/status`
- `PATCH /api/admin/orders/{id}/payment-status`
- `PATCH /api/admin/orders/{id}/shipping-status`

前端新增 `/cart`、`/checkout`、`/checkout/success`、`/member/orders/:id` 與 `/admin/orders/:id`。`DEMO_CARD` 僅為展示用模擬付款，不會連接真實金流或產生扣款。建立訂單會再次檢查庫存並於同一交易中扣除庫存；符合條件的取消訂單會恢復庫存。

## 第三、四批：維修、舊機收購與會員管理

登入會員：

- `POST|GET /api/member/repairs`
- `GET /api/member/repairs/{id}`
- `POST|GET /api/member/trade-ins`
- `GET /api/member/trade-ins/{id}`
- `POST /api/member/trade-ins/{id}/accept`
- `POST /api/member/trade-ins/{id}/reject`

`STAFF`、`ADMIN`：

- `GET|POST|PUT /api/repairs/**`
- `GET|PATCH /api/admin/trade-ins/**`
- `GET /api/admin/members`
- `GET /api/admin/members/{id}`
- `PATCH /api/admin/members/{id}/status`

僅 `ADMIN`：

- `PATCH /api/admin/members/{id}/role`
- 原有會員新增、完整修改與刪除功能

前端提供 `/repairs`、`/trade-in`、會員維修與收購紀錄／詳情，以及後台收購管理頁面。公開服務頁可瀏覽，但送出申請時必須先登入；後端一律從 JWT 身分取得會員，不接受前端指定會員 ID。

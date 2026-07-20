# Second Phone Platform

二手手機販售與維修整合平台的期中後台管理系統。目前提供 Vue 後台與 Spring Boot REST API，商城前台屬於期末下一階段，尚未開發。

## 專案架構與技術

- 前端：Vue 3、Vue Router、Axios、Vite、HTML、CSS、JavaScript
- 後端：Java 21、Spring Boot 3.5.3、Spring MVC、Spring Data JPA、Jakarta Validation
- API 文件：Springdoc OpenAPI 2.8.17
- 資料庫：MySQL
- 架構：前後端分離；Vue 透過共用 Axios instance 呼叫 Spring Boot JSON API

主要目錄：

```text
second-phone-platform/
├─ frontend/   Vue 後台管理畫面
├─ backend/    Spring Boot REST API
├─ database/   資料庫建表參考
└─ docs/       ERD、TODO 與開發說明
```

## 五大功能

1. 會員管理：CRUD、帳號／姓名搜尋、Email 與必填驗證
2. 商品管理：CRUD、商品／品牌搜尋、狀態篩選、價格與庫存驗證
3. 訂單管理：CRUD、搜尋、狀態篩選、後端計價與交易式庫存調整
4. 維修管理：CRUD、搜尋、狀態篩選、費用驗證與完成日期處理
5. 儀表板：數量、狀態、金額、低庫存與最近五筆資料

## 主要 API

| 功能 | API |
| --- | --- |
| 會員 | `GET/POST /api/members`、`GET/PUT/DELETE /api/members/{id}` |
| 商品 | `GET/POST /api/products`、`GET/PUT/DELETE /api/products/{id}` |
| 訂單 | `GET/POST /api/orders`、`GET/PUT/DELETE /api/orders/{id}` |
| 維修 | `GET/POST /api/repairs`、`GET/PUT/DELETE /api/repairs/{id}` |
| 儀表板 | `GET /api/dashboard`、`GET /api/dashboard/summary`、`GET /api/dashboard/recent` |

會員列表可使用 `keyword`；商品列表可使用 `keyword`、`brand`、`status`；訂單與維修列表可使用 `keyword`、`status`。

## 前端頁面

- 儀表板：`http://localhost:5173/dashboard`
- 會員：`http://localhost:5173/members`
- 商品：`http://localhost:5173/products`
- 訂單：`http://localhost:5173/orders`
- 維修：`http://localhost:5173/repairs`

`5173` 是 Vue 後台管理畫面；`8080` 是 Spring Boot API。Swagger UI 位於 `http://localhost:8080/swagger-ui/index.html`，只作為開發與 API 測試工具。

## 啟動與測試

先建立 MySQL 資料庫 `second_phone_platform`，並在本機確認 `backend/src/main/resources/application.properties` 的資料庫連線設定。

後端：

```bash
cd backend
mvn test
mvn spring-boot:run
```

前端：

```bash
cd frontend
npm install
npm run build
npm run dev
```

一般開發時若 `node_modules` 已存在，不必重複執行 `npm install`。

## 主要業務規則

- 訂單狀態：`PENDING`、`CONFIRMED`、`SHIPPED`、`COMPLETED`、`CANCELLED`。
- 訂單價格與總額由後端計算；建立、修改、取消及刪除會依狀態正確扣除或回補庫存。
- 維修狀態：`RECEIVED`、`INSPECTING`、`WAITING_PARTS`、`REPAIRING`、`COMPLETED`、`RETURNED`、`CANCELLED`。
- 維修完成且未指定完成日期時，後端自動填入今天。

## 專案階段

目前只完成期中後台管理介面。商城前台、登入、JWT、角色權限、購物車、結帳、圖片上傳、Docker 與雲端部署均不在本階段範圍。

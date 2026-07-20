# Second Phone Platform

二手手機販賣與維修整合平台，作為五人期中專題的個人完整試作版本。

## 技術
- Frontend: Vue 3、HTML、CSS、JavaScript、Axios
- Backend: Java 21、Spring Boot、Spring MVC、Spring Data JPA
- Database: MySQL
- IDE: VS Code
- Version Control: GitHub

## 五大功能
1. 會員管理：已完成前後端 CRUD
2. 二手手機商品管理：已完成前後端 CRUD 與搜尋
3. 訂單管理：已完成前後端 CRUD
4. 維修管理：已完成前後端 CRUD
5. 後台儀表板：已完成統計、最近資料與快捷操作

## 啟動方式

### Backend
1. 建立 MySQL 資料庫：`second_phone_platform`
2. 修改 `backend/src/main/resources/application.properties`
3. VS Code 開啟 backend，執行 `SecondPhonePlatformApplication.java`

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Dashboard: http://localhost:5173/dashboard

## Dashboard API

- `GET /api/dashboard`：完整統計與最近資料
- `GET /api/dashboard/summary`：統計摘要
- `GET /api/dashboard/recent`：每類最近 5 筆資料

## 測試
- Backend: http://localhost:8080/api/test
- Frontend: http://localhost:5173

# Second Phone Platform

二手手機販賣與維修整合平台，作為五人期中專題的個人完整試作版本。

## 技術
- Frontend: Vue 3、HTML、CSS、JavaScript、Axios
- Backend: Java 21、Spring Boot、Spring MVC、Spring Data JPA
- Database: MySQL
- IDE: VS Code
- Version Control: GitHub

## 五大功能
1. 會員管理
2. 二手手機商品管理
3. 訂單管理
4. 維修管理
5. 後台管理

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

## 測試
- Backend: http://localhost:8080/api/test
- Frontend: http://localhost:5173

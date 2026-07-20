# Codex 任務指令

## 任務 1：會員後端 CRUD

請先閱讀 AGENTS.md、docs/ERD.md、docs/TODO.md。

只完成會員後端 CRUD：
1. 建立 Member Entity
2. 建立 MemberRepository
3. 建立 MemberService
4. 建立 MemberController
5. API 路徑使用 `/api/members`
6. 提供 GET、GET by id、POST、PUT、DELETE
7. 不製作登入驗證
8. 完成後更新 docs/TODO.md
9. 列出測試用 HTTP 請求

不要修改其他功能。

## 任務 2：會員 Vue 畫面

請只完成會員前端：
1. 建立 src/api/memberApi.js
2. 建立會員列表
3. 建立新增與修改表單
4. 使用 Axios 呼叫 `/api/members`
5. 保持 CSS 簡單清楚
6. 不加入 UI Framework
7. 完成後更新 docs/TODO.md

## 任務 3：商品 CRUD

參考會員功能的架構，只建立 PhoneProduct 的前後端 CRUD。
不要製作購物車與訂單。

# Codex 專案指令

## 專案目標
建立一個適合期中展示的二手手機販賣與維修平台，五大功能都具備簡化 CRUD。

## 固定技術
- Vue 3
- HTML / CSS / JavaScript
- Axios HTTP
- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- MySQL
- VS Code

## Codex 必須遵守
1. 先閱讀 README.md、docs/ERD.md、docs/TODO.md。
2. 不更換技術、不加入 TypeScript。
3. 前後端分離。
4. Controller 回傳 JSON。
5. 前端集中在 src/api 呼叫 HTTP。
6. 每次只處理一個功能。
7. 程式碼保持簡單，適合學生口頭解釋。
8. 每次完成後列出修改檔案、測試方式、尚未完成項目。
9. 不要一次重構整個專案。
10. 不得把資料庫密碼提交到 GitHub。

## 開發順序
Entity → Repository → Service → Controller → API 測試 → Vue View → Axios 串接

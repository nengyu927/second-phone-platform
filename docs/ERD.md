# 簡化 ERD

## Member
- id: 主鍵
- name: 姓名
- email: 帳號
- password: 密碼
- phone: 電話
- role: MEMBER / ADMIN

## PhoneProduct
- id: 主鍵
- name: 商品名稱
- brand: 品牌
- model: 型號
- price: 售價
- stock: 庫存
- conditionLevel: 商品狀況
- description: 商品說明

## CustomerOrder
- id: 主鍵
- memberId: 會員外鍵
- orderDate: 訂單日期
- totalAmount: 總金額
- status: 訂單狀態

## OrderItem
- id: 主鍵
- orderId: 訂單外鍵
- phoneProductId: 商品外鍵
- quantity: 數量
- unitPrice: 單價

## Repair
- id: 主鍵
- memberId: 會員外鍵
- deviceModel: 維修裝置
- problemDescription: 故障說明
- repairStatus: 維修狀態
- estimatedPrice: 預估價格

## 關係
- 一位會員可以有多張訂單：Member 1 → N CustomerOrder
- 一張訂單可以有多筆明細：CustomerOrder 1 → N OrderItem
- 一項商品可以出現在多筆明細：PhoneProduct 1 → N OrderItem
- 一位會員可以有多筆維修單：Member 1 → N Repair

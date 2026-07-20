# 期中簡化 ERD

## Member

- `id`：主鍵
- `account`：唯一帳號
- `password`：課堂展示版本密碼
- `name`、`email`、`phone`
- `role`、`status`
- `createdAt`、`updatedAt`

## Product

- `id`：主鍵
- `productName`、`brand`、`model`
- `storageCapacity`、`color`、`conditionLevel`
- `price`、`stock`
- `description`、`imageUrl`、`status`
- `createdAt`、`updatedAt`

## Order

- 資料表：`orders`
- `id`：主鍵
- `member`：多對一 Member
- `product`：多對一 Product
- `quantity`、`unitPrice`、`totalAmount`
- `orderStatus`
- `recipientName`、`recipientPhone`、`shippingAddress`、`note`
- `createdAt`、`updatedAt`

期中版本一張訂單只對應一項商品，不建立 OrderItem；期末可再擴充一對多明細。

## RepairOrder

- 資料表：`repair_orders`
- `id`：主鍵
- `member`：多對一 Member
- `deviceBrand`、`deviceModel`、`imei`
- `problemDescription`、`repairStatus`
- `estimatedCost`、`finalCost`
- `receivedDate`、`expectedCompletionDate`、`completedDate`
- `technicianName`、`repairNotes`
- `createdAt`、`updatedAt`

## 關係

- Member 1 → N Order
- Product 1 → N Order
- Member 1 → N RepairOrder

Entity 關聯均由 Order／RepairOrder 單向指向 Member 或 Product，API 使用簡潔回傳資料，避免 JSON 無限遞迴與過大的關聯物件。

# ER図

## エンティティ関連図

```mermaid
erDiagram
    m_product {
        int         product_code      PK  "商品コード（AUTO_INCREMENT）"
        varchar50   product_name          "商品名"
        int         price                 "単価"
        datetime    register_datetime     "登録日時"
        datetime    update_datetime       "更新日時（楽観的排他制御に使用）"
        datetime    delete_datetime       "削除日時（NULL=有効、値あり=論理削除済み）"
    }

    t_sales {
        date        sales_date        PK  "売上日（複合主キー）"
        int         product_code      PK  "商品コード（複合主キー・外部キー）"
        int         quantity              "数量"
        datetime    register_datetime     "登録日時"
        datetime    update_datetime       "更新日時"
    }

    m_product ||--o{ t_sales : "1つの商品に対して\n0件以上の売上"
```

## 補足

| 項目 | 説明 |
|---|---|
| `m_product` の `delete_datetime` | `NULL` のレコードのみ有効。売上実績がある商品は削除不可 |
| `t_sales` の複合主キー | 同一日・同一商品のレコードは1件のみ（upsert で制御） |
| `m_product.update_datetime` | 楽観的排他制御のキー。編集開始時と更新直前の値を比較し、差異があれば競合エラーとする |

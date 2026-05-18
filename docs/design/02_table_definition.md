# テーブル定義書

## 1. テーブル一覧

| テーブル名 | 論理名 | 説明 |
|---|---|---|
| m_product | 商品マスタ | 商品の基本情報を管理する。論理削除対応 |
| t_sales | 売上テーブル | 日次の商品別売上数量を管理する |

---

## 2. m_product（商品マスタ）

| # | カラム名 | 論理名 | データ型 | NULL | デフォルト | 備考 |
|---|---|---|---|---|---|---|
| 1 | product_code | 商品コード | INT | NOT NULL | AUTO_INCREMENT | 主キー |
| 2 | product_name | 商品名 | VARCHAR(50) | NOT NULL | - | |
| 3 | price | 単価 | INT | NOT NULL | - | 1〜1,000,000 |
| 4 | register_datetime | 登録日時 | DATETIME | NOT NULL | - | レコード作成時にセット |
| 5 | update_datetime | 更新日時 | DATETIME | NOT NULL | - | 更新時に現在時刻をセット。楽観的排他制御に使用 |
| 6 | delete_datetime | 削除日時 | DATETIME | NULL | NULL | 論理削除時にセット。NULL の場合は有効レコード |

**主キー：** `product_code`

**インデックス：** PRIMARY KEY (product_code)

**論理削除の仕様：**  
`delete_datetime IS NULL` のレコードのみ有効として扱う。  
物理削除は行わない。売上実績がある商品は削除不可。

---

## 3. t_sales（売上テーブル）

| # | カラム名 | 論理名 | データ型 | NULL | デフォルト | 備考 |
|---|---|---|---|---|---|---|
| 1 | sales_date | 売上日 | DATE | NOT NULL | - | 主キー（複合） |
| 2 | product_code | 商品コード | INT | NOT NULL | - | 主キー（複合）。m_product への外部キー |
| 3 | quantity | 数量 | INT | NOT NULL | - | 1〜1,000 |
| 4 | register_datetime | 登録日時 | DATETIME | NOT NULL | - | レコード作成時にセット |
| 5 | update_datetime | 更新日時 | DATETIME | NOT NULL | - | 更新時に現在時刻をセット |

**主キー：** `(sales_date, product_code)`（複合主キー）

**外部キー：** `product_code` → `m_product.product_code`

**登録仕様：**  
同一日・同一商品のレコードが既存の場合は数量を UPDATE、存在しない場合は INSERT（upsert）。  
複数商品の登録は1トランザクションで一括処理される。

---

## 4. テーブル間の関係

| 親テーブル | 子テーブル | 結合キー | カーディナリティ |
|---|---|---|---|
| m_product | t_sales | product_code | 1対多（1商品に対して複数の売上日レコード） |

---

## 5. DDL

### m_product

```sql
CREATE TABLE m_product(
    product_code     INT          AUTO_INCREMENT COMMENT '商品コード',
    product_name     VARCHAR(50)  NOT NULL       COMMENT '商品名',
    price            INT          NOT NULL       COMMENT '単価',
    register_datetime DATETIME    NOT NULL       COMMENT '登録日時',
    update_datetime  DATETIME     NOT NULL       COMMENT '更新日時',
    delete_datetime  DATETIME                    COMMENT '削除日時',
    PRIMARY KEY (product_code)
) COMMENT='商品マスタ';
```

### t_sales

```sql
CREATE TABLE t_sales(
    sales_date        DATE     COMMENT '売上日',
    product_code      INT      COMMENT '商品コード',
    quantity          INT      NOT NULL COMMENT '数量',
    register_datetime DATETIME NOT NULL COMMENT '登録日時',
    update_datetime   DATETIME NOT NULL COMMENT '更新日時',
    PRIMARY KEY (sales_date, product_code)
) COMMENT='売上テーブル';
```

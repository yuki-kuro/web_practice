# システム概要

## 1. システム名

商品管理システム

## 2. 目的

スマートフォン等の商品情報と日次売上を管理するWebアプリケーション。  
商品のCRUD操作・売上登録・売上集計CSVダウンロードを提供する。

## 3. 技術スタック

| 区分 | 技術 | バージョン |
|---|---|---|
| 言語 | Java | 17 |
| フレームワーク | Java EE（Servlet / JSP） | - |
| テンプレートエンジン | JSTL | 1.2 |
| アプリケーションサーバー | Apache Tomcat | 10.x |
| データベース | MySQL | 8.x |
| DB接続 | JNDI コネクションプール | - |
| ビルドツール | Maven | - |
| バージョン管理 | Git / GitHub | - |

## 4. システム構成

```
ブラウザ
  │  HTTP
  ▼
Apache Tomcat（Servlet コンテナ）
  │  JNDI DataSource
  ▼
MySQL（kadaidb）
```

## 5. 主要機能一覧

| # | 機能名 | 概要 |
|---|---|---|
| 1 | 商品検索 | 商品名キーワードによる部分一致検索。未入力時は全件表示 |
| 2 | 商品登録 | 商品名・単価を入力して新規商品をマスタに登録 |
| 3 | 商品変更・削除 | 既存商品の商品名・単価の変更、および論理削除 |
| 4 | 売上登録 | 商品と数量を選択してその日の売上を登録。複数商品を一括登録可能 |
| 5 | CSVダウンロード | 全期間または指定年月の商品別売上集計をCSV出力 |

## 6. 画面一覧

| 画面名 | URL | HTTPメソッド | 説明 |
|---|---|---|---|
| メインメニュー | /Index | GET | ナビゲーションリンクのみ表示 |
| 商品検索 | /SearchProduct | GET | キーワード検索・商品一覧表示 |
| 商品登録 | /RegisterProduct | GET / POST | 商品登録フォームの表示と登録処理 |
| 商品変更・削除 | /EditProduct | GET / POST | 商品編集フォームの表示と更新・削除処理 |
| 売上登録 | /RegisterSales | GET / POST | 売上登録フォームの表示と登録処理 |
| CSVダウンロード | /CSVDownload | GET | 売上集計CSVのダウンロード |

## 7. セキュリティ対策

| 対策 | 実装方法 |
|---|---|
| XSS対策 | JSPの出力すべてに `<c:out>` を使用 |
| CSRF対策 | セッションに UUID トークンを発行し、POST リクエスト時に照合・使い捨て |
| SQLインジェクション対策 | 全クエリで `PreparedStatement` を使用 |

## 8. 設計上の特記事項

| 項目 | 内容 |
|---|---|
| 楽観的排他制御 | 商品編集時に `update_datetime` を比較し、他ユーザーによる更新を検出 |
| 論理削除 | 商品は物理削除せず `delete_datetime` に削除日時をセットして管理 |
| トランザクション | 売上一括登録は単一トランザクションで処理。途中エラー時は全件ロールバック |
| PRGパターン | 登録・更新・削除後は `sendRedirect` でGETにリダイレクトし二重送信を防止 |

## 9. ディレクトリ構成

```
web_practice/
├── src/main/
│   ├── java/
│   │   ├── controller/
│   │   │   ├── Index.java
│   │   │   ├── SearchProduct.java
│   │   │   ├── RegisterProduct.java
│   │   │   ├── EditProduct.java
│   │   │   ├── RegisterSales.java
│   │   │   └── CSVDownload.java
│   │   └── model/
│   │       ├── Product.java
│   │       └── ProductDAO.java
│   └── webapp/
│       ├── META-INF/
│       │   └── context.xml          # JNDI DataSource 設定
│       └── WEB-INF/
│           ├── web.xml              # サーブレット設定・resource-ref
│           └── view/
│               ├── link.jsp         # 共通ナビゲーション
│               ├── index.jsp
│               ├── searchProduct.jsp
│               ├── registerProduct.jsp
│               ├── editProduct.jsp
│               ├── registerSales.jsp
│               └── CSVDownload.jsp
└── docs/
    ├── sql/
    │   ├── m_product.sql
    │   ├── t_sales.sql
    │   └── insert_data.sql
    └── design/                      # 設計書（本ドキュメント）
```

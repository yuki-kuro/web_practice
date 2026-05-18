# 画面遷移図

## 画面遷移図

```mermaid
flowchart TD
    START([ブラウザアクセス]) --> INDEX[メインメニュー\n/Index]

    INDEX -- ナビ --> SEARCH
    INDEX -- ナビ --> REG_P
    INDEX -- ナビ --> REG_S
    INDEX -- ナビ --> CSV

    SEARCH[商品検索\n/SearchProduct GET]
    REG_P[商品登録\n/RegisterProduct GET]
    EDIT[商品変更・削除\n/EditProduct GET]
    REG_S[売上登録\n/RegisterSales GET]
    CSV[CSVダウンロード\n/CSVDownload GET]

    %% 商品検索からの遷移
    SEARCH -- 編集ボタン --> EDIT
    SEARCH -- ナビ --> REG_P
    SEARCH -- ナビ --> REG_S
    SEARCH -- ナビ --> CSV

    %% 商品登録
    REG_P -- POST 登録成功 --> SEARCH
    REG_P -- POST バリデーションエラー --> REG_P
    REG_P -- POST CSRF不一致 --> E403[403 Forbidden]

    %% 商品変更・削除
    EDIT -- POST 変更・削除成功 --> SEARCH
    EDIT -- POST バリデーションエラー\n排他制御エラー\n売上実績エラー --> EDIT
    EDIT -- POST CSRF不一致 --> E403

    %% 売上登録
    REG_S -- POST 追加ボタン\n（成功・エラー） --> REG_S
    REG_S -- POST 登録ボタン成功 --> REG_S
    REG_S -- POST CSRF不一致 --> E403

    %% CSVダウンロード
    CSV -- ダウンロード成功 --> CSVFILE[(CSVファイル\nダウンロード)]
    CSV -- 0件・バリデーションエラー --> CSV

    %% 全画面からナビで戻れる代表例（線が増えすぎるため主要なもののみ）
    EDIT -- ナビ --> SEARCH
    REG_S -- ナビ --> SEARCH
    CSV -- ナビ --> SEARCH
```

---

## 遷移パターン一覧

### 正常系

| 起点画面 | 操作 | 遷移先 | 方式 |
|---|---|---|---|
| メインメニュー | ナビリンク | 各画面 | GET リダイレクト |
| 商品検索 | 編集ボタン | 商品変更・削除 | GET フォーム送信 |
| 商品登録（POST） | 登録成功 | 商品検索 | `sendRedirect`（PRGパターン） |
| 商品変更・削除（POST） | 変更成功 | 商品検索 | `sendRedirect`（PRGパターン） |
| 商品変更・削除（POST） | 削除成功 | 商品検索 | `sendRedirect`（PRGパターン） |
| 売上登録（POST） | 登録ボタン成功 | 売上登録（リセット） | `doGet` 呼び出し → forward |
| CSVダウンロード | ダウンロード成功 | CSVファイル | レスポンスに直接出力 |

### 異常系

| 起点画面 | 条件 | 遷移先 | 方式 |
|---|---|---|---|
| 商品登録（POST） | バリデーションエラー | 商品登録（エラー表示） | forward |
| 商品変更・削除（POST） | バリデーションエラー | 商品変更・削除（エラー表示） | forward |
| 商品変更・削除（POST） | 楽観的排他制御エラー | 商品変更・削除（エラー表示） | forward |
| 商品変更・削除（POST） | 売上実績あり | 商品変更・削除（エラー表示） | forward |
| 売上登録（POST 追加） | バリデーションエラー | 売上登録（エラー表示） | forward |
| CSVダウンロード | 対象データ0件 | CSVダウンロード（エラー表示） | forward |
| CSVダウンロード | バリデーションエラー | CSVダウンロード（エラー表示） | forward |
| 全POST画面 | CSRFトークン不一致 | 403 Forbidden | `sendError` |

---

## PRGパターンについて

登録・更新・削除の POST 処理が成功した場合は `sendRedirect` でGETにリダイレクトする。  
これによりブラウザの「戻る」「再読み込み」による二重送信を防止している。

```
[POST /RegisterProduct]
       ↓ 登録成功
[302 Redirect]
       ↓
[GET /SearchProduct]  ← ブラウザが再読み込みしても安全
```

# セキュリティ設計書

## 1. 対策一覧

| # | 脅威 | 対策 | 実装箇所 |
|---|---|---|---|
| 1 | クロスサイトリクエストフォージェリ（CSRF） | CSRFトークンによるリクエスト検証 | 全POSTサーブレット |
| 2 | クロスサイトスクリプティング（XSS） | 出力時のHTMLエスケープ（`<c:out>`） | 全JSP |
| 3 | SQLインジェクション | `PreparedStatement` によるパラメータバインド | ProductDAO 全メソッド |

---

## 2. CSRF対策

### 脅威の概要

悪意のあるサイトが被害者のブラウザを経由して、本システムに対して意図しない POST リクエストを送信する攻撃。  
（例：商品を勝手に削除・売上データを改ざん）

### 対策：CSRFトークン

```
[フロー]
GET（画面表示）
  → サーバーでランダムな UUID トークンを生成
  → セッションと hidden フィールドの両方にセット

POST（フォーム送信）
  → リクエストのトークンとセッションのトークンを照合
  → 一致：処理を継続し、トークンをセッションから削除（使い捨て）
  → 不一致・null：403 Forbidden を返し処理を中断
```

### 実装ポイント

| ポイント | 内容 |
|---|---|
| トークン生成 | `UUID.randomUUID().toString()` で予測不可能な値を使用 |
| 使い捨て | 照合成功後すぐに `session.removeAttribute("csrfToken")` で破棄 |
| エラー時の再生成 | バリデーションエラーで画面を再表示する際に新トークンを発行（再送信を可能にするため） |
| 文字コード設定 | `request.setCharacterEncoding("UTF-8")` を `getParameter()` より先に実行 |

### 実装例（サーブレット）

```java
// doGet：トークン生成
String csrfToken = UUID.randomUUID().toString();
request.getSession().setAttribute("csrfToken", csrfToken);
request.setAttribute("csrfToken", csrfToken);

// doPost：トークン照合（setCharacterEncoding の直後に実行）
request.setCharacterEncoding("UTF-8");
String sessionToken = (String) request.getSession().getAttribute("csrfToken");
String requestToken = request.getParameter("csrfToken");
if (sessionToken == null || !sessionToken.equals(requestToken)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです");
    return;
}
request.getSession().removeAttribute("csrfToken"); // 使い捨て
```

### 実装例（JSP）

```jsp
<input type="hidden" name="csrfToken" value="<%=csrfToken%>">
```

### 対象サーブレット

| サーブレット | 対象フォーム |
|---|---|
| RegisterProduct | 商品登録フォーム |
| EditProduct | 商品変更・削除フォーム |
| RegisterSales | 追加フォーム・登録フォーム（両方） |

---

## 3. XSS対策

### 脅威の概要

DBや入力値に含まれるスクリプトタグ（例：`<script>alert(1)</script>`）がブラウザで実行される攻撃。  
（例：セッション情報の窃取・画面改ざん）

### 対策：出力時エスケープ（`<c:out>`）

JSP での全文字列出力に `<c:out>` を使用し、HTMLの特殊文字を自動エスケープする。

| 文字 | エスケープ後 |
|---|---|
| `<` | `&lt;` |
| `>` | `&gt;` |
| `&` | `&amp;` |
| `"` | `&quot;` |
| `'` | `&#039;` |

### 実装例

```jsp
<%-- テキスト出力 --%>
<td><c:out value="<%=p.getProductName()%>"/></td>

<%-- input の value 属性（" エスケープのためシングルクォートを使用）--%>
<input type="text" name="productNameToChange"
       value="<c:out value='<%=productDataToChange.getProductName()%>'/>">
```

### 設計上の注意点

**エスケープは出力時に1回だけ行う。**  
サーブレット側でエスケープ処理（`escapeHtml()`）を行ってDBに保存すると、JSP 出力時に二重エスケープ（`&amp;amp;` 等）が発生する。  
本システムでは DB に生のデータを保存し、JSP 表示時のみ `<c:out>` でエスケープする設計とする。

```
NG（二重エスケープ）: サーブレット escapeHtml() → DB保存 → JSP <c:out> → 二重エスケープ
OK（正しい設計）  :                              DB保存（生データ） → JSP <c:out> → 正しく表示
```

### 対象箇所

| JSP | 対象 |
|---|---|
| searchProduct.jsp | 商品名 |
| editProduct.jsp | 変更前商品名（表示）・変更後商品名（input value） |
| registerSales.jsp | セレクトボックス内商品名・一時リスト商品名・当日売上商品名 |

---

## 4. SQLインジェクション対策

### 脅威の概要

入力値に SQL 構文を混入させ、意図しないクエリを実行させる攻撃。  
（例：`' OR '1'='1` による全件取得・`DROP TABLE` によるデータ破壊）

### 対策：PreparedStatement

全 SQL クエリで `PreparedStatement` を使用し、パラメータをプレースホルダー（`?`）でバインドする。  
これにより、入力値が SQL として解釈されることを防ぐ。

### 実装例

```java
// NG: 文字列結合（SQLインジェクションの危険あり）
String sql = "SELECT * FROM m_product WHERE product_name = '" + keyword + "'";

// OK: PreparedStatement によるバインド
String sql = "SELECT * FROM m_product WHERE product_name LIKE ?";
try (Connection conn = getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, "%" + keyword + "%");  // パラメータとしてセット
    ResultSet rs = pstmt.executeQuery();
}
```

### 対象箇所

ProductDAO の全メソッドで PreparedStatement を使用している。

| メソッド | SQL種別 |
|---|---|
| findAll() | SELECT |
| search() | SELECT（LIKE 句） |
| insert() | INSERT |
| getProductDetails() | SELECT |
| update() | UPDATE |
| logicalDelete() | UPDATE |
| existSales() | SELECT |
| registerSalesAll() | SELECT / INSERT / UPDATE |
| findSales() | SELECT（JOIN） |
| download() | SELECT（集計） |
| downloadForPeriod() | SELECT（集計・LIKE 句） |

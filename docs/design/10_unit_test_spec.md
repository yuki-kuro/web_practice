# 単体テスト仕様書

## テスト対象

`model.ProductDAO` の全パブリックメソッド

## テストケース表の見方

| 列 | 説明 |
|---|---|
| テストID | UT-DAO-連番 |
| テスト内容 | 何を確認するか |
| 事前条件 | DB の状態など |
| 入力値 | メソッドの引数 |
| 期待結果 | 戻り値・DB状態 |
| 合否 | テスト実施後に記入（OK / NG） |

---

## 1. findAll()

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-01 | 有効な商品が複数存在する場合、全件返す | delete_datetime = NULL の商品が3件存在する | なし | 3件の List\<Product\> が返る | |
| UT-DAO-02 | 論理削除済み商品は結果に含まれない | 有効商品2件・削除済み商品1件が存在する | なし | 2件の List\<Product\> が返る（削除済みを含まない） | |
| UT-DAO-03 | 有効な商品が0件の場合、空リストを返す | 全件が論理削除済み | なし | 空の List\<Product\> が返る（null ではない） | |
| UT-DAO-04 | 商品コード昇順で返す | 商品コード 3, 1, 2 の順で登録されている | なし | 商品コード 1, 2, 3 の順で返る | |

---

## 2. search(String keyword)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-05 | キーワードに部分一致する商品が返る | 「iPhone」「Galaxy」「Pixel」が登録済み | keyword = "i" | 「iPhone」「Pixel」の2件が返る | |
| UT-DAO-06 | 完全一致でも返る | 「iPhone17」が登録済み | keyword = "iPhone17" | 1件が返る | |
| UT-DAO-07 | 一致する商品がない場合、空リストを返す | 「iPhone」が登録済み | keyword = "ZZZZZ" | 空リストが返る（null ではない） | |
| UT-DAO-08 | キーワードが空文字の場合、全件返す | 有効な商品が3件存在する | keyword = "" | 3件が返る | |
| UT-DAO-09 | 論理削除済み商品は結果に含まれない | 「iPhone」が有効1件・削除済み1件 | keyword = "iPhone" | 1件のみ返る | |

---

## 3. insert(String productName, int price)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-10 | 商品がm_productに登録される | 対象商品が未登録 | productName = "テスト商品", price = 1000 | m_product にレコードが1件追加される | |
| UT-DAO-11 | register_datetime・update_datetime が現在時刻でセットされる | - | productName = "テスト商品", price = 1000 | 両日時カラムに NULL でない値がセットされる | |
| UT-DAO-12 | product_code が AUTO_INCREMENT で採番される | - | productName = "テスト商品", price = 1000 | product_code に正の整数がセットされる | |

---

## 4. getProductDetails(int productCode)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-13 | 存在する商品コードで商品情報が取得できる | productCode = 1 の商品が存在する | productCode = 1 | productName・price・updateDatetime が正しく返る | |
| UT-DAO-14 | 存在しない商品コードの場合、productCode のみセットされた Product を返す | productCode = 999 が存在しない | productCode = 999 | productCode = 999、productName = null、price = 0 の Product が返る | |

---

## 5. update(int productCode, String productName, int price)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-15 | 商品名・単価が指定の値に更新される | productCode = 1 が登録済み | productCode = 1, productName = "更新後商品名", price = 9999 | m_product の product_name・price が更新される | |
| UT-DAO-16 | update_datetime が現在時刻に更新される | productCode = 1 が登録済み | productCode = 1, productName = "更新後", price = 100 | update_datetime が更新前より新しい値になる | |

---

## 6. logicalDelete(int productCode)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-17 | delete_datetime に現在時刻がセットされる | productCode = 1 の商品が有効（delete_datetime = NULL） | productCode = 1 | delete_datetime に NULL でない値がセットされる | |
| UT-DAO-18 | update_datetime が現在時刻に更新される | productCode = 1 が登録済み | productCode = 1 | update_datetime が更新される | |
| UT-DAO-19 | レコードが物理削除されない | productCode = 1 が登録済み | productCode = 1 | m_product のレコードが残っている（件数が変わらない） | |

---

## 7. existSales(int productCode)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-20 | 売上レコードが存在する場合、true を返す | productCode = 1 の売上が t_sales に存在する | productCode = 1 | true が返る | |
| UT-DAO-21 | 売上レコードが存在しない場合、false を返す | productCode = 2 の売上が t_sales に存在しない | productCode = 2 | false が返る | |

---

## 8. registerSalesAll(List\<Product\> tempList, Date today)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-22 | 当日レコードが未存在の商品は INSERT される | 当日の t_sales にレコードなし | 商品1件の tempList, today = 当日 | t_sales に新規レコードが1件追加される | |
| UT-DAO-23 | 当日レコードが既存の商品は数量が UPDATE される | 当日の t_sales に productCode = 1 の数量 100 が存在する | productCode = 1・quantity = 50 の tempList, today = 当日 | t_sales の quantity が 50 に更新される（INSERTではない） | |
| UT-DAO-24 | 複数商品が1トランザクションで登録される | 当日の t_sales にレコードなし | 商品3件の tempList | t_sales に3件追加される | |
| UT-DAO-25 | 途中でエラーが発生した場合、全件ロールバックされる | 当日の t_sales にレコードなし | 2件目に不正な値を含む tempList | t_sales のレコード数が変わらない（1件目も登録されない） | |

---

## 9. findSales()

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-26 | 当日の売上データが返る | 当日と昨日の売上レコードが存在する | なし | 当日分のみ返る | |
| UT-DAO-27 | 過去・未来の売上は含まれない | 昨日・来月の売上レコードが存在する | なし | 空リストが返る | |
| UT-DAO-28 | 論理削除済み商品の売上は含まれない | 当日売上あり・対象商品が論理削除済み | なし | 当該商品の売上は返らない | |
| UT-DAO-29 | 当日の売上が0件の場合、空リストを返す | 当日の t_sales にレコードなし | なし | 空リストが返る（null ではない） | |

---

## 10. download(HttpServletResponse response)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-30 | 売上データが存在する場合、CSV が出力され true を返す | t_sales にレコードが存在する | モックのレスポンス | true が返り、レスポンスに CSV が書き込まれる | |
| UT-DAO-31 | 売上データが0件の場合、false を返す | t_sales にレコードが存在しない | モックのレスポンス | false が返り、レスポンスに何も書き込まれない | |
| UT-DAO-32 | CSV のヘッダー行が正しい | t_sales にレコードが存在する | モックのレスポンス | 1行目が「商品コード,商品名,単価,数量,金額」 | |

---

## 11. downloadForPeriod(HttpServletResponse response, String month)

| テストID | テスト内容 | 事前条件 | 入力値 | 期待結果 | 合否 |
|---|---|---|---|---|---|
| UT-DAO-33 | 指定年月に売上データがある場合、CSV が出力され true を返す | 2024-03 の売上レコードが存在する | response = モック, month = "2024-03" | true が返り、CSV が出力される | |
| UT-DAO-34 | 指定年月に売上データがない場合、false を返す | 2099-01 の売上レコードが存在しない | response = モック, month = "2099-01" | false が返る | |
| UT-DAO-35 | 指定年月以外のデータが含まれない | 2024-02・2024-03・2024-04 の売上が存在する | response = モック, month = "2024-03" | 2024-03 のデータのみ CSV に出力される | |

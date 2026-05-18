package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * 商品マスタ（m_product）・売上テーブル（t_sales）のDBアクセスを担うDAOクラス。
 */
public class ProductDAO {

	/**
	 * コネクションプールからDBコネクションを取得する。
	 *
	 * @return DBコネクション
	 * @throws NamingException JNDIリソースが見つからない場合
	 * @throws SQLException    コネクション取得に失敗した場合
	 */
	private Connection getConnection() throws NamingException, SQLException {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/kadaidb");
		return ds.getConnection();
	}

	/**
	 * 削除済み商品を除く全商品を商品コード昇順で返す。
	 *
	 * @return 全商品リスト。該当なしの場合は空リストを返す
	 */
	public List<Product> findAll() {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT product_code, product_name, price FROM m_product WHERE delete_datetime IS NULL ORDER BY product_code ASC";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Product p = new Product();
				p.setProductCode(rs.getInt("product_code"));
				p.setProductName(rs.getString("product_name"));
				p.setPrice(rs.getInt("price"));
				products.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return products;
	}

	/**
	 * 商品名がキーワードに部分一致する商品を返す。削除済み商品は除外される。
	 *
	 * @param keyword 検索キーワード（部分一致）。空文字の場合は全件取得する
	 * @return 条件に一致した商品リスト。該当なしの場合は空リストを返す
	 */
	public List<Product> search(String keyword) {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT product_code, product_name, price FROM m_product WHERE product_name LIKE ? AND delete_datetime IS NULL ORDER BY product_code ASC";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, "%" + keyword + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Product p = new Product();
				p.setProductCode(rs.getInt("product_code"));
				p.setProductName(rs.getString("product_name"));
				p.setPrice(rs.getInt("price"));
				products.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return products;
	}

	/**
	 * 指定された商品名と単価で新規商品をm_productに登録する。
	 *
	 * @param registerProductName 登録する商品名
	 * @param registerPriceInt    登録する単価
	 */
	public void insert(String registerProductName, int registerPriceInt) {
		String sql = "INSERT INTO m_product (product_name, price, register_datetime, update_datetime) VALUES (?, ?, now(), now())";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, registerProductName);
			pstmt.setInt(2, registerPriceInt);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定された商品コードの商品詳細（商品名・単価・更新日時）を取得する。
	 *
	 * @param productCode 取得対象の商品コード
	 * @return 取得した商品情報。該当なしの場合は productCode のみセットされた Product を返す
	 */
	public Product getProductDetails(int productCode) {
		Product p = new Product();
		p.setProductCode(productCode);
		String sql = "SELECT product_name, price, update_datetime FROM m_product WHERE product_code = ?";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, productCode);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				p.setProductName(rs.getString("product_name"));
				p.setPrice(rs.getInt("price"));
				p.setUpdateDatetime(rs.getString("update_datetime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * 指定された商品コードの商品名と単価を更新する。update_datetime も現在時刻に更新される。
	 *
	 * @param productCode 更新対象の商品コード
	 * @param productName 変更後の商品名
	 * @param price       変更後の単価
	 */
	public void update(int productCode, String productName, int price) {
		String sql = "UPDATE m_product SET product_name = ?, price = ?, update_datetime = now() WHERE product_code = ?";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, productName);
			pstmt.setInt(2, price);
			pstmt.setInt(3, productCode);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定された商品コードの商品を論理削除する。
	 * レコードは残したまま delete_datetime に現在時刻をセットする（物理削除ではない）。
	 *
	 * @param productCode 論理削除対象の商品コード
	 */
	public void logicalDelete(int productCode) {
		String sql = "UPDATE m_product SET update_datetime = now(), delete_datetime = now() WHERE product_code = ?";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, productCode);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定された商品コードの売上データが存在するかを判定する。
	 * 単価の変更可否および削除可否の判定に使用する。
	 *
	 * @param productCode 確認対象の商品コード
	 * @return 売上データが1件以上存在すれば {@code true}、存在しなければ {@code false}
	 */
	public boolean existSales(int productCode) {
		int resultInt = 0;
		String sql = "SELECT product_code FROM t_sales WHERE product_code = ?";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, productCode);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				resultInt = rs.getInt("product_code");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return !(resultInt == 0);
	}

	/**
	 * tempList の全商品を1トランザクションで売上登録する。
	 * 当日分のレコードが既存であれば数量を UPDATE、なければ INSERT（upsert相当）。
	 * 途中でエラーが発生した場合はすべてロールバックされる。
	 *
	 * @param tempList 登録対象の商品リスト
	 * @param today    売上日
	 */
	public void registerSalesAll(List<Product> tempList, Date today) {
		String selectSql = "SELECT product_code FROM t_sales WHERE product_code = ? AND sales_date = ?";
		String insertSql = "INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime) VALUES (now(), ?, ?, now(), now())";
		String updateSql = "UPDATE t_sales SET quantity = ? WHERE sales_date = ? AND product_code = ?";
		String strToday = new SimpleDateFormat("yyyy-MM-dd").format(today);
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try {
				for (Product p : tempList) {
					boolean exists = false;
					try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
						pstmt.setInt(1, p.getProductCode());
						pstmt.setString(2, strToday);
						ResultSet rs = pstmt.executeQuery();
						exists = rs.next();
					}
					if (exists) {
						try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
							pstmt.setInt(1, p.getQuantity());
							pstmt.setString(2, strToday);
							pstmt.setInt(3, p.getProductCode());
							pstmt.executeUpdate();
						}
					} else {
						try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
							pstmt.setInt(1, p.getProductCode());
							pstmt.setInt(2, p.getQuantity());
							pstmt.executeUpdate();
						}
					}
				}
				conn.commit();

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 当日（curdate()）の売上データ一覧を返す。削除済み商品は除外される。
	 *
	 * @return 当日の売上データリスト。該当なしの場合は空リストを返す
	 */
	public List<Product> findSales() {
		List<Product> productList = new ArrayList<>();
		String sql = "SELECT M.product_name, T.quantity, T.product_code FROM t_sales AS T INNER JOIN m_product AS M ON T.product_code = M.product_code WHERE delete_datetime IS NULL AND T.sales_date = curdate()";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Product p = new Product();
				p.setProductCode(rs.getInt("product_code"));
				p.setProductName(rs.getString("product_name"));
				p.setQuantity(rs.getInt("quantity"));
				productList.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return productList;
	}

	/**
	 * 全期間の商品別売上集計をCSV形式でダウンロードする。
	 * 対象データが0件の場合はレスポンスに何も書き込まず {@code false} を返す。
	 *
	 * @param response CSV出力先のHTTPレスポンス
	 * @return ダウンロード成功の場合は {@code true}、対象データ0件の場合は {@code false}
	 * @throws IOException レスポンス書き込みに失敗した場合
	 */
	public boolean download(HttpServletResponse response) throws IOException {
		boolean result = false;
		String sql = "SELECT M.product_code, M.product_name, M.price, SUM(T.quantity), SUM(T.quantity * M.price) AS 金額 FROM m_product AS M LEFT OUTER JOIN t_sales AS T ON M.product_code = T.product_code GROUP BY product_code";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rmd = rs.getMetaData();
			if (rs.next() == false) {
				return result;
			}
			response.setContentType("application/octet-stream;charset=Windows-31J");
			response.setHeader("Content-Disposition", "attachment; filename=SalesSummary.csv");
			PrintWriter out = response.getWriter();
			out.println("商品コード,商品名,単価,数量,金額");
			do {
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					String str = rs.getString(i);
					if (str == null) {
						str = "";
					}
					if (str.contains("\"")) {
						str = str.replace("\"", "\"\"");
					}
					if (str.contains(",")) {
						str = "\"" + str + "\"";
					}
					out.print(str);
					out.print(i < rmd.getColumnCount() ? "," : "");
				}
				out.print(System.getProperty("line.separator"));
			} while (rs.next());
			out.flush();
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		result = true;
		return result;
	}

	/**
	 * 指定年月の商品別売上集計をCSV形式でダウンロードする。
	 * 対象データが0件の場合はレスポンスに何も書き込まず {@code false} を返す。
	 *
	 * @param response CSV出力先のHTTPレスポンス
	 * @param month    集計対象の年月（yyyy-MM形式。例："2024-03"）
	 * @return ダウンロード成功の場合は {@code true}、対象データ0件の場合は {@code false}
	 * @throws IOException レスポンス書き込みに失敗した場合
	 */
	public boolean downloadForPeriod(HttpServletResponse response, String month) throws IOException {
		boolean result = false;
		String sql = "SELECT M.product_code, M.product_name, M.price, SUM(T.quantity), SUM(T.quantity * M.price) AS 金額 FROM m_product AS M INNER JOIN t_sales AS T ON M.product_code = T.product_code WHERE T.sales_date LIKE ? GROUP BY product_code";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, month + "%");
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rmd = rs.getMetaData();
			if (rs.next() == false) {
				return result;
			}
			response.setContentType("application/octet-stream;charset=Windows-31J");
			response.setHeader("Content-Disposition", "attachment; filename=SalesSummaryForPeriod.csv");
			PrintWriter out = response.getWriter();
			out.println("商品コード,商品名,単価,数量,金額");
			do {
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					String str = rs.getString(i);
					if (str == null) {
						str = "";
					}
					if (str.contains("\"")) {
						str = str.replace("\"", "\"\"");
					}
					if (str.contains(",")) {
						str = "\"" + str + "\"";
					}
					out.print(str);
					out.print(i < rmd.getColumnCount() ? "," : "");
				}
				out.print(System.getProperty("line.separator"));
			} while (rs.next());
			out.flush();
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		result = true;
		return result;
	}
}

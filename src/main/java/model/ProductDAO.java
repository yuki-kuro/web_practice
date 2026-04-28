package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class ProductDAO {

	private final String JDBC_URL = "com.mysql.cj.jdbc.Driver";
	private final String URL = "jdbc:mysql://localhost/kadaidb";
	private final String USER = "root";
	private final String PASSWD = "pass";

	public List<Product> findAll() {
		List<Product> products = new ArrayList<>();
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT product_code, product_name, price FROM m_product WHERE delete_datetime IS NULL ORDER BY product_code ASC";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Product p = new Product();
				p.setProductCode(rs.getInt("product_code"));
				p.setProductName(rs.getString("product_name"));
				p.setPrice(rs.getInt("price"));
				products.add(p);
			}
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return products;
	}

	public List<Product> search(String keyword) {
		List<Product> products = new ArrayList<>();
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT product_code, product_name, price FROM m_product WHERE product_name LIKE ? AND delete_datetime IS NULL ORDER BY product_code ASC;";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + keyword + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Product p = new Product();
				p.setProductCode(rs.getInt("product_code"));
				p.setProductName(rs.getString("product_name"));
				p.setPrice(rs.getInt("price"));
				products.add(p);
			}
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return products;
	}

	public void insert(String registerProductName, int registerPriceInt) {
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);

			String sql = "INSERT INTO m_product (product_name, price, register_datetime, update_datetime) VALUES (?, ?, now(), now())";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, registerProductName);
			pstmt.setInt(2, registerPriceInt);
			pstmt.executeUpdate();

			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Product getProductDetails(int productCode) {
		Product p = new Product();
		p.setProductCode(productCode);
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT product_name, price, update_datetime FROM m_product WHERE product_code = ?;";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productCode);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				p.setProductName(rs.getString("product_name"));
				p.setPrice(rs.getInt("price"));
				p.setUpdateDatetime(rs.getString("update_datetime"));
			}
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return p;
	}

	public void update(int productCode, String productName, int price) {
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "UPDATE m_product SET product_name = ?, price = ?, update_datetime = now() WHERE product_code = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, productName);
			pstmt.setInt(2, price);
			pstmt.setInt(3, productCode);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void logicalDelete(int productCode) {
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "UPDATE m_product SET update_datetime = now(), delete_datetime = now() "
					+ "WHERE product_code = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productCode);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean existSales(int productCode) {
		int resultInt = 0;
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT product_code FROM t_sales WHERE product_code = ?;";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productCode);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				resultInt = rs.getInt("product_code");
			}
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return !(resultInt == 0);

	}
	public boolean existSales(int productCode, Date salesDate) {
		int resultInt = 0;
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT product_code FROM t_sales WHERE product_code = ? AND sales_date = ?;";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productCode);
			pstmt.setString(2, new SimpleDateFormat("yyyy-MM-dd").format(salesDate));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				resultInt = rs.getInt("product_code");
			}
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return !(resultInt == 0);
		
	}
	public void insertSales(int productCode, int quantity) {
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "INSERT INTO t_sales (sales_date, product_code, quantity, register_datetime, update_datetime) VALUES (now(), ?, ?, now(), now())";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productCode);
			pstmt.setInt(2, quantity);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateSales(int quantity, Date salesDate, int productCode) {
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "UPDATE t_sales SET quantity = ? WHERE sales_date = ? AND product_code = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, quantity);
			pstmt.setString(2, new SimpleDateFormat("yyyy-MM-dd").format(salesDate));
			pstmt.setInt(3, productCode);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public List<Product> findSales() {
		List<Product> productList = new ArrayList<>();
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT M.product_name, T.quantity , T.product_code FROM t_sales AS T INNER JOIN m_product AS M ON T.product_code = M.product_code WHERE delete_datetime IS NULL AND T.sales_date = curdate()";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Product p = new Product();
				p.setProductCode(rs.getInt("product_code"));
				p.setProductName(rs.getString("product_name"));
				p.setQuantity(rs.getInt("quantity"));
				productList.add(p);
			}
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productList;
	}

	public boolean download(HttpServletResponse response) throws IOException {
		boolean result = false;
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT M.product_code, M.product_name, M.price, SUM(T.quantity), SUM(T.quantity * M.price) AS 金額 FROM m_product AS M LEFT OUTER JOIN t_sales AS T ON M.product_code = T.product_code GROUP BY product_code";
			PreparedStatement pstmt = conn.prepareStatement(sql);
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
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		result = true;
		return result;
	}

	public boolean downloadForPeriod(HttpServletResponse response, String month) throws IOException {
		boolean result = false;
		try {
			Class.forName(JDBC_URL);
			Connection conn = DriverManager.getConnection(URL, USER, PASSWD);
			String sql = "SELECT M.product_code, M.product_name, M.price, SUM(T.quantity), SUM(T.quantity * M.price) AS 金額 FROM m_product AS M INNER JOIN t_sales AS T ON M.product_code = T.product_code WHERE T.sales_date like ? GROUP BY product_code";
			PreparedStatement pstmt = conn.prepareStatement(sql);
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
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		result = true;
		return result;
	}
}

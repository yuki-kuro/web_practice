package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Product;
import model.ProductDAO;

@WebServlet("/EditProduct")
public class EditProduct extends HttpServlet {
	ProductDAO dao = new ProductDAO();
	Product preEditProductData = new Product();
	Product productDataToChange = new Product();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int productCode = Integer.parseInt(request.getParameter("productCode"));
		preEditProductData = dao.getProductDetails(productCode);
		request.setAttribute("productDataToChange", preEditProductData);
		request.setAttribute("preEditProductData", preEditProductData);
		request.getRequestDispatcher("/WEB-INF/view/editProduct.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// リクエストパラメータの文字コードをUTF-8で受け取る
		request.setCharacterEncoding("UTF-8");
		// リクエストパラメータ取得
		String edit = request.getParameter("edit");
		String productNameToChange = request.getParameter("productNameToChange");
		String stringPriceToChange = request.getParameter("priceToChange");
		int productCodeToChange = preEditProductData.getProductCode();
		productNameToChange = escapeHtml(productNameToChange);
		stringPriceToChange = escapeHtml(stringPriceToChange);
		List<String> errorMessages = new ArrayList<>();
		// エラー処理
		// 楽観的排他制御
		String beforeUpdateDatetime = preEditProductData.getUpdateDatetime();
		String afterUpdateDatetime = dao.getProductDetails(productCodeToChange)
				.getUpdateDatetime();
		if (!beforeUpdateDatetime.equals(afterUpdateDatetime)) {
			errorMessages.add("この商品は他の人によって更新されています｡商品検索ページから再編集してください｡");
		}
		if (productNameToChange.length() == 0) {
			errorMessages.add("商品名を入力してください");
		}
		if (productNameToChange.length() > 50) {
			errorMessages.add("商品名は50字以内で入力してください｡");
		}
		int intPriceToChange = 0;
		if (stringPriceToChange.equals("")) {
			errorMessages.add("単価を入力してください");
		} else {
			try {
				intPriceToChange = Integer.parseInt(stringPriceToChange);
				if (intPriceToChange < 1 || intPriceToChange > 1000000) {
					errorMessages.add("単価は1から1,000,000の範囲で入力してください");
				}
			} catch (NumberFormatException e) {
				errorMessages.add("整数ではない値が入力されました｡整数を入力してください");
			}
		}
		if (dao.existSales(productCodeToChange)) {
			if (intPriceToChange != preEditProductData.getPrice()) {
				errorMessages.add("すでに売り上げがあるので、単価の変更はできません。");
			}
			if (edit.equals("削除")) {
				errorMessages.add("すでに売り上げがあるので、削除はできません。");
			}
		}
		if (errorMessages.size() != 0) {	// エラー有りの処理
			Product productDataToChange = new Product();
			productDataToChange.setProductName(productNameToChange);
			productDataToChange.setPrice(intPriceToChange);
			request.setAttribute("productDataToChange", productDataToChange);
			request.setAttribute("preEditProductData", preEditProductData);
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("/WEB-INF/view/editProduct.jsp").forward(request, response);
		} else {	// エラー無しの処理
			if (edit.equals("変更")) {
				dao.update(productCodeToChange, productNameToChange, intPriceToChange);
			} else if (edit.equals("削除")) {
				dao.logicalDelete(productCodeToChange);
			}
			response.sendRedirect("/web_practice/SearchProduct");
		}
	}

	public static String escapeHtml(String input) {
		if (input == null) {
			return null;
		}
		StringBuilder escaped = new StringBuilder();
		for (char c : input.toCharArray()) {
			switch (c) {
			case '<':
				escaped.append("&lt;");
				break;
			case '>':
				escaped.append("&gt;");
				break;
			case '&':
				escaped.append("&amp;");
				break;
			case '"':
				escaped.append("&quot;");
				break;
			case '\'':
				escaped.append("&#x27;");
				break;
			case '/':
				escaped.append("&#x2F;");
				break;
			default:
				escaped.append(c);
				break;
			}
		}
		return escaped.toString();
	}
}

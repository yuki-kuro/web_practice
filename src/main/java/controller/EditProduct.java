package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Product;
import model.ProductDAO;

/**
 * 商品変更・削除画面の表示（GET）と更新処理（POST）を行うサーブレット。
 * 楽観的排他制御により、複数ユーザーが同一商品を同時編集した場合に後勝ちを防止する。
 */
@WebServlet("/EditProduct")
public class EditProduct extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProductDAO dao = new ProductDAO();
		int productCode = Integer.parseInt(request.getParameter("productCode"));
		Product preEditProductData = dao.getProductDetails(productCode);
		request.getSession().setAttribute("preEditProductData", preEditProductData);
		request.setAttribute("productDataToChange", preEditProductData);
		String csrfToken = UUID.randomUUID().toString();
		request.getSession().setAttribute("csrfToken", csrfToken);
		request.setAttribute("csrfToken", csrfToken);
		request.getRequestDispatcher("/WEB-INF/view/editProduct.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String sessionToken = (String) request.getSession().getAttribute("csrfToken");
		String requestToken = request.getParameter("csrfToken");
		if (sessionToken == null || !sessionToken.equals(requestToken)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです");
			return;
		}
		request.getSession().removeAttribute("csrfToken");
		ProductDAO dao = new ProductDAO();
		// リクエストパラメータ取得
		String edit = request.getParameter("edit");
		String productNameToChange = request.getParameter("productNameToChange");
		String stringPriceToChange = request.getParameter("priceToChange");
		Product preEditProductData = (Product) request.getSession().getAttribute("preEditProductData");
		int productCodeToChange = preEditProductData.getProductCode();
		List<String> errorMessages = new ArrayList<>();
		// エラー処理
		// 楽観的排他制御
		String beforeUpdateDatetime = preEditProductData.getUpdateDatetime();
		String afterUpdateDatetime = dao.getProductDetails(productCodeToChange)
				.getUpdateDatetime();
		if (!beforeUpdateDatetime.equals(afterUpdateDatetime)) {
			errorMessages.add("この商品は他の人によって更新されています｡商品検索ページから再編集してください｡");
		}
		if (productNameToChange == null || productNameToChange.isEmpty()) {
			errorMessages.add("商品名を入力してください");
		} else if (productNameToChange.length() > 50) {
			errorMessages.add("商品名は50字以内で入力してください｡");
		}
		int intPriceToChange = 0;
		if ("".equals(stringPriceToChange)) {
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

		// ビジネスルール：売上実績がある商品は単価の変更および削除を禁止する。商品名の変更は可能。
		// 単価を変えると過去売上の金額集計が変わるため変更不可。
		// 削除すると売上レポートで商品名が参照できなくなるため削除不可。
		if (dao.existSales(productCodeToChange)) {
			if (intPriceToChange != preEditProductData.getPrice()) {
				errorMessages.add("すでに売り上げがあるので、単価の変更はできません。");
			}
			if (edit.equals("削除")) {
				errorMessages.add("すでに売り上げがあるので、削除はできません。");
			}
		}
		if (errorMessages.size() != 0) {
			Product productDataToChange = new Product();
			productDataToChange.setProductName(productNameToChange);
			productDataToChange.setPrice(intPriceToChange);
			request.setAttribute("productDataToChange", productDataToChange);
			request.setAttribute("errorMessages", errorMessages);
			String newToken = UUID.randomUUID().toString();
			request.getSession().setAttribute("csrfToken", newToken);
			request.setAttribute("csrfToken", newToken);
			request.getRequestDispatcher("/WEB-INF/view/editProduct.jsp").forward(request, response);
		} else {
			if (edit.equals("変更")) {
				dao.update(productCodeToChange, productNameToChange, intPriceToChange);
			} else if (edit.equals("削除")) {
				dao.logicalDelete(productCodeToChange);
			}
			request.getSession().removeAttribute("preEditProductData");
			response.sendRedirect("/web_practice/SearchProduct");
		}
	}
}

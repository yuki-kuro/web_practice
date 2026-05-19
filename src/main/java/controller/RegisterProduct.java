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

import model.ProductDAO;

/**
 * 商品登録画面の表示（GET）と登録処理（POST）を行うサーブレット。
 */
@WebServlet("/RegisterProduct")
public class RegisterProduct extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// CSRF対策
		String csrfToken = UUID.randomUUID().toString();
		request.getSession().setAttribute("csrfToken", csrfToken);
		request.setAttribute("csrfToken", csrfToken);
		// 商品登録画面に遷移
		request.getRequestDispatcher("/WEB-INF/view/registerProduct.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// CSRF対策
		String sessionToken = (String) request.getSession().getAttribute("csrfToken");
		String requestToken = request.getParameter("csrfToken");
		if (sessionToken == null || !sessionToken.equals(requestToken)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです");
			return;
		}
		request.getSession().removeAttribute("csrfToken");

		String registerProductName = request.getParameter("registerProductName");
		String registerPriceString = request.getParameter("registerPrice");
		List<String> errorMessages = new ArrayList<>();

		// 商品名チェック
		if (registerProductName == null || registerProductName.isEmpty()) {
			errorMessages.add("商品名を入力してください");
		} else if (registerProductName.length() > 50) {
			errorMessages.add("商品名は50字以内で入力してください｡");
		}

		// 単価チェック
		int registerPriceInt = 0;
		if ("".equals(registerPriceString)) {
			errorMessages.add("単価を入力してください");
		} else {
			try {
				registerPriceInt = Integer.parseInt(registerPriceString);
				if (registerPriceInt < 1 || registerPriceInt > 1000000) {
					errorMessages.add("単価は1から1,000,000の範囲で入力してください");
				}
			} catch (NumberFormatException e) {
				errorMessages.add("整数ではない値が入力されました｡整数を入力してください");
			}
		}

		if (errorMessages.size() != 0) {
			// エラーありの場合、自画面に遷移してエラーメッセージを表示
			String newToken = UUID.randomUUID().toString();
			request.getSession().setAttribute("csrfToken", newToken);
			request.setAttribute("csrfToken", newToken);
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("/WEB-INF/view/registerProduct.jsp").forward(request, response);
		} else {
			// エラーなしの場合、登録を実行し、PRGパターンで商品検索画面にリダイレクト
			ProductDAO dao = new ProductDAO();
			dao.insert(registerProductName, registerPriceInt);
			response.sendRedirect("/web_practice/SearchProduct");
		}
	}

}

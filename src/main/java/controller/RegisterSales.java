package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Product;
import model.ProductDAO;

/**
 * 売上登録画面の表示（GET）と登録処理（POST）を行うサーブレット。
 * 追加ボタンでセッションの一時リストに売上データを積み、登録ボタンで一括でDBにコミットする。
 */
@WebServlet("/RegisterSales")
public class RegisterSales extends HttpServlet {
	ProductDAO dao = new ProductDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// CSRF対策
		String csrfToken = UUID.randomUUID().toString();
		request.getSession().setAttribute("csrfToken", csrfToken);
		request.setAttribute("csrfToken", csrfToken);

		request.setAttribute("today", new Date());
		request.setAttribute("productList", dao.findAll());
		request.setAttribute("salesList", dao.findSales());
		// 売上登録画面に遷移
		request.getRequestDispatcher("/WEB-INF/view/registerSales.jsp").forward(request, response);
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

		Date today = new Date();
		HttpSession session = request.getSession();
		List<Product> tempList = (List<Product>) session.getAttribute("tempList");
		if (tempList == null) {
			tempList = new ArrayList<>();
		}

		String add = request.getParameter("add");
		if ("追加".equals(add)) {
			List<String> errorMessages = new ArrayList<>();
			// 商品名チェック
			String stringProductCodeToAdd = request.getParameter("productCodeToAdd");
			int productCodeToAdd = 0;
			if ("".equals(stringProductCodeToAdd)) {
				errorMessages.add("商品名が選択されていません。");
			} else {
				productCodeToAdd = Integer.parseInt(stringProductCodeToAdd);
			}

			// 数量チェック
			String stringQuantity = request.getParameter("quantityToAdd");
			int intQuantity = 0;
			if ("".equals(stringQuantity)) {
				errorMessages.add("数量を入力してください");
			} else {
				try {
					intQuantity = Integer.parseInt(stringQuantity);
					if (intQuantity < 1 || intQuantity > 1000) {
						errorMessages.add("数量は1から1,000の範囲で入力してください");
					}
				} catch (NumberFormatException e) {
					errorMessages.add("整数ではない値が入力されました｡整数を入力してください");
				}
			}

			if (errorMessages.size() != 0) {
				// エラーありの場合、エラーメッセージを生成
				request.setAttribute("errorMessages", errorMessages);
			} else {
				// エラーなしの場合、一時リストを更新
				Product productDetailsToAdd = dao.getProductDetails(productCodeToAdd);
				productDetailsToAdd.setQuantity(intQuantity);
				tempList.add(productDetailsToAdd);
			}
			session.setAttribute("tempList", tempList);
			// CSRF対策
			String newToken = UUID.randomUUID().toString();
			request.getSession().setAttribute("csrfToken", newToken);
			request.setAttribute("csrfToken", newToken);

			request.setAttribute("today", new Date());
			request.setAttribute("productList", dao.findAll());
			request.setAttribute("salesList", dao.findSales());
			// 自画面に遷移
			request.getRequestDispatcher("/WEB-INF/view/registerSales.jsp").forward(request, response);
		} else {
			// ｢登録｣ボタンがクリックされた場合、一時リストをDBに一括登録し、GETリクエストすることで連続で登録できるようにする。
			dao.registerSalesAll(tempList, today);
			tempList.clear();
			doGet(request, response);
		}
	}

}

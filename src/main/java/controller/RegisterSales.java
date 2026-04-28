package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Product;
import model.ProductDAO;

@WebServlet("/RegisterSales")
public class RegisterSales extends HttpServlet {
	List<Product> tempList = new ArrayList<>();
	ProductDAO dao = new ProductDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Date today = new Date();
		// 売上日を保存
		request.setAttribute("today", today);
		// 削除されていない商品リストを取得する
		List<Product> productList = dao.findAll();
		request.setAttribute("productList", productList);
		// 売上日の売上リストを取得する
		List<Product> salesList = dao.findSales();
		request.setAttribute("salesList", salesList);
		// 追加ボタンがクリックされたときの処理
		String add = request.getParameter("add");
		if (add != null) {
			List<String> errorMessages = new ArrayList<>();
			// 商品名必須チェック
			String stringProductCodeToAdd = request.getParameter("productCodeToAdd");
			int productCodeToAdd = 0;
			if (stringProductCodeToAdd.equals("")) {
				errorMessages.add("商品名が選択されていません。");
			} else {
				productCodeToAdd = Integer.parseInt(stringProductCodeToAdd);
			}
			
			// 数量必須チェックと範囲チェック
			String stringQuantity = request.getParameter("quantityToAdd");
			int intQuantity = 0;
			if (stringQuantity.equals("")) {
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
			
			if (errorMessages.size() != 0) {	//エラーが有るときの処理
				request.setAttribute("errorMessages", errorMessages);
			} else {	//エラーが無いときの処理
				Product productDetailsToAdd = dao.getProductDetails(productCodeToAdd);
				productDetailsToAdd.setQuantity(intQuantity);
				tempList.add(productDetailsToAdd);
			}
			request.setAttribute("tempList", tempList);
		}
		request.getRequestDispatcher("/WEB-INF/view/registerSales.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		Date today = new Date();
		for (Product p : tempList) {
			if (dao.existSales(p.getProductCode(), today)) {
				dao.updateSales(p.getQuantity(), today, p.getProductCode());
			} else {
				dao.insertSales(p.getProductCode(), p.getQuantity());
			}
		}
		tempList.clear();
		doGet(request, response);
	}

}

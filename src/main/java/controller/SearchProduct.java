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

@WebServlet("/SearchProduct")
public class SearchProduct extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Product> products = new ArrayList<>();
		String keyword = request.getParameter("keyword");
		ProductDAO dao = new ProductDAO();
		List<String> errorMessages = new ArrayList<>();
		if (keyword == null) {
			products = dao.findAll();
		} else if (keyword.length() > 50) {
			errorMessages.add("50字以内で入力してください");
			products = dao.findAll();
		} else {
			products = dao.search(keyword);
			if (products.size() == 0) {
				errorMessages.add("検索結果は0件です｡");
				products = dao.findAll();
			}
		}
		request.setAttribute("errorMessages", errorMessages);
		request.setAttribute("products", products);
		request.getRequestDispatcher("/WEB-INF/view/searchProduct.jsp").forward(request, response);
	}
}

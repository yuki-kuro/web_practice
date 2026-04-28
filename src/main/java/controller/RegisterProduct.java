package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ProductDAO;

@WebServlet("/RegisterProduct")
public class RegisterProduct extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/view/registerProduct.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String registerProductName = request.getParameter("registerProductName");
		String registerPriceString = request.getParameter("registerPrice");
		registerProductName = escapeHtml(registerProductName);
		registerPriceString = escapeHtml(registerPriceString);
		
		List<String> errorMessages = new ArrayList<>();
		if (registerProductName.length() == 0) {
			errorMessages.add("商品名を入力してください");
		}
		if (registerProductName.length() > 50) {
			errorMessages.add("商品名は50字以内で入力してください｡");
		}
		int registerPriceInt = 0;
		if (registerPriceString.equals("")) {
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
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("/WEB-INF/view/registerProduct.jsp").forward(request, response);
		} else {
			ProductDAO dao = new ProductDAO();
			dao.insert(registerProductName, registerPriceInt);
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

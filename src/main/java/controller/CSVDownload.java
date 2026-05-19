package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ProductDAO;

/**
 * 売上集計CSVのダウンロードを行うサーブレット。
 * 全期間の商品別集計と、指定年月の商品別集計の2種類に対応する。
 */
@WebServlet("/CSVDownload")
public class CSVDownload extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProductDAO dao = new ProductDAO();
		String download = request.getParameter("download");
		String month = request.getParameter("month");
		try {
			List<String> errorMessages = new ArrayList<>();
			boolean downloaded = false;
			// ｢商品別売上集計CSV｣ボタンがクリックされた場合
			if ("商品別売上集計CSV".equals(download)) {
				if (dao.download(response)) {
					downloaded = true;
				} else {
					errorMessages.add("対象データは0件です。");
				}
				// ｢指定年月商品別売上集計CSV｣ボタンがクリックされた場合
			} else if ("指定年月商品別売上集計CSV".equals(download)) {
				// 年月チェック
				if (month == null || month.equals("")) {
					errorMessages.add("年月が指定されていません。");
				} else {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
					Date formatMonth = simpleDateFormat.parse(month);
					final String LOWER_MONTH = "2020-01";
					Date formatLowerMonth = simpleDateFormat.parse(LOWER_MONTH);
					Date today = new Date();
					String stringToday = simpleDateFormat.format(today);
					if (today.before(formatMonth)) {
						errorMessages.add(
								"未来の年月が入力されました。");
						errorMessages.add(
								LOWER_MONTH + "以降" + stringToday + "以前の年月を指定してください。");
					}
					if (formatMonth.before(formatLowerMonth)) {
						errorMessages.add(
								"古すぎる年月が入力されました｡");
						errorMessages.add(
								LOWER_MONTH + "以降" + stringToday + "以前の年月を指定してください。");
					}
				}

				if (errorMessages.size() == 0) {
					if (dao.downloadForPeriod(response, month)) {
						downloaded = true;
					} else {
						errorMessages.add("対象データは0件です。");
					}
				}
			}
			if (!downloaded) {
				// ダウンロード失敗の場合、自画面に遷移してエラーメッセージを表示
				request.setAttribute("errorMessages", errorMessages);
				request.getRequestDispatcher("/WEB-INF/view/CSVDownload.jsp").forward(request, response);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="model.Product"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
List<String> errorMessages = (List<String>) request.getAttribute("errorMessages");
List<Product> productList = (List<Product>) request.getAttribute("productList");
List<Product> tempList = (List<Product>) session.getAttribute("tempList");
List<Product> salesList = (List<Product>) request.getAttribute("salesList");
Date today = (Date) request.getAttribute("today");
SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
String csrfToken = (String) request.getAttribute("csrfToken");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>売上登録</title>
</head>
<body>
	<h1>売上登録</h1>
	<jsp:include page="link.jsp" />
		<% if (errorMessages != null) { %>
		<% for (String err : errorMessages) { %>
			<p><%= err %></p>
		<% } %>
	<% } %>	
	<table>
		<tr>
			<th>売上日</th>
			<td><%=df.format(today)%></td>
		</tr>
		<tr>
			<th>商品名</th>
			<td>
				<form action="/web_practice/RegisterSales" method="post">
					<select name="productCodeToAdd">
						<option value="" selected>選択してください</option>
						<%
						for (Product p : productList) {
						%>
						<option value="<%=p.getProductCode()%>">
							<%=p.getProductCode()%> :
							<c:out value="<%=p.getProductName()%>"/>
						</option>
						<%
						}
						%>
					</select>
			</td>
			<th>数量</th>
			<td>
				<input type="text" name="quantityToAdd">
			</td>
		</tr>
	</table>
	<input type="hidden" name="csrfToken" value="<%=csrfToken%>">
	<input type="submit" name="add" value="追加">
	</form>
	<form action="/web_practice/RegisterSales" method="post">
	<table>
		<tr>
			<th>商品コード</th>
			<th>商品名</th>
			<th>数量</th>
		</tr>
		<%
		if (tempList != null) {
			for (Product p : tempList) {
		%>
		<tr>
			<td><%=p.getProductCode()%></td>
			<td><c:out value="<%=p.getProductName()%>"/></td>
			<td><%=p.getQuantity()%></td>
		</tr>
		<%
		}
		}
		%>
	</table>
	<input type="hidden" name="csrfToken" value="<%=csrfToken%>">
	<input type="submit" name="register" value="登録">
	</form>
		<table>
			<tr>
				<th>商品コード</th>
				<th>商品名</th>
				<th>数量</th>
			</tr>
			<%
			if (salesList != null) {
				for (Product p : salesList) {
			%>
			<tr>
				<td><%=p.getProductCode()%></td>
				<td><c:out value="<%=p.getProductName()%>"/></td>
				<td><%=p.getQuantity()%></td>
			</tr>
			<%
			}
			}
			%>
		</table>

</body>
</html>
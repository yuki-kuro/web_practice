<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.Product"%>
<%
List<Product> products = (List<Product>) request.getAttribute("products");
List<String> errorMessages = (List<String>) request.getAttribute("errorMessages");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品検索</title>
</head>
<body>
	<h1>商品検索</h1>
	<jsp:include page="link.jsp" />
	商品名
	<form action="/web_practice/SearchProduct" method="get">
		<input type="search" name="keyword" autofocus="autofocus">
		<input type="submit" value="検索">
	</form>
	<%
	if (errorMessages != null) {
		for (String err : errorMessages) {
	%>
	<p><%=err%></p>
	<%
		}
	}
	%>
	<table border="1">
		<tr>
			<th>商品コード</th>
			<th>商品名</th>
			<th>単価</th>
			<th>操作</th>
		</tr>
		<%
		for (Product p : products) {
		%>
		<tr>
			<td><%=String.format("%03d", new Integer(p.getProductCode()))%></td>
			<td><%=p.getProductName()%></td>
			<td><%=String.format("%,d", new Integer(p.getPrice()))%></td>
			<td>
				<form action="/web_practice/EditProduct" method="get">
					<input type="hidden" name="productCode" value="<%=p.getProductCode()%>">
					<input type="submit" value="編集">
				</form>
			</td>
		</tr>
		<%
		}
		%>
	</table>
</body>
</html>
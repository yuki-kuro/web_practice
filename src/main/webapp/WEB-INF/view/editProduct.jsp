<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Product"%>
<%@ page import="java.util.List"%>
<%
Product preEditProductData = (Product) request.getAttribute("preEditProductData");
Product productDataToChange = (Product) request.getAttribute("productDataToChange");
List<String> errorMessages = (List<String>) request.getAttribute("errorMessages");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品変更・削除</title>
</head>
<body>
	<h1>商品変更・削除</h1>
	<jsp:include page="link.jsp" />
	<form action="/web_practice/EditProduct" method="post">
		<table>
			<tr>
				<th>商品コード</th>
				<td><%=String.format("%03d", new Integer(preEditProductData.getProductCode()))%></td>
			</tr>
			<tr>
				<th>変更前 商品名</th>
				<td><%=preEditProductData.getProductName()%></td>
			</tr>
			<tr>
				<th>変更後 商品名</th>
				<td>
					<input type="text" name="productNameToChange" value="<%=productDataToChange.getProductName()%>">
				</td>
			</tr>
			<tr>
				<th>変更前 単価</th>
				<td><%=String.format("%,d", new Integer(preEditProductData.getPrice()))%></td>
			</tr>
			<tr>
				<th>変更後 単価</th>
				<td>
					<input type="text" name="priceToChange" value="<%=productDataToChange.getPrice()%>">
				</td>
			</tr>
		</table>
		<input type="submit" name="edit" value="変更">
		<input type="submit" name="edit" value="削除">
	</form>
	<%
	if (errorMessages != null) {
	%>
	<%
	for (String err : errorMessages) {
	%>
	<p><%=err%></p>
	<%
	}
	%>
	<%
	}
	%>
</body>
</html>
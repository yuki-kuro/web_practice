<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%
List<String> errorMessages = (List<String>) request.getAttribute("errorMessages");
String csrfToken = (String) request.getAttribute("csrfToken");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品登録</title>
</head>
<body>
	<h1>商品登録</h1>
	<jsp:include page="link.jsp" />
	<form action="/web_practice/RegisterProduct" method="post">
		<input type="hidden" name="action" value="register">
		商品名
		<input type="text" name="registerProductName" autofocus="autofocus">
		<br> 単価
		<input type="text" name="registerPrice">
		<br>
		<input type="submit" name="submit" value="登録">
		<input type="hidden" name="csrfToken" value="<%=csrfToken%>">
	</form>
	<% if (errorMessages != null) { %>
		<% for (String err : errorMessages) { %>
			<p><%= err %></p>
		<% } %>
	<% } %>	
</body>
</html>
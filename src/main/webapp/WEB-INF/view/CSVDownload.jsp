<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%
List<String> errorMessages = (List<String>) request.getAttribute("errorMessages");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CSVダウンロード</title>
</head>
<body>
	<h1>CSVダウンロード</h1>
	<jsp:include page="link.jsp" />
	<form action="/web_practice/CSVDownload" method="get">
		<input type="submit" name="download" value="商品別売上集計CSV">
		<br>
		<input type="month" name="month">
		<input type="submit" name="download" value="指定年月商品別売上集計CSV">
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
</body>
</html>
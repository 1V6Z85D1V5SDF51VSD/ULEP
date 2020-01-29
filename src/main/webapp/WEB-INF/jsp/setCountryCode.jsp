<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Set Country Code ULEP</title>
</head>
<body>
	Please Wait...
	<form id="returnToAccessManager" name="returnToAccessManager" action='${targetUrl}' method="get">
		<input type="hidden" name="id" value='${id}' />
		<input type="hidden" name="sid" value='${sid}'/>
	</form>
	<script>document.returnToAccessManager.submit();</script>

</body>
</html>
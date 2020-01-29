<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>ULEP Response Receiver</title>
</head>
<body>
	Your Response will be redirected.. Please Wait...
	<form id="responseController" name="responseController" action='${responseUrl}' method="post">
		<!--  <input type="hidden" name="AttributeMap" value='${AttributeMap}' />-->
	</form>

<script>document.responseController.submit();</script>
</body>
</html>
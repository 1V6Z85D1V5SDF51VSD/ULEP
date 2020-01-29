<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>ULEP Error Page</title>
</head>
<body>
	${errorString}
	<p/>
	There was an error during the authentication process, please try again.
	<form action="https://amis-dev.ulisboa.pt" method="post">
		<input type="submit" value="Redirect"/>
	</form>
</body>
</html>
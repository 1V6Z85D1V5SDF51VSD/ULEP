<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Testing page</title>
</head>
<body>
	Press the button to send a test SAML Response to Access Manager
	<form  id="sendTest" name="sendTest" action="/ULEP/sendAccessManagerResponse" method="post">
		<input type="submit" value="Send Test"/>
	</form>
	<!--  <script>document.sendTest.submit();</script> -->

</body>

</html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>ULEP Request Receiver</title>
</head>
<body>
	Your Request will be redirected.. Please Wait...
	<form id="spConnector" name="spConnector" action="/ULEP/EidasSendRequest" method="post">
		<input type="hidden" name="countryCode" value='${CountryCode}' />
		<input type="hidden" name="SAMLRequest" value='${SAMLRequest}' />
	</form>

<script>document.spConnector.submit();</script>
</body>
</html>
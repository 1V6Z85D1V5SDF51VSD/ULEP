<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>ULEP Country Selection</title>
</head>
<body>
	<fieldset>
	
		<legend>Select Destination Country Form</legend>
		<label>Please Select Your Country</label>
		<form id="spConnector" name="spConnector" action="/ULEP/EidasSendRequest" method="post">
		<p>
			<select name="countryCode" id="countryCode">
				<option value="AT">Austria</option>
				<option value="IT">Italy</option>
				<option value="PT">Portugal</option>
				<option value="SI">Slovenia</option>
				<option value="ES">Spain</option>
			</select>
			
			<input type="hidden" name="SAMLRequest" value='${SAMLRequest}' />
			<input type="submit" value="Proceed" />
		</p>
		</form>
	</fieldset>
</body>
</html>
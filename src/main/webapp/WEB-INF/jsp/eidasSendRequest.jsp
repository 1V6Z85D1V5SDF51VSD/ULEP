<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<body>
	Your Request will be redirected.. Please Wait...
</body>
<form id="startEidasForm" name="startEidasForm" action="<c:out value='${nodeUrl}' />" target="_parent" method="post">
	
		<input type="hidden" name="country" value="<c:out value='${citizenCountryCode}' />" />
		<input type="hidden" name="SAMLRequest" value="<c:out value='${SAMLRequest}' />" />
	</form>

<script>document.startEidasForm.submit();</script>
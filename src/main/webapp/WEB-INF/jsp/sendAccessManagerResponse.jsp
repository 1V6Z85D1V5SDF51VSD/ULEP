<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--	<form id="sendAccessManagerResponse" name="sendAccessManagerResponse" action="<c:out value='${AsserionConsumerServiceURL}'/>" method="post">
		<input type="hidden" name="SAMLResponse" value="<c:out value='${SAMLResponse}' />" />
		<input type="hidden" name="RelayState" value="<c:out value='${RelayState}' />" />
	</form>
	<script>document.sendAccessManagerResponse.submit();</script>--%>
<body>
	Your Response will be redirected.. Please Wait...
	<p/>
	<!--  <xmp>${printable}</xmp>-->
	<form action="https://amis-dev.ulisboa.pt" method="post">
		<input type="submit" value="Redirect Dev SSO"/>
	</form>
	<p/>
	<form id="sendAccessManagerResponse" name="sendAccessManagerResponse" action="<c:out value='${AsserionConsumerServiceURL}'/>" method="post">
		<input type="hidden" name="SAMLResponse" value="<c:out value='${SAMLResponse}' />" />
		<input type="hidden" name="RelayState" value="<c:out value='${RelayState}' />" />
		<input type="submit" value="Submit Authentication Response to Access Manager"/>
	</form>
	<script>document.sendAccessManagerResponse.submit();</script>
</body>
</html>
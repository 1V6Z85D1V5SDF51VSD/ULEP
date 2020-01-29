<!DOCTYPE html>

<html>
	<body>
	<center> <h1 style="padding-top: 5%">University of Lisbon Legacy Proxy</h1> </center>
	<div></div>
	<center><h2 style="padding-top: 10%">ULEP is a proxy that connects ULisboa's Identity Management System to the Portuguese eIDAS Node </h3></center>
		<div style="width: 49%; padding-top: 5%; display: inline-block; text-align: center;">
			<form action="/ULEP/IdPmetadata" method="post">
				<input type="submit" value="Identity Provider Metadata" style="height:60px"/>
			</form>
		</div>
		<div style="width: 49%; display: inline-block; text-align: center;">
			<form action="/ULEP/SPmetadata" method="post">
				<input type="submit" value="Service Provider Metadata" style="height:60px"/>
			</form>
		</div>
	</body>
</html>
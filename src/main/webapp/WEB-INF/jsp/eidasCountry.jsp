<!DOCTYPE html>

<html>
	<body>
		<fieldset>
			<legend>Select Destination Country Form</legend>
			<label>Please Select Your Country</label>
			<form action="/eidas" method="post">
				<p>
					<select name="countryCode" id="countryCode">
						<option value="AT">Austria</option>
						<option value="IT">Italy</option>
						<option value="PT">Portugal</option>
						<option value="SI">Slovenia</option>
						<option value="ES">Spain</option>				
					</select>
					<input type="hidden" name="SAMLRequest" value='${samlRequest}' />
					<input type="submit" value="Proceed" />
				</p>
			</form>
		</fieldset>
	</body>
</html>
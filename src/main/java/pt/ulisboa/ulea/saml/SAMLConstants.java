package pt.ulisboa.ulea.saml;

public final class SAMLConstants {

	private SAMLConstants(){
	}
	
	public static final String SAML_REQUEST = "SAMLRequest";
	
	public static final String SAML_RESPONSE = "SAMLResponse";
	
	public static final String RELAY_STATE = "RelayState";
	
	public static final String SAML_IN_RESPONSE_TO = "SamlAuthRequestInResponseTo";
	public static final String SAML_ASSERTION_CONSUMER_SERVICE_URL = "AsserionConsumerServiceURL";
	
	public static final String SP_ISSUER = "SpIssuer";
	public static final String COUNTRY_CODE = "CountryCode";
	
	public static final String REQUEST_TYPE = "requestType";
	
	public static final String ATTRIBUTE_MISSING = "A required Attribute is missing";
	public static final String SAML_REQUEST_SENT_ERROR = "The SAML Request is invalid or nonexistent";
	public static final String SAML_RESPONSE_VALIDATION_ERROR = "Could not validate token for SAML Response";
	public static final String SAML_ACS_ERROR = "Could not obtain the Assertion Consumer Service for this Service Provider";
	public static final String SAML_RESPONSE_NULL = "SAML Response is missing";
}

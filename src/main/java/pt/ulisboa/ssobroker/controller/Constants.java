package pt.ulisboa.ssobroker.controller;

import java.util.List;

import com.google.common.collect.Lists;

public final class Constants {
	private Constants() {
		// constructor	
	}
	
	// dev change to id.ulisboa.pt for production
	public static final String ACCESS_MANAGER_ISSUER = "https://amis-dev.ulisboa.pt/nidp/saml2/metadata";
	public static final String ACCESS_MANAGER_PRODUCTION_ISSUER = "https://id.ulisboa.pt/nidp/saml2/metadata";
	public static final String ACCESS_MANAGER_RESPONSE_CONTROLLER = "/sendAccessManagerResponse";
	
	// Response Attributes
	public static final List<String> RequestedAttributes = Lists.newArrayList("IdNumber", "PersonIdentifier", "FullName", "DateOfBirth");
	public static final String DATE_OF_BIRTH = "DateOfBirth";
	
	public static final String ACCESS_MANAGER_PRODUCTION_ENVIRONMENT = "am.use.production";
	public static final String ACCESS_MANAGER_PRODUCTION_ACS = "am.prod.acs_default";
	public static final String ACCESS_MANAGER_PRODUCTION_SPSEND = "am.prod.spsend_url";
	public static final String ACCESS_MANAGER_DEV_ACS = "am.dev.acs_default";
	public static final String ACCESS_MANAGER_DEV_SPSEND = "am.dev.spsend_url";
	
	//IdP metadata URL (in case it can't read from configs)
	public static final String FALLBACK_IDP_ISSUER = "https://eidas.ulisboa.pt/ULEP/IdPmetadata";
	//must change
	public static final String ZEROSHELL_ISSUER = "zeroshell";
	public static final String ZEROSHELL_RESPONSE_CONTROLLER = "/sendZeroshellResponse";
	public static final String ZEROSHELL_ACS= "zeroshell.acs_default";
	
	public static final String RESPONSE_CONTROLLER_URL = "responseUrl";
	
	public static final String REQUEST_TYPE_EIDAS = "eIDAS";
	public static final String REQUEST_TYPE_PT = "PT";

	public static final String ATTRIBUTE_MAP = "AttributeMap";
	
	public static final String TESTING_ENVIRONENT = "do.test";
	public static final String APPLICATION_PATH = "/ULEP";
	
	public static final String ERROR_STRING = "errorString";
	
	public static final String NO_ASSERTION = "no assertion found";

    public static final String ASSERTION_XPATH = "//*[local-name()='Assertion']";
    
    public static final String SESSION_NULL = "There was a problem obtaining the session";
    public static final String SP_ISSUER_ERROR = "Invalid Service Provider Issuer";
    
    public static final String ATTRIBUTE_LIST = "attributeList";
}

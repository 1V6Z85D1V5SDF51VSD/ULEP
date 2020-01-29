package pt.ulisboa.ssobroker.eidas;

import java.util.List;

import com.google.common.collect.Lists;

public final class EidasConstants {
	
		private EidasConstants() {
			// constructor	
		}
		
		public static final List<String> RequestedAttributes = Lists.newArrayList("IdNumber","PersonIdentifier", "FamilyName", "FirstName", "DateOfBirth");
		//public static final List<String> MinimumDataSet = Lists.newArrayList("PersonIdentifier", "FamilyName", "FirstName", "DateOfBirth");
		public static final String ID_NUMBER = "IdNumber";
		public static final String PERSON_IDENTIFIER = "PersonIdentifier";
		public static final String COUNTRY_CODE = "CountryCode";
		public static final String DATE_OF_BIRTH = "DateOfBirth";
		public static final String FIRST_NAME = "FirstName";
		public static final String FAMILY_NAME = "FamilyName";
		public static final String FULL_NAME = "FullName";
		
		public static final String COUNTRY_SP = "PT";
		
		public static final String SP_PROPERTIES = "sp.properties";
	    
	    public static final String SP_QAALEVEL = "sp.qaalevel";
	    
	    public static final String SP_RETURN = "sp.return";

	    public static final String PROVIDER_NAME = "provider.name";

	    public static final String SP_TYPE = "sp.type";
	    
	    public static final String SP_SECTOR = "sp.sector";
	    
	    public static final String SP_APLICATION = "sp.aplication";
	    public static final String NODE_URL = "sp.nodeurl";
	    public static final String USE_NODE_URL = "sp.use.node";
	 

	    public static final String COUNTRY_NUMBER = "country.number";

	    public static final String SP_CONF = "SP";
	    public static final String SP_SAMLENGINE_FILE = "SPSamlEngine.xml";
	    public static final String SP_CONFIG_REPOSITORY = "SP_CONFIG_REPOSITORY";
	    public static final String SP_REPO_BEAN_NAME = "spConfigRepository";
	    
	    // public static final String METADATA_REPO_BEAN_NAME = "metadataConfigRepository";
	    //public static final String SSOBROKER_REPO_BEAN_NAME = "ssoBrokerConfigRepository";

	    public static final String SP_METADATA_URL = "sp.metadata.url";
	    public static final String SP_METADATA_ACTIVATE = "sp.metadata.activate";
	    public static final String SP_METADATA_HTTPFETCH = "sp.metadata.httpfetch";
	    public static final String SP_METADATA_REPOPATH = "sp.metadata.repository.path";

	    public static final String SP_METADATA_VALIDATESIGN = "sp.metadata.validatesignature";
	    public static final String SP_METADATA_TRUSTEDDS = "sp.metadata.trusteddescriptors";

	    public static final String SP_EIDAS_ONLY="eidasNodeOnly";

	    public static final String SYSADMIN_RESOURCE_BUNDLE_BASE_NAME="sysadmin";
	    
	    // IDP Constants
	    
	    public static final String SAMLENGINE_NAME="IdP";

	    public static final String IDP_SAMLENGINE_FILE = "IdPSamlEngine.xml";
	    public static final String IDP_CONFIG_REPOSITORY = "IDP_CONFIG_REPOSITORY";
	    public static final String IDP_REPO_BEAN_NAME = "idpConfigRepository";

	    public static final String IDP_PROPERTIES="idp.properties";
	    public static final String IDP_METADATA_URL="idp.metadata.url";
	    public static final String IDP_COUNTRY="idp.country";
	    public static final String IDP_METADATA_HTTPFETCH = "idp.metadata.httpfetch";
	    public static final String IDP_METADATA_REPOPATH = "idp.metadata.repository.path";

	    public static final String IDP_METADATA_VALIDATESIGN = "idp.metadata.validatesignature";
	    public static final String IDP_METADATA_TRUSTEDDS = "idp.metadata.trusteddescriptors";

	    public static final String SSOS_POST_LOCATION_URL = "idp.ssos.post.location";
	    public static final String SSOS_REDIRECT_LOCATION_URL = "idp.ssos.redirect.location";
	    public static final String SEND_NATIONALITY_IN_RESPONSE = "idp.nationality.response";
	    
	    public static final String IDP_KEYSTORE_FILEPATH = "keystore.filepath";
	    public static final String IDP_KEYSTORE_NAME = "keystore.name";
	    public static final String IDP_KEYSTORE_PASSWORD = "keystore.pass";
	    public static final String IDP_KEY_NAME = "key.name";
	    public static final String IDP_KEY_PASS = "key.pass";
	    
	    
}

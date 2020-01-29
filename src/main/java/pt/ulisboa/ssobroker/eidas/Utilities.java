package pt.ulisboa.ssobroker.eidas;


import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.jcabi.xml.XMLDocument;

import eu.eidas.auth.commons.xml.DocumentBuilderFactoryUtil;
import eu.eidas.auth.engine.ProtocolEngineFactory;
import eu.eidas.auth.engine.ProtocolEngineI;
import eu.eidas.auth.engine.configuration.SamlEngineConfigurationException;
import eu.eidas.auth.engine.configuration.dom.ProtocolEngineConfigurationFactory;
import eu.eidas.auth.engine.metadata.MetadataFetcherI;
import eu.eidas.auth.engine.metadata.MetadataSignerI;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import pt.ulisboa.ssobroker.controller.Constants;

public class Utilities {
	
	private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);
	
	static ProtocolEngineConfigurationFactory protocolEngineConfigurationFactory = null;
	static ProtocolEngineFactory defaultProtocolEngineFactory = null;
	
	private Utilities() {
	};
	
	private static Properties loadConfigs(String repoBeanName, String fileName) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(repoBeanName + fileName));
        return properties;
    }

    public static Properties loadSPConfigs() throws ApplicationSpecificException {
        try {
            return Utilities.loadConfigs(getSPConfigFilePath(), EidasConstants.SP_PROPERTIES);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new ApplicationSpecificException("Could not load configuration file", e.getMessage());
        }
    }
    
    public static Properties loadIDPConfigs() throws ApplicationSpecificException {
        try {
            return Utilities.loadConfigs(getIDPConfigFilePath(), EidasConstants.IDP_PROPERTIES);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new ApplicationSpecificException("Could not load configuration file", e.getMessage());
        }
    }
    
    private static String getSPConfigFilePath() {
        return getConfigFilePath(EidasConstants.SP_REPO_BEAN_NAME);
    }
    
    private static String getIDPConfigFilePath() {
        return getConfigFilePath(EidasConstants.IDP_REPO_BEAN_NAME);
    }
    
    private static String getConfigFilePath(String repoBeanName) {
        return (String) ApplicationContextProvider.getApplicationContext().getBean(repoBeanName);
    }
    
    public static synchronized ProtocolEngineI getIdpProtocolEngine() {
        if (defaultProtocolEngineFactory == null) {
            protocolEngineConfigurationFactory = new ProtocolEngineConfigurationFactory(EidasConstants.IDP_SAMLENGINE_FILE, null, Utilities.getIDPConfigFilePath());
            try {
                defaultProtocolEngineFactory = new ProtocolEngineFactory(protocolEngineConfigurationFactory);
            } catch (SamlEngineConfigurationException e) {
                LOG.error("Error creating protocol engine factory : ", e);
            }
        }
        return defaultProtocolEngineFactory.getProtocolEngine(EidasConstants.SAMLENGINE_NAME);
    }
    
    public String getCountry() {
    	return EidasConstants.COUNTRY_SP;
    }
    
    public static Boolean isMetadataFetchable(String spIssuer) {
    	
    	switch(spIssuer) {
		case Constants.ACCESS_MANAGER_ISSUER:
			return true;
		case Constants.ZEROSHELL_ISSUER:
			return false;
		default:
			return true;
		}
    }
    
    @Nullable
    public static String getAssertionConsumerUrlFromMetadata(@Nonnull MetadataFetcherI metadataFetcher,
                                                             @Nonnull MetadataSignerI metadataSigner,
                                                             String spIssuer)
            throws EIDASSAMLEngineException {
        if (StringUtils.isNotBlank(spIssuer)) {
            // This would fetch the metadata only once!
            EntityDescriptor entityDescriptor = metadataFetcher.getEntityDescriptor(spIssuer, metadataSigner);
            SPSSODescriptor spSsoDescriptor = getSPSSODescriptor(entityDescriptor);
            return getAssertionConsumerUrl(spSsoDescriptor);
        }
        return null;
    }
    
    @Nullable
    public static SPSSODescriptor getSPSSODescriptor(@Nonnull EntityDescriptor entityDescriptor) {
        return getFirstRoleDescriptor(entityDescriptor, SPSSODescriptor.class);
    }
    
    @Nullable
    private static <T extends RoleDescriptor> T getFirstRoleDescriptor(@Nonnull EntityDescriptor entityDescriptor,
                                                                       @Nonnull Class<T> clazz) {
        for (RoleDescriptor rd : entityDescriptor.getRoleDescriptors()) {
            if (clazz.isInstance(rd)) {
                return (T) rd;
            }
        }
        return null;
    }
    
    @Nullable
    public static String getAssertionConsumerUrl(@Nullable SPSSODescriptor spSsoDescriptor) {
        if (spSsoDescriptor == null || spSsoDescriptor.getAssertionConsumerServices().isEmpty()) {
            return null;
        }
        for (AssertionConsumerService acs : spSsoDescriptor.getAssertionConsumerServices()) {
            if (acs.isDefault()) {
                return acs.getLocation();
            }
        }
        return spSsoDescriptor.getAssertionConsumerServices().get(0).getLocation();
    }
    
    public static Boolean isMetadataFetchable() {
    	return true;
    }
    
    
    public static String redirectToErrorPage( String errorString) {
    	return "redirect:/ErrorPage" + "?errorString=" +  errorString;
    }
    
    //Printing Functions 
    
    public static void printByteStream(ByteArrayInputStream stream) {
    	int c;
        String composite ="";
        for(int y = 0 ; y < 1; y++) {
            while(( c = stream.read())!= -1) {
               composite += Character.toUpperCase((char)c);
            }
            stream.reset(); 
         }
        System.out.println(composite);
    }
    
    public static void printBreakLine() {
    	System.out.println("\n");
    }
    
    public static void printRequestParameters(HttpServletRequest request) {
    	Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()){
			 String paramName = params.nextElement();
			 System.out.println("Parameter Name - "+paramName+", Value - "+ request.getParameter(paramName));
		}
    }
    
    public static void printSessionParameters(HttpSession session) {
    	Enumeration<String> params = session.getAttributeNames();
		while(params.hasMoreElements()){
			 String paramName = params.nextElement();
			 System.out.println("Parameter Name - "+paramName+", Value - "+ session.getAttribute(paramName));
		}
    }
    
    public static String sessionParametersToString(HttpSession session) {
    	Enumeration<String> params = session.getAttributeNames();
    	String str = "";
		while(params.hasMoreElements()){
			 String paramName = params.nextElement();
			 str += "Parameter Name - "+paramName+", Value - "+ session.getAttribute(paramName) + "\n";
		}
		return str;
    }
    
    public static void printDocument(Document doc) {
    	System.out.println(Utilities.documentToString(doc));
    }
    
    public static String documentToString(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	}
    
    public static String elementToString(Element element) {
		return new XMLDocument(element).toString();
	}
    
    public static void printMap(Map<String, String> map) {
    	for(String key : map.keySet()) {
    		System.out.println(key + " = " + map.get(key));
    	}
    	
    }
    
    public static void printAssertion(Assertion assertion) {
    	  
    	  System.out.println("Attributes:");
    	  if (assertion.getAttributeStatements().isEmpty()) {
    	    System.out.println("  No attribute statement available in assertion");
    	  }
    	  else {
    	    AttributeStatement as = assertion.getAttributeStatements().get(0);
    	    for (Attribute attr : as.getAttributes()) {
    	      System.out.println("  " + attr.getName());        
    	    }
    	  }    	  
    	}
    
    /**
     * @return true when the metadata support should be active
     */
    public static boolean isMetadataEnabled() {
        Properties properties = Utilities.loadSPConfigs();
        return properties.getProperty(EidasConstants.SP_METADATA_ACTIVATE) == null || Boolean.parseBoolean(
                properties.getProperty(EidasConstants.SP_METADATA_ACTIVATE));
    }

}

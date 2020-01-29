package pt.ulisboa.ulea.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import pt.ulisboa.ssobroker.eidas.Utilities;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.eidas.auth.commons.EidasStringUtil;

public class SAMLUtilities {
	
	private static final Logger logger = LoggerFactory.getLogger(SAMLUtilities.class);
	
	public static XMLObject unmarshallSamlToXMLObject (String encodedSamlString) throws UnmarshallingException, SAXException, IOException, ParserConfigurationException, ConfigurationException{
		byte[] samlToken = EidasStringUtil.decodeBytesFromBase64(encodedSamlString);

        ByteArrayInputStream stream = new ByteArrayInputStream(samlToken);        
        
        DefaultBootstrap.bootstrap();
        //Creation of a document to write the SAMLRequest to
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document samlDocument = docBuilder.parse(stream);
        
        Element samlElem = samlDocument.getDocumentElement();
        
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(samlElem);
       
        //XMLObject requestXmlObj = unmarshaller.unmarshall(samlElem);
        //AuthnRequest authRequest = (AuthnRequest) requestXmlObj;

        return unmarshaller.unmarshall(samlElem);
	}
	
	public static String createRandomID() {
	    return "_" + UUID.randomUUID().toString();
	 }
	
	public static void printXMLObject(XMLObject xmlObject) throws MarshallingException{
		//Element targetElement = XMLObjectHelper.marshall(xmlObject);
		//String printable = Utilities.elementToString(targetElement);
		System.out.println(getStringFromXMLObject(xmlObject));
	}
	
	public static String getStringFromXMLObject(XMLObject xmlObject) throws MarshallingException{
		Element targetElement = XMLObjectHelper.marshall(xmlObject);
		//String stringXML = Utilities.elementToString(targetElement); 
		//String stringXML = SerializeSupport.nodeToString(targetElement);
		String stringXML = SerializeSupport.prettyPrintXML(targetElement);
		return stringXML;
	}
	
	
	public static AuthnRequest decodeSamlRequest(String encodedSAMLRequest) throws UnmarshallingException, SAXException, IOException, ParserConfigurationException, ConfigurationException {
		return (AuthnRequest) unmarshallSamlToXMLObject(encodedSAMLRequest);
	}
	
	public static Response decodeSamlResponse(String encodedSAMLResponse) throws UnmarshallingException, SAXException, IOException, ParserConfigurationException, ConfigurationException {
		return (Response) unmarshallSamlToXMLObject(encodedSAMLResponse);
		
	}
	
}

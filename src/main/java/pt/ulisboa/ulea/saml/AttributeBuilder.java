package pt.ulisboa.ulea.saml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.opensaml.Configuration;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;

import pt.ulisboa.ssobroker.controller.Constants;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;

public class AttributeBuilder {

	private static Properties idpProperties = Utilities.loadIDPConfigs();
	
	private static Attribute buildAttribute(String name, String value) {
		Attribute attribute = BuildSAMLResponse.buildSAMLObject(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
		
		//XMLObjectBuilder stringBuilder = getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
		XSStringBuilder stringBuilder = (XSStringBuilder) Configuration.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        XSString attrValue = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attrValue.setValue(value);

        //XSStringBuilder stringBuilder = (XSStringBuilder) Configuration.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        
		attribute.setName(name);
		attribute.setNameFormat(Attribute.URI_REFERENCE);
		attribute.getAttributeValues().add(attrValue);
		
		return attribute;
	}
	
	public static HashMap<String, Attribute> createEidasResponseAttributeMap(Map<String, String> eidasAttributeMap){
		HashMap<String, Attribute> responseAttributeMap = new HashMap<String, Attribute>();
		Attribute samlAttribute = null;
		
		String firstName = eidasAttributeMap.get(EidasConstants.FIRST_NAME);
		String familyName = eidasAttributeMap.get(EidasConstants.FAMILY_NAME);
		
		Attribute fullNameAttribute = createFullNameAttribute(firstName, familyName);
		responseAttributeMap.put(EidasConstants.FULL_NAME, fullNameAttribute);
		
		for(String attributeName : eidasAttributeMap.keySet()) {
			switch (attributeName) {
            case EidasConstants.DATE_OF_BIRTH:
            	samlAttribute = createDateAttribute(eidasAttributeMap.get(attributeName));
                break;
            case EidasConstants.PERSON_IDENTIFIER:
            	// Example PersonIdentifier "ES/PT/12345678"
            	
            	String nationalityBoolean = idpProperties == null ? null : idpProperties.getProperty(EidasConstants.SEND_NATIONALITY_IN_RESPONSE);
            	Boolean nationality = new Boolean(nationalityBoolean);
            	if(nationality) {
            		String countryCode = eidasAttributeMap.get(attributeName).split("/")[0];
            		Attribute countryCodeAttribtue = buildAttribute(EidasConstants.COUNTRY_CODE, countryCode);
            		responseAttributeMap.put(EidasConstants.COUNTRY_CODE, countryCodeAttribtue);
            	}
            	samlAttribute = buildAttribute(attributeName, eidasAttributeMap.get(attributeName));
            	break;
            default:
            	samlAttribute = buildAttribute(attributeName, eidasAttributeMap.get(attributeName));
			}
			responseAttributeMap.put(attributeName, samlAttribute);
		}
		
		return responseAttributeMap;
	}
	
	public static HashMap<String, Attribute> createPortugueseResponseAttributeMap(Map<String, String> eidasAttributeMap){
		HashMap<String, Attribute> responseAttributeMap = new HashMap<String, Attribute>();
		Attribute samlAttribute = createIdNumberAttribute(eidasAttributeMap);
		
		responseAttributeMap.put(EidasConstants.ID_NUMBER, samlAttribute);
		return responseAttributeMap;
	}
	
	public static Map<String, String> createEidasAttributeMap(HttpSession session){
		HashMap<String, String> eidasAttributeMap = new HashMap<String, String>();
		ArrayList<String> attributeNameList = (ArrayList<String>) session.getAttribute(Constants.ATTRIBUTE_LIST);
		
		for(String attributeName : attributeNameList) {
			eidasAttributeMap.put(attributeName, (String) session.getAttribute(attributeName));
		}
		return eidasAttributeMap;
	}
	
	public static HashMap<String, Attribute> createTestAttributeMap(HttpSession session){
		HashMap<String, Attribute> testAttributeMap = new HashMap<String, Attribute>();
		HashMap<String, String> personalIdentifierMap = new HashMap<String, String>();
		
		personalIdentifierMap.put(EidasConstants.PERSON_IDENTIFIER, "PT/PT/14542135");
		Attribute id = createIdNumberAttribute(personalIdentifierMap);
		
		testAttributeMap.put(EidasConstants.PERSON_IDENTIFIER, id);
		
		return testAttributeMap;
	}
	
	private static Attribute createIdNumberAttribute(Map<String, String> eidasAttributeMap) {
		String idString = null;
		
		if(eidasAttributeMap.containsKey(EidasConstants.ID_NUMBER)) {
    		idString = eidasAttributeMap.get(EidasConstants.ID_NUMBER);
    	}else{
    		idString = eidasAttributeMap.get(EidasConstants.PERSON_IDENTIFIER).split("/")[2];
    	}
		Attribute samlAttribute = buildAttribute(EidasConstants.ID_NUMBER, idString);
		return samlAttribute;
		
	}
	
	private static Attribute createFullNameAttribute(String firstName, String familyName) {
		String fullName = firstName + " " + familyName;
		Attribute samlAttribute = buildAttribute(EidasConstants.FULL_NAME, fullName);
		
		return samlAttribute;
		
	}
	
	private static Attribute createDateAttribute(String dateString) {
		Attribute dateAttribute;
		String dateResponse = dateString.replace("-", "");
		
		dateAttribute = buildAttribute(Constants.DATE_OF_BIRTH, dateResponse);
		
		return dateAttribute;
	}
}

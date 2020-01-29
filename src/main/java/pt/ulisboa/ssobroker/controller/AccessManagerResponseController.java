package pt.ulisboa.ssobroker.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.Subject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.signature.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eidas.auth.commons.EidasStringUtil;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;
import pt.ulisboa.ulea.saml.AttributeBuilder;
import pt.ulisboa.ulea.saml.BuildSAMLResponse;
import pt.ulisboa.ulea.saml.SAMLConstants;
import pt.ulisboa.ulea.saml.SAMLUtilities;

@Controller
@RequestMapping(Constants.ACCESS_MANAGER_RESPONSE_CONTROLLER)
public class AccessManagerResponseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);	
	
	@RequestMapping(method = RequestMethod.POST)
	public String sendAccessManagerResponse(
			final Model model,
			final HttpServletResponse httpServletResponse, 
			final HttpServletRequest request) throws EIDASSAMLEngineException, MarshallingException, SignatureException{
		
		// Obtain the parameters from the session necessary to create the SAML Response
		final HttpSession session = request.getSession();
		final Properties idpProperties = Utilities.loadIDPConfigs();
		DateTime authenticationInstant = new DateTime();
		ArrayList<String> attributeList = (ArrayList<String>) session.getAttribute(Constants.ATTRIBUTE_LIST);
		String spassertionIssuer = (String) session.getAttribute(SAMLConstants.SP_ISSUER);
		String relayState = (String) session.getAttribute(SAMLConstants.RELAY_STATE);
		
		// Builds assertionIssuer Element (IDP)
		String entityID = idpProperties == null ? Constants.FALLBACK_IDP_ISSUER : idpProperties.getProperty(EidasConstants.IDP_METADATA_URL);
		Issuer assertionIssuer = BuildSAMLResponse.buildIssuer(entityID);
		
		// Build Subject
		String personalIdentifier = (String) session.getAttribute(EidasConstants.PERSON_IDENTIFIER);
		
		String test = idpProperties.getProperty(Constants.TESTING_ENVIRONENT);
		Boolean testBoolean = new Boolean(test);
		if( personalIdentifier == null && testBoolean == false) {
			return Utilities.redirectToErrorPage("Personal Identifier is Missing");
		}

		String assertionConsumerServiceURL = (String) session.getAttribute(SAMLConstants.SAML_ASSERTION_CONSUMER_SERVICE_URL);
		String inResponseTo = (String) session.getAttribute(SAMLConstants.SAML_IN_RESPONSE_TO);
		Subject subject = BuildSAMLResponse.buildSubject(personalIdentifier, spassertionIssuer, assertionConsumerServiceURL, inResponseTo, personalIdentifier);
		
		// Build Conditions
		Conditions conditions = BuildSAMLResponse.buildConditions(spassertionIssuer);
		
		// Build Authentication Statement sub-Assertion
		AuthnStatement authnStatement = BuildSAMLResponse.buildAuthnStatement(authenticationInstant);

		// Build Attribute Statement sub-Assertion
		// Build Attribute List Retrieved from eIDAS
		String countryCode = (String) session.getAttribute(SAMLConstants.COUNTRY_CODE);
		
		// Map with the attributes returned from eIDAS
		HashMap<String, Attribute> responseMap = new HashMap<String,Attribute>();
		if(!testBoolean) {
			HashMap<String, String> eidasMap = (HashMap<String, String>) AttributeBuilder.createEidasAttributeMap(session);
			if(eidasMap == null) {
				return Utilities.redirectToErrorPage(SAMLConstants.ATTRIBUTE_MISSING);
			}
			responseMap = new HashMap<String, Attribute>();
			if(countryCode.equals(EidasConstants.COUNTRY_SP)) {
				responseMap = (HashMap<String, Attribute>) AttributeBuilder.createPortugueseResponseAttributeMap(eidasMap);
			}else {
				responseMap = (HashMap<String, Attribute>) AttributeBuilder.createEidasResponseAttributeMap(eidasMap);
			}
		}else {
			responseMap = AttributeBuilder.createTestAttributeMap(session);
		}
		
		//TODO ZEROSHELL TO EIDAS ATTRIBUTES
		
		//AttributeStatement attributeStatement = BuildSAMLResponse.buildAttributeStatement(responseMap);
		AttributeStatement attributeStatement = BuildSAMLResponse.buildAttributeStatement(responseMap);
		
		// Build Assertion
		String assertionID = SAMLUtilities.createRandomID();
		Assertion assertion = BuildSAMLResponse.buildAssertion(assertionIssuer, subject, conditions, authnStatement, attributeStatement, authenticationInstant, assertionID);
		
		// Sign Assertion
		BuildSAMLResponse.signXMLObject(assertion);
		
		// Build Status true = success
		Status status = BuildSAMLResponse.buildStatus(true);
		
		// Must create another Issuer because ResponseIssuer and AsssertionIssuer cannot be the same Object
		Issuer responseIssuer = BuildSAMLResponse.buildIssuer(entityID);
		
		// Build Response
		String responseID = SAMLUtilities.createRandomID();
		Response response = BuildSAMLResponse.buildResponse(assertion, status, responseIssuer, responseID, inResponseTo, assertionConsumerServiceURL, authenticationInstant);
		
		// Sign Response
		BuildSAMLResponse.signXMLObject(response);
		//BuildSAMLResponse.signXMLObject(response);
		
		//Utilities.printBreakLine();
		//SAMLUtilities.printXMLObject(response);
		String samlResponseString = SAMLUtilities.getStringFromXMLObject(response);
		
		//String x = samlResponseString.replace("<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">", "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#/\">");
		//String y = x.replace("</ds:Transform>", "");
		//String z = y.replace("<ec:InclusiveNamespaces PrefixList=\"xs\" xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>", "");
		
		final String samlEncodedResponse = EidasStringUtil.encodeToBase64(samlResponseString);
		
		//model.addAttribute("printable", SAMLUtilities.getStringFromXMLObject(signedResponse));
		model.addAttribute(SAMLConstants.SAML_ASSERTION_CONSUMER_SERVICE_URL, assertionConsumerServiceURL);
		model.addAttribute("printable", samlResponseString);
		model.addAttribute(SAMLConstants.SAML_RESPONSE, samlEncodedResponse);
		model.addAttribute(SAMLConstants.RELAY_STATE, relayState);
		//Response authResponse = BuildSAMLResponse.buildResponse(spassertionIssuer);
		//Response authResponse = SAMLUtilities.buildSAMLObject(Response.class, Response.DEFAULT_ELEMENT_NAME);
		
		//AuthenticationResponse responser = responseAuthReq.build();
		session.invalidate();
		return "sendAccessManagerResponse";
	}
}

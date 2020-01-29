package pt.ulisboa.ssobroker.controller;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.exceptions.Base64DecodingException;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.SAXException;

import eu.eidas.auth.commons.EidasStringUtil;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;
import pt.ulisboa.ulea.saml.SAMLConstants;
import pt.ulisboa.ulea.saml.SAMLUtilities;

@Controller
@RequestMapping("/RequestReceiver")
public class RequestReceiverController {
	
	//private static final Logger logger = LoggerFactory.getLogger(RequestReceiverController.class);
	//This class receives the requests from Zeroshell/Access Manager 
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestReceiverController.class);
	
	@RequestMapping(method = RequestMethod.POST)
    private String receiveRequest(final Model model, final HttpServletRequest request, final HttpServletResponse response)
    	throws  UnmarshallingException, ParserConfigurationException,
    			Base64DecodingException, SAXException, IOException, ConfigurationException, ServletException {
				
		HttpSession session = request.getSession();
		
		final String encodedSAMLRequest = request.getParameter(SAMLConstants.SAML_REQUEST);
		final String relayState = request.getParameter(SAMLConstants.RELAY_STATE);
		final String countryCode = (String) session.getAttribute(EidasConstants.COUNTRY_CODE);
		
		if(encodedSAMLRequest == null || relayState == null) {
			return Utilities.redirectToErrorPage(SAMLConstants.SAML_REQUEST_SENT_ERROR);
		}
		
		// Set Session attributes to be used in Response
		AuthnRequest authRequest = SAMLUtilities.decodeSamlRequest(encodedSAMLRequest);
		session.setAttribute(SAMLConstants.SAML_IN_RESPONSE_TO, authRequest.getID());
		session.setAttribute(SAMLConstants.RELAY_STATE, relayState);
		// Set model attributes to be sent to Class EidasSendRequest to send an eIDAS Request
		String samlToken = EidasStringUtil.decodeStringFromBase64(encodedSAMLRequest);
		model.addAttribute(SAMLConstants.SAML_REQUEST, samlToken);
		model.addAttribute(EidasConstants.COUNTRY_CODE, countryCode);
		
		//TODO validation of request (signature and certificates) 

		String issuer = authRequest.getIssuer().getValue();
		session.setAttribute(SAMLConstants.SP_ISSUER, issuer);
		
		// Obtains the URL to send the Response to
		try{
			String assertionConsumerServiceURL = authRequest.getAssertionConsumerServiceURL();
			session.setAttribute(SAMLConstants.SAML_ASSERTION_CONSUMER_SERVICE_URL, assertionConsumerServiceURL);
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Utilities.redirectToErrorPage(SAMLConstants.SAML_ACS_ERROR);
		}
		/*Zeroshell Code it doesn't matter
		if(issuer.equals(Constants.ZEROSHELL_ISSUER)) {
			String zeroshellDefaultACS = idpProperties.getProperty(Constants.ZEROSHELL_ACS);
			session.setAttribute(SAMLConstants.SAML_ASSERTION_CONSUMER_SERVICE_URL, zeroshellDefaultACS);
			return "RequestReceiver";
		}else {
			try{
				// Obtains the URL to send the Response to
				String assertionConsumerServiceURL = authRequest.getAssertionConsumerServiceURL();
				session.setAttribute(SAMLConstants.SAML_ASSERTION_CONSUMER_SERVICE_URL, assertionConsumerServiceURL);
			}catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return Utilities.redirectToErrorPage(SAMLConstants.SAML_ACS_ERROR);
			}
		}*/
		
		// TESTING MECHANISM
		final Properties idpProperties = Utilities.loadIDPConfigs();
		String test = idpProperties.getProperty(Constants.TESTING_ENVIRONENT);
		Boolean testBoolean = new Boolean(test);
		if(!testBoolean) {
			return "RequestReceiverAccessManager";
		}else {
			return "testing";
		}		
	}
	
}

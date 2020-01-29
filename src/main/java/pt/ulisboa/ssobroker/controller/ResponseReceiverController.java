package pt.ulisboa.ssobroker.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.bcel.classfile.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.ImmutableSet;

import eu.eidas.auth.commons.EidasStringUtil;
import eu.eidas.auth.commons.attribute.AttributeDefinition;
import eu.eidas.auth.commons.attribute.ImmutableAttributeMap;
import eu.eidas.auth.commons.protocol.IAuthenticationResponse;
import eu.eidas.sp.SpProtocolEngineFactory;
import eu.eidas.sp.SpProtocolEngineI;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;
import pt.ulisboa.ulea.saml.SAMLConstants;
import pt.ulisboa.ulea.saml.SAMLUtilities;

@Controller
@RequestMapping("/EidasReturnPage")
public class ResponseReceiverController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseReceiverController.class);

	@RequestMapping(method = RequestMethod.POST)
    private String receiveResponse(final Model model, final HttpServletRequest request, final HttpServletResponse response) {
		
		final HttpSession session = request.getSession();
		String encodedSAMLResponse = request.getParameter(SAMLConstants.SAML_RESPONSE);
		String spInResponseTo = (String) session.getAttribute(SAMLConstants.SAML_IN_RESPONSE_TO);
		String spIssuer = (String) session.getAttribute(SAMLConstants.SP_ISSUER);
		
		// Check if message and session is correct
		if(encodedSAMLResponse == null) {
			return Utilities.redirectToErrorPage(SAMLConstants.SAML_RESPONSE_NULL);
		}
		
		if( spInResponseTo == null ||	spIssuer == null){
			return Utilities.redirectToErrorPage(Constants.SESSION_NULL);
		}
		// Chooses Class to redirect depending on Request Issuer
		switch(spIssuer) {
			case Constants.ACCESS_MANAGER_ISSUER:
				model.addAttribute(Constants.RESPONSE_CONTROLLER_URL, Constants.APPLICATION_PATH + Constants.ACCESS_MANAGER_RESPONSE_CONTROLLER);
			break;
			case Constants.ACCESS_MANAGER_PRODUCTION_ISSUER:
				model.addAttribute(Constants.RESPONSE_CONTROLLER_URL, Constants.APPLICATION_PATH + Constants.ACCESS_MANAGER_RESPONSE_CONTROLLER);
			break;
			case Constants.ZEROSHELL_ISSUER:
				model.addAttribute(Constants.RESPONSE_CONTROLLER_URL, Constants.APPLICATION_PATH + Constants.ZEROSHELL_RESPONSE_CONTROLLER);
			break;
			default:
				return Utilities.redirectToErrorPage(Constants.SP_ISSUER_ERROR);
				//model.addAttribute(Constants.RESPONSE_CONTROLLER_URL, Constants.APPLICATION_PATH + Constants.ACCESS_MANAGER_RESPONSE_CONTROLLER);
		}
		
		byte[] decodedResponse = EidasStringUtil.decodeBytesFromBase64(encodedSAMLResponse);
	
		IAuthenticationResponse authnResponse;
		Properties configs = Utilities.loadSPConfigs();
		
		String metadataUrl = configs.getProperty(EidasConstants.SP_METADATA_URL);

		try {
			SpProtocolEngineI engine = SpProtocolEngineFactory.getSpProtocolEngine(EidasConstants.SP_CONF);
            //validates SAML Response and Assertions. Decrypts them.
            authnResponse = engine.unmarshallResponseAndValidate(decodedResponse, request.getRemoteHost(), 0, 0, metadataUrl, null, false);
            
            if (authnResponse.isFailure()) {
               return Utilities.redirectToErrorPage(authnResponse.getStatusMessage());
            }
            
            /*byte[] eidasTokenSAML = engine.checkAndDecryptResponse(decodedResponse);
            final String samlUnencryptedResponseXML = EidasStringUtil.toString(eidasTokenSAML);
            Utilities.printBreakLine();
    		Utilities.printBreakLine();
    		System.out.println("====================================== \n" + samlUnencryptedResponseXML + "\n====================================== \n");
    		Utilities.printBreakLine();
    		Utilities.printBreakLine();*/
            
            ImmutableAttributeMap responseMap = authnResponse.getAttributes();
            final ImmutableSet<AttributeDefinition<?>> attributesInResponseMap = responseMap.getDefinitions();
            
            ArrayList<String> attributeList = new ArrayList<String>();
            // Gets all attributes and puts them in the session
            for(AttributeDefinition<?> attributeDefinition : attributesInResponseMap) {
            	
                String attributeName = attributeDefinition.getFriendlyName();
                attributeList.add(attributeName);
                String attributeValue = responseMap.getFirstAttributeValue(attributeDefinition).toString();
                
                session.setAttribute(attributeName, attributeValue);
            }
            session.setAttribute(Constants.ATTRIBUTE_LIST, attributeList);
            
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Utilities.redirectToErrorPage(SAMLConstants.SAML_RESPONSE_VALIDATION_ERROR);
		}
	
		return "responseReceiverEidas";
	}
	
}

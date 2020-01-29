package pt.ulisboa.ssobroker.controller;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.eidas.auth.commons.EidasStringUtil;
import eu.eidas.auth.commons.attribute.AttributeDefinition;
import eu.eidas.auth.commons.attribute.ImmutableAttributeMap;
import eu.eidas.auth.commons.protocol.IRequestMessage;
import eu.eidas.auth.commons.protocol.eidas.LevelOfAssurance;
import eu.eidas.auth.commons.protocol.eidas.LevelOfAssuranceComparison;
import eu.eidas.auth.commons.protocol.eidas.impl.EidasAuthenticationRequest;
import eu.eidas.auth.commons.protocol.impl.EidasSamlBinding;
import eu.eidas.auth.engine.ProtocolEngineI;
import eu.eidas.auth.engine.xml.opensaml.SAMLEngineUtils;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import eu.eidas.sp.ApplicationSpecificServiceException;
import eu.eidas.sp.SPUtil;
import eu.eidas.sp.SpProtocolEngineFactory;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;
import pt.ulisboa.ulea.saml.SAMLConstants;

@Controller
@RequestMapping("/EidasSendRequest")
public class EidasSendRequestController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EidasSendRequestController.class);
	
	@RequestMapping(method = RequestMethod.POST)
	private String eidasSendRequest(String countryCode, Model model, final HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		final Properties spConfigs = Utilities.loadSPConfigs();
		final ProtocolEngineI protocolEngine = SpProtocolEngineFactory.getSpProtocolEngine(EidasConstants.SP_CONF);
		final String providerName = spConfigs.getProperty(EidasConstants.PROVIDER_NAME);

		Stream<AttributeDefinition<?>> stream = protocolEngine.getProtocolProcessor().getAllSupportedAttributes()
				.stream();
		
		final List<AttributeDefinition<?>> reqAttrList;
		reqAttrList = stream
				.filter(attr -> EidasConstants.RequestedAttributes.contains(attr.getFriendlyName()))
				.collect(Collectors.toList());

		/*
		if(reqType.equals(Constants.REQUEST_TYPE_PT)) {
			reqAttrList = stream
					.filter(attr -> EidasConstants.RequestedAttributesPT.contains(attr.getFriendlyName()))
					.collect(Collectors.toList());
		}else {
			reqAttrList = stream
					.filter(attr -> EidasConstants.MinimumDataSet.contains(attr.getFriendlyName()))
					.collect(Collectors.toList());
		}
		*/
		final ImmutableAttributeMap reqAttrMap = new ImmutableAttributeMap.Builder().putAll(reqAttrList).build();

		// build the request
		final EidasAuthenticationRequest.Builder reqBuilder = new EidasAuthenticationRequest.Builder();
		final String nodeUrl = spConfigs.getProperty(spConfigs.getProperty(EidasConstants.USE_NODE_URL));
		//final String nodeUrl = spConfigs.getProperty(EidasConstants.NODE_URL);

		reqBuilder.destination(nodeUrl);
		reqBuilder.providerName(providerName);

		reqBuilder.requestedAttributes(reqAttrMap);
		reqBuilder.levelOfAssurance(LevelOfAssurance.LOW.stringValue());
		reqBuilder.levelOfAssuranceComparison(LevelOfAssuranceComparison.MINIMUM);

		final String nameIdIdentifier = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
		reqBuilder.nameIdFormat(nameIdIdentifier);
		reqBuilder.binding(EidasSamlBinding.EMPTY.getName());

		String metadataUrl = spConfigs.getProperty(EidasConstants.SP_METADATA_URL);
		if (metadataUrl != null && !metadataUrl.isEmpty() && SPUtil.isMetadataEnabled()) {
			reqBuilder.issuer(metadataUrl);
		}
		
		//TODO is this necessary
		reqBuilder.citizenCountryCode(countryCode);
		try{
			reqBuilder.originCountryCode(EidasConstants.COUNTRY_SP);
			reqBuilder.serviceProviderCountryCode(EidasConstants.COUNTRY_SP);
			reqBuilder.originalIssuer(session.getAttribute(SAMLConstants.SP_ISSUER).toString());
		}catch (Exception e){
			LOGGER.error(e.getMessage(), e);
		}
		
		IRequestMessage binaryRequestMessage;

		try {
			reqBuilder.id(SAMLEngineUtils.generateNCName());
			final String nodeMetadataUrl = "https://eidas.ulisboa.pt//EidasNode/ConnectorResponderMetadata";
			binaryRequestMessage = protocolEngine.generateRequestMessage(reqBuilder.build(), nodeMetadataUrl);
		} catch (EIDASSAMLEngineException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("", e);
			final String errorMessage = e.getErrorMessage();
			throw new ApplicationSpecificServiceException("Could not generate token for Saml Request", errorMessage);
		}

		final byte[] token = binaryRequestMessage.getMessageBytes();
		final String samlRequest = EidasStringUtil.encodeToBase64(token);		
		
		//Citizen country code is the country of origin of the citizen (Service Node eIDAS)
        //nodeUrl is the url of the Portuguese Node (Connector Node eIDAS)
		model.addAttribute("citizenCountryCode", countryCode);
		model.addAttribute(SAMLConstants.SAML_REQUEST, samlRequest);
		model.addAttribute("nodeUrl", nodeUrl);
		
		return "eidasSendRequest";
	}
}

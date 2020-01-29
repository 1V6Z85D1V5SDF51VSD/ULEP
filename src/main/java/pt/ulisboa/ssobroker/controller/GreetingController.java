package pt.ulisboa.ssobroker.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

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

@Controller
public class GreetingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);
	
    @Autowired
    private ApplicationContext ctx;
	    
	@GetMapping({ "/", "/start"})
	public String start(final Model model, final HttpSession httpSession) {
		//String issuer = StringUtils.substringBetween(requestTest, "<saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">", "</saml:Issuer>");
        return "start";
	}
}
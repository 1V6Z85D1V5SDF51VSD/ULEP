package pt.ulisboa.ssobroker.controller;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;
import pt.ulisboa.ulea.saml.SAMLConstants;

@Controller
@RequestMapping("/SetCountryCode")
public class SetCountryCodeController {
		
	@RequestMapping(method = RequestMethod.GET)
	public String setCountryCode(
			@RequestParam(name="CountryCode", required=true) String countryCode,
			@RequestParam(name="sid", required=false) String sid,
			@RequestParam(name="id", required=true) String id,
			Model model, final HttpSession session){
		
		// http://10.110.58.100:8080/ULEP/SetCountryCode?id=IDP_EIDAS&sid=4&CountryCode=PT
		// https://eidas.ulisboa.pt/ULEP/SetCountryCode?id=IDP_ULEP&CountryCode=PT&sid=1561
			
		String cC = countryCode.toUpperCase();
		session.setAttribute(SAMLConstants.COUNTRY_CODE, cC);
		
		session.setAttribute("sid", sid);
		model.addAttribute("sid", sid);
		model.addAttribute("id", id);
		
		final Properties idpProperties = Utilities.loadIDPConfigs();
    	String prodEnvBoolean = idpProperties.getProperty(Constants.ACCESS_MANAGER_PRODUCTION_ENVIRONMENT);
		Boolean prodEnv = new Boolean(prodEnvBoolean);
    	
		//Default targetUrl
		String targetUrl = "https://amis-dev.ulisboa.pt/nidp/saml2/spsend";
		
		if(prodEnv) {
			targetUrl = idpProperties.getProperty(Constants.ACCESS_MANAGER_PRODUCTION_SPSEND);
		}else {
			targetUrl = idpProperties.getProperty(Constants.ACCESS_MANAGER_DEV_SPSEND);
		}
		model.addAttribute("targetUrl", targetUrl);
		return "setCountryCode";
	}
}

package pt.ulisboa.ssobroker.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ulisboa.ssobroker.eidas.Utilities;

@Controller
@RequestMapping(Constants.ZEROSHELL_RESPONSE_CONTROLLER)
public class sendZeroshellResponse {

	private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);
		
	@RequestMapping(method = RequestMethod.POST)
	public String sendAccessManagerResponse(
			final Model model,
			final HttpServletResponse response, 
			final HttpServletRequest request){
			
		//System.out.println(attrMap.toString());
		final HttpSession session = request.getSession();
		Utilities.printSessionParameters(session);

			
		return "sendZeroshellResponse";
	}	
}

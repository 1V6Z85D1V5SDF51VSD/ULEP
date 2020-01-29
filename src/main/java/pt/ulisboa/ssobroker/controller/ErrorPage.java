package pt.ulisboa.ssobroker.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ErrorPage")
public class ErrorPage {
	
	@RequestMapping(method = RequestMethod.GET)
    private String errorPage(@RequestParam(name = Constants.ERROR_STRING) final String errorString, 
    		final Model model, final HttpServletRequest request, final HttpServletResponse response) {
		
		model.addAttribute(Constants.ERROR_STRING, errorString);
		return "errorPage";
	}
}

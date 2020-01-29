package pt.ulisboa.ssobroker.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

// Imports aplicationContext.xml in resource folder
// Necessary for bean creation

@Configuration
@ImportResource({"classpath*:applicationContext.xml"})
public class XmlConfiguration {
	
	
}
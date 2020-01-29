package eu.eidas.sp.metadata;
//package pt.ulisboa.ssobroker;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.opensymphony.xwork2.Action;
//import com.opensymphony.xwork2.ActionSupport;
//
//import org.apache.struts2.interceptor.ServletRequestAware;
//import org.apache.struts2.interceptor.ServletResponseAware;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class GenerateMetadata extends ActionSupport implements ServletRequestAware, ServletResponseAware{
//	
//	static final Logger logger = LoggerFactory.getLogger(GenerateMetadata.class.getName());
//	public static final String METADATA_URL = "/metadata";
//	private static final long serialVersionUID = -6481654351681354613L;
//	
//	private transient InputStream dataStream;
//	
//    private static final String INVALID_METADATA = "invalid metadata";
//    private static final String ERROR_GENERATING_METADATA = "error generating metadata {}";
//    Properties configs;
//    
//    
//    
//	@Override
//	public void setServletRequest(HttpServletRequest request) {
//	}
//
//	@Override
//	public void setServletResponse(HttpServletResponse response) {
//	}
//
//	public InputStream getInputStream(){return dataStream;}
//	public void setInputStream(InputStream inputStream){dataStream=inputStream;}
//	
//}

package pt.ulisboa.ulea.saml;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.SignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;

import eu.eidas.auth.engine.ProtocolEngineI;
import eu.eidas.engine.exceptions.EIDASSAMLEngineException;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;


public class BuildSAMLResponse {
	
	private static ProtocolEngineI idpEngine;
	private static final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
	private static Properties idpProperties = Utilities.loadIDPConfigs();
	
	@SuppressWarnings({"unused", "unchecked"})
	public static <T> T buildSAMLObject(final Class<T> objectClass, QName qName) {
	  return (T) builderFactory.getBuilder(qName).buildObject(qName);
	}	
	
	/* Builds Response
	 * 
	 */
	public static Response buildResponse(Assertion assertion, Status status, Issuer issuer, String responseID, String inResponseTo,
										String assertionConsumerServiceURL, DateTime authenticationInstant) {
		
		Response response = buildSAMLObject(Response.class, Response.DEFAULT_ELEMENT_NAME);;
		//Set Params
		response.setIssueInstant(authenticationInstant);
		response.setDestination(assertionConsumerServiceURL);
		response.setID(responseID);
		response.setInResponseTo(inResponseTo);
		// Set Elements
		response.setIssuer(issuer);
		response.setStatus(status);
		response.getAssertions().add(assertion);
		/*
		NamespaceManager namespaceManager = response.getNamespaceManager();
		LazySet<Namespace> namespaceSet = (LazySet<Namespace>) namespaceManager.getAllNamespacesInSubtreeScope();
		String s;
		for (Namespace namespace : namespaceSet) {
			s = namespace.toString();
		    System.out.println(s);
		}
		*/
		return response;
	}
	
	/* Builds Assertion
	 */
	public static Assertion buildAssertion(Issuer issuer, Subject subject, Conditions conditions, AuthnStatement authnStatement,
										   DateTime authenticationInstant, String assertionID) {
		Assertion assertion = buildSAMLObject(Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
		
		assertion.setID(assertionID);
		assertion.setIssueInstant(authenticationInstant);
		assertion.setIssuer(issuer);
		//The same random ID must be used in the signature
		assertion.setSubject(subject);
		assertion.setConditions(conditions);
		assertion.getAuthnStatements().add(authnStatement);
		
		return assertion;
	}
	
	public static Assertion buildAssertion(Issuer issuer, Subject subject, Conditions conditions, AuthnStatement authnStatement,
										  AttributeStatement attributeStatement, DateTime authenticationInstant, String assertionID) {
		Assertion assertion = buildSAMLObject(Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);

		assertion.setID(assertionID);
		assertion.setIssueInstant(authenticationInstant);
		assertion.setIssuer(issuer);
		//The same random ID must be used in the signature
		assertion.setSubject(subject);
		assertion.setConditions(conditions);
		assertion.getAuthnStatements().add(authnStatement);
		assertion.getAttributeStatements().add(attributeStatement);

		return assertion;
	}
	
	
	/* Builds Authentication Statement
	 * <saml:AuthnStatement AuthnInstant="2019-07-18T13:08:24Z" SessionIndex="_64d76b34cbdc1c7b42ad3b1dd5688420f856e409bf" SessionNotOnOrAfter="2019-07-18T21:08:24Z">
            <saml:AuthnContext>
                <saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</saml:AuthnContextClassRef>
            </saml:AuthnContext>
        </saml:AuthnStatement>
	 */
	
	public static AuthnStatement buildAuthnStatement(DateTime authnInstant) {
	
		AuthnContextClassRef authnContextClassRef = buildSAMLObject(AuthnContextClassRef.class, AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
		authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);
	
		//AuthenticatingAuthority authenticatingAuthority = buildSAMLObject(AuthenticatingAuthority.class, AuthenticatingAuthority.DEFAULT_ELEMENT_NAME);
		//authenticatingAuthority.setURI(entityID);

		AuthnContext authnContext = buildSAMLObject(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
		authnContext.setAuthnContextClassRef(authnContextClassRef);
		//authnContext.getAuthenticatingAuthorities().add(authenticatingAuthority);

		AuthnStatement authnStatement = buildSAMLObject(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
		authnStatement.setAuthnContext(authnContext);

		authnStatement.setAuthnInstant(authnInstant);
		//authnStatement.setSessionNotOnOrAfter(authnInstant.plusHours(8));

		return authnStatement;
	}
	
	/* Builds Authentication Statement
	 * <saml:AttributeStatement>
            <saml:Attribute 
                Name="ULBI" 
                NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
                <saml:AttributeValue 
                    xsi:type="xs:string">12345678
                </saml:AttributeValue>
            </saml:Attribute>
        </saml:AttributeStatement>
	 */
	
	public static AttributeStatement buildAttributeStatement() {
	    AttributeStatement attributeStatement = buildSAMLObject(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
	    
	    Attribute attribute = buildAttribute("ULBI", "14542135");
	    attributeStatement.getAttributes().add(attribute);
	    
	    return attributeStatement;
	}
	
	public static AttributeStatement buildAttributeStatement(Map<String, Attribute> responseAttributeMap) {
	    AttributeStatement attributeStatement = buildSAMLObject(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
	    
	    for(String attributeName : responseAttributeMap.keySet()) {
	    	attributeStatement.getAttributes().add(responseAttributeMap.get(attributeName));
		}
	    
	    return attributeStatement;
	}
	
	/* Builds one Attribute with:
	 *  Name 
	 * 	NameFormat
	 *  Value
	 */
	
	private static Attribute buildAttribute(String name, String value) {
		Attribute attribute = buildSAMLObject(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
		
		//XMLObjectBuilder stringBuilder = getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
		XSStringBuilder stringBuilder = (XSStringBuilder) Configuration.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        XSString attrValue = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attrValue.setValue(value);

        //XSStringBuilder stringBuilder = (XSStringBuilder) Configuration.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        
		attribute.setName(name);
		attribute.setNameFormat(Attribute.URI_REFERENCE);
		attribute.getAttributeValues().add(attrValue);
		
		return attribute;
	}
	
	/* Builds the Conditions with the Issuer of the Request stored in the session as spIssuer
	 * 
	 * <saml2:Conditions NotBefore="xx" NotOnOrAfter="yy">
     *   <saml2:AudienceRestriction>
     *     <saml2:Audience>spIssuer</saml2:Audience>
     *   </saml2:AudienceRestriction>
     * </saml2:Conditions>  
     */
	
	public static Conditions buildConditions(String spIssuer) {
		AudienceRestriction audienceRestriction = buildAudience(spIssuer);
			
	    Conditions conditions = buildSAMLObject(Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
		//ConditionsBuilder conditionsBuilder = (ConditionsBuilder) builderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
	    //Conditions conditions = (Conditions) conditionsBuilder.buildObject();
	    conditions.setNotBefore(new DateTime().minusMinutes(5));
	    conditions.setNotOnOrAfter(new DateTime().plusMinutes(5));
	    conditions.getAudienceRestrictions().add(audienceRestriction);
	    return conditions;
	}
	
	private static AudienceRestriction buildAudience(String spIssuer) {
		Audience audience = buildSAMLObject(Audience.class, Audience.DEFAULT_ELEMENT_NAME);
	    audience.setAudienceURI(spIssuer);
	    AudienceRestriction audienceRestriction = buildSAMLObject(AudienceRestriction.class, AudienceRestriction.DEFAULT_ELEMENT_NAME);
	    audienceRestriction.getAudiences().add(audience);
		return audienceRestriction;
	}
	
	/* Build the Status Code:
	 * <saml2p:Status>
	 * 	<saml2p:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success"/>
	 * </saml2p:Status>
	*/
	public static Status buildStatus(boolean Success) {
		Status status = buildSAMLObject(Status.class, Status.DEFAULT_ELEMENT_NAME);
	    StatusCode statusCode = buildSAMLObject(StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
	    //statusCode.setValue(value);
	    if(Success) {
	    	statusCode.setValue(StatusCode.SUCCESS_URI);
	    }else {
	    	statusCode.setValue(StatusCode.AUTHN_FAILED_URI);
	    }
	    status.setStatusCode(statusCode);
	    return status;
	}
	
	/* Build the Issuer:
	 * <saml:Issuer>http://10.110.58.100:8080/ULEP/IdPmetadata</saml:Issuer> 
	 * */
	public static Issuer buildIssuer(String issuingEntityName) {
	    Issuer issuer = buildSAMLObject(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
	    issuer.setValue(issuingEntityName);
	    //issuer.setFormat(NameIDType.ENTITY);
	    return issuer;
	  }
	
	/* Build the Subject: 
	 * <saml:Subject>
            <saml:NameID 
                SPNameQualifier="https://id.ulisboa.pt/nidp/saml2/metadata" 
                Format="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent">fc46571@alunos.fc.ul.pt
            </saml:NameID>
            <saml:SubjectConfirmation 
                Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
                <saml:SubjectConfirmationData 
                    NotOnOrAfter="2019-07-18T13:19:45Z" 
                    Recipient="https://id.ulisboa.pt/nidp/saml2/spassertion_consumer" 
                    InResponseTo=<personalIdentifier>/>
            </saml:SubjectConfirmation>
        </saml:Subject>
	 * */
	
	public static Subject buildSubject(String subjectNameID, String SPNameQualifier, String recipient, String inResponseTo, String personalIdentifier) {
		
		NameID nameID = buildNameID(subjectNameID, SPNameQualifier, personalIdentifier);
		SubjectConfirmation subjectConfirmation = buildSubjectConfirmation(recipient, inResponseTo);
	    
	    Subject subject = buildSAMLObject(Subject.class, Subject.DEFAULT_ELEMENT_NAME);
	    subject.setNameID(nameID);
	    subject.getSubjectConfirmations().add(subjectConfirmation);
	    
		return subject;
	}
	
	private static NameID buildNameID(String subjectNameID, String SPNameQualifier, String personalIdentifier) {
		NameID nameID = buildSAMLObject(NameID.class, NameID.DEFAULT_ELEMENT_NAME);
	    nameID.setValue(subjectNameID);
	    nameID.setSPNameQualifier(SPNameQualifier);
	    nameID.setFormat(NameID.PERSISTENT);
		nameID.setValue(personalIdentifier);
		
	    return nameID;
	}
	
	private static SubjectConfirmation buildSubjectConfirmation(String recipient, String inResponseTo) {
		
		SubjectConfirmation subjectConfirmation = buildSAMLObject(SubjectConfirmation.class, SubjectConfirmation.DEFAULT_ELEMENT_NAME);
	    subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
		
	    SubjectConfirmationData subjectConfirmationData = buildSubjectConfirmationData(recipient, inResponseTo);
	    subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
	    
	    return subjectConfirmation;
	}
	
	private static SubjectConfirmationData buildSubjectConfirmationData(String recipient, String inResponseTo) {
		
		SubjectConfirmationData subjectConfirmationData = buildSAMLObject(SubjectConfirmationData.class, SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
	    subjectConfirmationData.setRecipient(recipient);
	    subjectConfirmationData.setInResponseTo(inResponseTo);
	    subjectConfirmationData.setNotOnOrAfter(new DateTime().plusMinutes(60));
	    
	    return subjectConfirmationData;
	}
	
	// Signing Method
	public static void signXMLObject(SignableXMLObject signableXMLObject) throws EIDASSAMLEngineException, MarshallingException, SignatureException {
		try {
		
		URL keystoreURL = BuildSAMLResponse.class.getResource(
				idpProperties.getProperty(EidasConstants.IDP_KEYSTORE_FILEPATH) + idpProperties.getProperty(EidasConstants.IDP_KEYSTORE_NAME));
		
		InputStream keystoreStream = keystoreURL.openStream();
		
		byte[] keyStoreContents = IOUtils.toByteArray(keystoreURL);

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		
		keyStore.load(keystoreStream, idpProperties.getProperty(EidasConstants.IDP_KEYSTORE_PASSWORD).toCharArray());
		keystoreStream.close();
		
		Map<String, String> passwordMap = new HashMap<>();
		passwordMap.put(idpProperties.getProperty(EidasConstants.IDP_KEY_NAME), idpProperties.getProperty(EidasConstants.IDP_KEY_PASS));
		

		PublicKey publicKey = keyStore.getCertificate(idpProperties.getProperty(EidasConstants.IDP_KEY_NAME)).getPublicKey();
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(
				idpProperties.getProperty(EidasConstants.IDP_KEY_NAME), idpProperties.getProperty(EidasConstants.IDP_KEY_PASS).toCharArray());
		BasicX509Credential signingCredential = new BasicX509Credential();
		signingCredential.setPublicKey(publicKey);
		signingCredential.setPrivateKey(privateKey);
		signingCredential.setEntityId(idpProperties.getProperty(EidasConstants.IDP_METADATA_URL));
		
		Signature signature = buildSAMLObject(Signature.class, Signature.DEFAULT_ELEMENT_NAME);

	    signature.setSigningCredential(signingCredential);
	    signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
	    //signature.setSignatureAlgorithm(Configuration.getGlobalSecurityConfiguration().getSignatureAlgorithmURI(signingCredential));
	    signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
	    //signature.setKeyInfo(newKeyInfo);
	   
	    //KeyInfo keyInfo = createKeyInfo(signingCredential);
	    //signature.setKeyInfo(keyInfo);

	    signableXMLObject.setSignature(signature);
	    
	    Set<Namespace> namespacer = signableXMLObject.getNamespaceManager().getAllNamespacesInSubtreeScope();
	    
	    for (Namespace ns : namespacer)  {
	    	signableXMLObject.getNamespaceManager().registerNamespaceDeclaration(ns);
	    }
	    
	    Configuration.getMarshallerFactory().getMarshaller(signableXMLObject).marshall(signableXMLObject);
	    Signer.signObject(signature);
	    
		} catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
			throw new RuntimeException(e);
		}
		
		/*idpEngine = getSamlEngineInstance();
		idpEngine.getSigner().sign(signableXMLObject);
        ((MetadataSignerI) idpEngine.getSigner()).signMetadata(signableXMLObject);*/
		       
		//return signableXMLObject;
		
	}
	
	private static KeyInfo createKeyInfo(BasicX509Credential signingCredential) throws CertificateEncodingException {
		KeyInfo keyInfo = (KeyInfo) buildSAMLObject(KeyInfo.class , KeyInfo.DEFAULT_ELEMENT_NAME);
        X509Data data = (X509Data) buildSAMLObject(X509Data.class, X509Data.DEFAULT_ELEMENT_NAME);
        X509Certificate cert = 
        		(X509Certificate) buildSAMLObject( X509Certificate.class, X509Certificate.DEFAULT_ELEMENT_NAME);
        
        PrivateKey privateKey = signingCredential.getPrivateKey();
        signingCredential.getEntityCertificate();
        String value = 
                org.apache.xml.security.utils.Base64.encode(signingCredential.getEntityCertificate().getEncoded());
        System.out.println(value);
        
        cert.setValue(value);
        data.getX509Certificates().add(cert);
        keyInfo.getX509Datas().add(data);
        
		return keyInfo;
		
	}
	
	private static ProtocolEngineI getSamlEngineInstance() throws EIDASSAMLEngineException {
        return Utilities.getIdpProtocolEngine();
    }
	
}

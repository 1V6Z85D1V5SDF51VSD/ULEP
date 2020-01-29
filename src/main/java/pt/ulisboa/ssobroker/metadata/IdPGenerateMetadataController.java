package pt.ulisboa.ssobroker.metadata;

import java.util.Properties;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.xml.security.credential.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.eidas.auth.commons.EIDASValues;
import eu.eidas.auth.commons.EidasErrorKey;
import eu.eidas.auth.commons.exceptions.EIDASServiceException;
import eu.eidas.auth.engine.configuration.dom.EncryptionKey;
import eu.eidas.auth.engine.configuration.dom.SignatureKey;
import eu.eidas.auth.engine.metadata.EidasMetadata;
import eu.eidas.auth.engine.metadata.MetadataConfigParams;
import eu.eidas.auth.engine.metadata.MetadataUtil;
import pt.ulisboa.ssobroker.controller.Constants;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;


@Controller
public class IdPGenerateMetadataController {
	
	static final Logger logger = LoggerFactory.getLogger(IdPGenerateMetadataController.class.getName());
	
	public static final String IDP_METADATA_URL = "/IdPmetadata";
	public static final String INVALID_METADATA = "invalid metadata";
	public static final String ERROR_GENERATING_METADATA = "error generating metadata {}";
	Properties configs;
	
	@RequestMapping(value= IDP_METADATA_URL, produces="application/xml;charset=UTF-8")
    @ResponseBody
    public byte[] generateMetadata(final Model model) {
		configs = Utilities.loadIDPConfigs();
        String metadata = INVALID_METADATA;
		
        try {
        	
            EidasMetadata.Generator generator = EidasMetadata.generator();
            MetadataConfigParams.Builder mcp = MetadataConfigParams.builder();
            mcp.idpEngine(Utilities.getIdpProtocolEngine());
            mcp.entityID(configs.getProperty(EidasConstants.IDP_METADATA_URL));
            putSSOSBindingLocation(mcp, SAMLConstants.SAML2_REDIRECT_BINDING_URI, EidasConstants.SSOS_REDIRECT_LOCATION_URL);
            putSSOSBindingLocation(mcp, SAMLConstants.SAML2_POST_BINDING_URI, EidasConstants.SSOS_POST_LOCATION_URL);
            mcp.technicalContact(MetadataUtil.createTechnicalContact(configs));
            mcp.supportContact(MetadataUtil.createSupportContact(configs));
            mcp.organization(MetadataUtil.createOrganization(configs));
            mcp.signingMethods(configs == null ? null : configs.getProperty(SignatureKey.SIGNATURE_ALGORITHM_WHITE_LIST.getKey()));
            mcp.digestMethods(configs == null ? null : configs.getProperty(SignatureKey.SIGNATURE_ALGORITHM_WHITE_LIST.getKey()));
            mcp.encryptionAlgorithms(configs == null ? null : configs.getProperty(EncryptionKey.ENCRYPTION_ALGORITHM_WHITE_LIST.getKey()));
            mcp.eidasProtocolVersion(configs == null ? null : configs.getProperty(EIDASValues.EIDAS_PROTOCOL_VERSION.toString()));
            mcp.eidasApplicationIdentifier(configs == null ? null : configs.getProperty(EIDASValues.EIDAS_APPLICATION_IDENTIFIER.toString()));
            MetadataConfigParams metadataConfigParams = mcp.build();
            //generator.configParams(mcp.build());
            generator.configParams(metadataConfigParams);
            
            //Credential credential = metadataConfigParams.getIdpSigningCredential();
            //System.out.println(credential.getEntityId());
            //System.out.println(credential.toString());
            
            metadata = generator.build().getMetadata();
            
            return metadata.getBytes("UTF-8");
        } catch (Exception see) {
        	logger.error(ERROR_GENERATING_METADATA, see);
            throw new RuntimeException(see);
        }
    }
	
	private void putSSOSBindingLocation(MetadataConfigParams.Builder mcp, final String binding, final String locationKey){
        if (isValidSSOSBindingLocation(configs.getProperty(locationKey))) {
            mcp.addProtocolBindingLocation(binding, configs.getProperty(locationKey));
        } else {
            String msg = String.format("BUSINESS EXCEPTION : Missing property %3$s for binding %1$s at %2$s",
            		binding,
            		configs.getProperty(EidasConstants.IDP_METADATA_URL),
            		locationKey);
            logger.error(msg);
            throwSAMLEngineNoMetadataException();
        }
    }
	
	private boolean isValidSSOSBindingLocation(final String location) {
        return location != null;
    }
	
	private void throwSAMLEngineNoMetadataException() {
        final String exErrorCode = configs.getProperty(EidasErrorKey.SAML_ENGINE_NO_METADATA.errorCode());
        final String exErrorMessage = configs.getProperty(EidasErrorKey.SAML_ENGINE_NO_METADATA.errorMessage());
        throw new EIDASServiceException(exErrorCode, exErrorMessage);
    }
}

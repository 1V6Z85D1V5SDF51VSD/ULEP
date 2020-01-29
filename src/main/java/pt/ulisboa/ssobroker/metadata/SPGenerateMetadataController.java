package pt.ulisboa.ssobroker.metadata;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eidas.auth.commons.EIDASValues;
import eu.eidas.auth.engine.configuration.dom.EncryptionKey;
import eu.eidas.auth.engine.configuration.dom.SignatureKey;
import eu.eidas.auth.engine.metadata.EidasMetadata;
import eu.eidas.auth.engine.metadata.MetadataConfigParams;
import eu.eidas.auth.engine.metadata.MetadataUtil;
import eu.eidas.sp.SpProtocolEngineFactory;
import pt.ulisboa.ssobroker.controller.Constants;
import pt.ulisboa.ssobroker.eidas.EidasConstants;
import pt.ulisboa.ssobroker.eidas.Utilities;

@Controller
public class SPGenerateMetadataController {

	static final Logger logger = LoggerFactory.getLogger(SPGenerateMetadataController.class.getName());
	
    public static final String SP_METADATA_URL = "/SPmetadata";
	public static final String INVALID_METADATA = "invalid metadata";
	public static final String ERROR_GENERATING_METADATA = "error generating metadata {}";
    
	@RequestMapping(value= SP_METADATA_URL, produces="application/xml;charset=UTF-8")
    @ResponseBody
    public byte[] generateMetadata(final Model model) {
        try {
            Properties configs = Utilities.loadSPConfigs();

            String metadata = INVALID_METADATA;

            if (Utilities.isMetadataEnabled()) {
                EidasMetadata.Generator generator = EidasMetadata.generator();
                MetadataConfigParams.Builder mcp = MetadataConfigParams.builder();
                mcp.spEngine(SpProtocolEngineFactory.getSpProtocolEngine(EidasConstants.SP_CONF));
                mcp.entityID(configs.getProperty(EidasConstants.SP_METADATA_URL));
                String returnUrl = configs.getProperty(EidasConstants.SP_RETURN);
                mcp.assertionConsumerUrl(returnUrl);
                mcp.technicalContact(MetadataUtil.createTechnicalContact(configs));
                mcp.supportContact(MetadataUtil.createSupportContact(configs));
                mcp.organization(MetadataUtil.createOrganization(configs));
                mcp.signingMethods(
                        configs == null ? null : configs.getProperty(SignatureKey.SIGNATURE_ALGORITHM_WHITE_LIST.getKey()));
                mcp.digestMethods(
                        configs == null ? null : configs.getProperty(SignatureKey.SIGNATURE_ALGORITHM_WHITE_LIST.getKey()));
                mcp.encryptionAlgorithms(
                        configs == null ? null : configs.getProperty(EncryptionKey.ENCRYPTION_ALGORITHM_WHITE_LIST.getKey()));
                String spType = configs.getProperty(EidasConstants.SP_TYPE, null);
                mcp.spType(StringUtils.isBlank(spType) ? null : spType);
                mcp.eidasProtocolVersion(
                        configs == null ? null : configs.getProperty(EIDASValues.EIDAS_PROTOCOL_VERSION.toString()));
                mcp.eidasApplicationIdentifier(
                        configs == null ? null : configs.getProperty(EIDASValues.EIDAS_APPLICATION_IDENTIFIER.toString()));
                generator.configParams(mcp.build());
                metadata = generator.build().getMetadata();
            }

            return metadata.getBytes("UTF-8");
        } catch (Exception see) {
        	logger.error(ERROR_GENERATING_METADATA, see);
            throw new RuntimeException(see);
        }
    }

}
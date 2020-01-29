package pt.ulisboa.ssobroker.metadata;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eidas.auth.engine.metadata.IStaticMetadataChangeListener;
import eu.eidas.auth.engine.metadata.impl.CachingMetadataFetcher;
import eu.eidas.auth.engine.metadata.impl.FileMetadataLoader;
import eu.eidas.idp.IDPUtil;
import eu.eidas.idp.metadata.IDPCachingMetadataFetcher;
import eu.eidas.idp.metadata.IDPMetadataCache;

public class IdPCachingMetadataFetcher extends CachingMetadataFetcher implements IStaticMetadataChangeListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(IDPCachingMetadataFetcher.class);

	public IdPCachingMetadataFetcher(Boolean isMetadataFetchable) {
		super();
		setCache(new IDPMetadataCache());
		if (isMetadataFetchable == false) {
			FileMetadataLoader fp = new FileMetadataLoader();
			fp.setRepositoryPath(IDPUtil.getMetadataRepositoryPath());
			setMetadataLoaderPlugin(fp);
		}
		initProcessor();
	}

	@Override
	public boolean isHttpRetrievalEnabled() {
		return IDPUtil.isMetadataHttpFetchEnabled();
	}

	@Override
	protected boolean mustUseHttps() {
		return false;
	}

	@Override
	protected boolean mustValidateSignature(@Nonnull String url) {
		setTrustedEntityDescriptors(IDPUtil.getTrustedEntityDescriptors());
		return super.mustValidateSignature(url);
	}


}

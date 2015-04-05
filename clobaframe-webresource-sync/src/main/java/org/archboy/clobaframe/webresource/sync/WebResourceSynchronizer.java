package org.archboy.clobaframe.webresource.sync;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoFactory;
import org.archboy.clobaframe.blobstore.BlobResourceRepository;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.blobstore.PartialCollection;
import org.archboy.clobaframe.blobstore.impl.DefaultBlobResourceInfoFactory;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;

/**
 *
 * @author yang
 */
@Named
public class WebResourceSynchronizer{

	private final Logger logger = LoggerFactory.getLogger(WebResourceSynchronizer.class);

	@Inject
	private WebResourceManager webResourceManager;
	
	@Inject
	@Named("defaultBlobstore")
	private Blobstore blobstore;

	private BlobResourceInfoFactory blobResourceInfoFactory = new DefaultBlobResourceInfoFactory();

	@Value("${clobaframe.webresource.sync.repositoryName}")
	private String repositoryName;

	@Value("${clobaframe.webresource.sync.autoCreateRepository}")
	private boolean autoCreateRepository;

	@Value("${clobaframe.webresource.sync.update}")
	private boolean enableUpdate;

	@Value("${clobaframe.webresource.sync.deleteNoneExists}")
	private boolean deleteNoneExists;

	@PostConstruct
	public void init(){
		if (!enableUpdate) {
			return;
		}
		
	}
	
	/**
	 * Update the remote web resources repository with local web resources.
	 *
	 * @param webResourceInfos
	 */
	public void update(WebResourceManager webResourceManager, Collection<WebResourceInfo> webResourceInfos) throws IOException{

		// check repo first
		if (!blobstore.exist(repositoryName)) {
			if (autoCreateRepository){
				blobstore.create(repositoryName);

				logger.info("Create blob repository [{}] for storing web resources.",
					repositoryName);
			}else{
				throw new FileNotFoundException(String.format("Can not find the blob repository [{}].", repositoryName));
			}
		}

		BlobResourceRepository repository = blobstore.getRepository(repositoryName);
		
		// find all web resources that exists in the blobstore
		Map<String, BlobResourceInfo> remoteResources = new HashMap<String, BlobResourceInfo>();

		// build a map for remote resource, use the resource name as map key
		for (BlobResourceInfo blobResourceInfo : getRemoteBlobResourceInfos(repository)){
			String key = blobResourceInfo.getKey();
			remoteResources.put(key, blobResourceInfo);
		}

		// build a map for local resource, use the version name as map key
		Map<String, WebResourceInfo> localResources = new HashMap<String, WebResourceInfo>();
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			String key = webResourceManager.getVersionName(webResourceInfo);
			localResources.put(key, webResourceInfo);
		}

		// find the already update-to-date resources
		List<String> unmodifiedKeys = new ArrayList<String>();
		for (String key : localResources.keySet()) {
			if (remoteResources.containsKey(key)) {
				// compare the resources with hash
				unmodifiedKeys.add(key);
			}
		}

		// remove the keys of unmodified resources
		// to prevent updated.
		for (String key : unmodifiedKeys) {
			localResources.remove(key);
			remoteResources.remove(key);
		}

		// delete none exists resource
		if (deleteNoneExists) {
			for (BlobResourceInfo blobResourceInfo : remoteResources.values()){
				repository.delete(blobResourceInfo.getKey());
			}
		}

		// upload none-exists resources
		for(Map.Entry<String, WebResourceInfo> entry : localResources.entrySet()){
			upload(repository, entry.getKey(), entry.getValue());
		}
	}

	private List<BlobResourceInfo> getRemoteBlobResourceInfos(BlobResourceRepository repository) {
		List<BlobResourceInfo> blobResourceInfos = new ArrayList<BlobResourceInfo>();

		while(true) {
			PartialCollection<BlobResourceInfo> partialBlobResourceInfos = repository.list();
			blobResourceInfos.addAll(partialBlobResourceInfos);
			if (!partialBlobResourceInfos.hasMore()) {
				break;
			}
		}

		return blobResourceInfos;
	}

	private void upload(
			BlobResourceRepository repository, 
			String key,
			WebResourceInfo webResourceInfo) throws IOException {

		logger.info("Sync web resource [{}] to blob repository [{}] with key [{}].", new Object[]{
				repositoryName,
				repository.getName(),
				key});

		//ResourceContent resourceContent = webResourceInfo.getContentSnapshot();
		InputStream in = webResourceInfo.getContent();
		BlobResourceInfo blobResourceInfo = blobResourceInfoFactory.make(
				repositoryName,
				key,
				in,
				webResourceInfo.getContentLength(),
				webResourceInfo.getMimeType(),
				webResourceInfo.getLastModified(), null);

		try{
			repository.put(blobResourceInfo, true, Blobstore.PRIOTITY_DEFAULT);
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
}

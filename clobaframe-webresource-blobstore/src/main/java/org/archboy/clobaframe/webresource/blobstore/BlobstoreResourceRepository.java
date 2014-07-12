package org.archboy.clobaframe.webresource.blobstore;

import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.ResourceLocationGenerator;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.local.LocalWebResourceRepository;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
@Named
public class BlobstoreResourceRepository implements ResourceRepository{

	@Value("${webresource.blobstore.keyNamePrefix}")
	private String keyNamePrefix;

	@Value("${webresource.blobstore.location}")
	private String location;

	@Inject
	@Qualifier("localWebResourceRepository")
	private ResourceRepository localResourceRepository;
	
	private List<WebResourceInfo> webResourceInfos;
	
	private ResourceLocationGenerator locationGenerator;

	@Inject
	private BlobstoreWebResourceSynchronizer resourceSynchronizer;

	private final Logger logger = LoggerFactory.getLogger(BlobstoreResourceRepository.class);
	
	@PostConstruct
	public void init(){
		locationGenerator = new BlobstoreLocationGenerator(keyNamePrefix, location);
		webResourceInfos = localResourceRepository.findAll();
		
		try{
			resourceSynchronizer.update(webResourceInfos);
		}catch(IOException e){
			logger.error("Fail to synchronize the remote web resources.", e);
		}
	}
	
	@Override
	public String getName() {
		return "blobstore";
	}

	@Override
	public ResourceLocationGenerator getResourceLocationGenerator() {
		return locationGenerator;
	}

	@Override
	public List<WebResourceInfo> findAll() {
		return webResourceInfos;
	}

}

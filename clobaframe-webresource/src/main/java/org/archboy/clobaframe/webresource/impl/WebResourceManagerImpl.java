package org.archboy.clobaframe.webresource.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.archboy.clobaframe.webresource.ResourceLocationGenerator;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.UniqueNameGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 *
 */
@Named
public class WebResourceManagerImpl implements WebResourceManager {

	private Map<String, WebResourceInfo> webResources = new HashMap<String, WebResourceInfo>();
	private Map<String, String> uniqueNames = new HashMap<String, String>(); // the unique name to resource mapper.

	@Value("${webresource.strategy}")
	private String strategyName;

	@Inject
	private List<ResourceRepository> resourceRepositories;
	
	// the default resource repository
	private ResourceRepository resourceRepository;

	// the default location generator
	private ResourceLocationGenerator locationGenerator;
	
	// the content types that can be location replaced.
	private List<String> contentTypes = Arrays.asList(
			"text/javascript",
			"text/css",
			"application/x-javascript");

	/**
	 * cache millisecond:
	 * 	less than 0 = cache always.
	 * 	equals 0 = no cache
	 */
	@Value("${webresource.cacheSeconds}")
	private int cacheSeconds;

	@Value("${webresource.autoConvertCssUrl}")
	private boolean autoConvertCssUrl;
	
	// cache the file that less than 1MiB.
	private static final int MAX_CACHE_FILE_SIZE = 1024 * 1024;
	private int maxCacheFileSize = MAX_CACHE_FILE_SIZE;
	
	private final Logger logger = LoggerFactory.getLogger(WebResourceManagerImpl.class);

	@PostConstruct
	public void init(){
		// get the config resource repository
		resourceRepository = getResourceRepository(strategyName);
		locationGenerator = resourceRepository.getResourceLocationGenerator();
		logger.info("Using [{}] web resource repository as the default.", strategyName);
		
		List<WebResourceInfo> webResourceInfos = resourceRepository.findAll();
		if (webResourceInfos.isEmpty()){
			// no web resource present
			return;
		}

		webResourceInfos = postHandle(webResourceInfos);

		// get all resource name and unique names
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			webResources.put(webResourceInfo.getName(), webResourceInfo);
			uniqueNames.put(webResourceInfo.getUniqueName(), webResourceInfo.getName());
		}
	}

	public List<ResourceRepository> getResourceRepositories() {
		return resourceRepositories;
	}

	public ResourceRepository getResourceRepository(String name) {
		Assert.hasText(name);
		
		for(ResourceRepository repository : resourceRepositories){
			if (repository.getName().equals(name)){
				return repository;
			}
		}

		throw new IllegalArgumentException(
				String.format("The specify web resource repository [%s] not found.", name));
	}

	@Override
	public String getLocation(String name) throws FileNotFoundException {
		Assert.hasText(name, "Name should not empty.");
		
		WebResourceInfo webResourceInfo = webResources.get(name);
		if (webResourceInfo == null) {
			throw new FileNotFoundException(name);
		}
		return getLocation(webResourceInfo);
	}

	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		Assert.notNull(webResourceInfo);
		
		return locationGenerator.getLocation(webResourceInfo);
	}

	@Override
	public WebResourceInfo getResource(String name) throws FileNotFoundException {
		Assert.hasText(name, "Resource name should not empty.");
		
		WebResourceInfo info = webResources.get(name);
		if (info == null) {
			throw new FileNotFoundException(name);
		}
		return info;
	}

	@Override
	public WebResourceInfo getResourceByUniqueName(String uniqueName) throws FileNotFoundException {
		Assert.hasText(uniqueName, "Resource unique name should not empty.");
		
		String name = uniqueNames.get(uniqueName);
		if (name == null) {
			throw new FileNotFoundException(name);
		}
		return getResource(name);
	}

	@Override
	public Collection<WebResourceInfo> getAllResources() {
		return webResources.values();
	}
	
	/**
	 * Add buffer and location replacement features.
	 *
	 * @param webResourceInfos
	 * @return
	 */
	protected List<WebResourceInfo> postHandle(List<WebResourceInfo> webResourceInfos){
		webResourceInfos =  handleLocationReplacing(webResourceInfos);
		webResourceInfos = handleBuffer(webResourceInfos);
		return webResourceInfos;
	}

	protected List<WebResourceInfo> handleLocationReplacing(List<WebResourceInfo> webResourceInfos) {
		Map<String, String> locations = new HashMap<String, String>();
		
		// get all resource locations
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			locations.put(
					webResourceInfo.getName(),
					locationGenerator.getLocation(webResourceInfo));
		}

		List<WebResourceInfo> result = new ArrayList<WebResourceInfo>();

		// select web resources that can be location replaced.
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			// convert into location-replacing resource
			if (contentTypes.contains(webResourceInfo.getContentType())) {
				webResourceInfo = new LocationReplacingWebResourceInfo(
						webResourceInfo, locations, autoConvertCssUrl);
			}
			result.add(webResourceInfo);
		}

		return result;
	}
	
	protected List<WebResourceInfo> handleBuffer(List<WebResourceInfo> webResourceInfos) {
		if (cacheSeconds == 0) {
			return webResourceInfos;
		}
		
		List<WebResourceInfo> result = new ArrayList<WebResourceInfo>();
		for (WebResourceInfo webResourceInfo : webResourceInfos) {

			// convert info buffered web resource
			if (webResourceInfo.getContentLength() < maxCacheFileSize){
				webResourceInfo = new BufferedWebResourceInfo(
						webResourceInfo, cacheSeconds);
			}

			result.add(webResourceInfo);
		}

		return result;
	}

}

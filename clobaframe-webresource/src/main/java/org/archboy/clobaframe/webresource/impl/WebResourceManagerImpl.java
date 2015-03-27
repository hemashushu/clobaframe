package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.AbstractVersionStrategy;
import org.archboy.clobaframe.webresource.CacheableResource;
import org.archboy.clobaframe.webresource.CacheableResourceUpdateListener;
import org.archboy.clobaframe.webresource.ConcatenateResourceRepository;
import org.archboy.clobaframe.webresource.ResourceCollection;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 *
 */
@Named
public class WebResourceManagerImpl implements WebResourceManager, CacheableResourceUpdateListener {

	@Value("${clobaframe.webresource.versionStrategy}")
	private String versionStrategyName;
		
	@Inject
	private List<AbstractVersionStrategy> versionStrategys;

	// fields
	
	private VersionStrategy versionStrategy;

	@Inject
	private ResourceCollection resourceCollection;
	
	@Inject
	private ConcatenateResourceRepository concatenateResourceRepository;
	

	
//	// the default resource repository
//	private ResourceRepository resourceRepository;
//
//	// the default location generator
//	private ResourceLocationGenerator locationGenerator;
	
	// the content types that can be compressed and minify.
	private List<String> textWebResourceMimeTypes; // = Arrays.asList(
//		MIME_TYPE_JAVA_SCRIPT, MIME_TYPE_STYLE_SHEET
//	);

	@Value("${clobaframe.webresource.minify}")
	private boolean canMinify;
	
	@Value("${clobaframe.webresource.compress}")
	private boolean canCompress;
		
	@Value("${clobaframe.webresource.cache}")
	private boolean canCache;

	@Value("${clobaframe.webresource.baseLocation}")
	private String baseLocation;
	
//	@Value("${webresource.autoConvertCssUrl}")
//	private boolean autoConvertCssUrl;
//	
//	// cache the file that less than 1MiB.
//	private static final int MAX_CACHE_FILE_SIZE = 1024 * 1024;
//	private int maxCacheFileSize = MAX_CACHE_FILE_SIZE;
	
	private static final int DEFAULT_CACHE_SECONDS = 60;
	private int cacheSeconds = DEFAULT_CACHE_SECONDS;
	
	private final Logger logger = LoggerFactory.getLogger(WebResourceManagerImpl.class);

	@PostConstruct
	public void init(){
		// get the config resource repository
//		resourceRepository = getResourceRepository(strategyName);
//		locationGenerator = resourceRepository.getResourceLocationGenerator();
		
//		List<WebResourceInfo> webResourceInfos = resourceRepository.getAll();
//		if (webResourceInfos.isEmpty()){
//			// no web resource present
//			return;
//		}
//
//		webResourceInfos = postHandle(webResourceInfos);
//
//		// get all resource name and unique names
//		for (WebResourceInfo webResourceInfo : webResourceInfos) {
//			webResources.put(webResourceInfo.getName(), webResourceInfo);
//			uniqueNames.put(webResourceInfo.getUniqueName(), webResourceInfo.getName());
//		}
		
		for(AbstractVersionStrategy strategy : versionStrategys) {
			if (strategy.getName().equals(versionStrategyName)) {
				this.versionStrategy = strategy;
				break;
			}
		}
		
		if (versionStrategy == null) {
			throw new IllegalArgumentException(String.format(
					"Can not find the version strategy [%s]", versionStrategyName));
		}
		
		logger.info("Using [{}] web resource version name strategy.", versionStrategyName);
		
		textWebResourceMimeTypes = new ArrayList<String>();
		textWebResourceMimeTypes.add(MIME_TYPE_STYLE_SHEET);
		textWebResourceMimeTypes.addAll(MIME_TYPE_JAVA_SCRIPT);
		
		//loadFunctionWrapper = new CompositeResourceRepositoryImpl.DefaultWebResourceLoadFunctionWrapper(this);
	}
//
//	public List<ResourceRepository> getResourceRepositories() {
//		return resourceRepositories;
//	}
//
//	public ResourceRepository getResourceRepository(String name) {
//		Assert.hasText(name);
//		
//		for(ResourceRepository repository : resourceRepositories){
//			if (repository.getName().equals(name)){
//				return repository;
//			}
//		}
//
//		throw new IllegalArgumentException(
//				String.format("The specify web resource repository [%s] not found.", name));
//	}
//
//	@Override
//	public String getLocation(String name) throws FileNotFoundException {
//		Assert.hasText(name, "Name should not empty.");
//		
//		WebResourceInfo webResourceInfo = webResources.get(name);
//		if (webResourceInfo == null) {
//			throw new FileNotFoundException(name);
//		}
//		return getLocation(webResourceInfo);
//	}
//
//	@Override
//	public String getLocation(WebResourceInfo webResourceInfo) {
//		Assert.notNull(webResourceInfo);
//		
//		return locationGenerator.getLocation(webResourceInfo);
//	}
//
//	@Override
//	public WebResourceInfo getResource(String name) throws FileNotFoundException {
//		Assert.hasText(name, "Resource name should not empty.");
//		
//		WebResourceInfo info = webResources.get(name);
//		if (info == null) {
//			throw new FileNotFoundException(name);
//		}
//		return info;
//	}
//
//	@Override
//	public WebResourceInfo getResourceByUniqueName(String uniqueName) throws FileNotFoundException {
//		Assert.hasText(uniqueName, "Resource unique name should not empty.");
//		
//		String name = uniqueNames.get(uniqueName);
//		if (name == null) {
//			throw new FileNotFoundException(name);
//		}
//		return getResource(name);
//	}
//
//	@Override
//	public Collection<WebResourceInfo> getAllResources() {
//		return webResources.values();
//	}
//	
//	/**
//	 * Add buffer and location replacement features.
//	 *
//	 * @param webResourceInfos
//	 * @return
//	 */
//	protected List<WebResourceInfo> postHandle(List<WebResourceInfo> webResourceInfos){
//		webResourceInfos =  handleLocationReplacing(webResourceInfos);
//		webResourceInfos = handleBuffer(webResourceInfos);
//		return webResourceInfos;
//	}
//
//	protected List<WebResourceInfo> handleLocationReplacing(List<WebResourceInfo> webResourceInfos) {
//		Map<String, String> locations = new HashMap<String, String>();
//		
//		// get all resource locations
//		for (WebResourceInfo webResourceInfo : webResourceInfos) {
//			locations.put(
//					webResourceInfo.getName(),
//					locationGenerator.getLocation(webResourceInfo));
//		}
//
//		List<WebResourceInfo> result = new ArrayList<WebResourceInfo>();
//
//		// select web resources that can be location replaced.
//		for (WebResourceInfo webResourceInfo : webResourceInfos) {
//			// convert into location-replacing resource
//			if (mimeTypes.contains(webResourceInfo.getMimeType())) {
//				webResourceInfo = new LocationReplacingWebResourceInfo(
//						webResourceInfo, locations, autoConvertCssUrl);
//			}
//			result.add(webResourceInfo);
//		}
//
//		return result;
//	}
//	
//	protected List<WebResourceInfo> handleBuffer(List<WebResourceInfo> webResourceInfos) {
//		if (cacheSeconds == 0) {
//			return webResourceInfos;
//		}
//		
//		List<WebResourceInfo> result = new ArrayList<WebResourceInfo>();
//		for (WebResourceInfo webResourceInfo : webResourceInfos) {
//
//			// convert info buffered web resource
//			if (webResourceInfo.getContentLength() < maxCacheFileSize){
//				webResourceInfo = new BufferedWebResourceInfo(
//						webResourceInfo, cacheSeconds);
//			}
//
//			result.add(webResourceInfo);
//		}
//
//		return result;
//	}

	private WebResourceInfo getResourceInternal(String name) throws IOException {
		
		// load from collection first
		WebResourceInfo resourceInfo = resourceCollection.getByName(name);
		if (resourceInfo != null) {
			return resourceInfo;
		}
		
		// then load from composites and repository
		resourceInfo = concatenateResourceRepository.getByName(name);
		
		if (resourceInfo == null) {
			return null;
		}
		
		// wrap resource
		
		// transform url location
		if (resourceInfo.getMimeType().equals(MIME_TYPE_STYLE_SHEET)) {
			resourceInfo = new LocationTransformWebResourceInfo(this, resourceInfo);
		}
		
		// minify
		if (canMinify && textWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new MinifyWebResourceInfo(resourceInfo);
		}
		
		// compress
		if (canCompress && textWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new CompressWebResourceInfo(resourceInfo);
		}
		
		// cache
		if (canCache) {
			resourceInfo = new CacheWebResourceInfo(resourceInfo, cacheSeconds);
		}
		
		resourceCollection.add(resourceInfo);
		
		return resourceInfo;
	}
	
	@Override
	public WebResourceInfo getResource(String name) throws FileNotFoundException {
		WebResourceInfo resource = null;
		
		try{
			resource = getResourceInternal(name);
		}catch(IOException e){
			throw new FileNotFoundException(String.format(
					"There is error in the web resource [%s], cause: %s", 
					name, 
					e.getMessage()));
		}
		
		if (resource == null) {
			throw new FileNotFoundException(String.format("Can not found the web resource [%s]", name));
		}
		
		return resource;
	}
	
	@Override
	public WebResourceInfo getResourceByVersionName(String versionName) throws FileNotFoundException {
		String name = versionStrategy.revert(versionName);
		return getResource(name);
	}

	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		String versionName = versionStrategy.getVersionName(webResourceInfo);
		return baseLocation + versionName;
	}

	@Override
	public String getLocation(String name) throws FileNotFoundException {
		WebResourceInfo resource = getResource(name);
		return getLocation(resource);
	}

	@Override
	public void refresh(String name) {
		try{
			WebResourceInfo resource = getResourceInternal(name);
			if (resource != null) {
				if (resource instanceof CacheableResource) {
					((CacheableResource)resource).refresh();
				}
			}
		}catch(IOException e) {
			// ignore
		}
	}

	@Override
	public void onUpdate(String resourceName, Collection<String> referenceResourceNames) {
		for(String name : referenceResourceNames) {
			refresh(name);
		}
	}
	
}

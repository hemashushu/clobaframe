package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.AbstractVersionStrategy;
import org.archboy.clobaframe.webresource.CacheableWebResource;
import org.archboy.clobaframe.webresource.CacheableWebResourceUpdateListener;
import org.archboy.clobaframe.webresource.WebResourceRepositorySet;
import org.archboy.clobaframe.webresource.LocationGenerator;
import org.archboy.clobaframe.webresource.WebResourceCache;
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
public class WebResourceManagerImpl implements WebResourceManager {

	@Value("${clobaframe.webresource.versionStrategy}")
	private String versionStrategyName;
		
	@Inject
	private List<AbstractVersionStrategy> versionStrategys;

	// fields
	private VersionStrategy versionStrategy;

	private WebResourceCache webResourceCache;

	private LocationGenerator locationGenerator; 
	
	@Inject
	private WebResourceRepositorySet webResourceRepositorySet;
	
	private List<String> compressibleWebResourceMimeTypes; 
	private List<String> minifyWebResourceMimeTypes; 

	@Value("${clobaframe.webresource.minify}")
	private boolean canMinify;
	
	@Value("${clobaframe.webresource.compress}")
	private boolean canCompress;
		
	@Value("${clobaframe.webresource.serverCache}")
	private boolean canServerCache;

	@Value("${clobaframe.webresource.baseLocation}")
	private String baseLocation;
	
	private static final int DEFAULT_SERVER_CACHE_SECONDS = 10 * 60;
	
	@Value("${clobaframe.webresource.serverCacheSeconds}")
	private int cacheSeconds = DEFAULT_SERVER_CACHE_SECONDS;
	
	private final Logger logger = LoggerFactory.getLogger(WebResourceManagerImpl.class);

	// keep the current post-handling resources name.
	// to prevent infinite loop
	private Stack<String> buildingResourceNames = new Stack<String>();
	
	@PostConstruct
	public void init(){
		
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
		
		locationGenerator = new DefaultLocationGenerator(versionStrategy, baseLocation);
		webResourceCache = new DefaultWebResourceCache();
		
		compressibleWebResourceMimeTypes = new ArrayList<String>();
		compressibleWebResourceMimeTypes.add(MIME_TYPE_STYLE_SHEET);
		compressibleWebResourceMimeTypes.addAll(MIME_TYPE_JAVA_SCRIPT);
		compressibleWebResourceMimeTypes.addAll(MIME_TYPE_TEXT);
		
		// Some types of font file are compressed,
		// excepted the ttf and svg.
		compressibleWebResourceMimeTypes.add("application/x-font-ttf"); // ttf
		compressibleWebResourceMimeTypes.add("image/svg+xml"); // svg
		
		minifyWebResourceMimeTypes = new ArrayList<String>();
		minifyWebResourceMimeTypes.add(MIME_TYPE_STYLE_SHEET);
		minifyWebResourceMimeTypes.addAll(MIME_TYPE_JAVA_SCRIPT);
	}

	/**
	 * 
	 * @param name
	 * @return NULL if the specify resource not found.
	 */
	private WebResourceInfo postHandleResource(String name) {
		
		// load from in-momery cache first
		WebResourceInfo resourceInfo = webResourceCache.getByName(name);
		if (resourceInfo != null) {
			return resourceInfo;
		}
		
		// then load from repository set
		resourceInfo = webResourceRepositorySet.getByName(name);
		
		if (resourceInfo == null) {
			return null;
		}
		
		// post-handle resource
		
		// to prevent infinite loop
		if (!buildingResourceNames.empty() && buildingResourceNames.contains(name)) {
			return null;
		}
		
		buildingResourceNames.push(name);
		
		Collection<String> childResourceNames = null;
		
		// transform url location
		if (resourceInfo.getMimeType().equals(MIME_TYPE_STYLE_SHEET)) {
			resourceInfo = new LocationTransformWebResourceInfo(this, resourceInfo);
			childResourceNames = ((LocationTransformWebResourceInfo)resourceInfo).getChildResourceNames();
		}
		
		// minify
		if (canMinify && minifyWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new MinifyWebResourceInfo(resourceInfo);
		}
		
		// compress
		if (canCompress && compressibleWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new CompressibleWebResourceInfo(resourceInfo);
		}
		
		// server cache
		if (canServerCache) {
			resourceInfo = new CacheableWebResourceInfo(resourceInfo, cacheSeconds);
			
			// insert the update listener into the child resources
			if (childResourceNames != null){
				for(String n : childResourceNames) {
					WebResourceInfo r = postHandleResource(n);
					if (r != null && r instanceof CacheableWebResource) {
						((CacheableWebResource)r).addUpdateListener((CacheableWebResourceUpdateListener)resourceInfo);
					}
				}
			}
		}
		
		// store into in-momery cache
		webResourceCache.add(resourceInfo);
		
		buildingResourceNames.pop();
		
		return resourceInfo;
	}
	
	@Override
	public WebResourceInfo getResource(String name) {
		return postHandleResource(name);
	}

	@Override
	public WebResourceInfo getOriginalResource(String name) {
		return webResourceRepositorySet.getByName(name);
	}

	@Override
	public Collection<String> getAllNames() {
		return webResourceRepositorySet.getAllNames();
	}
	
	@Override
	public WebResourceInfo getResourceByVersionName(String versionName) {
		String name = versionStrategy.revert(versionName);
		return name == null ? null : getResource(name);
	}

	@Override
	public String getVersionName(WebResourceInfo webResourceInfo) {
		return versionStrategy.getVersionName(webResourceInfo);
	}
	
	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		return locationGenerator.getLocation(webResourceInfo);
	}

	@Override
	public String getLocation(String name) {
		WebResourceInfo resource = getResource(name);
		return resource == null ? null : getLocation(resource);
	}

	@Override
	public void refresh(String name) {
		WebResourceInfo resource = postHandleResource(name);
		if (resource != null) {
			if (resource instanceof CacheableWebResource) {
				((CacheableWebResource)resource).refresh();
			}
		}
	}
	
	@Override
	public void setLocationGenerator(LocationGenerator locationGenerator) {
		this.locationGenerator = locationGenerator;
	}

	@Override
	public void setWebResourceCache(WebResourceCache webResourceCache) {
		this.webResourceCache = webResourceCache;
	}

	@Override
	public void setVersionStrategy(VersionStrategy versionStrategy) {
		this.versionStrategy = versionStrategy;
	}
	
}

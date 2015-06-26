package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.CacheableWebResourceInfo;
import org.archboy.clobaframe.webresource.CacheableWebResourceInfoUpdateListener;
import org.archboy.clobaframe.webresource.WebResourceProviderSet;
import org.archboy.clobaframe.webresource.LocationStrategy;
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

	private static final String DEFAULT_LOCATION_STRATEGY = "default";
	private static final boolean DEFAULT_CAN_MINIFY = false;
	private static final boolean DEFAULT_CAN_COMPRESS = true;
	private static final boolean DEFAULT_CAN_SERVER_CACHE = true;
	private static final int DEFAULT_SERVER_CACHE_SECONDS = 10 * 60;

	
	private WebResourceCache resourceCache;

	@Value("${clobaframe.webresource.locationStrategy:" + DEFAULT_LOCATION_STRATEGY + "}")
	private String locationStrategyName;
	
	@Inject
	private List<LocationStrategy> locationStrategys;
	
	private LocationStrategy locationStrategy; 
	
	@Inject
	private WebResourceProviderSet webResourceProviderSet;
	
	private List<String> compressibleWebResourceMimeTypes; 
	private List<String> minifyWebResourceMimeTypes; 
	
	@Value("${clobaframe.webresource.minify:" + DEFAULT_CAN_MINIFY + "}")
	private boolean canMinify;
	
	@Value("${clobaframe.webresource.compress:" + DEFAULT_CAN_COMPRESS + "}")
	private boolean canCompress;
	
	@Value("${clobaframe.webresource.serverCache:" + DEFAULT_CAN_SERVER_CACHE + "}")
	private boolean canServerCache;
	
	@Value("${clobaframe.webresource.serverCacheSeconds:" + DEFAULT_SERVER_CACHE_SECONDS + "}")
	private int cacheSeconds;
	
	private final Logger logger = LoggerFactory.getLogger(WebResourceManagerImpl.class);

	// keep the current post-handling resources name.
	// to prevent infinite loop
	private Stack<String> buildingResourceNames = new Stack<String>();
	
	@PostConstruct
	public void init(){
		
		for(LocationStrategy strategy : locationStrategys) {
			if (strategy.getName().equals(locationStrategyName)) {
				this.locationStrategy = strategy;
				break;
			}
		}
		
		if (locationStrategy == null) {
			throw new IllegalArgumentException(String.format(
					"Can not find the location strategy [%s]", locationStrategyName));
		}
		
		logger.info("Using [{}] web resource location strategy.", locationStrategyName);
		
		resourceCache = new InMemoryWebResourceCache();
		
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
	 * Cache, compress, minify etc. the web resource.
	 * @param name
	 * @return NULL if the specify resource not found.
	 */
	protected WebResourceInfo serveResource(String name) {
		
		// load from in-momery cache first
		WebResourceInfo resourceInfo = resourceCache.get(name);
		if (resourceInfo != null) {
			return resourceInfo;
		}
		
		// then load from repository set
		resourceInfo = webResourceProviderSet.getByName(name);
		
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
			resourceInfo = new DefaultLocationTransformWebResourceInfo(this, resourceInfo);
			childResourceNames = ((DefaultLocationTransformWebResourceInfo)resourceInfo).listChildResourceNames();
		}
		
		// minify
		if (canMinify && minifyWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new DefaultMinifyWebResourceInfo(resourceInfo);
		}
		
		// compress
		if (canCompress && compressibleWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new DefaultCompressibleWebResourceInfo(resourceInfo);
		}
		
		// server cache
		if (canServerCache) {
			resourceInfo = new DefaultCacheableWebResourceInfo(resourceInfo, cacheSeconds);
			
			// insert the update listener into the child resources
			if (childResourceNames != null){
				for(String n : childResourceNames) {
					WebResourceInfo r = serveResource(n);
					if (r != null && r instanceof CacheableWebResourceInfo) {
						((CacheableWebResourceInfo)r).addUpdateListener((CacheableWebResourceInfoUpdateListener)resourceInfo);
					}
				}
			}
		}
		
		// store into in-momery cache
		resourceCache.set(resourceInfo);
		
		buildingResourceNames.pop();
		
		return resourceInfo;
	}
	
	@Override
	public WebResourceInfo getServerResource(String name) {
		return serveResource(name);
	}

	@Override
	public WebResourceInfo getResource(String name) {
		return webResourceProviderSet.getByName(name);
	}

	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		return locationStrategy.getLocation(webResourceInfo);
	}

	@Override
	public String getLocation(String name) {
		WebResourceInfo resource = getServerResource(name);
		return resource == null ? null : getLocation(resource);
	}

	@Override
	public void refresh(String name) {
		WebResourceInfo resource = serveResource(name);
		if (resource != null) {
			if (resource instanceof CacheableWebResourceInfo) {
				((CacheableWebResourceInfo)resource).refresh();
			}
		}
	}
	
	@Override
	public Collection<WebResourceInfo> list() {
		return webResourceProviderSet.list();
	}

	@Override
	public WebResourceInfo getServerResourceByVersionName(String versionName) {
		String name = locationStrategy.fromVersionName(versionName);
		return (name == null ? null : getServerResource(name));
	}
	
}

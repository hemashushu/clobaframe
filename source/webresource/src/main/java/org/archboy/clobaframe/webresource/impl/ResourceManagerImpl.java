package org.archboy.clobaframe.webresource.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.webresource.NotificationCacheableResourceInfo;
import org.archboy.clobaframe.webresource.ResourceUpdateListener;
import org.archboy.clobaframe.webresource.ResourceProviderSet;
import org.archboy.clobaframe.webresource.LocationStrategy;
import org.archboy.clobaframe.webresource.ManageResourceCache;
import org.archboy.clobaframe.webresource.ContentHashResourceInfo;
import org.archboy.clobaframe.webresource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 *
 */
@Named
public class ResourceManagerImpl implements ResourceManager {
	//, InitializingBean {

	public static final String DEFAULT_LOCATION_STRATEGY = "default";
	public static final boolean DEFAULT_CAN_MINIFY = false;
	public static final boolean DEFAULT_CAN_COMPRESS = true;
	public static final boolean DEFAULT_CAN_SERVER_CACHE = true;
	public static final int DEFAULT_SERVER_CACHE_SECONDS = 10 * 60;

	public static final String SETTING_KEY_LOCATION_STRATEGY = "clobaframe.resource.locationStrategy";
	public static final String SETTING_KEY_CAN_MINIFY = "clobaframe.resource.minify";
	public static final String SETTING_KEY_CAN_COMPRESS = "clobaframe.resource.compress";
	public static final String SETTING_KEY_CAN_SERVER_CACHE = "clobaframe.resource.serverCache";
	public static final String SETTING_KEY_SERVER_CACHE_SECONDS = "clobaframe.resource.serverCacheSeconds";
	
	private ManageResourceCache manageResourceCache;

	@Value("${" + SETTING_KEY_LOCATION_STRATEGY + ":" + DEFAULT_LOCATION_STRATEGY + "}")
	private String locationStrategyName;
	
	@Inject
	private List<LocationStrategy> locationStrategys;
	
	private LocationStrategy locationStrategy; 
	
	@Inject
	private ResourceProviderSet resourceProviderSet;
	
	private List<String> compressibleResourceMimeTypes; 
	private List<String> minifyResourceMimeTypes; 
	
	@Value("${" + SETTING_KEY_CAN_MINIFY + ":" + DEFAULT_CAN_MINIFY + "}")
	private boolean canMinify = DEFAULT_CAN_MINIFY;
	
	@Value("${" + SETTING_KEY_CAN_COMPRESS + ":" + DEFAULT_CAN_COMPRESS + "}")
	private boolean canCompress = DEFAULT_CAN_COMPRESS;
	
	@Value("${" + SETTING_KEY_CAN_SERVER_CACHE + ":" + DEFAULT_CAN_SERVER_CACHE + "}")
	private boolean canServerCache = DEFAULT_CAN_SERVER_CACHE;
	
	@Value("${" + SETTING_KEY_SERVER_CACHE_SECONDS + ":" + DEFAULT_SERVER_CACHE_SECONDS + "}")
	private int cacheSeconds = DEFAULT_SERVER_CACHE_SECONDS;
	
	private final Logger logger = LoggerFactory.getLogger(ResourceManagerImpl.class);

	// keep the current post-handling resources name.
	// to prevent infinite loop
	private Stack<String> buildingResourceNames = new Stack<String>();

	public void setLocationStrategys(List<LocationStrategy> locationStrategys) {
		this.locationStrategys = locationStrategys;
	}

	public void setLocationStrategyName(String locationStrategyName) {
		this.locationStrategyName = locationStrategyName;
	}

	public void setResourceProviderSet(ResourceProviderSet resourceProviderSet) {
		this.resourceProviderSet = resourceProviderSet;
	}

	public void setCanMinify(boolean canMinify) {
		this.canMinify = canMinify;
	}

	public void setCanCompress(boolean canCompress) {
		this.canCompress = canCompress;
	}

	public void setCanServerCache(boolean canServerCache) {
		this.canServerCache = canServerCache;
	}

	public void setCacheSeconds(int cacheSeconds) {
		this.cacheSeconds = cacheSeconds;
	}

	@PostConstruct
	//@Override
	public void init() throws Exception {
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
		
		manageResourceCache = new InMemoryManageResourceCache();
		
		compressibleResourceMimeTypes = new ArrayList<String>();
		compressibleResourceMimeTypes.add(MIME_TYPE_STYLE_SHEET);
		compressibleResourceMimeTypes.addAll(MIME_TYPE_JAVA_SCRIPT);
		compressibleResourceMimeTypes.addAll(MIME_TYPE_TEXT);
		
		// Some types of font file are compressed,
		// excepted the ttf and svg.
		compressibleResourceMimeTypes.add("application/x-font-ttf"); // ttf
		compressibleResourceMimeTypes.add("image/svg+xml"); // svg
		
		minifyResourceMimeTypes = new ArrayList<String>();
		minifyResourceMimeTypes.add(MIME_TYPE_STYLE_SHEET);
		minifyResourceMimeTypes.addAll(MIME_TYPE_JAVA_SCRIPT);
	}

	/**
	 * Cache, compress, minify etc. the web resource.
	 * @param name
	 * @return NULL if the specify resource not found.
	 */
	protected NamedResourceInfo serveResource(String name) {
		
		// load from in-momery cache first
		NamedResourceInfo resourceInfo = manageResourceCache.get(name);
		if (resourceInfo != null) {
			return resourceInfo;
		}
		
		// then load from repository set
		resourceInfo = resourceProviderSet.getByName(name);
		
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
			resourceInfo = new DefaultLocationTransformResourceInfo(this, resourceInfo);
			childResourceNames = ((DefaultLocationTransformResourceInfo)resourceInfo).listChildResourceNames();
		}
		
		// minify
		if (canMinify && minifyResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new DefaultMinifyResourceInfo(resourceInfo);
		}
		
		// compress
		if (canCompress && compressibleResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new DefaultCompressibleResourceInfo(resourceInfo);
		}
		
		// server cache
		if (canServerCache) {
			resourceInfo = new DefaultCacheableResourceInfo(resourceInfo, cacheSeconds);
			
			// insert the update listener into the child resources
			if (childResourceNames != null){
				for(String n : childResourceNames) {
					NamedResourceInfo r = serveResource(n);
					if (r != null && r instanceof NotificationCacheableResourceInfo) {
						((NotificationCacheableResourceInfo)r).addUpdateListener((ResourceUpdateListener)resourceInfo);
					}
				}
			}
		}
		
		// store into in-momery cache
		manageResourceCache.set(resourceInfo);
		
		buildingResourceNames.pop();
		
		return resourceInfo;
	}
	
	@Override
	public NamedResourceInfo getServedResource(String name) {
		return serveResource(name);
	}

	@Override
	public NamedResourceInfo get(String name) {
		return resourceProviderSet.getByName(name);
	}

	@Override
	public String getLocation(NamedResourceInfo resourceInfo) {
		return locationStrategy.getLocation(resourceInfo);
	}

	@Override
	public String getLocation(String name) {
		NamedResourceInfo resource = getServedResource(name);
		return resource == null ? null : getLocation(resource);
	}

	@Override
	public void refresh(String name) {
		NamedResourceInfo resource = serveResource(name);
		if (resource != null) {
			if (resource instanceof NotificationCacheableResourceInfo) {
				((NotificationCacheableResourceInfo)resource).refresh();
			}
		}
	}
	
	@Override
	public Collection<NamedResourceInfo> list() {
		return resourceProviderSet.list();
	}

	@Override
	public NamedResourceInfo getServedResourceByVersionName(String versionName) {
		String name = locationStrategy.fromVersionName(versionName);
		return (name == null ? null : getServedResource(name));
	}
	
}

package org.archboy.clobaframe.webresource.impl;

import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.webresource.WebResourceCache;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 * In-memory cache.
 * 
 * @author yang
 */
public class InMemoryWebResourceCache implements WebResourceCache {

	private Map<String, WebResourceInfo> resources = new HashMap<String, WebResourceInfo>();
	
	@Override
	public WebResourceInfo get(String name) {
		return resources.get(name);
	}

	@Override
	public void set(WebResourceInfo webResourceInfo) {
		resources.put(webResourceInfo.getName(), webResourceInfo);
	}
}

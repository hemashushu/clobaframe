package org.archboy.clobaframe.resource.impl;

import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.resource.ManageResourceCache;
import org.archboy.clobaframe.resource.ContentHashResourceInfo;

/**
 * In-memory cache.
 * 
 * @author yang
 */
public class InMemoryManageResourceCache implements ManageResourceCache {

	private Map<String, NamedResourceInfo> resources = new HashMap<String, NamedResourceInfo>();
	
	@Override
	public NamedResourceInfo get(String name) {
		return resources.get(name);
	}

	@Override
	public void set(NamedResourceInfo webResourceInfo) {
		resources.put(webResourceInfo.getName(), webResourceInfo);
	}
}

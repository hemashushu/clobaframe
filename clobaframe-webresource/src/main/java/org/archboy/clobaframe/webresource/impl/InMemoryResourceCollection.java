package org.archboy.clobaframe.webresource.impl;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.ResourceCollection;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class InMemoryResourceCollection implements ResourceCollection {

	private Map<String, WebResourceInfo> resources = new HashMap<String, WebResourceInfo>();
	
	@Override
	public WebResourceInfo getByName(String name) {
		return resources.get(name);
	}

	@Override
	public void add(WebResourceInfo webResourceInfo) {
		resources.put(webResourceInfo.getName(), webResourceInfo);
	}
}

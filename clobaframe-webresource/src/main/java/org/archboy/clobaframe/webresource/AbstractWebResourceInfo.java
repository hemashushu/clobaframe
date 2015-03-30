package org.archboy.clobaframe.webresource;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author yang
 */
public abstract class AbstractWebResourceInfo implements WebResourceInfo {
	
	private Set<Class<?>> underlayWebResourceInfoTypes = new HashSet<Class<?>>();
	
	protected void addUnderlayWebResourceType(WebResourceInfo webResourceInfo) {
		underlayWebResourceInfoTypes.add(webResourceInfo.getClass());
	}

	public Set<Class<?>> getUnderlayWebResourceInfoTypes() {
		return underlayWebResourceInfoTypes;
	}
	
}

package org.archboy.clobaframe.webresource;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author yang
 */
public abstract class AbstractWebResourceInfo implements WebResourceInfo {
	
	private Set<String> underlayWebResourceInfoNames = new HashSet<String>();
	
	protected void addUnderlayWebResource(WebResourceInfo webResourceInfo) {
		underlayWebResourceInfoNames.add(webResourceInfo.getClass().getSimpleName());
	}

	public Set<String> getUnderlayWebResourceInfoNames() {
		return underlayWebResourceInfoNames;
	}
	
}

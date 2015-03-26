package org.archboy.clobaframe.webresource;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yang
 */
public abstract class AbstractWebResourceInfo implements WebResourceInfo {
	
	private List<String> underlayWebResourceInfoNames = new ArrayList<String>();
	
	protected void addUnderlayWebResource(WebResourceInfo webResourceInfo) {
		underlayWebResourceInfoNames.add(webResourceInfo.getClass().getSimpleName());
	}

	public List<String> getUnderlayWebResourceInfoNames() {
		return underlayWebResourceInfoNames;
	}
	
}

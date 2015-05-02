package org.archboy.clobaframe.webresource.impl;

import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.VirtualWebResourceProvider;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class NullVirtualWebResourceProvider implements VirtualWebResourceProvider {

	@Override
	public WebResourceInfo getByName(String name) {
		return null;
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		return new ArrayList<WebResourceInfo>();
	}
	
}

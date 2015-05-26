package org.archboy.clobaframe.webresource.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.archboy.clobaframe.webresource.*;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * The virtual resource repository.
 * 
 * The virtual resource means the resource that generate by dynamic,
 * such as the user custom style sheet that stored in database.
 * 
 * @author yang
 */
@Named
public class VirtualWebResourceRepositoryImpl implements VirtualWebResourceRepository {

	@Inject
	private List<VirtualWebResourceProvider> virtualResourceProviders;
	
	@Override
	public String getName() {
		return "virtual";
	}

	@Override
	public int getOrder() {
		return PRIORITY_HIGHER;
	}

	@Override
	public WebResourceInfo getByName(String name) {
		for(VirtualWebResourceProvider virtualResourceProvider : virtualResourceProviders) {
			WebResourceInfo webResourceInfo = virtualResourceProvider.getByName(name);
			if (webResourceInfo != null) {
				return webResourceInfo;
			}
		}
		
		return null;
	}

	@Override
	public Collection<VirtualWebResourceProvider> getResourceProviders() {
		return virtualResourceProviders;
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		// NOTE:: not all virtual web resource repository can be list.
		Set<WebResourceInfo> resourceInfos = new HashSet<WebResourceInfo>();
		
		for (VirtualWebResourceProvider resourceProvider : virtualResourceProviders){
			Collection<WebResourceInfo> ns = resourceProvider.getAll(); 
			resourceInfos.addAll(ns);
		}
		
		return resourceInfos;
	}
}

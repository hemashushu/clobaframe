package org.archboy.clobaframe.webresource.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.WebResourceProviderSet;
import org.archboy.clobaframe.webresource.WebResourceProvider;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class WebResourceProviderSetImpl implements WebResourceProviderSet {
		
	@Inject
	private List<WebResourceProvider> webResourceProviders;

	@Override
	public WebResourceInfo getByName(String name) {
		WebResourceInfo webResourceInfo = null;
		for (WebResourceProvider resourceRepository : webResourceProviders){
			webResourceInfo = resourceRepository.getByName(name);
			if (webResourceInfo != null) {
				break;
			}
		}
		return webResourceInfo;
	}

//	@Override
//	public Collection<WebResourceProvider> getResourceRepositories() {
//		return resourceRepositories;
//	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		Set<WebResourceInfo> resourceInfos = new HashSet<WebResourceInfo>();
		
		// list resource in the revert repository priority order
		for (int idx = webResourceProviders.size() -1; idx>=0; idx--){
			WebResourceProvider resourceRepository = webResourceProviders.get(idx);
			Collection<WebResourceInfo> ns = resourceRepository.getAll(); 
			resourceInfos.addAll(ns);
		}
		
		return resourceInfos;
	}

}

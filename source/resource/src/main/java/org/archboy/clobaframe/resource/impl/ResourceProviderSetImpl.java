package org.archboy.clobaframe.resource.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.resource.ResourceProviderSet;
import org.archboy.clobaframe.resource.ResourceProvider;
import org.archboy.clobaframe.resource.ContentHashResourceInfo;
import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class ResourceProviderSetImpl implements ResourceProviderSet {
		
	@Inject
	private List<ResourceProvider> resourceProviders;

	public void setResourceProviders(List<ResourceProvider> resourceProviders) {
		this.resourceProviders = resourceProviders;
	}
	
	@PostConstruct
	public void init() throws Exception {
		if (resourceProviders != null && !resourceProviders.isEmpty()) {
			OrderComparator.sort(resourceProviders);
		}
	}
	
	@Override
	public NamedResourceInfo getByName(String name) {
		NamedResourceInfo webResourceInfo = null;
		for (ResourceProvider webResourceProvider : resourceProviders){
			webResourceInfo = webResourceProvider.getByName(name);
			if (webResourceInfo != null) {
				break;
			}
		}
		return webResourceInfo;
	}

	@Override
	public Collection<NamedResourceInfo> list() {
		Set<NamedResourceInfo> resourceInfos = new HashSet<NamedResourceInfo>();
		
		// list resource from big (lower) to small (higher) order value,
		// so the higher priority can override the lower one.
		for (int idx = resourceProviders.size() -1; idx>=0; idx--){
			ResourceProvider resourceRepository = resourceProviders.get(idx);
			Collection<NamedResourceInfo> ns = resourceRepository.list(); 
			resourceInfos.addAll(ns);
		}
		
		return resourceInfos;
	}

	@Override
	public void addProvider(ResourceProvider webResourceProvider) {
		if (resourceProviders == null) {
			resourceProviders = new ArrayList<ResourceProvider>();
		}
		
		resourceProviders.add(webResourceProvider);
		
		// sort 0-9
		OrderComparator.sort(resourceProviders);
//		resourceProviders.sort(new Comparator<ResourceProvider>() {
//
//			@Override
//			public int compare(ResourceProvider o1, ResourceProvider o2) {
//				return o1.getOrder() - o2.getOrder();
//			}
//		});
	}

	@Override
	public void removeProvider(String providerName) {
		Assert.notNull(providerName);
		
		for (int idx = resourceProviders.size() - 1; idx >= 0; idx--){
			ResourceProvider provider = resourceProviders.get(idx);
			if (providerName.equals(provider.getName())){
				resourceProviders.remove(idx);
				break;
			}
		}
	}
}

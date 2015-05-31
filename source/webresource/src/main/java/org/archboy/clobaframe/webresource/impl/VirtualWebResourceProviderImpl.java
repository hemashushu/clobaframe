package org.archboy.clobaframe.webresource.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.VirtualWebResourceProvider;
import org.archboy.clobaframe.webresource.VirtualWebResourceSource;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * The virtual resource repository.
 * 
 * The virtual resource means the resource that generate by dynamic,
 * such as the user custom style sheet that stored in database.
 * 
 * @author yang
 */
@Named
public class VirtualWebResourceProviderImpl implements VirtualWebResourceProvider {

	@Autowired(required = false)
	private List<VirtualWebResourceSource> virtualWebResourceSources;
	
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
		if (virtualWebResourceSources == null || virtualWebResourceSources.isEmpty()) {
			return null;
		}
		
		for(VirtualWebResourceSource virtualResourceProvider : virtualWebResourceSources) {
			WebResourceInfo webResourceInfo = virtualResourceProvider.getByName(name);
			if (webResourceInfo != null) {
				return webResourceInfo;
			}
		}
		
		return null;
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		// NOTE:: not all virtual web resource repository can be list.
		Set<WebResourceInfo> resourceInfos = new HashSet<WebResourceInfo>();
		
		if (virtualWebResourceSources == null || virtualWebResourceSources.isEmpty()) {
			return resourceInfos;
		}
		
		for (VirtualWebResourceSource resourceProvider : virtualWebResourceSources){
			Collection<WebResourceInfo> ns = resourceProvider.getAll(); 
			resourceInfos.addAll(ns);
		}
		
		return resourceInfos;
	}

	@Override
	public void addSource(VirtualWebResourceSource virtualWebResourceSource) {
		if (virtualWebResourceSources == null) {
			virtualWebResourceSources = new ArrayList<VirtualWebResourceSource>();
		}
		
		virtualWebResourceSources.add(virtualWebResourceSource);
	}

	@Override
	public void removeSource(String sourceName) {
		Assert.notNull(sourceName);
		
		for (int idx = virtualWebResourceSources.size() - 1; idx >= 0; idx--){
			VirtualWebResourceSource source = virtualWebResourceSources.get(idx);
			if (sourceName.equals(source.getName())){
				virtualWebResourceSources.remove(idx);
			}
		}
	}
}

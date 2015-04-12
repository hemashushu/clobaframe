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
public class VirtualWebResourceRepositoryImpl extends AbstractWebResourceRepository implements VirtualWebResourceRepository {

	private List<VirtualWebResourceProvider> virtualResourceProviders = new ArrayList<VirtualWebResourceProvider>();
	
	@Override
	public String getName() {
		return "virtual";
	}

	@Override
	public int getPriority() {
		return PRIORITY_LESS_THAN_TOP;
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
	public void addProvider(VirtualWebResourceProvider provider) {
		virtualResourceProviders.add(provider);
	}

	@Override
	public Collection<String> getAllNames() {
		// NOTE:: not all virtual web resource repository can be list.
		Set<String> names = new HashSet<String>();
		
		for (VirtualWebResourceProvider resourceProvider : virtualResourceProviders){
			Collection<String> ns = resourceProvider.getAllNames(); 
			names.addAll(ns);
		}
		
		return names;
	}
}

package org.archboy.clobaframe.webresource.impl;

import java.util.ArrayList;
import java.util.Collection;
import org.archboy.clobaframe.webresource.*;
import java.util.List;
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
public class VirtualResourceRepositoryImpl extends AbstractResourceRepository implements VirtualResourceRepository {

	private List<VirtualResourceProvider> virtualResourceProviders = new ArrayList<VirtualResourceProvider>();
	
	@Override
	public String getName() {
		return "virtual";
	}

	@Override
	public WebResourceInfo getByName(String name) {
		for(VirtualResourceProvider virtualResourceProvider : virtualResourceProviders) {
			WebResourceInfo webResourceInfo = virtualResourceProvider.lookup(name);
			if (webResourceInfo != null) {
				return webResourceInfo;
			}
		}
		
		return null;
	}

	@Override
	public void addProvider(VirtualResourceProvider provider) {
		virtualResourceProviders.add(provider);
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		return new ArrayList<WebResourceInfo>();
	}
}

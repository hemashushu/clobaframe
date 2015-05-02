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
import org.archboy.clobaframe.webresource.WebResourceRepositorySet;
import org.archboy.clobaframe.webresource.WebResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class WebResourceRepositorySetImpl implements WebResourceRepositorySet {
		
	@Inject
	private List<WebResourceRepository> resourceRepositories;

	@PostConstruct
	public void init() throws IOException {
		// sort the repositories
		resourceRepositories.sort(new Comparator<WebResourceRepository>() {

			@Override
			public int compare(WebResourceRepository o1, WebResourceRepository o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
	}

	@Override
	public WebResourceInfo getByName(String name) {
		WebResourceInfo webResourceInfo = null;
		for (WebResourceRepository resourceRepository : resourceRepositories){
			webResourceInfo = resourceRepository.getByName(name);
			if (webResourceInfo != null) {
				break;
			}
		}
		return webResourceInfo;
	}

	@Override
	public Collection<WebResourceRepository> getResourceRepositories() {
		return resourceRepositories;
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		Set<WebResourceInfo> resourceInfos = new HashSet<WebResourceInfo>();
		
		// list resource in the revert repository priority order
		for (int idx = resourceRepositories.size() -1; idx>=0; idx--){
			WebResourceRepository resourceRepository = resourceRepositories.get(idx);
			Collection<WebResourceInfo> ns = resourceRepository.getAll(); 
			resourceInfos.addAll(ns);
		}
		
		return resourceInfos;
	}

}

/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.archboy.clobaframe.webresource.LocationGenerator;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.util.Assert;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.ResourceStrategy;
import org.archboy.clobaframe.webresource.ResourceStrategyFactory;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceService;
import org.archboy.clobaframe.webresource.local.LocalLocationGenerator;

/**
 *
 * @author young
 *
 */
@Named
public class WebResourceServiceImpl implements WebResourceService {

	private Map<String, WebResourceInfo> webResources = new HashMap<String, WebResourceInfo>();
	//private Map<String, WebResourceInfo> uniqueNameWebResources = new HashMap<String, WebResourceInfo>();
	private Map<String, String> uniqueNames = new HashMap<String, String>(); // the unique name to resource mapper.

	@Inject
	private ResourceStrategyFactory resourceStrategyFactory;

	//private ResourceStrategy resourceStrategy;
	private LocationGenerator locationGenerator;

	@PostConstruct
	public void init() {

		ResourceStrategy resourceStrategy = resourceStrategyFactory.getResourceStrategy();
		ResourceRepository resourceRepository = resourceStrategy.getResourceRepository();
		this.locationGenerator = resourceStrategy.getLocationGenerator();

		List<WebResourceInfo> webResourceInfos = resourceRepository.findAll();

		if (webResourceInfos.isEmpty()){
			// no web resource present
			return;
		}

		AbstractResourceStrategy strategy = (AbstractResourceStrategy)resourceStrategy;

		webResourceInfos = strategy.preHandle(webResourceInfos);
		webResourceInfos = strategy.postHandle(webResourceInfos);

		// get all resource name and unique names
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			webResources.put(webResourceInfo.getName(), webResourceInfo);
			uniqueNames.put(webResourceInfo.getUniqueName(), webResourceInfo.getName());
			
//			uniqueNameWebResources.put(webResourceInfo.getUniqueName(),
//					webResourceInfo);
		}
	}

	@Override
	public String getLocation(String name) {
		WebResourceInfo webResourceInfo = webResources.get(name);
		return getLocation(webResourceInfo);
	}

	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		Assert.notNull(webResourceInfo);
		return locationGenerator.getLocation(webResourceInfo);
	}

	@Override
	public WebResourceInfo getResource(String name) throws FileNotFoundException {
		WebResourceInfo info = webResources.get(name);
		if (info == null) {
			throw new FileNotFoundException(name);
		}
		return info;
	}

	@Override
	public WebResourceInfo getResourceByUniqueName(String uniqueName) throws FileNotFoundException {
		String name = uniqueNames.get(uniqueName);
		if (name == null) {
			throw new FileNotFoundException(name);
		}
		return getResource(name);
	}

	@Override
	public Collection<WebResourceInfo> getAllResources() {
		return webResources.values();
	}
}

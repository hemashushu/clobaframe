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
package org.archboy.clobaframe.webresource.blobstore;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.webresource.LocationGenerator;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.impl.AbstractResourceStrategy;

/**
 *
 * @author young
 */
@Component
public class BlobstoreResourceStrategy extends AbstractResourceStrategy{

	private final Logger logger = LoggerFactory.getLogger(BlobstoreResourceStrategy.class);

	@Autowired
	@Qualifier("blobstoreLocationGenerator")
	private LocationGenerator locationGenerator;

	@Autowired
	@Qualifier("localWebResourceRepository")
	private ResourceRepository resourceRepository;

	@Autowired
	private BlobstoreWebResourceSynchronizer resourceSynchronizer;

	@Override
	public String getName() {
		return "blobstore";
	}

	@Override
	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	@Override
	public LocationGenerator getLocationGenerator() {
		return locationGenerator;
	}

	@Override
	protected List<WebResourceInfo> postHandle(List<WebResourceInfo> webResourceInfos) {
		try{
			resourceSynchronizer.update(webResourceInfos);
		}catch(IOException e){
			logger.warn("Fail to synchronize the remote web resources.", e);
		}

		return webResourceInfos;
	}
}

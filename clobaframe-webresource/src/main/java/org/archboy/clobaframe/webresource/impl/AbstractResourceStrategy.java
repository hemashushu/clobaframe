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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.archboy.clobaframe.webresource.LocationGenerator;
import org.archboy.clobaframe.webresource.ResourceStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author young
 */
public abstract class AbstractResourceStrategy implements ResourceStrategy{

	// the content types that can be location replaced.
	private List<String> contentTypes = Arrays.asList(
			"text/javascript",
			"text/css",
			"application/x-javascript");

	/**
	 * cache millisecond:
	 * 	less than 0 = cache always.
	 * 	equals 0 = no cache
	 */
	@Value("${webresource.cacheSeconds}")
	private int cacheSeconds;

	// cache the file that less than 1MiB.
	private static final int MAX_CACHE_FILE_SIZE = 1024 * 1024;
	private int maxCacheFileSize = MAX_CACHE_FILE_SIZE;

	/**
	 * Add buffer and location replacement features.
	 *
	 * @param webResourceInfos
	 * @return
	 */
	protected List<WebResourceInfo> preHandle(List<WebResourceInfo> webResourceInfos){
		Map<String, String> locations = new HashMap<String, String>();

		LocationGenerator locationGenerator = getLocationGenerator();

		// get all resource locations
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			locations.put(
					webResourceInfo.getName(),
					locationGenerator.getLocation(webResourceInfo));
		}

		List<WebResourceInfo> result = new ArrayList<WebResourceInfo>();

		// select web resources that can be location replaced.
		for (WebResourceInfo webResourceInfo : webResourceInfos) {

			// convert into location-replacing resource
			if (contentTypes.contains(webResourceInfo.getContentType())) {
				webResourceInfo = new LocationReplacingWebResourceInfo(
					webResourceInfo, locations);
			}

			// convert info buffered web resource
			if (cacheSeconds != 0 && webResourceInfo.getContentLength() < maxCacheFileSize){
				webResourceInfo = new BufferedWebResourceInfo(
						webResourceInfo, cacheSeconds);
			}

			result.add(webResourceInfo);
		}

		return result;
	}

	protected abstract List<WebResourceInfo> postHandle(List<WebResourceInfo> webResourceInfos);

}

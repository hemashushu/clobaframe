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
package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * Check the 'If-Modified-Since' and 'Range' HTTP headers of request
 * before sending resource.
 * 
 * The 'Last-Modified' HTTP header will be sent.
 *
 * @author young
 *
 */
public abstract class AbstractDataSenderWithLastModifiedAndRangeCheck extends
		AbstractDataSender {

	public void sendDataWithLastModifiedAndRangeCheck(
			HttpServletResponse response, ResourceInfo resourceInfo,
			Map<String, String> extraHeaders, long ifModifiedSince, String range)
			throws IOException {

		long lastModifiedTime = 0;

		// send last-modified header.
		if (resourceInfo.getLastModified() != null){
			lastModifiedTime = resourceInfo.getLastModified().getTime();
			response.setDateHeader("Last-Modified", lastModifiedTime);
		}

		// check 'if-modified-since' request
		if (ifModifiedSince > 0 && lastModifiedTime>0){
			if (lastModifiedTime / 1000 <= ifModifiedSince / 1000) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
		}

		// check 'range' request
		if (StringUtils.isNotEmpty(range) &&
			resourceInfo.isSeekable()) {

			RequestRange requestRange = new RequestRange(range,
					resourceInfo.getContentLength());

			sendPartialData(response, resourceInfo,
					extraHeaders,
					requestRange.getStartPosition(),
					requestRange.getLength());
		} else {

			sendData(response, resourceInfo, extraHeaders);
		}
	}

}

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
		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();

		if (StringUtils.isNotEmpty(range) &&
				resourceInfo.isContentSeekable()) {
				//&& (resourceContent.getInputStream() instanceof SeekableInputStream)) {

			RequestRange requestRange = new RequestRange(range,
					resourceInfo.getContentLength());

//			ContentReuseResourceInfo reuseResourceInfo = new ContentReuseResourceInfo(
//					resourceInfo.getContentLength(),
//					resourceInfo.getContentType(),
//					resourceContent);

			sendPartialData(response, resourceInfo,
					extraHeaders,
					requestRange.getStartPosition(),
					requestRange.getLength());
		} else {

//			ContentReuseResourceInfo reuseResourceInfo = new ContentReuseResourceInfo(
//					resourceInfo.getContentLength(),
//					resourceInfo.getContentType(),
//					resourceContent);

			sendData(response, resourceInfo, extraHeaders);
		}

		// close resource content
		//IOUtils.closeQuietly(resourceContent);
	}

	/**
	 * Deprecated!!
	 *
	 * Re-package the resource info object, so that to re-use the
	 * resource content object that already fetch.
	 *
	 * Because the #sendDataWithLastModifiedAndRangeCheck method fetch the
	 * resource content snapshot to check the stream type.
	 */
//	public class ContentReuseResourceInfo implements ResourceInfo {
//
//		private long contentLength;
//		private String contentType;
//		private ResourceContent resourceContent;
//
//		private boolean contentSnapshotCreated;
//
//		public ContentReuseResourceInfo(long contentLength, String contentType,
//				ResourceContent resourceContent) {
//
//			this.contentLength = contentLength;
//			this.contentType = contentType;
//			this.resourceContent = resourceContent;
//		}
//
//		@Override
//		public long getContentLength() {
//			return contentLength;
//		}
//
//		@Override
//		public String getContentType() {
//			return contentType;
//		}
//
//		@Override
//		public ResourceContent getContentSnapshot() throws IOException {
//			if (contentSnapshotCreated){
//				// this ResourceInfo implementation only can be got content snapshot once
//				throw new IOException("The content snapshot is gone.");
//			}
//
//			contentSnapshotCreated = true;
//			return resourceContent;
//		}
//
//		@Override
//		public String getName() {
//			// just drop this property's value
//			return null;
//		}
//
//		@Override
//		public Date getLastModified() {
//			// just drop this property's value
//			return null;
//		}
//
//	}
}

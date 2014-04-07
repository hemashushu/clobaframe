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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.archboy.clobaframe.webresource.WebResourceInfo;

public class BufferedWebResourceInfo implements WebResourceInfo {

	private final Logger logger = LoggerFactory.getLogger(BufferedWebResourceInfo.class);

	/**
	 * cache millisecond less than 0 means cache always.
	 * 0 means no cache.
	 */
	private long cacheMilliSeconds;
	private long lastCheckTime;
	private WebResourceInfo webResourceInfo;

	private Date lastModified;
	private byte[] content;
	private String hash;

	public BufferedWebResourceInfo(WebResourceInfo webResourceInfo, int cacheSeconds) {
		this.webResourceInfo = webResourceInfo;
		this.cacheMilliSeconds = cacheSeconds * 1000;
		rebuildSnapshot();
	}

	@Override
	public InputStream getInputStream() throws IOException{
		refresh();
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException {
		refresh();
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public String getName() {
		return webResourceInfo.getName();
	}

	@Override
	public String getHash() {
		refresh();
		return hash;
	}

	@Override
	public long getContentLength() {
		refresh();
		return content.length;
	}

	@Override
	public Date getLastModified() {
		refresh();
		return lastModified;
	}

	@Override
	public String getContentType() {
		return webResourceInfo.getContentType();
	}

	@Override
	public String getUniqueName() {
		return webResourceInfo.getUniqueName();
	}

	private void refresh(){
		// cache millisecond less than 0 means cache always
		if (cacheMilliSeconds < 0){
			return;
		}

		long now = System.currentTimeMillis();
		long span = now - lastCheckTime;
		if (span > cacheMilliSeconds){
			lastCheckTime = now;
			rebuildSnapshot();
		}
	}

	private void rebuildSnapshot() {

		if (lastModified != null &&
				webResourceInfo.getLastModified().getTime() - lastModified.getTime() <= 0){
			// resource not changed
			return;
		}

		this.lastModified = webResourceInfo.getLastModified();

		//ResourceContent resourceContent = null;
		InputStream in = null;
		try {
//			resourceContent = webResourceInfo.getContentSnapshot();
//			InputStream in = resourceContent.getInputStream();
			in = webResourceInfo.getInputStream();
			
			this.content = IOUtils.toByteArray(in);
			this.hash = DigestUtils.sha256Hex(this.content);
		} catch (IOException e) {
			logger.error("Read the web resource [{}] content fail.", webResourceInfo.getName());
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}

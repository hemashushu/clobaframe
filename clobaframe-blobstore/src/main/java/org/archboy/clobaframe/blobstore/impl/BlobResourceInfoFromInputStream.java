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

package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobKey;

/**
 *
 * @author young
 *
 */
public class BlobResourceInfoFromInputStream implements BlobResourceInfo{

	private BlobKey blobKey;
	private long contentLength;
	private String contentType;
	private Date lastModified;
	private InputStream inputStream;
	private Map<String, String> metadata;

	private boolean consumed;

	public BlobResourceInfoFromInputStream(BlobKey blobKey, long contentLength,
			String contentType, Date lastModified, InputStream inputStream) {
		this.blobKey = blobKey;
		this.contentLength = contentLength;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.inputStream = inputStream;
		this.metadata = new HashMap<String, String>();
	}

	@Override
	public BlobKey getBlobKey() {
		return blobKey;
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (consumed){
			throw new IOException("The content snapshot has already created.");
		}

		consumed = true;
		return inputStream;
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException{
		throw new UnsupportedOperationException("Does not supported.");
	}

	@Override
	public boolean isSeekable() {
		return false;
	}

	@Override
	public Map<String, String> getMetadata() {
		return metadata;
	}

	@Override
	public void addMetadata(String key, String value) {
		metadata.put(key, value);
	}
}

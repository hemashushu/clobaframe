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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobInfo;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.webio.ResourceContent;
import org.archboy.clobaframe.webio.impl.DefaultResourceContent;

/**
 *
 * @author young
 */
public class BlobInfoFromByteArray implements BlobInfo{

	private BlobKey blobKey;

	private String contentType;
	private Date lastModified;
	private byte[] content;

	private Map<String, String> metadata;

	public BlobInfoFromByteArray(BlobKey blobKey, String contentType,
			Date lastModified, byte[] content) {
		this.blobKey = blobKey;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.content = content;
		this.metadata = new HashMap<String, String>();
	}

	@Override
	public BlobKey getBlobKey() {
		return blobKey;
	}

	@Override
	public long getContentLength() {
		return content.length;
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
	public ResourceContent getContentSnapshot() throws IOException{
		return new DefaultResourceContent(content);
	}

	@Override
	public ResourceContent getContentSnapshot(long start, long length) throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				content, (int)start, (int)length);
		return new DefaultResourceContent(inputStream, length);
	}

	@Override
	public boolean isContentSeekable() {
		return true;
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

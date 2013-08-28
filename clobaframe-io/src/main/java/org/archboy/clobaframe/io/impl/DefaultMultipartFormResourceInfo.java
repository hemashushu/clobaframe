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
package org.archboy.clobaframe.io.impl;

import java.io.IOException;
import java.util.Date;
import org.apache.commons.fileupload.FileItem;
import org.archboy.clobaframe.io.ResourceContent;
import org.archboy.clobaframe.io.http.MultipartFormResourceInfo;

/**
 *
 * @author young
 */
public class DefaultMultipartFormResourceInfo implements MultipartFormResourceInfo{

	private FileItem fileItem;

	/**
	 * This object is used to hold the disk temporary file.
	 *
	 * Details see:
	 * {@link org.apache.commons.fileupload.disk.DiskFileItemFactory#createItem}
	 */
	private Object fileCleanerMarker;

	//private Logger logger = LoggerFactory.getLogger(DefaultMultipartFormResourceInfo.class);

	public DefaultMultipartFormResourceInfo(FileItem fileItem, Object fileCleanerMarker) {
		this.fileItem = fileItem;
		this.fileCleanerMarker = fileCleanerMarker;
	}

	@Override
	public boolean isFile() {
		return !fileItem.isFormField();
	}

	@Override
	public String getFileName() {
		return fileItem.getName();
	}

	@Override
	public String getContentAsString() {
		return fileItem.getString();
	}

	@Override
	public long getContentLength() {
		return fileItem.getSize();
	}

	@Override
	public String getContentType() {
		return fileItem.getContentType();
	}

	@Override
	public ResourceContent getContentSnapshot() throws IOException {
		return new DefaultResourceContent(
				fileItem.getInputStream(),
				fileItem.getSize());
	}

	@Override
	public ResourceContent getContentSnapshot(long start, long length) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isContentSeekable() {
		return false;
	}

	@Override
	public String getName() {
		return fileItem.getFieldName();
	}

	@Override
	public Date getLastModified() {
		return new Date();
	}
}

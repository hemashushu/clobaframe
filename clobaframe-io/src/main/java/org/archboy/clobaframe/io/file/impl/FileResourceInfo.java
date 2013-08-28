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
package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.ContentTypeDetector;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

public class FileResourceInfo implements FileBaseResourceInfo {

	private File file;
	private String contentType;

	/**
	 * Detect the mime type by the file extension name.
	 * 
	 * @param file
	 * @param contentTypeDetector 
	 */
	public FileResourceInfo(File file, ContentTypeDetector contentTypeDetector) {
		this.file = file;
		this.contentType = contentTypeDetector.getByExtensionName(file.getName());
	}

	public FileResourceInfo(File file, String contentType) {
		this.file = file;
		this.contentType = contentType;
	}

	@Override
	public long getContentLength() {
		// get the length just in time.
		return file.length();
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException {
		return new PartialFileInputStream(file, start, length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public Date getLastModified() {
		// get the last modified time just in time.
		return new Date(file.lastModified());
	}

	@Override
	public File getFile() {
		return file;
	}
	
}

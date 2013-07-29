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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webio.ResourceContent;
import org.archboy.clobaframe.webio.impl.DefaultResourceContent;
import org.archboy.clobaframe.webio.impl.PartialFileInputStream;
import org.archboy.clobaframe.webresource.WebResourceInfo;

public class DefaultWebResourceInfo implements WebResourceInfo {

	private File file;

	private String name;
	private String uniqueName;
	private String contentType;

	public DefaultWebResourceInfo(File file, String name, String uniqueName,
			String contentType) {
		this.file = file;
		this.name = name;
		this.uniqueName = uniqueName;
		this.contentType = contentType;
	}

	@Override
	public String getHash() {
		String hash = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			hash = DigestUtils.sha256Hex(in);
		}catch(IOException e){
			// ignore
		}finally{
			IOUtils.closeQuietly(in);
		}
		return hash;
	}

	@Override
	public String getUniqueName() {
		return uniqueName;
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
	public ResourceContent getContentSnapshot() throws IOException {
		InputStream in = new FileInputStream(file);
		return new DefaultResourceContent(in, file.length());
	}

	@Override
	public ResourceContent getContentSnapshot(long start, long length) throws IOException {
		InputStream in = new PartialFileInputStream(file, start, length);
		return new DefaultResourceContent(in, length);
	}

	@Override
	public boolean isContentSeekable() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date getLastModified() {
		// get the last modified time just in time.
		return new Date(file.lastModified());
	}
}

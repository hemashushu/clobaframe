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
package org.archboy.clobaframe.webio.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webio.ResourceContent;

/**
 *
 * @author young
 */
public class DefaultResourceContent implements ResourceContent{

	private long contentLength;
	private InputStream inputStream;

	public DefaultResourceContent(InputStream inputStream, long contentLength) {
		this.inputStream = inputStream;
		this.contentLength = contentLength;
	}

	public DefaultResourceContent(byte[] content) {
		this.contentLength = content.length;
		this.inputStream = new ByteArrayInputStream(content);
	}

	@Override
	public long getLength() {
		return contentLength;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(inputStream);
	}

}

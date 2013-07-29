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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;



/**
 * Add seeking ability to {@link FileInputStream}.
 *
 * @author young
 *
 */
public class PartialFileInputStream extends InputStream {

	private RandomAccessFile in;
	//private long length;
	private long endPosition; // the last exclude byte.

	private static final int MAX_AVAILABLE_BYTES = 256 * 1024;

	public PartialFileInputStream(File file, long start, long length) throws FileNotFoundException, IOException {
		Assert.isTrue(start>=0);
		Assert.isTrue(length>0);

		this.in = new RandomAccessFile(file, "r");
		in.seek(start);

		if (start + length > in.length()) {
			IOUtils.closeQuietly(in);
			throw new IllegalArgumentException("length");
		}

		//this.length = length;
		this.endPosition = start + length;
	}

	@Override
	public int available() throws IOException {
		long avaiable = endPosition - in.getFilePointer();
		if (avaiable < 0) {
			avaiable = 0;
		}

		if (avaiable > MAX_AVAILABLE_BYTES) {
			return MAX_AVAILABLE_BYTES;
		} else {
			return (int) MAX_AVAILABLE_BYTES;
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (in.getFilePointer() >= endPosition) {
			return -1;
		}

		int readLength = len;
		if (in.getFilePointer() + len > endPosition) {
			readLength = (int)(endPosition - in.getFilePointer());
		}

		return in.read(b, off, readLength);
	}

	@Override
	public int read() throws IOException {
		if (in.getFilePointer() >= endPosition) {
			return -1;
		}
		return in.read();
	}
}

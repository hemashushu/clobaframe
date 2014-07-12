package org.archboy.clobaframe.io.file.impl;

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
 * @author yang
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
		return read(b, 0, b.length);
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

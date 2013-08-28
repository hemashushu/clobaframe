package org.archboy.clobaframe.io.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import org.springframework.util.Assert;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author arch
 */
public class ByteArrayResourceInfo implements ResourceInfo {

	private long contentLength;
	private String contentType;
	private Date lastModified;
	private byte[] content;

	public ByteArrayResourceInfo(byte[] content,
			String contentType,
			Date lastModified) {
		this.contentLength = content.length;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.content = content;
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
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException {
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}
}
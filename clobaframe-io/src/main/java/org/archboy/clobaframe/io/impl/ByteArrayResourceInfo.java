package org.archboy.clobaframe.io.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public class ByteArrayResourceInfo implements ResourceInfo {

	private long contentLength;
	private String mimeType;
	private Date lastModified;
	private byte[] content;

	public ByteArrayResourceInfo(byte[] content,
			String mimeType,
			Date lastModified) {
		this.contentLength = content.length;
		this.mimeType = mimeType;
		this.lastModified = lastModified;
		this.content = content;
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
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
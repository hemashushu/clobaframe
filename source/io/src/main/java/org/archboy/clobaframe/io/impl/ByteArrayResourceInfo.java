package org.archboy.clobaframe.io.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class ByteArrayResourceInfo implements ResourceInfo {

	protected String mimeType;
	protected Date lastModified;
	protected byte[] content;

	public ByteArrayResourceInfo(byte[] content,
			String mimeType,
			Date lastModified) {
		Assert.notNull(content);
		Assert.hasText(mimeType);
		Assert.notNull(lastModified);
		
		this.mimeType = mimeType;
		this.lastModified = lastModified;
		this.content = content;
	}

	@Override
	public long getContentLength() {
		return content.length;
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
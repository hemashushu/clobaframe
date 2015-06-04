package org.archboy.clobaframe.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class InputStreamResourceInfo implements ResourceInfo {

	protected long contentLength;
	protected String mimeType;
	protected Date lastModified;
	protected InputStream inputStream;
	
	private boolean consumed;

	public InputStreamResourceInfo(InputStream inputStream, long contentLength, 
			String mimeType, Date lastModified) {
		Assert.notNull(inputStream);
		Assert.hasText(mimeType);
		Assert.notNull(lastModified);
		
		this.contentLength = contentLength;
		this.mimeType = mimeType;
		this.lastModified = lastModified;
		this.inputStream = inputStream;
		
		this.consumed = false;
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
		if (consumed){
			throw new IOException("The content has already consumed.");
		}

		consumed = true;
		return inputStream;
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		throw new UnsupportedOperationException("Does not supported.");
	}

	@Override
	public boolean isSeekable() {
		return false;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}
	
}

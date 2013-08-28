package org.archboy.clobaframe.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public class InputStreamResourceInfo implements ResourceInfo {

	private long contentLength;
	private String contentType;
	private Date lastModified;
	private InputStream inputStream;
	
	private boolean consumed;

	public InputStreamResourceInfo(InputStream inputStream, long contentLength, String contentType, Date lastModified) {
		this.contentLength = contentLength;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.inputStream = inputStream;
		
		this.consumed = false;
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
		if (consumed){
			throw new IOException("The content has already consumed.");
		}

		consumed = true;
		return inputStream;
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException {
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

package org.archboy.clobaframe.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.ResourceContent;
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
	
	private boolean contentSnapshotCreated;

	public InputStreamResourceInfo(InputStream inputStream, long contentLength, String contentType, Date lastModified) {
		this.contentLength = contentLength;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.inputStream = inputStream;
		
		this.contentSnapshotCreated = false;
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
	public ResourceContent getContentSnapshot() throws IOException {
		if (contentSnapshotCreated){
			// this BlobInfo implementation only can be got content snapshot once
			throw new IOException("The content snapshot has already created.");
		}

		contentSnapshotCreated = true;
		return new DefaultResourceContent(inputStream, contentLength);
	}

	@Override
	public ResourceContent getContentSnapshot(long start, long length) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isContentSeekable() {
		return false;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}
	
}

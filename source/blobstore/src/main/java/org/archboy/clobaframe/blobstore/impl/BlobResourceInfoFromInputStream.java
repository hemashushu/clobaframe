package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author yang
 *
 */
public class BlobResourceInfoFromInputStream extends AbstractBlobResourceInfo {

	private String repositoryName;
	private String key;
	private long contentLength;
	private String mimeType;
	private Date lastModified;
	private InputStream inputStream;
	
	private Map<String, Object> metadata;

	private boolean consumed;

	public BlobResourceInfoFromInputStream(
			String repositoryName, String key,
			InputStream inputStream, long contentLength,
			String mimeType, Date lastModified, Map<String, Object> metadata) {
		this.repositoryName = repositoryName;
		this.key = key;
		this.inputStream = inputStream;
		this.contentLength = contentLength;
		this.mimeType = mimeType;
		this.lastModified = lastModified;
		this.metadata = metadata;
	}

	@Override
	public String getRepositoryName() {
		return repositoryName;
	}

	@Override
	public String getKey() {
		return key;
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
	public Date getLastModified() {
		return lastModified;
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
	public InputStream getContent(long start, long length) throws IOException{
		throw new UnsupportedOperationException("Does not supported.");
	}

	@Override
	public boolean isSeekable() {
		return false;
	}

	@Override
	public Map<String, Object> getMetadata() {
		return metadata;
	}
}

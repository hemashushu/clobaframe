package org.archboy.clobaframe.blobstore.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;

/**
 *
 * @author yang
 */
public class BlobResourceInfoFromByteArray extends AbstractBlobResourceInfo{

	private String repositoryName;
	private String key;
	private byte[] content;
	private String mimeType;
	private Date lastModified;
	private Map<String, Object> metadata;

	public BlobResourceInfoFromByteArray(String repositoryName, String key,
			byte[] content, String mimeType, Date lastModified, Map<String, Object> metadata) {
		this.repositoryName = repositoryName;
		this.key = key;
		this.content = content;
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
		return content.length;
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
	public InputStream getContent() throws IOException{
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
	public Map<String, Object> getMetadata() {
		return metadata;
	}
}

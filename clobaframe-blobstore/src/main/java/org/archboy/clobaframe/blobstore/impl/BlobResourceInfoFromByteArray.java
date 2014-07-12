package org.archboy.clobaframe.blobstore.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobKey;

/**
 *
 * @author yang
 */
public class BlobResourceInfoFromByteArray implements BlobResourceInfo{

	private BlobKey blobKey;

	private String contentType;
	private Date lastModified;
	private byte[] content;

	private Map<String, String> metadata;

	public BlobResourceInfoFromByteArray(BlobKey blobKey, String contentType,
			Date lastModified, byte[] content) {
		this.blobKey = blobKey;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.content = content;
		this.metadata = new HashMap<String, String>();
	}

	@Override
	public BlobKey getBlobKey() {
		return blobKey;
	}

	@Override
	public long getContentLength() {
		return content.length;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public InputStream getInputStream() throws IOException{
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
	public Map<String, String> getMetadata() {
		return metadata;
	}

	@Override
	public void addMetadata(String key, String value) {
		metadata.put(key, value);
	}

}

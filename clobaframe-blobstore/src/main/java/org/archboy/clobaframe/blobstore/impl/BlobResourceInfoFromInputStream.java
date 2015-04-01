package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;

/**
 *
 * @author yang
 *
 */
public class BlobResourceInfoFromInputStream extends AbstractBlobResourceInfo {

	private String bucketName;
	private String key;
	private long contentLength;
	private String mimeType;
	private Date lastModified;
	private InputStream inputStream;
	
	private Map<String, Object> metadata;

	private boolean consumed;

	public BlobResourceInfoFromInputStream(
			String bucketName, String key,
			InputStream inputStream, long contentLength,
			String mimeType, Date lastModified, Map<String, Object> metadata) {
		this.bucketName = bucketName;
		this.key = key;
		this.inputStream = inputStream;
		this.contentLength = contentLength;
		this.mimeType = mimeType;
		this.lastModified = lastModified;
		this.metadata = metadata;
	}

	@Override
	public String getBucketName() {
		return bucketName;
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

//	@Override
//	public void setMetadata(Map<String, Object> metadata) {
//		this.metadata = metadata;
//	}
//	
//	@Override
//	public void addMetadata(String key, Object value) {
//		if (metadata == null){
//			metadata = new HashMap<String, Object>();
//		}
//		
//		metadata.put(key, value);
//	}
}

package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 *
 */
public class BlobResourceInfoFromResourceInfo extends AbstractBlobResourceInfo {

	private String repositoryName;
	private String key;
	private ResourceInfo resourceInfo;
	private Map<String, Object> metadata;

	private boolean consumed;

	public BlobResourceInfoFromResourceInfo(
			String repositoryName, String key,
			ResourceInfo resourceInfo, Map<String, Object> metadata) {
		this.repositoryName = repositoryName;
		this.key = key;
		this.resourceInfo = resourceInfo;
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
		return resourceInfo.getContentLength();
	}

	@Override
	public String getMimeType() {
		return resourceInfo.getMimeType();
	}

	@Override
	public Date getLastModified() {
		return resourceInfo.getLastModified();
	}

	@Override
	public InputStream getContent() throws IOException {
		return resourceInfo.getContent();
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException{
		return resourceInfo.getContent(start, length);
	}

	@Override
	public boolean isSeekable() {
		return resourceInfo.isSeekable();
	}

	@Override
	public Map<String, Object> getMetadata() {
		return metadata;
	}

}

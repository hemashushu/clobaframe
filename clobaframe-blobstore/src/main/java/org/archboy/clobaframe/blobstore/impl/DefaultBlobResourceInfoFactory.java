package org.archboy.clobaframe.blobstore.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoFactory;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 *
 */
public class DefaultBlobResourceInfoFactory implements BlobResourceInfoFactory {

	@Override
	public BlobResourceInfo make(String repositoryName, String key,
			InputStream inputStream, long contentLength, 
			String mimeType, Date lastModified,
			Map<String, Object> metadata) {
		Assert.notNull(repositoryName);
		Assert.notNull(key);
		Assert.notNull(inputStream);
		Assert.isTrue(contentLength >0);
		Assert.hasText(mimeType);
		//Assert.notNull(lastModified);
		
		return new BlobResourceInfoFromInputStream(repositoryName, key, 
				inputStream, contentLength, mimeType, lastModified,
				metadata);
	}

	@Override
	public BlobResourceInfo make(String repositoryName, String key,
			byte[] content, String mimeType, Date lastModified,
			Map<String, Object> metadata) {
		Assert.notNull(repositoryName);
		Assert.notNull(key);
		Assert.notNull(content);
		Assert.hasText(mimeType);
		//Assert.notNull(lastModified);
		
		return new BlobResourceInfoFromByteArray(repositoryName, key, 
				content, mimeType, lastModified,
				metadata);
	}

}

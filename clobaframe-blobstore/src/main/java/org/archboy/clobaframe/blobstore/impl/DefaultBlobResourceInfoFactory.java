package org.archboy.clobaframe.blobstore.impl;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoFactory;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 *
 */
public class DefaultBlobResourceInfoFactory implements BlobResourceInfoFactory {

	@Override
	public BlobResourceInfo make(BlobKey blobKey,
			String mimeType, InputStream content, long size) {
		Assert.notNull(blobKey);
		Assert.hasText(mimeType);
		Assert.notNull(content);
		Assert.isTrue(size >0);
		
		Date lastModified = new Date();
		return new BlobResourceInfoFromInputStream(blobKey, size, mimeType,
				lastModified, content);
	}

	@Override
	public BlobResourceInfo make(BlobKey blobKey, String mimeType, byte[] content) {
		Assert.notNull(blobKey);
		Assert.hasText(mimeType);
		Assert.notNull(content);
		
		Date lastModified = new Date();
		return new BlobResourceInfoFromByteArray(blobKey, mimeType, 
				lastModified, content);
	}

}

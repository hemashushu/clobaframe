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
@Named
public class BlobResourceInfoFactoryImpl implements BlobResourceInfoFactory {

	@Override
	public BlobResourceInfo make(BlobKey blobKey,
			String contentType, InputStream content, long size) {
		Assert.notNull(blobKey);
		Assert.hasText(contentType);
		Assert.notNull(content);
		Assert.isTrue(size >0);
		
		Date lastModified = new Date();
		return new BlobResourceInfoFromInputStream(blobKey, size, contentType,
				lastModified, content);
	}

	@Override
	public BlobResourceInfo make(BlobKey blobKey, String contentType, byte[] content) {
		Assert.notNull(blobKey);
		Assert.hasText(contentType);
		Assert.notNull(content);
		
		Date lastModified = new Date();
		return new BlobResourceInfoFromByteArray(blobKey, contentType, lastModified, content);
	}

}

package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceRepository;

/**
 *
 * @author yang
 */
public abstract class AbstractBlobResourceRepository implements BlobResourceRepository {

	@Override
	public void put(BlobResourceInfo blobResourceInfo) throws IOException {
		put(blobResourceInfo, true, PRIORITY_DEFAULT);
	}
	
}

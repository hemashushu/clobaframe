package org.archboy.clobaframe.blobstore.impl;

import java.util.ArrayList;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoPartialCollection;

/**
 *
 * @author yang
 */
public abstract class AbstractBlobResourceInfoPartialCollection
		extends ArrayList<BlobResourceInfo>
		implements BlobResourceInfoPartialCollection {

	private boolean hasMore = false;

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	@Override
	public boolean hasMore() {
		return hasMore;
	}
}

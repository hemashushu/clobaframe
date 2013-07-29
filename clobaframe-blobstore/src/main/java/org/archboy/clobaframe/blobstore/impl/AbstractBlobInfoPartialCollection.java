package org.archboy.clobaframe.blobstore.impl;

import java.util.ArrayList;
import org.archboy.clobaframe.blobstore.BlobInfo;
import org.archboy.clobaframe.blobstore.BlobInfoPartialCollection;

/**
 *
 * @author arch
 */
public abstract class AbstractBlobInfoPartialCollection
		extends ArrayList<BlobInfo>
		implements BlobInfoPartialCollection {

	private boolean hasMore = false;

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	@Override
	public boolean hasMore() {
		return hasMore;
	}
}

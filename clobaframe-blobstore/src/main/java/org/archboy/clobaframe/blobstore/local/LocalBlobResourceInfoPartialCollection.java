package org.archboy.clobaframe.blobstore.local;

import org.archboy.clobaframe.blobstore.impl.AbstractBlobResourceInfoPartialCollection;

/**
 *
 * @author yang
 */
public class LocalBlobResourceInfoPartialCollection extends AbstractBlobResourceInfoPartialCollection{

	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasMore() {
		return false;
	}
}

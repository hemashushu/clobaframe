package org.archboy.clobaframe.blobstore.local;

import org.archboy.clobaframe.blobstore.impl.AbstractBlobInfoPartialCollection;

/**
 *
 * @author arch
 */
public class LocalBlobInfoPartialCollection extends AbstractBlobInfoPartialCollection{

	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasMore() {
		return false;
	}
}

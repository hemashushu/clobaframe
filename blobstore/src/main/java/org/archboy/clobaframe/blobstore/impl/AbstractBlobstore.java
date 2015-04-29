package org.archboy.clobaframe.blobstore.impl;

import org.archboy.clobaframe.blobstore.Blobstore;

/**
 *
 * @author yang
 */
public abstract class AbstractBlobstore implements Blobstore {
	
	/**
	 * The implementation name.
	 * @return 
	 */
	public abstract String getName();
}

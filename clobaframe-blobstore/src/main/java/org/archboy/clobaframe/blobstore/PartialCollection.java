package org.archboy.clobaframe.blobstore;

import java.util.Collection;

/**
 * Partial connection.
 * <p>
 *     Because the quantity of the blob objects in a bucket maybe huge,
 *     so the caller should acquire the list in many times.
 * </p>
 *
 * @author yang
 *
 * @param <E>
 */
public interface PartialCollection<E> extends Collection<E> {

	boolean hasMore();
}

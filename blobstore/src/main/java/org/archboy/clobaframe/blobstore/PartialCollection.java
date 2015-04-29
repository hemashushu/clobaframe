package org.archboy.clobaframe.blobstore;

import java.util.Collection;

/**
 * Partial connection.
 * Because the amount of the blob objects in a repository maybe huge,
 * so the caller should fetch the list in many times.
 *
 * @author yang
 *
 * @param <E>
 */
public interface PartialCollection<E> extends Collection<E> {

	boolean hasMore();
}

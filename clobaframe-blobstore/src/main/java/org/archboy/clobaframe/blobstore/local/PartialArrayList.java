package org.archboy.clobaframe.blobstore.local;

import java.util.ArrayList;
import java.util.Collection;
import org.archboy.clobaframe.blobstore.PartialCollection;

/**
 *
 * @author yang
 * @param <T>
 */
public class PartialArrayList<T>
		extends ArrayList<T>
		implements PartialCollection<T> {

	private static final long serialVersionUID = 1L;
	
	private boolean hasMore;

	public PartialArrayList(Collection<? extends T> c, boolean hasMore) {
		super(c);
		this.hasMore = hasMore;
	}

	@Override
	public boolean hasMore() {
		return hasMore;
	}
}

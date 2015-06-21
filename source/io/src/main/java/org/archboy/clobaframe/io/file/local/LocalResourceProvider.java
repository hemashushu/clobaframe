package org.archboy.clobaframe.io.file.local;

import java.util.Collection;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

/**
 *
 * @author yang
 */
public interface LocalResourceProvider {

	/**
	 * 
	 * @param name
	 * @return NULL when file not found.
	 */
	FileBaseResourceInfo getByName(String name);

	Collection<FileBaseResourceInfo> getAll();
}

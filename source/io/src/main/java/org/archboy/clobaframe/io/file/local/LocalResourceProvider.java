package org.archboy.clobaframe.io.file.local;

import java.util.Collection;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

/**
 *
 * @author yang
 */
public interface LocalResourceProvider {
	
	FileBaseResourceInfo getByName(String name);

	Collection<FileBaseResourceInfo> getAll();
}

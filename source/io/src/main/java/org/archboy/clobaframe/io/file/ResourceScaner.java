package org.archboy.clobaframe.io.file;

import java.io.File;
import java.util.Collection;

/**
 *
 * @author yang
 */
public interface ResourceScaner {
	
	Collection<FileBaseResourceInfo> list(
			File basePath, File file, 
			ResourceNameStrategy resourceNameStrategy);
}

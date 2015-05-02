package org.archboy.clobaframe.io.file;

import java.io.File;
import java.util.Collection;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface ResourceScanner {
	
	/**
	 * 
	 * @param basePath
	 * @param fileBaseResourceInfoFactory
	 * @return 
	 */
	Collection<ResourceInfo> scan(
			File basePath,
			FileBaseResourceInfoFactory fileBaseResourceInfoFactory);
}

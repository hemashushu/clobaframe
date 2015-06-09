package org.archboy.clobaframe.io.file.local;

import java.io.File;
import java.util.Collection;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;

/**
 *
 * @author yang
 */
public interface LocalResourceScanner {
	
	/**
	 * 
	 * @param basePath
	 * @param fileBaseResourceInfoFactory
	 * @return 
	 */
	Collection<FileBaseResourceInfo> scan(
			File basePath,
			FileBaseResourceInfoFactory fileBaseResourceInfoFactory);
}

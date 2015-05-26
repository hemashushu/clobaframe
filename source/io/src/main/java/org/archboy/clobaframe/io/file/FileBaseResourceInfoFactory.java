package org.archboy.clobaframe.io.file;

import java.io.File;

/**
 * Use by {@link ResourceScanner}.
 * 
 * @author yang
 */
public interface FileBaseResourceInfoFactory {
	
	FileBaseResourceInfo make(File file);
	
}

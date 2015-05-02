package org.archboy.clobaframe.io.file;

import java.io.File;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * Use by {@link ResourceScanner}.
 * 
 * @author yang
 */
public interface FileBaseResourceInfoFactory {
	
	FileBaseResourceInfo make(File file);
	
}

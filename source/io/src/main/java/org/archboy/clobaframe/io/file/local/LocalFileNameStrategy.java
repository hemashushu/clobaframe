package org.archboy.clobaframe.io.file.local;

import java.io.File;

/**
 * Get file by name.
 * 
 * @author yang
 */
public interface LocalFileNameStrategy {
	
	File getFile(String name);
	
	//String getName(File file);
}

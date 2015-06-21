package org.archboy.clobaframe.io.file.local;

import java.io.File;

/**
 * Get file by name.
 * 
 * @author yang
 */
public interface LocalFileNameStrategy {
	
	/**
	 * 
	 * @param name
	 * @return 
	 * @throws IllegalArgumentException When the name arg is illegal or 
	 * can not get a file.
	 */
	File getFile(String name) throws IllegalArgumentException;
	
}

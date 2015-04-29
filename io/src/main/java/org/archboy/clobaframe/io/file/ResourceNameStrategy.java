package org.archboy.clobaframe.io.file;

import java.io.File;

/**
 *
 * @author yang
 */
public interface ResourceNameStrategy {
	
	String getName(File basePath, File file);
	
}

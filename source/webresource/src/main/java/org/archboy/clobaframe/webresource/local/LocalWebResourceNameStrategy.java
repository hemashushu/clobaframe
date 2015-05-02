package org.archboy.clobaframe.webresource.local;

import java.io.File;

/**
 * Generate the resource name.
 * 
 * @author yang
 */
public interface LocalWebResourceNameStrategy {
	
	String getName(File file);
	
}

package org.archboy.clobaframe.webresource.local;

import java.io.File;
import org.archboy.clobaframe.io.file.local.LocalFileNameStrategy;

/**
 * Generate the resource name.
 * 
 * @author yang
 */
public interface LocalWebResourceNameStrategy extends LocalFileNameStrategy {
	
	String getName(File file);
	
}

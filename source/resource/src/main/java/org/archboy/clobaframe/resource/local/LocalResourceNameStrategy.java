package org.archboy.clobaframe.resource.local;

import java.io.File;
import org.archboy.clobaframe.io.file.local.LocalFileNameStrategy;

/**
 * Generate the resource name.
 * 
 * @author yang
 */
public interface LocalResourceNameStrategy extends LocalFileNameStrategy {
	
	String getName(File file);
	
}

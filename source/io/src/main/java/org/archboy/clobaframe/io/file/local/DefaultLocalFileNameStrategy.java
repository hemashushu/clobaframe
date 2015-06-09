package org.archboy.clobaframe.io.file.local;

import java.io.File;

/**
 *
 * @author yang
 */
public class DefaultLocalFileNameStrategy implements LocalFileNameStrategy {

	protected File basePath;

	public DefaultLocalFileNameStrategy(File basePath) {
		this.basePath = basePath;
	}
	
	@Override
	public File getFile(String name) {
		return new File(basePath, name);
	}
	
}

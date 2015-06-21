package org.archboy.clobaframe.io.file.local;

import java.io.File;
import org.springframework.util.Assert;

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
		Assert.hasText(name);
		return new File(basePath, name);
	}
	
}

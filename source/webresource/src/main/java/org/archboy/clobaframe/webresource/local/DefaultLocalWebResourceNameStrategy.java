package org.archboy.clobaframe.webresource.local;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.file.local.DefaultLocalFileNameStrategy;
import org.archboy.clobaframe.webresource.local.LocalWebResourceNameStrategy;
import org.springframework.util.Assert;

/**
 * Return resource name by file relate to the base path.
 * 
 * By default, the resource name is the file name that excludes the base folder path.
 * 
 * e.g. base folder is "/var/lib/clobaframe", then the file
 * "/var/lib/clobaframe/common.css" gets name "common.css",
 * and
 * "/var/lib/js/moments.js" gets name "js/moments.js".
 * 
 * @author yang
 */
public class DefaultLocalWebResourceNameStrategy extends DefaultLocalFileNameStrategy implements LocalWebResourceNameStrategy {
	
	private String namePrefix;
	
	private int basePathLength;
	private int namePrefixLength;
	
	public DefaultLocalWebResourceNameStrategy(File basePath, String namePrefix) {
		super(basePath);
		// the base path length plus 1 to exclude the resource 
		// file name path '/' prefix character.
		this.basePathLength = basePath.getPath().length() + 1; 
		
		this.namePrefix = namePrefix == null ? StringUtils.EMPTY : namePrefix;
		this.namePrefixLength = namePrefix.length();
	}

	@Override
	public String getName(File file) {
		String name = file.getPath().substring(basePathLength);
		return namePrefix + name.replace('\\', '/');
	}

	@Override
	public File getFile(String name) {
		Assert.hasText(name);
		Assert.isTrue(name.length() > namePrefixLength);
		String baseName = name.substring(namePrefixLength);
		return super.getFile(baseName);
	}
	
}

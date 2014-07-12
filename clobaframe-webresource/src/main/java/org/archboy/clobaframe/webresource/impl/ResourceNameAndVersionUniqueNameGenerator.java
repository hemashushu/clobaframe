package org.archboy.clobaframe.webresource.impl;

import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.UniqueNameGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class ResourceNameAndVersionUniqueNameGenerator implements UniqueNameGenerator {

	@Override
	public String getName() {
		return "version";
	}

	/**
	 * resource name + "-" + last modified + ".extend-name".
	 * 
	 * @param webResourceInfo
	 * @return 
	 */
	@Override
	public String getUniqueName(WebResourceInfo webResourceInfo) {
		String name = webResourceInfo.getName();
		String extendNameWithDot = null;
		
		int pos = name.lastIndexOf('.');
		if (pos > 0) {
			extendNameWithDot = name.substring(pos);
			name = name.substring(0, pos);
		}
		
		long lastModified = webResourceInfo.getLastModified().getTime();
		
		return name + "-" + lastModified/1000 + extendNameWithDot;
	}
}

package org.archboy.clobaframe.resource.impl;

import javax.inject.Named;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.resource.VersionStrategy;
import org.archboy.clobaframe.resource.ContentHashResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class NullVersionStrategy implements VersionStrategy {

	@Override
	public String getName() {
		return "null";
	}
	
	@Override
	public String getVersionName(NamedResourceInfo webResourceInfo) {
		return webResourceInfo.getName();
	}

	@Override
	public String revert(String versionName) {
		return versionName;
	}
}

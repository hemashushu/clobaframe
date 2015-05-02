package org.archboy.clobaframe.webresource.impl;

import javax.inject.Named;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;

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
	public String getVersionName(WebResourceInfo webResourceInfo) {
		return webResourceInfo.getName();
	}

	@Override
	public String revert(String versionName) {
		return versionName;
	}
}

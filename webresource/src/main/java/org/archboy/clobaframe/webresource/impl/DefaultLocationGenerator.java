package org.archboy.clobaframe.webresource.impl;

import org.archboy.clobaframe.webresource.LocationGenerator;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
public class DefaultLocationGenerator implements LocationGenerator {

	private VersionStrategy versionStrategy;
	private String baseLocation;

	public DefaultLocationGenerator(VersionStrategy versionStrategy, String baseLocation) {
		this.versionStrategy = versionStrategy;
		this.baseLocation = baseLocation;
	}
	
	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		String versionName = versionStrategy.getVersionName(webResourceInfo);
		return baseLocation + versionName;
	}
	
}

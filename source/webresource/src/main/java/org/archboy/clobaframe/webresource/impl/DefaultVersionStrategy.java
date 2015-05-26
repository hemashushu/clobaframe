package org.archboy.clobaframe.webresource.impl;

import javax.inject.Named;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 * The default version strategy.
 * 
 * Format:
 * resource name + "?" + "v" + left(content hash, 8).
 * 
 * @author yang
 */
@Named
public class DefaultVersionStrategy implements VersionStrategy {
	
	@Override
	public String getName() {
		return "default";
	}

	@Override
	public String getVersionName(WebResourceInfo webResourceInfo) {
		String name = webResourceInfo.getName();
		String contentHash = webResourceInfo.getContentHash();
		String shortContentHash = "v" + contentHash.substring(0, 8); // take the first 8 characters
		return name + "?" + shortContentHash;
	}

	@Override
	public String revert(String versionName) {
		int queryPos = versionName.indexOf('?');
		return (queryPos > 0 ? versionName.substring(0, queryPos) : versionName);
	}
}

package org.archboy.clobaframe.webresource.impl;

import javax.inject.Named;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.ContentHashResourceInfo;
import org.springframework.util.Assert;

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
	public String getVersionName(NamedResourceInfo resourceInfo) {
		Assert.isInstanceOf(ContentHashResourceInfo.class, resourceInfo, 
				"Default version strategy only supports ContentHashResourceInfo instance currently.");
		
		String name = resourceInfo.getName();
		String contentHash = ((ContentHashResourceInfo)resourceInfo).getContentHash();
		String shortContentHash = "v" + contentHash.substring(0, 8); // take the first 8 characters
		return name + "?" + shortContentHash;
	}

	@Override
	public String revert(String versionName) {
		int queryPos = versionName.indexOf('?');
		return (queryPos > 0 ? versionName.substring(0, queryPos) : versionName);
	}
}

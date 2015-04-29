package org.archboy.clobaframe.webresource.impl;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.AbstractVersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
@Named
public class DefaultVersionStrategy extends AbstractVersionStrategy {
	
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

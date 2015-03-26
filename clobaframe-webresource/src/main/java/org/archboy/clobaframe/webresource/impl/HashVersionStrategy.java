package org.archboy.clobaframe.webresource.impl;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.AbstractVersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class HashVersionStrategy extends AbstractVersionStrategy {

	private Map<String, String> hashs = new HashMap<String, String>();
	
	@Override
	public String getName() {
		return "hash";
	}

	@Override
	public String getVersionName(WebResourceInfo webResourceInfo) {
		String hash = webResourceInfo.getContentHash();
		if (!hashs.containsKey(hash)) {
			hashs.put(hash, webResourceInfo.getName());
		}
		return hash;
	}

	@Override
	public String revert(String versionName) {
		return hashs.get(versionName);
	}
	
}

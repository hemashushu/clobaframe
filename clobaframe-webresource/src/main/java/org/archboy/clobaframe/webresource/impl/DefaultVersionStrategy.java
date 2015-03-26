package org.archboy.clobaframe.webresource.impl;

import javax.inject.Named;
import org.archboy.clobaframe.webresource.AbstractVersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;

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
		String shortContentHash = contentHash.substring(0, 8); // take the first 8 characters
		
		/**
		 * The name maybe includes the query and url hash.
		 * E.g.
		 * 
		 * webfont.eot?v=4.2.0
		 * webfont.eot?#iefix
		 * webfont.eot?#iefix&v=4.2.0
		 * webfont.eot?v=4.2.0#iefix
		 * 
		 */
		StringBuilder builder = new StringBuilder();
		
		int queryPos = name.lastIndexOf('?');
		if (queryPos == -1) {
			//name += "?" + shortHash;
			builder.append(name);
			builder.append("?");
			builder.append(shortContentHash);
		}else{
			if (queryPos == name.length() - 1){
				//name += shortHash;
				builder.append(name);
				builder.append(shortContentHash);
			}else{
				if (name.charAt(queryPos + 1) == '#') {
					//name = name.substring(0, queryPos + 1) + shortHash + name.substring(queryPos + 1);
					builder.append(name.substring(0, queryPos + 1));
					builder.append(shortContentHash);
					builder.append(name.substring(queryPos + 1));
				}else{
					//name = name.substring(0, queryPos + 1) + shortHash + "&" + name.substring(queryPos + 1);
					builder.append(name.substring(0, queryPos + 1));
					builder.append(shortContentHash);
					builder.append("&");
					builder.append(name.substring(queryPos + 1));
				}
			}
		}
		
		return builder.toString();
	}

	@Override
	public String revert(String versionName) {
		int queryPos = versionName.indexOf('?');
		return versionName.substring(0, queryPos);
	}
}

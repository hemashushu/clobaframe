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

	@Value("${clobaframe.webresource.baseLocation}")
	private String baseLocation;
	
	private String concatMark = "?";
	
	@Override
	public String getName() {
		return "default";
	}
	
	@PostConstruct
	public void init(){
		// the base location already has the query mark.
		if (baseLocation.indexOf('?') > 0) {
			concatMark = "&";
		}
	}

	@Override
	public String getVersionName(WebResourceInfo webResourceInfo) {
		String name = webResourceInfo.getName();
		String contentHash = webResourceInfo.getContentHash();
		String shortContentHash = "v" + contentHash.substring(0, 8); // take the first 8 characters
		
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
			builder.append(concatMark);
			builder.append(shortContentHash);
		}else{
			if (queryPos == name.length() - 1){
				//name += shortHash;
				builder.append(name.substring(0, queryPos));
				builder.append(concatMark);
				builder.append(shortContentHash);
			}else{
				if (name.charAt(queryPos + 1) == '#') {
					//name = name.substring(0, queryPos + 1) + shortHash + name.substring(queryPos + 1);
					builder.append(name.substring(0, queryPos));
					builder.append(concatMark);
					builder.append(shortContentHash);
					builder.append(name.substring(queryPos + 1));
				}else{
					//name = name.substring(0, queryPos + 1) + shortHash + "&" + name.substring(queryPos + 1);
					builder.append(name.substring(0, queryPos));
					builder.append(concatMark);
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
		int queryPos = versionName.lastIndexOf('?');
		return (queryPos > 0 ? versionName.substring(0, queryPos) : versionName);
	}
}

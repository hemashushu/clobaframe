package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface LocationStrategy {
	
	/**
	 * Get the strategy implementation name.
	 * 
	 * @return 
	 */
	String getName();
	
	/**
	 * 
	 * @param webResourceInfo
	 * @return 
	 */
	String getLocation(WebResourceInfo webResourceInfo);
	
	String getVersionName(WebResourceInfo webResourceInfo);
	
	String fromVersionName(String versionName);
}

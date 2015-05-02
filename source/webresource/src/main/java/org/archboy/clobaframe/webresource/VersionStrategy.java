package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface VersionStrategy {
	
	/**
	 * Get the strategy implementation name.
	 * 
	 * @return 
	 */
	String getName();
	
	/**
	 * Get the resource name combine with the version string.
	 * 
	 * @param webResourceInfo
	 * @return 
	 */
	String getVersionName(WebResourceInfo webResourceInfo);
	
	/**
	 * Revert version name into resource name.
	 * 
	 * @param versionName
	 * @return 
	 */
	String revert(String versionName);
	
}

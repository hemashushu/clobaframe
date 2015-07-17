package org.archboy.clobaframe.resource;

import org.archboy.clobaframe.io.NamedResourceInfo;

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
	 * @param resourceInfo
	 * @return 
	 */
	String getVersionName(NamedResourceInfo resourceInfo);
	
	/**
	 * Revert version name into resource name.
	 * 
	 * @param versionName
	 * @return 
	 */
	String revert(String versionName);
	
}

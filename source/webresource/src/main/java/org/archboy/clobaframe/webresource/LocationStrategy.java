package org.archboy.clobaframe.webresource;

import org.archboy.clobaframe.io.NamedResourceInfo;

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
	 * @param resourceInfo
	 * @return 
	 */
	String getLocation(NamedResourceInfo resourceInfo);
	
	/**
	 * 
	 * @param resourceInfo
	 * @return 
	 */
	String getVersionName(NamedResourceInfo resourceInfo);
	
	/**
	 * 
	 * @param versionName
	 * @return 
	 */
	String fromVersionName(String versionName);
}

package org.archboy.clobaframe.webresource;

import java.util.List;

/**
 *
 * @author yang
 */
public interface ResourceRepository {

	/**
	 * The repository implements name.
	 * 
	 * @return 
	 */
	String getName();
	
	List<WebResourceInfo> findAll();

	ResourceLocationGenerator getResourceLocationGenerator();
}

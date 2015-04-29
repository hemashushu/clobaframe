package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface WebResourceRepository extends WebResourceCollection {

	public static final int PRIORITY_HIGHEST = 0;
	public static final int PRIORITY_HIGHER = 2;
	public static final int PRIORITY_HIGH = 4;
	public static final int PRIORITY_NORMAL = 5;
	
	/**
	 * Get the priority of repository.
	 * The high priority repository will be check first when get a resource by
	 * name.
	 * I.e. when there are resources that with duplicate name, the resource
	 * resist in the high priority repository will be selected.
	 * 
	 * @return 
	 */
	int getPriority();
}

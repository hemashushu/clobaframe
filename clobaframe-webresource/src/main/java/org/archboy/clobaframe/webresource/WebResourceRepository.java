package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface WebResourceRepository extends WebResourceCollection {

	public static final int PRIORITY_TOP = 0;
	public static final int PRIORITY_LESS_THAN_TOP = 1;
	public static final int PRIORITY_DEFAULT = 5;
	
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

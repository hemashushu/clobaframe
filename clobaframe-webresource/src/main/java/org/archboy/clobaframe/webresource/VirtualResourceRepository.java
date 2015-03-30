package org.archboy.clobaframe.webresource;

import javax.inject.Named;

/**
 * The virtual resource repository.
 * 
 * The virtual resource means the resource that generate by dynamic,
 * such as the user custom style sheet that stored in the database.
 * 
 * @author yang
 */
@Named
public interface VirtualResourceRepository extends ResourceRepository {

	void addProvider(VirtualResourceProvider provider);
	
}

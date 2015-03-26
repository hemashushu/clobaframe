package org.archboy.clobaframe.webresource;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * The virtual resource repository.
 * 
 * The virtual resource means the resource that generate by dynamic,
 * such as the user custom style sheet.
 * 
 * @author yang
 */
@Named
public interface VirtualResourceRepository extends ResourceRepository {

	void addProvider(VirtualResourceProvider provider);
	
}

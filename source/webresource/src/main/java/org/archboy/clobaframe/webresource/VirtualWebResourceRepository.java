package org.archboy.clobaframe.webresource;

import java.util.Collection;
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
public interface VirtualWebResourceRepository extends WebResourceRepository {

	Collection<VirtualWebResourceProvider> getResourceProviders();
}

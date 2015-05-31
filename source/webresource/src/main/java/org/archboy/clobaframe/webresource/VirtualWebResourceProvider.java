package org.archboy.clobaframe.webresource;

/**
 * The virtual resource repository.
 * 
 * The virtual resource means the resource that generate by dynamic,
 * such as the user custom style sheet that stored in the database.
 * 
 * @author yang
 */
public interface VirtualWebResourceProvider extends WebResourceProvider {

	//Collection<VirtualWebResourceSource> getResourceProviders();
	
	/**
	 * Add source.
	 * @param virtualWebResourceSource 
	 */
	void addSource(VirtualWebResourceSource virtualWebResourceSource);
	
	/**
	 * Remove source.
	 * @param sourceName 
	 */
	void removeSource(String sourceName);
}

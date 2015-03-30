package org.archboy.clobaframe.webresource;

/**
 * By using the listener pattern to perform the chain update.
 * 
 * The update listener commonly is the parent resource.
 * 
 * E.g. "common.css" import the "main.css" by the "@import url('main.css')", 
 * then the "common.css" resource is the parent resource / update listener, 
 * and the "main.css" is the child resource
 * 
 * @author yang
 */
public interface CacheableResourceUpdateListener {
	
	/**
	 * Occur while the child resource has been modified.
	 * 
	 * @param childResourceName 
	 */
	void onUpdate(String childResourceName);
}

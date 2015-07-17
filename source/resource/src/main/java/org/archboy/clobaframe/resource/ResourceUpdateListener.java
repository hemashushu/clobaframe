package org.archboy.clobaframe.resource;

/**
 * By using the listener pattern to perform the chain update.
 * 
 * The update listener is commonly the parent resource.
 * 
 * E.g. "common.css" import the "main.css" by the "@import url('main.css')", 
 * then the "common.css" resource can be the parent resource(/update listener), 
 * and the "main.css" is the child resource
 * 
 * @author yang
 */
public interface ResourceUpdateListener {
	
	/**
	 * Occur while the child resource has been modified.
	 * 
	 * @param childResourceName 
	 */
	void onUpdate(String childResourceName);
}

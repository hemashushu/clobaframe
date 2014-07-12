package org.archboy.clobaframe.webresource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Send the web resource to the user's browser.
 *
 * @author yang
 */
public interface WebResourceSender {

	/**
	 * Send resource by resource name.
	 * <p>
	 *     Resource name excludes path name, so the web resource file name
	 *     can not duplicate, event if they are place in different folders.
	 * </p>
	 *
	 * @param resourceName
	 * @param request
	 * @param response
	 * @throws IOException The exception occurs while the specify resource not found.
	 */
	void send(String resourceName, 
			HttpServletRequest request,
			HttpServletResponse response) throws IOException;

	/**
	 * Send resource by unique name.
	 *
	 * @param resourceUniqueName
	 * @param request
	 * @param response
	 * @throws IOException The exception occurs while the specify resource not found.
	 */
	void sendByUniqueName(String resourceUniqueName,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException;
}

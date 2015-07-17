package org.archboy.clobaframe.webresource.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Send the web resource to the user's browser.
 *
 * @author yang
 */
public interface NamedResourceSender {

	/**
	 * Send resource by resource name.
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
	 * Send resource by version name.
	 *
	 * @param versionName
	 * @param request
	 * @param response
	 * @throws IOException The exception occurs while the specify resource not found.
	 */
	void sendByVersionName(String versionName,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException;

}

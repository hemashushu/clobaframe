package org.archboy.clobaframe.io.http;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * Send resource to the user's browser.
 *
 * @author yang
 *
 */
public interface ResourceSender {

	/**
	 * Send resource.
	 *
	 * @param resourceInfo
	 * @param extraHeaders
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	void send(ResourceInfo resourceInfo, 
			Map<String, Object> extraHeaders,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException;

}

package org.archboy.clobaframe.io.http;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * Transfer resource content to the user's browser.
 *
 * @author yang
 *
 */
public interface ResourceSender {

	/**
	 * Send resource.
	 *
	 * @param resourceInfo
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	void send(ResourceInfo resourceInfo, HttpServletRequest request,
			HttpServletResponse response) throws IOException;
}

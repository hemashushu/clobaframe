package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.http.ResourceSender;

/**
 * Check the 'If-Modified-Since' and 'Range' HTTP headers of request
 * before sending resource.
 * 
 * The 'Last-Modified' HTTP header will be sent.
 *
 * @author yang
 *
 */
public class LastModifiedCheckingResourceSender implements ResourceSender {

	private ResourceSender resourceSender;

	public LastModifiedCheckingResourceSender(ResourceSender resourceSender) {
		this.resourceSender = resourceSender;
	}

	@Override
	public void send(ResourceInfo resourceInfo, Map<String, String> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		long lastModifiedTime = 0;

		// send last-modified header.
		if (resourceInfo.getLastModified() != null){
			lastModifiedTime = resourceInfo.getLastModified().getTime();
			response.setDateHeader("Last-Modified", lastModifiedTime);
		}

		// check 'if-modified-since' request
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		
		if (ifModifiedSince > 0 && lastModifiedTime > 0){
			if (lastModifiedTime / 1000 <= ifModifiedSince / 1000) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
		}
		
		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}
}

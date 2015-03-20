package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 *
 */
@Named
public class ResourceSenderImpl	implements ResourceSender {

	private static final boolean DEFAULT_ENABLE_GZIP = false;
	
	@Value("${clobaframe.io.http.gzip}")
	private boolean enableGzip = DEFAULT_ENABLE_GZIP;

	@Override
	public void send(ResourceInfo resourceInfo, Map<String, String> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ResourceSender resourceSender = new DefaultResourceSender();
		
		if (enableGzip) {
			resourceSender = new GZipResourceSender(resourceSender);
		}
		
		resourceSender = new PartialResourceSender(resourceSender);
		resourceSender = new LastModifiedCheckingResourceSender(resourceSender);
		
		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}
}

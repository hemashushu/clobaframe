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

	// only the content length large than this value would be compress
	private static final int DEFAULT_MIN_COMPRESS_SIZE = 1024;
	
	@Value("${clobaframe.io.http.gzip.minCompressSize}")
	private int minCompressSize = DEFAULT_MIN_COMPRESS_SIZE;
			
			
	@Override
	public void send(ResourceInfo resourceInfo, Map<String, Object> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ResourceSender resourceSender = new DefaultResourceSender();
		
		if (enableGzip) {
			resourceSender = new GZipResourceSender(resourceSender, minCompressSize);
		}
		
		resourceSender = new PartialResourceSender(resourceSender);
		resourceSender = new LastModifiedCheckingResourceSender(resourceSender);
		
		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}
}

package org.archboy.clobaframe.io.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.archboy.clobaframe.io.impl.DefaultResourceInfoFactory;

/**
 * Gzip Rules.
 * 
 * See also Jetty Gzip filter: http://eclipse.org/jetty/documentation/current/gzip-filter.html
 * 
 * 1. accept-encoding header is set to either gzip, deflate or a combination of those
 * 2. The content length is more than the minGzipSize initParameter or the minGzipSize is 0 
 * 3. The content-type is in the comma separated list of mimeTypes set in the mimeTypes 
 * 4. If both gzip and deflate are specified in the accept-encoding header, then gzip will be used.
 * 
 * @author yang
 */
public class GZipResourceSender implements ResourceSender {

	// only the content lenth more than this value would be compress
	private static final int DEFAULT_MIN_COMPRESS_SIZE = 1024;
	private int minCompressSize = DEFAULT_MIN_COMPRESS_SIZE;
	
	private ResourceSender resourceSender;
	
	private List<String> mimeTypes = Arrays.asList(
			"text/html",
			"text/plain",
			"text/xml",
			"application/xhtml+xml",
			"text/css",
			"application/javascript",
			"image/svg+xml");
	
	public GZipResourceSender(ResourceSender resourceSender) {
		this.resourceSender = resourceSender;
	}
	
	@Override
	public void send(ResourceInfo resourceInfo, Map<String, String> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String acceptEncoding = request.getHeader("Accept-Encoding");
		
		if (StringUtils.isNotEmpty(acceptEncoding) && acceptEncoding.contains("gzip")){
			if (minCompressSize == 0 || resourceInfo.getContentLength() >= minCompressSize) {
				if (mimeTypes.contains(resourceInfo.getMimeType())){
					if (response.getHeader("Content-Encoding") == null &&
							(extraHeaders == null || !extraHeaders.containsKey("Content-Encoding"))){
						
						System.out.println("send gzip");
						sendWithGZip(resourceInfo, extraHeaders, request, response);
						return;
					}
				}
			}
		}
		
		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}

	private void sendWithGZip(ResourceInfo resourceInfo, Map<String, String> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		// compress data
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		InputStream in = resourceInfo.getContent();
		
		IOUtils.copy(in, gzip);
		gzip.close();
		out.close();
		
		// write header
		response.addHeader("Content-Encoding", "gzip");
		
		byte[] data = out.toByteArray();
		
		ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();
		ResourceInfo gzipResourceInfo = resourceInfoFactory.make(data, resourceInfo.getMimeType(), resourceInfo.getLastModified());
		resourceSender.send(gzipResourceInfo, extraHeaders, request, response);
	}
}

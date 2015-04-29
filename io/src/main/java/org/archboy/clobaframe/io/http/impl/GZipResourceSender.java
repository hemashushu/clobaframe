package org.archboy.clobaframe.io.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
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
 * Http compress.
 * http://en.wikipedia.org/wiki/HTTP_compression
 * 
 * It can be enable by the web server (such Apache Http, Tomcat and Jetty ...) either.
 * http://betterexplained.com/articles/how-to-optimize-your-site-with-gzip-compression/
 * 
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

	private ResourceSender resourceSender;
	private Set<String> mimeTypeList;
	private int minCompressSize;
	
	private ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();
	
//	private List<String> mimeTypes = Arrays.asList(
//			"text/html",
//			"text/plain",
//			"text/xml",
//			"application/xhtml+xml",
//			"text/css",
//			"application/javascript",
//			"image/svg+xml");

	public GZipResourceSender(ResourceSender resourceSender, Set<String> mimeTypeList, int minCompressSize) {
		this.resourceSender = resourceSender;
		this.mimeTypeList = mimeTypeList;
		this.minCompressSize = minCompressSize;
	}
	
	@Override
	public void send(ResourceInfo resourceInfo, Map<String, Object> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String acceptEncoding = request.getHeader("Accept-Encoding");
		
		if (StringUtils.isNotEmpty(acceptEncoding) && acceptEncoding.contains("gzip")){
			if (minCompressSize == 0 || resourceInfo.getContentLength() >= minCompressSize) {
				if (mimeTypeList.contains(resourceInfo.getMimeType())){
					if (response.getHeader("Content-Encoding") == null &&
							(extraHeaders == null || !extraHeaders.containsKey("Content-Encoding"))){
						
						sendWithGZip(resourceInfo, extraHeaders, request, response);
						return;
					}
				}
			}
		}
		
		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}

	private void sendWithGZip(ResourceInfo resourceInfo, Map<String, Object> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
		
		ResourceInfo gzipResourceInfo = resourceInfoFactory.make(data, resourceInfo.getMimeType(), resourceInfo.getLastModified());
		resourceSender.send(gzipResourceInfo, extraHeaders, request, response);
	}
}

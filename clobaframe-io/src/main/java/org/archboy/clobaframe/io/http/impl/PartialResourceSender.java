package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.archboy.clobaframe.io.impl.DefaultResourceInfoFactory;

/**
 * Send whole or partial resource to client.
 * The 'Content-Type' and 'Content-Length' HTTP headers will be sent.
 *
 * @author yang
 *
 */
public class PartialResourceSender implements ResourceSender {

	private ResourceSender resourceSender;

	public PartialResourceSender(ResourceSender resourceSender) {
		this.resourceSender = resourceSender;
	}
	
	@Override
	public void send(ResourceInfo resourceInfo, Map<String, String> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String range = request.getHeader("Range");
		if (StringUtils.isNotEmpty(range) && resourceInfo.isSeekable()) {
			sendWithPartial(resourceInfo, extraHeaders, request, response, range);
			return;
		}
			
		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}

	private void sendWithPartial(
			ResourceInfo resourceInfo, Map<String, String> extraHeaders, HttpServletRequest request, HttpServletResponse response,
			String range) throws IOException {

		long originalContentLength = resourceInfo.getContentLength();
		
		RequestRange requestRange = new RequestRange(range, resourceInfo.getContentLength());
		long startPosition = requestRange.getStartPosition();
		long length = requestRange.getLength();
		

		if (startPosition < 0 || length < 0 || startPosition + length > originalContentLength) {
			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			return;
		}

		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		
		String contentRange = String.format("bytes %d-%d/%d",
				startPosition, // both start and end position are include.
				startPosition + length - 1, // both start and end position are include.
				originalContentLength);
		
		response.addHeader("Content-Range", contentRange);
		response.addHeader("Accept-Ranges", "bytes");

		
		ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();
		
		InputStream in = resourceInfo.getContent(startPosition, length);
		ResourceInfo partialResourceInfo = resourceInfoFactory.make(in, length, resourceInfo.getMimeType(), resourceInfo.getLastModified());
		
		resourceSender.send(partialResourceInfo, extraHeaders, request, response);
	}


	/**
	 * Parse HTTP header 'Range: bytes=xx-yy' into starting and ending value.
	 *
	 * The startPosition and endPosition are include.
	 * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
	 * @author yang
	 *
	 */
	public static class RequestRange {
		private long startPosition;
		private long endPosition;
		private long length; // the request length

		public RequestRange(long startPosition, long endPosition) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.length = endPosition - startPosition + 1;
		}

		public RequestRange(String range, long contentLength) {
			// set the default position first.
			this.startPosition = 0;
			this.endPosition = contentLength - 1;

			if (range.indexOf(',') >= 0) {
				throw new UnsupportedOperationException(
						"Does not support multipart/byterange");
			}

			int pos1 = range.indexOf("bytes=");
			if (pos1 == 0) {
				int equalsPos = "bytes=".length();
				int pos2 = range.indexOf("-", equalsPos);
				if (pos2 > equalsPos) {
					// maybe "100-" or "100-200"
					this.startPosition = Long.parseLong(range.substring(equalsPos,
							pos2));
					if (pos2 < range.length() - 1) {
						// must be "100-200"
						this.endPosition = Long
								.parseLong(range.substring(pos2 + 1));
					}
				} else if (pos2 == equalsPos) {
					// must be "-100"
					long tailingLength = Long.parseLong(range.substring(pos2 + 1));
					if (tailingLength < contentLength) {
						this.startPosition = contentLength - tailingLength;
					}
				}
			}

			this.length = endPosition - startPosition + 1;
		}

		public long getStartPosition() {
			return startPosition;
		}

		public long getEndPosition() {
			return endPosition;
		}

		public long getLength() {
			//The request length.
			return length;
		}

	}
}

package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 *
 * @author yang
 */
public class DefaultResourceSender implements ResourceSender {

	private static final int DEFAULT_SEND_BUFFER_SIZE = 32 * 1024;
	private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
	
	@Override
	public void send(
			ResourceInfo resourceInfo, Map<String, String> extraHeaders, 
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Assert.hasText(resourceInfo.getMimeType(), "Mime type should not empty.");

		response.setBufferSize(sendBufferSize);
		
		// The default value is http 200 OK.
		//response.setStatus(HttpServletResponse.SC_OK);
		
		response.setContentType(resourceInfo.getMimeType());
		
		// To override the #setContentLength(int).
		//response.setContentLength((int)resourceInfo.getContentLength());
		response.addHeader("Content-Length", Long.toString(resourceInfo.getContentLength()));
		
		response.setDateHeader("Date", new Date().getTime());

		if (StringUtils.isEmpty(response.getHeader("Accept-Ranges"))) {
			if (resourceInfo.isSeekable()) { 
				response.addHeader("Accept-Ranges", "bytes");
			}else{
				response.addHeader("Accept-Ranges", "none");
			}
		}
		
		// add other headers
		addExtraHeaders(response, extraHeaders);

		InputStream in = resourceInfo.getContent();

		OutputStream out = null;
		try {
			out = response.getOutputStream();
			sendData(in, out, resourceInfo.getContentLength());
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}
	
	private void sendData(
			InputStream in, OutputStream out, 
			long lengthToBeSent) throws IOException {

		long totalCompleted = 0;

		int readLength = sendBufferSize;
		byte[] buffer = new byte[readLength];

		while (totalCompleted < lengthToBeSent) {
			long avaiableLength = lengthToBeSent - totalCompleted;

			if (avaiableLength < readLength) {
				readLength = (int) avaiableLength;
			}

			int readBytes = in.read(buffer, 0, readLength);

			if (readBytes < 0) {
				throw new IOException("End of input stream");
			}

			out.write(buffer, 0, readBytes);
			out.flush();

			totalCompleted += readBytes;
		}
	}

	private void addExtraHeaders(
			HttpServletResponse response,
			Map<String, String> extraHeaders) {
		
		if (extraHeaders == null) {
			return;
		}

		for (String name : extraHeaders.keySet()) {
			response.addHeader(name, extraHeaders.get(name));
		}
	}
	
}

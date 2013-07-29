/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.webio.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;
import org.archboy.clobaframe.webio.ResourceContent;
import org.archboy.clobaframe.webio.ResourceInfo;

/**
 * Send whole or partial resource to client.
 * The 'Content-Type' and 'Content-Length' HTTP headers will be sent.
 *
 * @author young
 *
 */
public abstract class AbstractDataSender {

	private static final int SEND_BUFFER_SIZE = 32 * 1024;
	private int sendBufferSize = SEND_BUFFER_SIZE;

	public void sendData(HttpServletResponse response, ResourceInfo resourceInfo,
			Map<String, String> extraHeaders) throws IOException {

		Assert.hasText(resourceInfo.getContentType());

		response.setBufferSize(sendBufferSize);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(resourceInfo.getContentType());
		response.addHeader("Content-Length",
				new Long(resourceInfo.getContentLength()).toString());
		response.setDateHeader("Date", new Date().getTime());// Calendar.getInstance().getTimeInMillis());

		if (resourceInfo.isContentSeekable()) { // instanceof SeekableInputStream) {
			response.addHeader("Accept-Ranges", "bytes");
		} else {
			response.addHeader("Accept-Ranges", "none");
		}

		// add other headers
		addHeaders(response, extraHeaders);

		ResourceContent resourceContent = resourceInfo.getContentSnapshot();
		InputStream in = resourceContent.getInputStream();

		OutputStream out = null;
		try {
			out = response.getOutputStream();
			send(in, out, resourceInfo.getContentLength());
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(resourceContent);
		}
	}

	public void sendPartialData(HttpServletResponse response,
			ResourceInfo resourceInfo, Map<String, String> extraHeaders,
			long startPosition, long length) throws IOException {

		Assert.hasText(resourceInfo.getContentType());
		Assert.isTrue(resourceInfo.isContentSeekable());

//		if (!(in instanceof SeekableInputStream)){
//			// close resource content
//			IOUtils.closeQuietly(resourceContent);
//			throw new IllegalArgumentException("Resource must seekable.");
//		}

		long contentLength = resourceInfo.getContentLength();

		if (startPosition < 0 || length < 0 || startPosition + length > contentLength) {
			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

			// close resource content
			//IOUtils.closeQuietly(resourceContent);
			return;
		}

		response.setBufferSize(sendBufferSize);

		// startPosition and endPosition are include
		long lengthToBeSent = length; //endPosition - startPosition + 1;

		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		response.setContentType(resourceInfo.getContentType());
		response.addHeader("Content-Length",
				new Long(lengthToBeSent).toString());
		response.setDateHeader("Date", new Date().getTime()); // Calendar.getInstance().getTimeInMillis());

		response.addHeader("Accept-Ranges", "bytes");
		String contentRange = String.format("bytes %d-%d/%d",
				startPosition, // both start and end position are include.
				startPosition + length - 1, // both start and end position are include.
				contentLength);
		response.addHeader("Content-Range", contentRange);

		// add other headers
		addHeaders(response, extraHeaders);

		ResourceContent resourceContent = resourceInfo.getContentSnapshot(startPosition, length);
		InputStream in = resourceContent.getInputStream();

		OutputStream out = null;
		try {
			out = response.getOutputStream();

			// seek to the startup position
			//seekableIn = (SeekableInputStream) resourceContent.getInputStream();
			//seekableIn.position(startPosition);
			send(in, out, lengthToBeSent);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(resourceContent);
		}
	}

	private void send(InputStream in, OutputStream out, long lengthToBeSent)
			throws IOException {

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

	private void addHeaders(HttpServletResponse response,
			Map<String, String> extraHeaders) {
		if (extraHeaders == null) {
			return;
		}

		for (String name : extraHeaders.keySet()) {
			response.addHeader(name, extraHeaders.get(name));
		}
	}

}

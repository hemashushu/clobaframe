package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * Check the 'If-Modified-Since' and 'Range' HTTP headers of request
 * before sending resource.
 * 
 * The 'Last-Modified' HTTP header will be sent.
 *
 * @author yang
 *
 */
public abstract class AbstractDataSenderWithLastModifiedAndRangeCheck extends
		AbstractDataSender {

	public void sendDataWithLastModifiedAndRangeCheck(
			HttpServletResponse response, ResourceInfo resourceInfo,
			Map<String, String> extraHeaders, long ifModifiedSince, String range)
			throws IOException {

		long lastModifiedTime = 0;

		// send last-modified header.
		if (resourceInfo.getLastModified() != null){
			lastModifiedTime = resourceInfo.getLastModified().getTime();
			response.setDateHeader("Last-Modified", lastModifiedTime);
		}

		// check 'if-modified-since' request
		if (ifModifiedSince > 0 && lastModifiedTime>0){
			if (lastModifiedTime / 1000 <= ifModifiedSince / 1000) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
		}

		// check 'range' request
		if (StringUtils.isNotEmpty(range) &&
			resourceInfo.isSeekable()) {

			RequestRange requestRange = new RequestRange(range,
					resourceInfo.getContentLength());

			sendPartialData(response, resourceInfo,
					extraHeaders,
					requestRange.getStartPosition(),
					requestRange.getLength());
		} else {

			sendData(response, resourceInfo, extraHeaders);
		}
	}

}

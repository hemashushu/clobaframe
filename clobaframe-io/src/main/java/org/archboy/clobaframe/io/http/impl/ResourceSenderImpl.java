package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.inject.Named;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.http.ResourceSender;

/**
 *
 * @author yang
 *
 */
@Named
public class ResourceSenderImpl extends AbstractDataSenderWithLastModifiedAndRangeCheck
	implements ResourceSender {

	@Override
	public void send(
			ResourceInfo resourceInfo, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		String range = request.getHeader("Range");

		sendDataWithLastModifiedAndRangeCheck(
				response, resourceInfo, null,
				ifModifiedSince, range);
	}

}

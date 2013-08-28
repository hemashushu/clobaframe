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
package org.archboy.clobaframe.io.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.http.ResourceSender;

/**
 *
 * @author young
 *
 */
@Component
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

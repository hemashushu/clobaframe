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
package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceSender;
import org.archboy.clobaframe.webresource.WebResourceService;

/**
 * Send the web static resources.
 * Such as '/robots.txt', '/favicon.ico' and java-script/css files.
 *
 * @author young
 *
 */
@Named
public class WebResourceSenderImpl implements WebResourceSender{

	@Inject
	private ResourceSender resourceSender ;

	@Inject
	private WebResourceService webResourceService;

	/**
	 * Send web resource.
	 *
	 * @param resourceName
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Override
	public void send(String resourceName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		// get resource
		WebResourceInfo webResourceInfo = null;

		try{
			webResourceInfo = webResourceService.getResource(resourceName);
		}catch(FileNotFoundException e){
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Resource not found");
			return;
		}

		resourceSender.send(webResourceInfo, request, response);
	}

	/**
	 * Send web resource by unique name.
	 *
	 * @param resourceUniqueName
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Override
	public void sendByUniqueName(String resourceUniqueName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		// get resource
		WebResourceInfo webResourceInfo = null;

		try{
			webResourceInfo = webResourceService.getResourceByUniqueName(
				resourceUniqueName);
		}catch(FileNotFoundException e){
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Resource not found");
			return;
		}

		resourceSender.send(webResourceInfo, request, response);
	}
}

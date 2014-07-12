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
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.springframework.util.Assert;

/**
 * Send the web static resources.
 * Such as '/robots.txt', '/favicon.ico' and java-script/css files.
 *
 * @author yang
 *
 */
@Named
public class WebResourceSenderImpl implements WebResourceSender{

	@Inject
	private ResourceSender resourceSender ;

	@Inject
	private WebResourceManager webResourceService;

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

		Assert.hasText(resourceName, "Resource name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
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

		Assert.hasText(resourceUniqueName, "Resource unique name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
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

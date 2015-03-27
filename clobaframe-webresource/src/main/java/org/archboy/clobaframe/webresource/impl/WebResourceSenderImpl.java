package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.http.CacheResourceSender;
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.CompressableResource;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.archboy.clobaframe.webresource.WebResourceSender;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 *
 */
@Named
public class WebResourceSenderImpl implements WebResourceSender{

	@Inject
	private CacheResourceSender cacheResourceSender;

	@Inject
	private WebResourceManager webResourceService;

	@Override
	public void send(String resourceName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Assert.hasText(resourceName, "Resource name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
		try{
			WebResourceInfo webResourceInfo = webResourceService.getResource(resourceName);
			send(webResourceInfo, request, response);
		}catch(FileNotFoundException e){
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Resource not found");
		}
	}

	private void send(WebResourceInfo webResourceInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> headers = new HashMap<String, Object>();
		
		if (webResourceInfo instanceof CompressableResource ||
			(webResourceInfo instanceof AbstractWebResourceInfo &&
				((AbstractWebResourceInfo)webResourceInfo).getUnderlayWebResourceInfoNames()
					.contains("CompressableResource"))){
			
			headers.put("Content-Encoding", "gzip");
		}
		
		cacheResourceSender.send(webResourceInfo,
				CacheResourceSender.CACHE_CONTROL_PUBLIC,
				CacheResourceSender.ONE_MONTH_SECONDS, headers, request, response);
	}

	@Override
	public void sendByVersionName(String versionName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Assert.hasText(versionName, "Resource name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
		try{
			WebResourceInfo webResourceInfo = webResourceService.getResourceByVersionName(versionName);
			send(webResourceInfo, request, response);
		}catch(FileNotFoundException e){
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Resource not found");
		}
	}
}

package org.archboy.clobaframe.resource.http.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.io.http.ClientCacheResourceSender;
import org.archboy.clobaframe.resource.AbstractWrapperResourceInfo;
import org.archboy.clobaframe.resource.NotificationCacheableResourceInfo;
import org.archboy.clobaframe.resource.CompressibleResourceInfo;
import org.archboy.clobaframe.resource.WrapperResourceInfo;
import org.archboy.clobaframe.resource.ContentHashResourceInfo;
import org.archboy.clobaframe.resource.ResourceManager;
import org.archboy.clobaframe.resource.http.NamedResourceSender;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 *
 */
@Named
public class NamedResourceSenderImpl implements NamedResourceSender{

	@Inject
	private ClientCacheResourceSender clientCacheResourceSender;

	@Inject
	private ResourceManager resourceManager;

	public void setCacheResourceSender(ClientCacheResourceSender cacheResourceSender) {
		this.clientCacheResourceSender = cacheResourceSender;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void send(String resourceName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Assert.hasText(resourceName, "Resource name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
		NamedResourceInfo resourceInfo = resourceManager.getServedResource(resourceName);
		if (resourceInfo == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
				"Resource not found");
		}else{
			send(resourceInfo, request, response);
		}
	}

	private void send(NamedResourceInfo resourceInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> headers = new HashMap<String, Object>();
		
//		if (resourceInfo instanceof CompressibleResourceInfo) { // ||
////			(resourceInfo instanceof AbstractWrapperResourceInfo &&
////				((AbstractWrapperResourceInfo)resourceInfo).listTypes()
////					.contains(WrapperResourceInfo.TYPE_COMPRESS))){
//			// it's compressed resource already.
//			headers.put("Content-Encoding", "gzip");
//		}
		
		Object r = resourceInfo;
		while(r != null) {
			if (r instanceof CompressibleResourceInfo) {
				headers.put("Content-Encoding", "gzip");
				break;
			}else if(r instanceof WrapperResourceInfo) {
				r = ((WrapperResourceInfo)r).getInheritedObject();
			}else{
				break;
			}
		}
		
		clientCacheResourceSender.send(resourceInfo,
				ClientCacheResourceSender.CACHE_CONTROL_PUBLIC,
				ClientCacheResourceSender.THREE_MONTH_SECONDS, headers, request, response);
	}

	@Override
	public void sendByVersionName(String versionName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Assert.hasText(versionName, "Resource name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
		NamedResourceInfo resourceInfo = resourceManager.getServedResourceByVersionName(versionName);
		if (resourceInfo == null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Resource not found");
		}else{
			send(resourceInfo, request, response);
		}
	}
}

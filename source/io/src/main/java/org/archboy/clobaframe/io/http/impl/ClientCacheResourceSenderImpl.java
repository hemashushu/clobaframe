package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.http.ClientCacheResourceSender;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.springframework.util.StringUtils;

/**
 *
 * @author yang
 */
@Named
public class ClientCacheResourceSenderImpl implements ClientCacheResourceSender {

	@Inject
	private ResourceSender resourceSender;

	public void setResourceSender(ResourceSender resourceSender) {
		this.resourceSender = resourceSender;
	}
	
	@Override
	public void send(ResourceInfo resourceInfo, 
			String cacheControl,
			int cacheSeconds, Map<String, Object> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (StringUtils.isEmpty(cacheControl)){
			cacheControl = "max-age=" + cacheSeconds;
		}else{
			cacheControl += ", max-age=" + cacheSeconds;
		}

		Calendar expires = Calendar.getInstance();
		expires.add(Calendar.SECOND, cacheSeconds);

		if (extraHeaders == null) {
			extraHeaders = new HashMap<String, Object>();
		}

		extraHeaders.put("Cache-Control", cacheControl);
		extraHeaders.put("Expires", expires.getTime());

		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}

}

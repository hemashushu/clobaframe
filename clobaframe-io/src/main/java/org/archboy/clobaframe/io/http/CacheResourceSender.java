package org.archboy.clobaframe.io.http;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface CacheResourceSender extends ResourceSender {
	
	public static final int ONE_YEAR_SECONDS = 31536000;
	public static final int ONE_MONTH_SECONDS = 2592000;
	public static final int THREE_MONTH_SECONDS = 7776000;
	
	public static final String CACHE_CONTROL_PUBLIC = "public";
	public static final String CACHE_CONTROL_PRIVATE = "private";
	public static final String CACHE_CONTROL_NO_CACHE = "no-cache";
	public static final String CACHE_CONTROL_NO_STORE = "no-store";
	
	/**
	 * Send resource with specify expire seconds.
	 * 
	 * @param resourceInfo
	 * @param cacheControl
	 * @param cacheSeconds
	 * @param extraHeaders
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	void send(ResourceInfo resourceInfo, String cacheControl, int cacheSeconds,
			Map<String, Object> extraHeaders,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException;
}

package org.archboy.clobaframe.io.http;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.archboy.clobaframe.io.TemporaryResources;

/**
 * Receive the resource(multi-part POST form data) from the user's browser.
 * 
 * @author yang
 */
public interface MultipartFormResourceReceiver {

	/**
	 * Receive resources from client (browser)
	 *
	 * @param request
	 * @param temporaryResources
	 * @return
	 * @throws IOException
	 */
	List<MultipartFormResourceInfo> receive(
			HttpServletRequest request,
			TemporaryResources temporaryResources)
			throws IOException;

	/**
	 *
	 * @param request
	 * @param temporaryResources
	 * @param maxUploadSizeByte unit in Byte.
	 * @return
	 * @throws IOException
	 */
	List<MultipartFormResourceInfo> receive(
			HttpServletRequest request,
			TemporaryResources temporaryResources,
			long maxUploadSizeByte) throws IOException;

}

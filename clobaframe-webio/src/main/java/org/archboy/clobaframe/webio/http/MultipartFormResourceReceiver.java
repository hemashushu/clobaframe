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
package org.archboy.clobaframe.webio.http;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Receive the resource(file/data) from the user's browser.
 * <p>
 *     Commonly receive the multi-part POST form data.
 * </p>
 *
 * @author young
 */
public interface MultipartFormResourceReceiver {

	/**
	 * Receive resources from client (browser)
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	List<MultipartFormResourceInfo> receive(HttpServletRequest request)
			throws IOException;

	/**
	 *
	 * @param request
	 * @param maxUploadSize
	 * @return
	 * @throws IOException
	 */
	List<MultipartFormResourceInfo> receive(HttpServletRequest request,
			long maxUploadSize) throws IOException;

}

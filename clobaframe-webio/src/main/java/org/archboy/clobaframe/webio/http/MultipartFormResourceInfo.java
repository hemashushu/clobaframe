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

import java.nio.charset.Charset;
import org.archboy.clobaframe.webio.ResourceInfo;

/**
 * Commonly means the multi-part POST form data.
 *
 * @author young
 */
public interface MultipartFormResourceInfo extends ResourceInfo{

	/**
	 * Indicates the current resource is a file or a form field.
	 *
	 * @return
	 */
	boolean isFile();

	/**
	 * The field name.
	 *
	 * @return
	 */
	String getName();

	/**
	 * Get the file name.
	 * <p>
	 *     Some browser may send the file name with full path,
	 *     the caller must handle it by itself.
	 * </p>
	 *
	 * @return
	 */
	String getFileName();

	/**
	 * Get the content as string with the (session) default {@link Charset} encoding.
	 *
	 * @return
	 */
	String getContentAsString();
}

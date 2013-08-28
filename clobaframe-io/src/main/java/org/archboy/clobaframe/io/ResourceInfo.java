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
package org.archboy.clobaframe.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * A resource object.
 * <p>
 *     A resource means a set of binary or text data with
 *     the fixed length and content type defined,
 *     optional with the name and the last modified time properties.
 * </p>
 *
 * @author young
 *
 */
public interface ResourceInfo {

	/**
	 * The content length.
	 *
	 * @return
	 */
	long getContentLength();

	/**
	 * The content type.
	 *
	 * @return The mime type name.
	 */
	String getContentType();

	/**
	 * Get the content snapshot.
	 * New instance will be created while each invoked.
	 *
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Get the content snapshot with the specify range.
	 * New instance will be created while each invoked.
	 *
	 * @param start
	 * @param length
	 * @return
	 * @throws IOException If the resource content is not seekable.
	 */
	InputStream getInputStream(long start, long length) throws IOException;


	/**
	 * Optional property.
	 * 
	 * Indicates the method {@link ResourceInfo#getInputStream(long, long) } can be invoked.
	 * 
	 * @return
	 */
	boolean isSeekable();

	/**
	 * Optional property.
	 *
	 * @return NULL if no date specified.
	 */
	Date getLastModified();

}

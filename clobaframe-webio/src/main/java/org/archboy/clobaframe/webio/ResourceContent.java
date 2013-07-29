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
package org.archboy.clobaframe.webio;

import java.io.Closeable;
import java.io.InputStream;

/**
 * The content of resource.
 * <p>
 *     This object must <strong>be closed as soon as possible</strong> after used,
 *     even if the caller doesn't read the content stream at all,
 *     or the resources will not be released.
 * </p>
 *
 * @author young
 */
public interface ResourceContent extends Closeable{

	/**
	 * Content length.
	 *
	 * @return
	 */
	long getLength();

	/**
	 * Get the stream of resource content.
	 *
	 * @return Return the stream of blob content.
	 * <p>
	 *     This function will always return the <strong>same</strong> instance.
	 * </p>
	 */
	InputStream getInputStream();

}

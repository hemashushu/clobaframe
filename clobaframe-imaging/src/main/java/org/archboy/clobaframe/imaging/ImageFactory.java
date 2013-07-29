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
package org.archboy.clobaframe.imaging;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.archboy.clobaframe.webio.ResourceInfo;

/**
 * Image factory, for generating {@link Image} object.
 *
 * @author young
 *
 */
public interface ImageFactory {

	/**
	 * Make image by byte array.
	 *
	 * @param imageData
	 * @return
	 * @throws ImagingException
	 */
	Image makeImage(byte[] imageData) throws IOException;

	/**
	 * Make image by file.
	 *
	 * @param file
	 * @return
	 * @throws ImagingException
	 */
	Image makeImage(File file) throws IOException;

	/**
	 * Make image by InputStream.
	 *
	 * @param stream A stream contains image data.
	 * <p>
	 *     The stream will be closed after this call.
	 * </p>
	 * @return
	 * @throws
	 */
	Image makeImage(InputStream stream) throws IOException;

	/**
	 * Make image by url.
	 *
	 * @param imageUrl
	 * @return
	 * @throws ImagingException
	 */
	Image makeImage(String imageUrl) throws IOException;

	Image makeImage(ResourceInfo resourceInfo) throws IOException;

	/**
	 * Make an blank image with the specify width, height and background color.
	 *
	 * @param width
	 * @param height
	 * @param backgroundColor
	 * @return
	 */
	Image makeImage(int width, int height, Color backgroundColor);

}

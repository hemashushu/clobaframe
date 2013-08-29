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

package org.archboy.clobaframe.media.image.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.springframework.util.Assert;
import org.archboy.clobaframe.media.MediaDataSizeLimitExceededException;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaLoader;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

/**
 *
 * @author young
 *
 */
@Named
public class ImageLoaderImpl implements MediaLoader {

	public static final String CONTENT_TYPE_JPEG = "image/jpeg";
	public static final String CONTENT_TYPE_PNG = "image/png";
	public static final String CONTENT_TYPE_BMP = "image/bmp";
	public static final String CONTENT_TYPE_GIF = "image/gif";
	
	private List<String> supportContentTypes = Arrays.asList(
			CONTENT_TYPE_JPEG, 
			CONTENT_TYPE_PNG, 
			CONTENT_TYPE_GIF, 
			CONTENT_TYPE_BMP);
	
	@Override
	public boolean support(String contentType) {
		for (String supportContentType : supportContentTypes){
			if (supportContentType.equals(contentType)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Media load(FileBaseResourceInfo resourceInfo) throws IOException {

		// OpenJDK current support bmp, jpg, wbmp, jpeg, png, gif
		
		//ResourceContent resourceContent = null;
		//InputStream in = null;
		ImageInputStream stream = null;
		ImageReader reader = null;
		Image image = null;

		try {
			//resourceContent = resourceInfo.getContentSnapshot();
			//in = resourceContent.getInputStream();
			//in = resourceInfo.getInputStream();
			File file = resourceInfo.getFile();
			stream = ImageIO.createImageInputStream(file);

			Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
			if (!readers.hasNext()) {
				throw new IOException("The image format is not supported.");
			}

			reader = readers.next();
			Image.Format format = Image.Format.fromFormatName(reader.getFormatName());

			if (format == null) {
				throw new IOException("The image format is not supported.");
			}

			reader.setInput(stream);
			BufferedImage bufferedImage = reader.read(0);

			image = new DefaultImageFromFactory(resourceInfo, format, bufferedImage);

		} finally {
			closeQuietly(reader);
			//IOUtils.closeQuietly(stream); // for java 7
			closeQuietly(stream);
			//IOUtils.closeQuietly(in);
			//IOUtils.closeQuietly(resourceContent);
		}

		return image;
	}
	
	/*
	 * java 6 ImageInputStream does not implement Closable interface
	 */
	private void closeQuietly(ImageInputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private void closeQuietly(ImageReader reader) {
		if (reader != null) {
			reader.dispose();
		}
	}	
}

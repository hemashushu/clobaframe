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

package org.archboy.clobaframe.imaging.impl;

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
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.archboy.clobaframe.imaging.Image;
import org.archboy.clobaframe.imaging.ImageDataSizeLimitExceededException;
import org.archboy.clobaframe.imaging.ImageFactory;
import org.archboy.clobaframe.webio.ResourceContent;
import org.archboy.clobaframe.webio.ResourceInfo;

/**
 *
 * @author young
 *
 */
@Component
public class ImageFactoryImpl implements ImageFactory {

	// default 10 MB
	private static final long DEFAULT_MAX_HANDLE_SIZE = 10L * 1024 * 1024;

	private long maxHandleSize = DEFAULT_MAX_HANDLE_SIZE;

	private Logger logger = LoggerFactory.getLogger(ImageFactoryImpl.class);

	@Value("${imaging.maxHandleSize}")
	public void setMaxHandleSizeKB(int maxHandleSizeKB) {
		this.maxHandleSize = maxHandleSizeKB * 1024L;
	}

	@Override
	public Image makeImage(byte[] imageData) throws IOException {

		if (imageData.length > maxHandleSize){
			throw new ImageDataSizeLimitExceededException();
		}

		return buildImage(imageData);
	}

	@Override
	public Image makeImage(File file) throws IOException {

		if (file.length() > maxHandleSize) {
			throw new ImageDataSizeLimitExceededException();
		}

		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] imageData = IOUtils.toByteArray(in);
			return buildImage(imageData);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@Override
	public Image makeImage(InputStream stream) throws IOException {
		try{
			byte[] imageData = IOUtils.toByteArray(stream);

			if (imageData.length > maxHandleSize){
				throw new ImageDataSizeLimitExceededException();
			}
			return buildImage(imageData);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Override
	public Image makeImage(String imageUrl) throws IOException {
		URL url = new URL(imageUrl);
		byte[] imageData = getDataFromURL(url);
		return buildImage(imageData);
	}

	@Override
	public Image makeImage(ResourceInfo resourceInfo) throws IOException {
		ResourceContent resourceContent = null;
		try{
			resourceContent = resourceInfo.getContentSnapshot();
			InputStream in = resourceContent.getInputStream();
			return makeImage(in);
		}finally{
			IOUtils.closeQuietly(resourceContent);
		}
	}

	@Override
	public Image makeImage(int width, int height, Color backgroundColor) {
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setBackground(backgroundColor);
		graphics2D.clearRect(0, 0, width, height);
		graphics2D.dispose();
		return new DefaultImage(Image.Format.PNG, bufferedImage);
	}

	/**
	 *
	 * @param imageData
	 * @return
	 */
	private Image buildImage(byte[] imageData) throws IOException{
		Assert.isTrue(imageData.length > 0);
		Assert.isTrue(imageData.length <= maxHandleSize);

		// OpenJDK current support bmp, jpg, wbmp, jpeg, png, gif
		InputStream in = null;
		ImageInputStream stream = null;
		ImageReader reader = null;
		Image image = null;

		try {
			in = new ByteArrayInputStream(imageData);
			stream = ImageIO.createImageInputStream(in);

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

			image = new DefaultImageFromFactory(format, bufferedImage, imageData);

		} finally {
			closeQuietly(reader);
			//IOUtils.closeQuietly(stream); // for java 7
			closeQuietly(stream);
			IOUtils.closeQuietly(in);
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

	private byte[] getDataFromURL(URL url) throws IOException {
		Assert.isTrue("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()));

		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		//connection.connect();

//		int contentLength = connection.getContentLength();
//		if (contentLength == 0) {
//			throw new IOException("The specify url has no content.");
//		}
//
//		if (contentLength > maxHandleSize) {
//			throw new ImageDataSizeLimitExceededException();
//		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = null;
		byte[] data = null;

		int totalReadBytes = 0;
		byte[] buffer = new byte[16 * 1024];

		try {
			in = connection.getInputStream();

			while (true) {
				int readBytes = in.read(buffer);
				if (readBytes < 0) {
					break;
				}
				totalReadBytes += readBytes;
				if (totalReadBytes > maxHandleSize) {
					throw new ImageDataSizeLimitExceededException();
				}
				out.write(buffer, 0, readBytes);
			}

			data = out.toByteArray();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			connection.disconnect();
		}
		return data;
	}
}

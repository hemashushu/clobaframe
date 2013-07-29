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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.archboy.clobaframe.imaging.Image;
import org.archboy.clobaframe.imaging.OutputSettings;

/**
 *
 * @author young
 */
public abstract class AbstractImage implements Image {

	private final Logger logger = LoggerFactory.getLogger(DefaultImage.class);

	public abstract BufferedImage getBufferedImage();

	protected byte[] buildImageData(BufferedImage bufferedImage,
			OutputSettings outputSettings) {

		byte[] bufferedImageData = null;

		String formatName = outputSettings.getOutputEncoding().toString();
		Iterator<ImageWriter> imageWriters = ImageIO
				.getImageWritersByFormatName(formatName);

		if (!imageWriters.hasNext()) {
			logger.warn("No image writer for [{}] format.", formatName);
			return null;
		}

		ImageWriter imageWriter = imageWriters.next();

		ByteArrayOutputStream out = null;
		ImageOutputStream imageOutputStream = null;

		try{
			out = new ByteArrayOutputStream();
			imageOutputStream = ImageIO.createImageOutputStream(out);
			imageWriter.setOutput(imageOutputStream);

			if (outputSettings.getOutputEncoding().equals(
					OutputSettings.OutputEncoding.JPEG)) {
				ImageWriteParam params = new JPEGImageWriteParam(Locale.getDefault());
				params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

				if (outputSettings.hasQuality()) {
					float quality = (float)(outputSettings.getQuality()) / 100F;
					params.setCompressionQuality(quality);
				}

				BufferedImage imageWithoutAlpha = removeAlpha(bufferedImage);

				imageWriter.write(null, new IIOImage(imageWithoutAlpha,
						null, null), params);
			} else {
				imageWriter.write(new IIOImage(bufferedImage, null, null));
			}

			imageWriter.dispose();
			bufferedImageData = out.toByteArray();

		} catch(IOException e) {
			logger.warn("Write image to byte array fail, [{}]",
					e.getMessage());
		} finally {
			closeQuietly(imageOutputStream);
			IOUtils.closeQuietly(out);
		}

		return bufferedImageData;
	}

	private BufferedImage removeAlpha(BufferedImage bufferedImage) {

		if (bufferedImage.getType() == BufferedImage.TYPE_INT_RGB) {
			return bufferedImage;
		}

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.drawImage(bufferedImage, 0, 0, null);
		graphics.dispose();
		return image;
	}

	private void closeQuietly(ImageOutputStream imageOutputStream) {
		if (imageOutputStream != null){
			try{
				imageOutputStream.close();
			}catch(IOException e){
				// just ignore
			}
		}
	}

}

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

import java.awt.image.BufferedImage;

import org.archboy.clobaframe.imaging.Image;
import org.archboy.clobaframe.imaging.OutputSettings;

/**
 *
 * @author young
 */
public class DefaultImageFromFactory extends AbstractImage implements Image{

	private Format format;
	private BufferedImage bufferedImage;
	private byte[] originalImageData;

	public DefaultImageFromFactory(Format format, BufferedImage bufferedImage,
			byte[] originalImageData) {
		this.format = format;
		this.bufferedImage = bufferedImage;
		this.originalImageData = originalImageData;
	}

	@Override
	public Format getFormat() {
		return format;
	}

	@Override
	public int getWidth() {
		return bufferedImage.getWidth();
	}

	@Override
	public int getHeight() {
		return bufferedImage.getHeight();
	}

	@Override
	public byte[] getImageData() {
		return originalImageData;
	}

	@Override
	public byte[] getImageData(OutputSettings outputSettings) {
		if ((format == Format.PNG &&
				outputSettings.getOutputEncoding() == OutputSettings.OutputEncoding.PNG) ||
			(format == Format.JPEG &&
				outputSettings.getOutputEncoding() == OutputSettings.OutputEncoding.JPEG &&
				!outputSettings.hasQuality())){
			return originalImageData;
		}
		return buildImageData(bufferedImage, outputSettings);
	}

	@Override
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}
}

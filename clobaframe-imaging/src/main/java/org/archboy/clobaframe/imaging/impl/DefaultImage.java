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

public class DefaultImage extends AbstractImage implements Image {

	private Format format;
	private BufferedImage bufferedImage;

	public DefaultImage(Format format, BufferedImage bufferedImage) {
		this.format = format;
		this.bufferedImage = bufferedImage;
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
		OutputSettings outputSettings = new OutputSettings();
		if (format == Format.PNG){
			outputSettings.setOutputEncoding(OutputSettings.OutputEncoding.PNG);
		}else if(format == Format.JPEG){
			outputSettings.setOutputEncoding(OutputSettings.OutputEncoding.JPEG);
		}else{
			throw new IllegalArgumentException(
					String.format("The image format [%s] does not support yet.",
						format.toString())
					);
		}

		return buildImageData(bufferedImage, outputSettings);
	}

	@Override
	public byte[] getImageData(OutputSettings outputSettings) {
		return buildImageData(bufferedImage, outputSettings);
	}

	@Override
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

}

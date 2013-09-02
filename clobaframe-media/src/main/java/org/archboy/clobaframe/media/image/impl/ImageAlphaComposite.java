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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.archboy.clobaframe.media.image.Image;


/**
 *
 * @author young
 */
public class ImageAlphaComposite extends AbstractComposite{

	private Image image; // the source image
	private int xOffset;
	private int yOffset;
	private float opacity;

	public ImageAlphaComposite(Image image, int xOffset, int yOffset, float opacity) {
		this.image = image;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.opacity = opacity;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {
		Graphics2D graphics2D = bufferedImage.createGraphics();
		AlphaComposite alphaComposite = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, opacity);
		graphics2D.setComposite(alphaComposite);
		BufferedImage compositeImage = image.getBufferedImage();
		graphics2D.drawImage(compositeImage,xOffset, yOffset, null);
		graphics2D.dispose();

		return bufferedImage;
	}

}
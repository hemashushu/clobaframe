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

import org.archboy.clobaframe.imaging.Transform;

import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;
import com.mortennobel.imagescaling.MultiStepRescaleOp;

/**
 *
 * @author young
 */
public class ResizeWithFixHeightTransform implements Transform{

	private int frameHeight;

	public ResizeWithFixHeightTransform(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	@Override
	public BufferedImage transform(BufferedImage bufferedImage) {

		double sourceWidth = bufferedImage.getWidth();
		double sourceHeight = bufferedImage.getHeight();

		int targetWidth = (int)(sourceWidth * (double)frameHeight / sourceHeight);

		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(targetWidth,
				frameHeight);
		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
		return rescaled;
	}
}

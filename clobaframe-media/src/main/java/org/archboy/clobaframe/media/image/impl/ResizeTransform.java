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

import java.awt.image.BufferedImage;


import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;
import com.mortennobel.imagescaling.MultiStepRescaleOp;

/**
 *
 * @author young
 *
 */
public class ResizeTransform extends AbstractTransform {

	private int frameWidth;
	private int frameHeight;

	public ResizeTransform(int frameWidth, int frameHeight) {
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {
		int targetWidth = frameWidth;
		int targetHeight = frameHeight;

		int sourceWidth = bufferedImage.getWidth();
		int sourceHeight = bufferedImage.getHeight();

		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
		double targetRatio = (double) targetWidth / (double) targetHeight;

		if (Double.compare(sourceRatio, targetRatio) != 0) {
			if (sourceRatio > targetRatio) {
				// targetWidth = itself;
				targetHeight = (int) (targetWidth / sourceRatio);
			} else {
				targetWidth = (int) (targetHeight * sourceRatio);
				// targetHeight = itself;
			}
		}

		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(targetWidth,
				targetHeight);
		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
		return rescaled;
	}

}

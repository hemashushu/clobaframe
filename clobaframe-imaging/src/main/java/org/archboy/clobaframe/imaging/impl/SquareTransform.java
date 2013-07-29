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

import com.jhlabs.image.CropFilter;

/**
 *
 * @author young
 *
 */
public class SquareTransform implements Transform {

	@Override
	public BufferedImage transform(BufferedImage bufferedImage) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		if (width == height){
			return bufferedImage;
		}

		int left = 0;
		int top = 0;

		if (width > height){
			left = (width - height) / 2;
			width = height;
		}else {
			top = (height - width) / 2;
			height = width;
		}

		CropFilter filter = new CropFilter(left, top, width, height);
		return filter.filter(bufferedImage, null);
	}
}

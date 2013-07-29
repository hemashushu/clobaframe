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

import com.jhlabs.image.FlipFilter;

public class RotateTransform implements Transform{

	private RotateDegree rotateDegree;

	public RotateTransform(RotateDegree rotateDegree) {
		this.rotateDegree = rotateDegree;
	}

	@Override
	public BufferedImage transform(BufferedImage bufferedImage) {
		int degree = 0;
		switch(rotateDegree){
		case Rotate90CW:
			degree = FlipFilter.FLIP_90CW;
			break;
		case Rotate180CW:
			degree = FlipFilter.FLIP_180;
			break;
		case Rotate270CW:
			degree = FlipFilter.FLIP_90CCW;
			break;
		}

		FlipFilter filter = new FlipFilter(degree);
		return filter.filter(bufferedImage, null);
	}

	public static enum RotateDegree{
		Rotate90CW,
		Rotate180CW,
		Rotate270CW
	}

}

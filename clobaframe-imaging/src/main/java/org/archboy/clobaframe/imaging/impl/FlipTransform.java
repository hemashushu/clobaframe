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

/**
 *
 * @author young
 *
 */
public class FlipTransform implements Transform{

	private FlipDirection flipDirection;

	public FlipTransform(FlipDirection flipDirection){
		this.flipDirection = flipDirection;
	}

	@Override
	public BufferedImage transform(BufferedImage bufferedImage) {
		int direction = 0;
		switch(flipDirection){
		case Horizontal:
			direction = FlipFilter.FLIP_H;
			break;
		case Vertical:
			direction = FlipFilter.FLIP_V;
			break;
		case Both:
			direction = FlipFilter.FLIP_HV;
			break;
		}

		FlipFilter filter = new FlipFilter(direction);
		return filter.filter(bufferedImage, null);
	}

	public static enum FlipDirection{
		Horizontal,
		Vertical,
		Both
	}
}

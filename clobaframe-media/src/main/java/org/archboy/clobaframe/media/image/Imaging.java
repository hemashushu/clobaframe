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
package org.archboy.clobaframe.media.image;

import java.awt.Color;
import java.awt.Font;

/**
 * Provides common image processing features, such as resize, crop and
 * makes watermark etc.
 *
 * @author young
 *
 */
public interface Imaging {

	/**
	 * Make a crop transform.
	 *
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @return
	 */
	Transform crop(int left, int top, int width, int height);

	/**
	 * Make horizontal flip transform.
	 *
	 * @return
	 */
	Transform horizontalFlip();

	/**
	 * Make a vertical flip transform.
	 *
	 * @return
	 */
	Transform verticalFlip();

	/**
	 * Make a resize with keeping the aspect ratio transform.
	 *
	 * @param width
	 * @param height
	 * @return
	 */
	Transform resize(int width, int height);

	/**
	 * Make a rotate transform.
	 *
	 * @param degrees
	 * @return
	 */
	Transform rotate(int degrees);

	/**
	 * Make a square crop transform.
	 *
	 * @return
	 */
	Transform square();

	/**
	 * Make a resize transform with fix height.
	 *
	 * @param height
	 * @return
	 */
	Transform resizeWithFixHeight(int height);

	/**
	 * Make an image composite.
	 *
	 * @param image The source image
	 * @param xOffset
	 * @param yOffset
	 * @param opacity From "0.0" to "1.0".
	 * @return
	 */
	Composite alpha(Image image, 
			int xOffset, int yOffset, float opacity);

	/**
	 * Make a text composite.
	 *
	 * @param text
	 * @param font
	 * @param color
	 * @param xOffset
	 * @param yOffset
	 * @param opacity From "0.0" to "1.0".
	 * @return
	 */
	Composite text(String text, 
			Font font, Color color, 
			int xOffset, int yOffset, 
			float opacity);

	/**
	 * Apply transforms.
	 *
	 * @param image
	 * @param transforms
	 * @return
	 */
	Image apply(Image image, Transform... transforms);

	/**
	 * Apply composites.
	 *
	 * @param image
	 * @param composites
	 * @return
	 */
	Image apply(Image image, Composite... composites);
}
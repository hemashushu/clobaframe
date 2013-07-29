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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.archboy.clobaframe.imaging.Composite;

/**
 *
 * @author young
 */
public class TextComposite implements Composite {

	private String text;
	private Font font;
	private Color color;
	private int xOffset;
	private int yOffset;
	private float opacity;

	public TextComposite(String text, Font font, Color color,
			int xOffset, int yOffset, float opacity) {
		this.text = text;
		this.font = font;
		this.color = color;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.opacity = opacity;
	}

	@Override
	public BufferedImage composite(BufferedImage bufferedImage) {
		Graphics2D graphics2D = bufferedImage.createGraphics();
		AlphaComposite alphaComposite = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, opacity);
		graphics2D.setComposite(alphaComposite);
		graphics2D.setColor(color);
		graphics2D.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setFont(font);
		graphics2D.drawString(text, xOffset, yOffset);
		graphics2D.dispose();
		return bufferedImage;
	}
}

package org.archboy.clobaframe.media.image.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author yang
 */
public class TextComposite extends AbstractComposite {

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
	protected BufferedImage handle(BufferedImage bufferedImage) {
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

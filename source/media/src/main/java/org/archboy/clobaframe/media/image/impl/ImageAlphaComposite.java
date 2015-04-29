package org.archboy.clobaframe.media.image.impl;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.archboy.clobaframe.media.image.Image;


/**
 *
 * @author yang
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

package org.archboy.clobaframe.media.image.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Date;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.media.image.ImageGenerator;
import javax.inject.Named;

/**
 *
 * @author yang
 */
@Named
public class ImageGeneratorImpl implements ImageGenerator{

	@Override
	public Image make(int width, int height, Color backgroundColor) {
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setBackground(backgroundColor);
		graphics2D.clearRect(0, 0, width, height);
		graphics2D.dispose();
		
		Date now = new Date();
		
		return new DefaultImage(ImageLoaderImpl.CONTENT_TYPE_PNG, now, Image.Format.PNG, bufferedImage);
	}
	
}

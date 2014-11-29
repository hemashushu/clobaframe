package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import org.imgscalr.Scalr;

/**
 *
 * @author yang
 *
 */
public class SquareTransform extends AbstractTransform  {

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {

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

		BufferedImage croped = Scalr.crop(bufferedImage, 
				left, top, width, height, Scalr.OP_ANTIALIAS);
//		CropFilter filter = new CropFilter(left, top, width, height);
//		return filter.filter(bufferedImage, null);
		return croped;
	}
}

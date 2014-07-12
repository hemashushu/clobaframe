package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;


import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;
import com.mortennobel.imagescaling.MultiStepRescaleOp;

/**
 *
 * @author yang
 */
public class ResizeWithFixHeightTransform extends AbstractTransform {

	private int frameHeight;

	public ResizeWithFixHeightTransform(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {

		double sourceWidth = bufferedImage.getWidth();
		double sourceHeight = bufferedImage.getHeight();

		int targetWidth = (int)(sourceWidth * (double)frameHeight / sourceHeight);

		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(targetWidth,
				frameHeight);
		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
		return rescaled;
	}

}

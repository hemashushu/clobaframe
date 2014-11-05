package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;


import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;
import com.mortennobel.imagescaling.MultiStepRescaleOp;

/**
 *
 * @author yang
 */
public class ResizeWithFixWidthTransform extends AbstractTransform {

	private int frameWidth;

	public ResizeWithFixWidthTransform(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {

		double sourceWidth = bufferedImage.getWidth();
		double sourceHeight = bufferedImage.getHeight();

		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
		int targetHeight = (int)((double)frameWidth / sourceRatio);

		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(frameWidth,
				targetHeight);
		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
		return rescaled;
	}

}

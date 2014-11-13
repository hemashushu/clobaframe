package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;


import org.imgscalr.Scalr;

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

//		double sourceWidth = bufferedImage.getWidth();
//		double sourceHeight = bufferedImage.getHeight();
//
//		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
//		int targetHeight = (int)((double)frameWidth / sourceRatio);

		BufferedImage rescaled = Scalr.resize(bufferedImage, 
				Scalr.Mode.FIT_TO_WIDTH, frameWidth, Scalr.OP_ANTIALIAS);
				//frameWidth, targetHeight, Scalr.OP_ANTIALIAS);
//		ResampleOp rescaleOp = new ResampleOp(frameWidth,
//				targetHeight);
//		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
//		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
		return rescaled;
	}

}

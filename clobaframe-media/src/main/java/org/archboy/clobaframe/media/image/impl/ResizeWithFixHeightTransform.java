package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;


import org.imgscalr.Scalr;

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

//		double sourceWidth = bufferedImage.getWidth();
//		double sourceHeight = bufferedImage.getHeight();
//
//		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
//		int targetWidth = (int)((double)frameHeight * sourceRatio);

//		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(targetWidth,
//				frameHeight);
//		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
//		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
		
		BufferedImage rescaled = Scalr.resize(bufferedImage, 
				Scalr.Mode.FIT_TO_HEIGHT, frameHeight, Scalr.OP_ANTIALIAS);
				//targetWidth, frameHeight, Scalr.OP_ANTIALIAS);
		return rescaled;
	}

}

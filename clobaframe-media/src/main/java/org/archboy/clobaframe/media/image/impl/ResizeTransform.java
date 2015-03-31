package org.archboy.clobaframe.media.image.impl;

import com.twelvemonkeys.image.ResampleOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;


import org.imgscalr.Scalr;

/**
 *
 * @author yang
 *
 */
public class ResizeTransform extends AbstractTransform {

	private int frameWidth;
	private int frameHeight;

	public ResizeTransform(int frameWidth, int frameHeight) {
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {
		int targetWidth = frameWidth;
		int targetHeight = frameHeight;

		int sourceWidth = bufferedImage.getWidth();
		int sourceHeight = bufferedImage.getHeight();

		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
		double targetRatio = (double) targetWidth / (double) targetHeight;

		if (Double.compare(sourceRatio, targetRatio) != 0) {
			if (sourceRatio > targetRatio) {
				// targetWidth = itself;
				targetHeight = (int)Math.round(targetWidth / sourceRatio);
			} else {
				targetWidth = (int)Math.round(targetHeight * sourceRatio);
				// targetHeight = itself;
			}
		}

//		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(targetWidth,
//				targetHeight);
//		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
//		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
//		BufferedImage rescaled = Scalr.resize(bufferedImage, 
//				targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
		
		// way 2
//		BufferedImage rescaled = Scalr.resize(bufferedImage, 
//				frameWidth, frameHeight, Scalr.OP_ANTIALIAS);
		
		// way 3
		BufferedImageOp resampler = new ResampleOp(frameWidth, frameHeight, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
		BufferedImage rescaled = resampler.filter(bufferedImage, null);
		return rescaled;
	}

}

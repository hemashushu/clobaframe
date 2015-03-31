package org.archboy.clobaframe.media.image.impl;

import com.twelvemonkeys.image.ResampleOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;


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

		double sourceWidth = bufferedImage.getWidth();
		double sourceHeight = bufferedImage.getHeight();

		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
		int targetWidth = (int)((double)frameHeight * sourceRatio);

//		MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(targetWidth,
//				frameHeight);
//		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
//		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);

		// way 2
//		BufferedImage rescaled = Scalr.resize(bufferedImage, 
//				Scalr.Mode.FIT_TO_HEIGHT, frameHeight, Scalr.OP_ANTIALIAS);

		BufferedImageOp resampler = new ResampleOp(targetWidth, frameHeight, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
		BufferedImage rescaled = resampler.filter(bufferedImage, null);
		
		return rescaled;
	}

}

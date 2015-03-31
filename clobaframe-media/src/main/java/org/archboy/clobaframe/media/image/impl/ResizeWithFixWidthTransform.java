package org.archboy.clobaframe.media.image.impl;

import com.twelvemonkeys.image.DiffusionDither;
import com.twelvemonkeys.image.ResampleOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;


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

		// way 1
		double sourceWidth = bufferedImage.getWidth();
		double sourceHeight = bufferedImage.getHeight();

		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
		int targetHeight = (int)((double)frameWidth / sourceRatio);

//		ResampleOp rescaleOp = new ResampleOp(frameWidth,
//				targetHeight);
//		rescaleOp.setUnsharpenMask(UnsharpenMask.Normal);
//		BufferedImage rescaled = rescaleOp.filter(bufferedImage, null);
		
		// way2
//		BufferedImage rescaled = Scalr.resize(bufferedImage, 
//				Scalr.Mode.FIT_TO_WIDTH, frameWidth, Scalr.OP_ANTIALIAS);
		
		BufferedImageOp resampler = new ResampleOp(frameWidth, targetHeight, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
		BufferedImage rescaled = resampler.filter(bufferedImage, null);
		
		return rescaled;
	}

}

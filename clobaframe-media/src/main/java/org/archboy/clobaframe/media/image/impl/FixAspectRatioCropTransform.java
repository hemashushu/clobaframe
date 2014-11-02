package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;

/**
 *
 * @author yang
 *
 */
public class FixAspectRatioCropTransform extends AbstractTransform {

	private double minRatio; // ratio = width / height
	private double maxRatio;

	public FixAspectRatioCropTransform(double minRatio, double maxRatio) {
		this.minRatio = minRatio;
		this.maxRatio = maxRatio;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {
		int sourceWidth = bufferedImage.getWidth();
		int sourceHeight = bufferedImage.getHeight();

		double sourceRatio = (double) sourceWidth / (double) sourceHeight;
		if (sourceRatio < minRatio){
			// too high
			int height = (int)(sourceWidth / minRatio);
			int top = (sourceHeight - height)/2; 
			return new CropTransform(0, top, sourceWidth, height).handle(bufferedImage);
		}else if (sourceRatio > maxRatio) {
			// too wide
			int width = (int)(sourceHeight * maxRatio);
			int left = (sourceWidth - width)/2;
			return new CropTransform(left, 0, width, sourceHeight).handle(bufferedImage);
		}else{
			return bufferedImage;
		}
	}

}

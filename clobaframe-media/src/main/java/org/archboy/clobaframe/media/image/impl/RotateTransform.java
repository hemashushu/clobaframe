package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;


import com.jhlabs.image.FlipFilter;

public class RotateTransform extends AbstractTransform {

	private RotateDegree rotateDegree;

	public RotateTransform(RotateDegree rotateDegree) {
		this.rotateDegree = rotateDegree;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {
		int degree = 0;
		switch(rotateDegree){
		case Rotate90CW:
			degree = FlipFilter.FLIP_90CW;
			break;
		case Rotate180CW:
			degree = FlipFilter.FLIP_180;
			break;
		case Rotate270CW:
			degree = FlipFilter.FLIP_90CCW;
			break;
		}

		FlipFilter filter = new FlipFilter(degree);
		return filter.filter(bufferedImage, null);
	}

	public static enum RotateDegree{
		Rotate90CW,
		Rotate180CW,
		Rotate270CW
	}

}

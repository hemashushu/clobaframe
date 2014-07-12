package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;


import com.jhlabs.image.FlipFilter;

/**
 *
 * @author yang
 *
 */
public class FlipTransform extends AbstractTransform {

	private FlipDirection flipDirection;

	public FlipTransform(FlipDirection flipDirection){
		this.flipDirection = flipDirection;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {
		int direction = 0;
		switch(flipDirection){
		case Horizontal:
			direction = FlipFilter.FLIP_H;
			break;
		case Vertical:
			direction = FlipFilter.FLIP_V;
			break;
		case Both:
			direction = FlipFilter.FLIP_HV;
			break;
		}

		FlipFilter filter = new FlipFilter(direction);
		return filter.filter(bufferedImage, null);
	}

	public static enum FlipDirection{
		Horizontal,
		Vertical,
		Both
	}
}

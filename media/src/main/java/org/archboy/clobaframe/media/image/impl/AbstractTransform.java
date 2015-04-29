package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.media.image.Transform;

/**
 *
 * @author yang
 */
public abstract class AbstractTransform implements Transform {

	@Override
	public Image transform(Image image) {
		BufferedImage bufferedImageBefore = image.getBufferedImage();
		BufferedImage bufferedImageAfter = handle(bufferedImageBefore);
		return new DefaultImage(image.getResourceInfo().getLastModified(), 
				Image.Format.PNG, bufferedImageAfter);
	}
	
	protected abstract BufferedImage handle(BufferedImage bufferedImage);
}

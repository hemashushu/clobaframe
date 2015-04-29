package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import org.archboy.clobaframe.media.image.Composite;
import org.archboy.clobaframe.media.image.Image;

/**
 *
 * @author yang
 */
public abstract class AbstractComposite implements Composite {

	@Override
	public Image composite(Image image) {
		BufferedImage bufferedImageBefore = image.getBufferedImage();
		BufferedImage bufferedImageAfter = handle(bufferedImageBefore);
		return new DefaultImage(image.getResourceInfo().getLastModified(), 
				Image.Format.PNG, bufferedImageAfter);
	}
	
	protected abstract BufferedImage handle(BufferedImage bufferedImage);
}

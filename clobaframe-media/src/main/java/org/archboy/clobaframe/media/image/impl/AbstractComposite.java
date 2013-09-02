package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import org.archboy.clobaframe.media.image.Composite;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.media.image.Transform;

/**
 *
 * @author yang
 */
public abstract class AbstractComposite implements Composite {

	//public static final String CONTENT_TYPE_IMAGE_PNG = "image/png";

	@Override
	public Image composite(Image image) {
		BufferedImage bufferedImageBefore = image.getBufferedImage();
		BufferedImage bufferedImageAfter = handle(bufferedImageBefore);
		return new DefaultImage(image.getResourceInfo().getLastModified(), 
				Image.Format.PNG, bufferedImageAfter);
	}
	
	protected abstract BufferedImage handle(BufferedImage bufferedImage);
}

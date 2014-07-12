package org.archboy.clobaframe.media.image;

import java.awt.image.BufferedImage;

/**
 * The transform object.
 *
 * @author yang
 *
 */
public interface Transform {

	/**
	 * 
	 * @param bufferedImage
	 * @return 
	 */
	Image transform(Image image);

}

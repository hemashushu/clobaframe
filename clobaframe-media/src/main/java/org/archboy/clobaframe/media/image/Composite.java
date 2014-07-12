package org.archboy.clobaframe.media.image;

import java.awt.image.BufferedImage;

/**
 *
 * @author yang
 *
 */
public interface Composite {

	/**
	 * 
	 * @param bufferedImage
	 * @return 
	 */
	Image composite(Image image);
}

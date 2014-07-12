package org.archboy.clobaframe.media.image;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * 
 *
 * @author yang
 *
 */
public interface ImageGenerator {

	/**
	 * Make an blank image with the specify width, height and background color.
	 *
	 * @param width
	 * @param height
	 * @param backgroundColor
	 * @return
	 */
	Image make(int width, int height, Color backgroundColor);

}

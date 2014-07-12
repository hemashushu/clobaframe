package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import com.jhlabs.image.CropFilter;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.media.image.Transform;

/**
 *
 * @author yang
 *
 */
public class CropTransform extends AbstractTransform {

	private int left;
	private int top;
	private int width;
	private int height;

	public CropTransform(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	@Override
	protected BufferedImage handle(BufferedImage bufferedImage) {
		CropFilter filter = new CropFilter(left, top, width, height);
		return filter.filter(bufferedImage, null);
	}

}

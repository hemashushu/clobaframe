package org.archboy.clobaframe.media.image.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import org.archboy.clobaframe.media.image.Composite;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.media.image.Imaging;
import org.archboy.clobaframe.media.image.Transform;

import javax.inject.Named;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 *
 */
@Named
public class ImagingImpl implements Imaging{

	@Override
	public Transform crop(int left, int top, int width, int height) {
		Assert.isTrue(left >= 0);
		Assert.isTrue(top >= 0);
		Assert.isTrue(width > 0);
		Assert.isTrue(height > 0);
		
		return new CropTransform(left, top, width, height);
	}

	@Override
	public Transform fixAspectRatioCrop(double minRatio, double maxRatio) {
		Assert.isTrue(minRatio > 0);
		Assert.isTrue(maxRatio > 0);
		Assert.isTrue(maxRatio > minRatio);
		return new FixAspectRatioCropTransform(minRatio, maxRatio);
	}
	
	@Override
	public Transform horizontalFlip() {
		return new FlipTransform(FlipTransform.FlipDirection.Horizontal);
	}

	@Override
	public Transform verticalFlip() {
		return new FlipTransform(FlipTransform.FlipDirection.Vertical);
	}

	@Override
	public Transform resize(int width, int height) {
		Assert.isTrue(width > 0);
		Assert.isTrue(height > 0);
		return new ResizeTransform(width, height);
	}

	@Override
	public Transform rotate(int degrees) {
		// current only support 90, 180 and 270 degrees clockwise
		Assert.isTrue(degrees == 90 || degrees == 180 || degrees == 270, 
				"Support 90,180,270 degrees only.");
		
		RotateTransform.RotateDegree rotateDegree = null;
		if (degrees == 90){
			rotateDegree = RotateTransform.RotateDegree.Rotate90CW;
		}else if (degrees == 180){
			rotateDegree = RotateTransform.RotateDegree.Rotate180CW;
		}else if (degrees == 270){
			rotateDegree = RotateTransform.RotateDegree.Rotate270CW;
		}
		return new RotateTransform(rotateDegree);
	}

	@Override
	public Transform square() {
		return new SquareTransform();
	}

	@Override
	public Transform resizeWithFixHeight(int height) {
		Assert.isTrue(height > 0);
		return new ResizeWithFixHeightTransform(height);
	}

	@Override
	public Composite alpha(Image image, int xOffset, int yOffset,
			float opacity) {
		Assert.notNull(image);
		Assert.isTrue(xOffset >= 0);
		Assert.isTrue(yOffset >= 0);
		Assert.isTrue(opacity > 0);
		
		return new ImageAlphaComposite(image, xOffset, yOffset, opacity);
	}

	@Override
	public Composite text(String text, Font font, Color color,
			int xOffset, int yOffset, float opacity) {
		Assert.hasText(text);
		Assert.notNull(font);
		Assert.notNull(color);
		Assert.isTrue(xOffset >= 0);
		Assert.isTrue(yOffset >= 0);
		Assert.isTrue(opacity > 0);
		
		return new TextComposite(text, font, color, xOffset, yOffset, opacity);
	}

	@Override
	public Image apply(Image image, Transform... transforms) {
		Assert.notNull(image);
		Assert.notNull(transforms);
		
		for (Transform transform : transforms){
			image = transform.transform(image);
		}
		return image;
	}

	@Override
	public Image apply(Image image, Composite... composites) {
		Assert.notNull(image);
		Assert.notNull(composites);
		
		for (Composite composite : composites){
			image = composite.composite(image);
		}
		return image;
	}
}

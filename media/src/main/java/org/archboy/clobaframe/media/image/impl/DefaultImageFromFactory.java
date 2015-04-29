package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import java.util.Date;
import org.archboy.clobaframe.media.image.OutputSettings;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public class DefaultImageFromFactory extends DefaultImage {

	private ResourceInfo resourceInfo;

	public DefaultImageFromFactory(
			ResourceInfo resourceInfo,
			Format format, BufferedImage bufferedImage) {
		super(resourceInfo.getLastModified(), format, bufferedImage);
		this.resourceInfo = resourceInfo;
	}

	@Override
	public ResourceInfo getResourceInfo(Date lastModified, OutputSettings outputSettings) {
		
		// if the original format is same as the output settings, just return the original resource.
		if ((getFormat() == Format.PNG &&
				outputSettings.getOutputEncoding() == OutputSettings.OutputEncoding.PNG) ||
			(getFormat() == Format.JPEG &&
				outputSettings.getOutputEncoding() == OutputSettings.OutputEncoding.JPEG &&
				!outputSettings.hasQuality())){
			return resourceInfo;
		}else{
			return super.getResourceInfo(lastModified, outputSettings);
		}
	}
	
}

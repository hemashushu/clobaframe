/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import java.util.Date;
import org.archboy.clobaframe.media.image.OutputSettings;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author young
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
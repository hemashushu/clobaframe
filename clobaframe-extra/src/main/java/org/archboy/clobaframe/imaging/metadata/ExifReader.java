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
package org.archboy.clobaframe.imaging.metadata;

import java.io.IOException;
import org.archboy.clobaframe.imaging.Image;

/**
 * Read the EXIF meta data from JPEG, TIFF and RAW image.
 *
 * @author young
 */
public interface ExifReader {

	/**
	 *
	 * @param image
	 * @return Return null no meta data.
	 */
	ExifMetadata getMetaData(Image image) throws IOException;

	Orientation translateOrientation (int orientation);

	/**
	 * Translate to fired or not fired.
	 * @param flash
	 * @return
	 */
	boolean translateFlash(int flash);

	public static enum Orientation{
		Normal, // orientation = 1: "Top, left side (Horizontal / normal)"
		MirrorHorizontal, // orientation = 2: "Top, right side (Mirror horizontal)"
		Rotate180CW, // orientation = 3: "Bottom, right side (Rotate 180)"
		MirrorVertical, // orientation = 4: "Bottom, left side (Mirror vertical)"
		MirrorHorizontalAndRotate270CW, // orientation = 5: "Left side, top (Mirror horizontal and rotate 270 CW)"
		Rotate90CW, // orientation = 6: "Right side, top (Rotate 90 CW)"
		MirrorHorizontalAndRoate90CW, // orientation = 7: "Right side, bottom (Mirror horizontal and rotate 90 CW)"
		Rotate270CW // orientation = 8: "Left side, bottom (Rotate 270 CW)"
	}
}

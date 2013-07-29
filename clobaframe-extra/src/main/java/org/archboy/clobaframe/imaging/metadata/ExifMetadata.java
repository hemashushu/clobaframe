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

import java.util.Date;

/**
 * Exif directory.
 * see: http://www.awaresystems.be/imaging/tiff/tifftags/privateifd/exif.html
 *
 * @author young
 */
public interface ExifMetadata {

	/**
	 * the seconds of exposure.
	 * @return
	 */
	String getExposureTime();

	/**
	 * translate the exif focal length:
	 * java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
     *   return formatter.format(focalLength);
	 *
	 * for display the view should append 'mm' to the ending.
	 * @return
	 */
	String getFocalLength();

	/**
	 * translate the exif fNumber:
	 *  java.text.DecimalFormat formatter = new DecimalFormat("0.#");
	 *	return formatter.format(fNumber);
	 *
	 * for display the view should append 'F' to the beginning.
	 * @return
	 */
	String getFNumber();

	/**
	 * It's a bit-mask value.
	 * if ((val & 0x1)!=0)
	 *	("Flash fired");
	 * else
	 *	("Flash did not fire");
	 * @return
	 */
	Integer getFlash();

	/**
	 * translate the exif 'ISO Equivalent':
	 * if ISO Equivalent less than 50 then isoEquiv *= 200;
	 * @return
	 */
	Integer getISOSpeedRatings();

	String getMake();

	String getModel();

	/**
	 * switch (orientation) {
     *       case 1: return "Top, left side (Horizontal / normal)";
     *       case 2: return "Top, right side (Mirror horizontal)";
     *       case 3: return "Bottom, right side (Rotate 180)";
     *       case 4: return "Bottom, left side (Mirror vertical)";
     *       case 5: return "Left side, top (Mirror horizontal and rotate 270 CW)";
     *       case 6: return "Right side, top (Rotate 90 CW)";
     *       case 7: return "Right side, bottom (Mirror horizontal and rotate 90 CW)";
     *       case 8: return "Left side, bottom (Rotate 270 CW)";
     *       default:
     *           return String.valueOf(orientation);
     *   }
	 * @return
	 */
	Integer getOrientation();

	Date getDateTimeOriginal();

	String getSoftware();

	/**
	 * In degree, East = +, West = -.
	 * @return
	 */
	Float getGpsLongitude();

	/**
	 * In degree, North = +, South = -.
	 * @return
	 */
	Float getGpsLatitude();

	/**
	 * In meter, (0)above see = +, (1)below see = -.
	 * @return
	 */
	Float getGpsAltitude();
}

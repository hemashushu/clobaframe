package org.archboy.clobaframe.media.image;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.media.impl.MetaDataSettableMedia;


/**
 *
 * @author yang
 *
 */
public interface Image extends Media, MetaDataSettableMedia {

	/**
	 * 
	 * @return 
	 */
	BufferedImage getBufferedImage();
	
	/**
	 * 
	 * @return 
	 */
	Format getFormat();

	/**
	 * Image width
	 * 
	 * @return 
	 */
	int getWidth();

	/**
	 * Image height
	 * 
	 * @return 
	 */
	int getHeight();

	/**
	 * Get the image data with the specify output settings.
	 *
	 * @param lastModified Specify the new last modified date. NULL for keep original.
	 * @param outputSettings
	 * @return
	 */
	ResourceInfo getResourceInfo(Date lastModified, OutputSettings outputSettings);

	public static enum MetaName {
		/**
		 * String: the seconds of exposure.
		 */
		 ExposureTime,

		/**
		 * String: translate the exif focal length:
		 * java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
		 *   return formatter.format(focalLength);
		 *
		 * for display the view should append 'mm' to the ending.
		 */
		FocalLength,

		/**
		 * String: * translate the exif fNumber:
		 *  java.text.DecimalFormat formatter = new DecimalFormat("0.#");
		 *	return formatter.format(fNumber);
		 *
		 * for display the view should append 'F' to the beginning.
		 */
		fNumber,

		/**
		 * Boolean:
		 * 
		 * The origin is a bit-mask value.
		 * if ((val & 0x1)!=0)
		 *	("Flash fired");
		 * else
		 *	("Flash did not fire");
		 * 
		 * 
		 */
		Flash,

		/**
		 * Integer:
		 * 
		 * The origin is a integer exif 'ISO Equivalent':
		 * if ISO Equivalent less than 50 then isoEquiv *= 200;
		 */
		ISOSpeedRatings,

		/**
		 * String
		 */
		Make,

		/**
		 * 
		 */
		Model,

		/**
		 * Integer:
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
		 */
		Orientation,

		/**
		 * Date
		 */
		DateTimeOriginal,

		/**
		 * String
		 */
		Software,

		/**
		 * Float: In degree, East = +, West = -.
		 */
		GpsLongitude,

		/**
		 * Float: In degree, North = +, South = -.
		 */
		GpsLatitude,

		/**
		 * Float: In meter, (0)above see = +, (1)below see = -.
		 */
		GpsAltitude
	}
	
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
	
	public static enum Format {
		BMP, GIF, ICO, JPEG, PNG, TIFF;

		public static Format fromFormatName(String formatName){
			Format format = null;
			String name = formatName.toUpperCase();
			if (name.equals("BMP")){
				format = Format.BMP;
			}else if (name.equals("GIF")){
				format = Format.GIF;
			}else if (name.equals("ICO")){
				format = Format.ICO;
			}else if (name.equals("JPG") || name.equals("JPEG")){
				format = Format.JPEG;
			}else if (name.equals("PNG")){
				format = Format.PNG;
			}else if (name.equals("TIF") || name.equals("TIFF")){
				format = Format.TIFF;
			}
			return format;
		}
	}
}

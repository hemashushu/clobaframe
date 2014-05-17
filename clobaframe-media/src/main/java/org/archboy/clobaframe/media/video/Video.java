package org.archboy.clobaframe.media.video;

import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.impl.MetaDataSettableMedia;

/**
 *
 * @author yang
 */
public interface Video extends Media, MetaDataSettableMedia  {
	
	Format getFormat(); // container format: *.mp3 *.m4a
	
	/**
	 * Video width
	 * 
	 * @return 
	 */
	int getWidth();

	/**
	 * Video height
	 * 
	 * @return 
	 */
	int getHeight();
	
	double getDuration(); // second
	
	public static enum Format {
		mp4,
		mov
	}
	
}

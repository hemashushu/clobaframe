package org.archboy.clobaframe.media.audio;

import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.impl.MetaDataSettableMedia;

/**
 *
 * @author yang
 */
public interface Audio extends Media, MetaDataSettableMedia {
	
	// audio common
	Format getFormat(); // container format: *.mp3 *.m4a
	double getDuration(); // second
	
	String getEncoding(); // mp3,aac
	int getBitrate(); // kbps
	BitrateMode getBitrateMode(); // Constant,Variable
	
	// mp3
	public static enum MetaName {
		Album,
		Title,
		Artist,
		Track,
		Genre
	}
	
	public static enum Format {
		mp3,
		m4a
	}
	
	public static enum BitrateMode {
		constant,
		variable
	}
}

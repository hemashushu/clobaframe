package org.archboy.clobaframe.media.audio.impl;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaLoader;
import org.archboy.clobaframe.media.audio.Audio;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class Mp3Loader implements MediaLoader {

	public static final String MIME_TYPE_AUDIO_MPEG = "audio/mpeg";
	
	@Override
	public boolean support(String mimeType) {
		return (MIME_TYPE_AUDIO_MPEG.equals(mimeType));
	}

	@Override
	public Media load(FileBaseResourceInfo fileBaseResourceInfo) throws IOException {
		Assert.notNull(fileBaseResourceInfo);
		
		File file = fileBaseResourceInfo.getFile();
		
		Mp3File mp3 = null;
		try{
			mp3 = new Mp3File(file.getPath());
		}catch(UnsupportedTagException e){
			return null;
		}catch (InvalidDataException ex) {
			return null;
		}
		
		Audio audio = new DefaultAudio(
				fileBaseResourceInfo,
				Audio.Format.mp3, 
				"mp3", 
				mp3.getLengthInSeconds(), 
				mp3.getBitrate(), 
				mp3.isVbr()?Audio.BitrateMode.variable:Audio.BitrateMode.constant);
		
		Mp3MetaDataPaser metaDataPaser = new Mp3MetaDataPaser();
		audio.setMetaData(metaDataPaser.parse(mp3));
		
		return audio;
	}
	
}

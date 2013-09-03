package org.archboy.clobaframe.media.audio.impl;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Named;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaLoader;
import org.archboy.clobaframe.media.audio.Audio;

/**
 *
 * @author yang
 */
@Named
public class M4aLoader implements MediaLoader{
	
	public static final String CONTENT_TYPE_AUDIO_MP4 = "audio/mp4"; // *.m4a, *.mp4a
//	public static final String CONTENT_TYPE_AUDIO_MPEG = "audio/mpeg"; // *.mp3
	
//	private List<String> supportContentTypes = Arrays.asList(
//			CONTENT_TYPE_AUDIO_MPEG, 
//			CONTENT_TYPE_AUDIO_MP4);
	
//	private static final List<String> mp4_audio_brands = Arrays.asList(
//             "M4A ", "M4B ", "F4A ", "F4B ");
//	
//	private static final List<String> mp4_video_brands = Arrays.asList(
//             "mp41", "mp42");
	
	   
	@Override
	public boolean support(String contentType) {
//		for (String supportContentType : supportContentTypes){
//			if (supportContentType.equals(contentType)){
//				return true;
//			}
//		}
//		
//		return false;
		return CONTENT_TYPE_AUDIO_MP4.equals(contentType);
	}

	@Override
	public Media load(FileBaseResourceInfo fileBaseResourceInfo) throws IOException {
		
		File file = fileBaseResourceInfo.getFile();
		
//		AudioFile audioFile = null;
//
//		try{
//			audioFile = AudioFileIO.read(file);
//		} catch(CannotReadException e){
//			return null;
//		} catch (TagException ex) {
//			return null;
//		} catch (ReadOnlyFileException ex) {
//			return null;
//		} catch (InvalidAudioFrameException ex) {
//			return null;
//		}
//		
//		AudioHeader audioHeader = audioFile.getAudioHeader();
//		audioHeader.getFormat() // AAC, MPEG-1 Layer 3

		IsoFile isoFile = new IsoFile(file);
		
		// Grab the file type box
        FileTypeBox fileType = getOrNull(isoFile, FileTypeBox.class);
        if (fileType == null) {
			return null;
		}
		
//		String brand = fileType.getMajorBrand();
//		String type = null;
//		
//		for(String t : mp4_audio_brands) {
//			if (brand.equals(t)){
//				type = t;
//				break;
//			}
//		}
//			
//		for(String t : mp4_video_brands) {
//			if (brand.equals(t)){
//				type = t;
//				break;
//			}
//		}
//		
//		if (type == null){
//			return null;
//		}
		
//		Audio.Format format = null;
//		String encoding = null;
//		if ("mp3".equals(audioHeader.getEncodingType())){
//			format = Audio.Format.mp3;
//			encoding = "mp3";
//		}else if ("AAC".equals(audioHeader.getEncodingType())){
//			format = Audio.Format.m4a;
//			encoding = "aac";
//		}
//		
		
		// Get the main MOOV box
        MovieBox moov = getOrNull(isoFile, MovieBox.class);
        if (moov == null) {
           // Bail out
           return null;
        }

		long duration = 0;
		
		// Pull out some information from the header box
        MovieHeaderBox mHeader = getOrNull(moov, MovieHeaderBox.class);
        if (mHeader == null) {
			return null;
		}
         
		// Get the duration. Seconds
		duration = mHeader.getDuration() / mHeader.getTimescale();
		
		if (duration == 0){
			duration = 1;
		}
		
		int bitrate = (int)(fileBaseResourceInfo.getContentLength() / 1024 / duration * 8);
		
		Audio audio = new DefaultAudio(
				fileBaseResourceInfo, 
				Audio.Format.m4a, "aac", 
				duration,
				bitrate,
				Audio.BitrateMode.variable);
		
		M4aMetaDataParser metaDataParser = new M4aMetaDataParser();
		audio.setMetaData(metaDataParser.parse(moov));
		
		return audio;

	}
	
	private static <T extends Box> T getOrNull(ContainerBox box, Class<T> clazz) {
       if (box == null) return null;

       List<T> boxes = box.getBoxes(clazz);
       if (boxes.isEmpty()) {
          return null;
       }
       return boxes.get(0);
    }
}

package org.archboy.clobaframe.media.audio.impl;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
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
public class M4aLoader implements MediaLoader{
	
	public static final String MIME_TYPE_AUDIO_MP4 = "audio/mp4"; // *.m4a, *.mp4a

	@Override
	public boolean support(String mimeType) {
		return MIME_TYPE_AUDIO_MP4.equals(mimeType);
	}

	@Override
	public Media load(FileBaseResourceInfo fileBaseResourceInfo) throws IOException {
		Assert.notNull(fileBaseResourceInfo);
		
		File file = fileBaseResourceInfo.getFile();
		
		DataSource dataSource = new FileDataSourceImpl(file);
		try{
			IsoFile isoFile = new IsoFile(dataSource);
			return loadIsoFile(isoFile, fileBaseResourceInfo);
		}finally{
			IOUtils.closeQuietly(dataSource);
		}
	}

	private Media loadIsoFile(IsoFile isoFile, FileBaseResourceInfo fileBaseResourceInfo) {
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
		// mp4 file format, see:
		// http://1.richitec.sinaapp.com/?p=46
		
		// Get the main MOOV box
		MovieBox moov = getOrNull(isoFile, MovieBox.class);
		if (moov == null) {
			// Bail out
			return null;
		}
		
		double duration = 0;
		
		// Pull out some information from the header box
		MovieHeaderBox mHeader = getOrNull(moov, MovieHeaderBox.class);
		if (mHeader == null) {
			return null;
		}
         
		// Get the duration. Seconds
		duration = (double)mHeader.getDuration() / mHeader.getTimescale();
		
		// see: http://en.wikipedia.org/wiki/Bit_rate
		int bitrate = (int)(fileBaseResourceInfo.getContentLength() * 8 / duration / 1000);
		
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
	
	private static <T extends Box> T getOrNull(Container container, Class<T> clazz) {
       if (container == null) return null;

       List<T> boxes = container.getBoxes(clazz);
       if (boxes.isEmpty()) {
          return null;
       }
       return boxes.get(0);
    }
}

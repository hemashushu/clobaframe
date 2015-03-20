
package org.archboy.clobaframe.media.video.impl;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaLoader;
import org.archboy.clobaframe.media.video.Video;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class VideoLoader implements MediaLoader {
	
	public static final String MIME_TYPE_VIDEO_MP4 = "video/mp4"; // *.mp4
	public static final String MIME_TYPE_VIDEO_MOV = "video/quicktime"; // *.mov
	
	private List<String> supportMimeTypes = Arrays.asList(MIME_TYPE_VIDEO_MP4, 
			MIME_TYPE_VIDEO_MOV);
	
	@Override
	public boolean support(String mimeType) {
		//return MIME_TYPE_VIDEO_MP4.equals(contentType);
		
		for (String supportMimeType : supportMimeTypes){
			if (supportMimeType.equals(mimeType)){
				return true;
			}
		}
		
		return false;
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
		
		if (duration == 0){
			duration = 1;
		}
		
		// Get some more information from the track header
		List<TrackBox> tb = moov.getBoxes(TrackBox.class);
		if (tb.isEmpty()) {
			return null;
		}
		
		// Get the video with and height
		int width = 0;
		int height = 0;

		for(int idx=0; idx<tb.size(); idx++){
			TrackBox track = tb.get(idx);
			TrackHeaderBox header = track.getTrackHeaderBox();

			int w = (int)header.getWidth();
			int h = (int)header.getHeight();
			
			if (w==0 && h==0){
				// skip the none-video track
				continue;
			}
			
			// Get the video with and height
			width = w;
			height = h;
			break;
		}

		if (width == 0 && height == 0) {
			// no video track found.
			return null;
		}
		
		Video.Format format = (
				MIME_TYPE_VIDEO_MP4.equals(fileBaseResourceInfo.getMimeType())?
				Video.Format.mp4:
				Video.Format.mov);
		
		return new DefaultVideo(
				fileBaseResourceInfo, format, 
				width, height, duration);
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

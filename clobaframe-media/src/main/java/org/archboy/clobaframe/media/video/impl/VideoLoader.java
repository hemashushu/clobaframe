
package org.archboy.clobaframe.media.video.impl;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.apple.AbstractAppleMetaDataBox;
import com.coremedia.iso.boxes.apple.AppleAlbumBox;
import com.coremedia.iso.boxes.apple.AppleArtistBox;
import com.coremedia.iso.boxes.apple.AppleCommentBox;
import com.coremedia.iso.boxes.apple.AppleCustomGenreBox;
import com.coremedia.iso.boxes.apple.AppleEncoderBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.apple.AppleRecordingYearBox;
import com.coremedia.iso.boxes.apple.AppleStandardGenreBox;
import com.coremedia.iso.boxes.apple.AppleTrackAuthorBox;
import com.coremedia.iso.boxes.apple.AppleTrackNumberBox;
import com.coremedia.iso.boxes.apple.AppleTrackTitleBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaLoader;
import static org.archboy.clobaframe.media.image.impl.ImageLoader.CONTENT_TYPE_IMAGE_BMP;
import static org.archboy.clobaframe.media.image.impl.ImageLoader.CONTENT_TYPE_IMAGE_GIF;
import static org.archboy.clobaframe.media.image.impl.ImageLoader.CONTENT_TYPE_IMAGE_JPEG;
import static org.archboy.clobaframe.media.image.impl.ImageLoader.CONTENT_TYPE_IMAGE_PNG;
import org.archboy.clobaframe.media.video.Video;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author yang
 */
@Named
public class VideoLoader implements MediaLoader {
	
	public static final String CONTENT_TYPE_VIDEO_MP4 = "video/mp4"; // *.mp4
	public static final String CONTENT_TYPE_VIDEO_MOV = "video/quicktime"; // *.mov
	
	private List<String> supportContentTypes = Arrays.asList(
			CONTENT_TYPE_VIDEO_MP4, 
			CONTENT_TYPE_VIDEO_MOV);
	
	@Override
	public boolean support(String contentType) {
		//return CONTENT_TYPE_VIDEO_MP4.equals(contentType);
		
		for (String supportContentType : supportContentTypes){
			if (supportContentType.equals(contentType)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Media load(FileBaseResourceInfo fileBaseResourceInfo) throws IOException {
		
		File file = fileBaseResourceInfo.getFile();
		
		IsoFile isoFile = new IsoFile(file);
		
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
		
        // Get some more information from the track header
        // TODO Decide how to handle multiple tracks
        List<TrackBox> tb = moov.getBoxes(TrackBox.class);
        if (tb.isEmpty()) {
			return null;
		}
		
		TrackBox track = tb.get(0);
		TrackHeaderBox header = track.getTrackHeaderBox();
            
		// Get the video with and height
		int width = (int)header.getWidth();
		int height =(int)header.getHeight();

		Video.Format format = (
				CONTENT_TYPE_VIDEO_MP4.equals(fileBaseResourceInfo.getContentType())?
				Video.Format.mp4:
				Video.Format.mov);
		
		return new DefaultVideo(
				fileBaseResourceInfo, format, 
				width, height, duration);
		
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

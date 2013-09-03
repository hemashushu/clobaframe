package org.archboy.clobaframe.media.audio.impl;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.apple.AbstractAppleMetaDataBox;
import com.coremedia.iso.boxes.apple.AppleAlbumBox;
import com.coremedia.iso.boxes.apple.AppleArtistBox;
import com.coremedia.iso.boxes.apple.AppleCustomGenreBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.apple.AppleStandardGenreBox;
import com.coremedia.iso.boxes.apple.AppleTrackAuthorBox;
import com.coremedia.iso.boxes.apple.AppleTrackTitleBox;
import java.util.List;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.audio.Audio;
import org.archboy.clobaframe.media.impl.MetaDataParser;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class M4aMetaDataParser implements MetaDataParser{

	@Override
	public MetaData parse(Object object) {

		Assert.isTrue(object instanceof MovieBox);
		MovieBox moov = (MovieBox)object;

        
        // Get metadata from the User Data Box
        UserDataBox userData = getOrNull(moov, UserDataBox.class);
        if (userData == null) {
			return null;
		}
        
		MetaBox meta = getOrNull(userData, MetaBox.class);

		// Check for iTunes Metadata
		// See http://atomicparsley.sourceforge.net/mpeg-4files.html and
		//  http://code.google.com/p/mp4v2/wiki/iTunesMetadata for more on these
		AppleItemListBox apple = getOrNull(meta, AppleItemListBox.class);
		if (apple == null) {
			return null;
		}
		
		MetaData metaData = new MetaData();
		
		// Title
		AppleTrackTitleBox title = getOrNull(apple, AppleTrackTitleBox.class);
		addMetadata(metaData, Audio.MetaName.Title, title);

		// Artist
		AppleArtistBox artist = getOrNull(apple, AppleArtistBox.class);
		addMetadata(metaData, Audio.MetaName.Artist, artist);

		// Album
		AppleAlbumBox album = getOrNull(apple, AppleAlbumBox.class);
		addMetadata(metaData, Audio.MetaName.Album, album);

		// Composer
		//AppleTrackAuthorBox composer = getOrNull(apple, AppleTrackAuthorBox.class);

		// Genre
		AppleStandardGenreBox sGenre = getOrNull(apple, AppleStandardGenreBox.class);
		AppleCustomGenreBox   cGenre = getOrNull(apple, AppleCustomGenreBox.class);
		addMetadata(metaData, Audio.MetaName.Genre, sGenre);
		addMetadata(metaData, Audio.MetaName.Genre, cGenre);

		// Year
//		AppleRecordingYearBox year = getOrNull(apple, AppleRecordingYearBox.class);
		
		// Track number 
//		AppleTrackNumberBox trackNum = getOrNull(apple, AppleTrackNumberBox.class);
//		if (trackNum != null) {
//		   metadata.set(XMPDM.TRACK_NUMBER, trackNum.getTrackNumber());
//		   //metadata.set(XMPDM.NUMBER_OF_TRACKS, trackNum.getNumberOfTracks()); // TODO
//		}

		// Comment
//		AppleCommentBox comment = getOrNull(apple, AppleCommentBox.class);
		
		// Encoder
//		AppleEncoderBox encoder = getOrNull(apple, AppleEncoderBox.class);
		
		return metaData;
	}
		
	private static <T extends Box> T getOrNull(ContainerBox box, Class<T> clazz) {
       if (box == null) return null;

       List<T> boxes = box.getBoxes(clazz);
       if (boxes.isEmpty()) {
          return null;
       }
       return boxes.get(0);
    }
	
	private static void addMetadata(MetaData metaData, Object key, AbstractAppleMetaDataBox box) {
       if (box != null) {
          metaData.put(key, box.getValue());
       }
    }
}

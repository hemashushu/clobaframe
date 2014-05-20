package org.archboy.clobaframe.media.audio.impl;


import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.googlecode.mp4parser.boxes.apple.AppleAlbumBox;
import com.googlecode.mp4parser.boxes.apple.AppleArtistBox;
import com.googlecode.mp4parser.boxes.apple.AppleGenreBox;
import com.googlecode.mp4parser.boxes.apple.AppleNameBox;
import com.googlecode.mp4parser.boxes.apple.Utf8AppleDataBox;
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
		Assert.isTrue(object instanceof MovieBox, "Support MovieBox class only.");
		
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
		
		for(Box box: apple.getBoxes()){
			System.out.println("BOX:" + box.getType() + "," + box.getClass().getName());
		}
		
		MetaData metaData = new MetaData();
		
		// Title
		AppleNameBox title = getOrNull(apple, AppleNameBox.class);
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
		AppleGenreBox genre = getOrNull(apple, AppleGenreBox.class);
		addMetadata(metaData, Audio.MetaName.Genre, genre);
		
//		AppleCustomGenreBox   cGenre = getOrNull(apple, AppleCustomGenreBox.class);
//		addMetadata(metaData, Audio.MetaName.Genre, cGenre);

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
		
	private static <T extends Box> T getOrNull(Container container, Class<T> clazz) {
       if (container == null) return null;

       List<T> boxes = container.getBoxes(clazz);
       if (boxes.isEmpty()) {
          return null;
       }
       return boxes.get(0);
    }
	
	private static void addMetadata(MetaData metaData, Object key, Utf8AppleDataBox box) {
       if (box != null) {
          metaData.put(key, box.getValue());
       }
    }
}

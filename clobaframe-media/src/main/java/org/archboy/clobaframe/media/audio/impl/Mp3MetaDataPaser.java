package org.archboy.clobaframe.media.audio.impl;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.audio.Audio;
import org.archboy.clobaframe.media.impl.MetaDataParser;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class Mp3MetaDataPaser implements MetaDataParser{

//	public static final String CONTENT_TYPE_AUDIO_MPEG = "audio/mpeg";

//	@Override
//	public boolean support(String contentType) {
//		return (CONTENT_TYPE_AUDIO_MPEG.equals(contentType));
//	}

	@Override
	public MetaData parse(Object object) {
		Assert.isTrue(object instanceof Mp3File, "Support Mp3File class only.");
		
//		FileBaseResourceInfo fileBaseResourceInfo = (FileBaseResourceInfo)object;
//		
//		File file = fileBaseResourceInfo.getFile();
//		
		Mp3File mp3 = (Mp3File)object;
//		try{
//			mp3 = new Mp3File(file.getPath());
//		}catch (UnsupportedTagException e){
//			return null;
//		}catch (InvalidDataException ex) {
//			return null;
//		}catch (IOException e) {
//			return null;
//		}
				
		MetaData metaData = new MetaData();
		
		if (mp3.hasId3v2Tag()) {
			ID3v2 id3v2Tag = mp3.getId3v2Tag();
			metaData.put(Audio.MetaName.Album, id3v2Tag.getAlbum());
			metaData.put(Audio.MetaName.Artist, id3v2Tag.getArtist());
			metaData.put(Audio.MetaName.Genre, id3v2Tag.getGenreDescription());
			metaData.put(Audio.MetaName.Title, id3v2Tag.getTitle());
			metaData.put(Audio.MetaName.Track, id3v2Tag.getTrack());
		}else if(mp3.hasId3v1Tag()) {
			ID3v1 id3v1Tag = mp3.getId3v1Tag();
			metaData.put(Audio.MetaName.Album, id3v1Tag.getAlbum());
			metaData.put(Audio.MetaName.Artist, id3v1Tag.getArtist());
			metaData.put(Audio.MetaName.Genre, id3v1Tag.getGenreDescription());
			metaData.put(Audio.MetaName.Title, id3v1Tag.getTitle());
			metaData.put(Audio.MetaName.Track, id3v1Tag.getTrack());
		}
		
		return metaData;
		
	}
	
}

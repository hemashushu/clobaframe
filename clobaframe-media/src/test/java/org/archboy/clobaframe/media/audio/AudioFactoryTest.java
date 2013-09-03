package org.archboy.clobaframe.media.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.inject.Inject;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.impl.DefaultTemporaryResources;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.image.Image;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AudioFactoryTest {
	
	private static final String DEFAULT_SAMPLE_AUDIO_FOLDER = "sample/audio/";
	private String sampleAudioFolder = DEFAULT_SAMPLE_AUDIO_FOLDER;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MediaFactory mediaFactory;
	
	private TemporaryResources temporaryResources = new DefaultTemporaryResources();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		temporaryResources.close();
	}

	@Test
	public void testMakeMp3() throws IOException {
		File file1 = getFileByName("test1.mp3");
		Audio audio1 = (Audio)mediaFactory.make(file1, temporaryResources);
		
		assertEquals(168, audio1.getBitrate());
		assertEquals(Audio.BitrateMode.variable, audio1.getBitrateMode());
		assertEquals(15, audio1.getDuration());
		assertEquals("mp3", audio1.getEncoding());
		assertEquals(Audio.Format.mp3, audio1.getFormat());
	}
	
	@Test
	public void testMp3MetaData() throws IOException {
		File file1 = getFileByName("test1.mp3");
		Audio audio1 = (Audio)mediaFactory.make(file1, temporaryResources);
		
		MetaData metaData1 = audio1.getMetaData();
		assertNotNull(metaData1);
		
		assertEquals("So long as you are still there", metaData1.get(Audio.MetaName.Title));
		assertEquals("Sandy", metaData1.get(Audio.MetaName.Artist));
		assertEquals("Belong To Me (Disc 1)", metaData1.get(Audio.MetaName.Album));
		assertEquals("01", metaData1.get(Audio.MetaName.Track));
		assertEquals("Pop", metaData1.get(Audio.MetaName.Genre));
		
	}
	
	@Test
	public void testMakeM4a() throws IOException {
		File file1 = getFileByName("test1.m4a");
		InputStream in1 = new FileInputStream(file1);
		Audio audio1 = (Audio)mediaFactory.make(in1, "audio/mp4", new Date(), temporaryResources);
		
		assertEquals(160, audio1.getBitrate());
		assertEquals(Audio.BitrateMode.variable, audio1.getBitrateMode());
		assertEquals(19, audio1.getDuration());
		assertEquals("aac", audio1.getEncoding());
		assertEquals(Audio.Format.m4a, audio1.getFormat());
		
		in1.close();
	}
	
	@Test
	public void testM4aMetaData() throws IOException {
		File file1 = getFileByName("test2.m4a");
		InputStream in1 = new FileInputStream(file1);
		Audio audio1 = (Audio)mediaFactory.make(in1, "audio/mp4", new Date(), temporaryResources);
		//Audio audio1 = (Audio)mediaFactory.make(file1, temporaryResources); // wrong content type.
		
		MetaData metaData1 = audio1.getMetaData();
		assertNotNull(metaData1);
		
		assertEquals("test42", metaData1.get(Audio.MetaName.Title));
		assertEquals("artist", metaData1.get(Audio.MetaName.Artist));
		assertEquals("album", metaData1.get(Audio.MetaName.Album));
		//assertEquals("01", metaData1.get(Audio.MetaName.Track));
		//assertEquals("Pop", metaData1.get(Audio.MetaName.Genre));
		
		in1.close();
	}

	private File getFileByName(String fileName) throws IOException{
		Resource resource = resourceLoader.getResource(sampleAudioFolder + fileName);
		return resource.getFile();
	}
}

package org.archboy.clobaframe.media.video;

import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.impl.DefaultTemporaryResources;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.audio.Audio;
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
public class VideoFactoryTest {
	
	private static final String DEFAULT_SAMPLE_VIDEO_FOLDER = "sample/video/";
	private String sampleVideoFolder = DEFAULT_SAMPLE_VIDEO_FOLDER;

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
	public void testMakeMp4() throws IOException {
		
		// test video 1
		File file1 = getFileByName("test1.mp4");
		Video video1 = (Video)mediaFactory.make(file1, temporaryResources);
				
		assertEquals(1, Math.round(video1.getDuration()));
		assertEquals(Video.Format.mp4, video1.getFormat());
		assertEquals(720, video1.getHeight());
		assertEquals(1280, video1.getWidth());
		
		// test video 2
		File file2 = getFileByName("test2.mp4");
		Video video2 = (Video)mediaFactory.make(file2, temporaryResources);
		
		assertEquals(19, Math.round(video2.getDuration()));
		assertEquals(Video.Format.mp4, video2.getFormat());
		assertEquals(368, video2.getHeight());
		assertEquals(368, video2.getWidth());
	}
	
	@Test
	public void testMakeMov() throws IOException {
		
		// test video 1
		File file1 = getFileByName("test1.mov");
		Video video1 = (Video)mediaFactory.make(file1, temporaryResources);
		
		assertEquals(1, Math.round(video1.getDuration()));
		assertEquals(Video.Format.mov, video1.getFormat());
		assertEquals(720, video1.getHeight());
		assertEquals(1280, video1.getWidth());
	}
	
	private File getFileByName(String fileName) throws IOException{
		Resource resource = resourceLoader.getResource(sampleVideoFolder + fileName);
		return resource.getFile();
	}
}

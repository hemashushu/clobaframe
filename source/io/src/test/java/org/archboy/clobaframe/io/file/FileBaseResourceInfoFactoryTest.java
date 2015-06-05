package org.archboy.clobaframe.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.file.impl.DefaultFileBaseResourceInfoFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class FileBaseResourceInfoFactoryTest {
	
	/**
	 * The content of this file is: 0x30 0x31 ... 0x39.
	 */
	private static final String DEFAULT_SAMPLE_FILE = "sample/data/test.txt";
	private String sampleFile = DEFAULT_SAMPLE_FILE;

	@Inject
	private ResourceLoader resourceLoader;
		
	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	//@Inject
	private FileBaseResourceInfoFactory fileBaseResourceInfoFactory;
	
	@Before
	public void setUp() throws Exception {
		fileBaseResourceInfoFactory = new DefaultFileBaseResourceInfoFactory(mimeTypeDetector);
	}

	@After
	public void tearDown() throws Exception {
		//
	}
	
	@Test
	public void testMakeByFile() throws IOException{
		File file = getFileByName(sampleFile);
		byte[] data = getFileContent(sampleFile);
		
		FileBaseResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(file);
		assertNotNull(resourceInfo);
		
		assertEquals(data.length, resourceInfo.getContentLength());
		assertEquals("text/plain", resourceInfo.getMimeType());
		assertDateEquals(new Date(file.lastModified()), resourceInfo.getLastModified());
		assertTrue(resourceInfo.isSeekable());
		assertEquals(file, resourceInfo.getFile());
		
		InputStream in1 = resourceInfo.getContent();
		assertArrayEquals(data, IOUtils.toByteArray(in1));
		in1.close();
		
		InputStream in2 = resourceInfo.getContent(1, 3);
		assertArrayEquals(new byte[]{0x31,0x32,0x33}, IOUtils.toByteArray(in2));
		in2.close();
		
	}
	
	/**
	 * Get the test resources by file name.
	 *
	 * @param name Relate to the 'src/test/resources' folder.
	 * @return
	 * @throws IOException
	 */
	private File getFileByName(String name) throws IOException{
		Resource resource = resourceLoader.getResource(name); //"file:target/test-classes/" +
		return resource.getFile();
	}

	/**
	 *
	 * @param name Relate to the 'src/test/resources' folder.
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileContent(String name) throws IOException {
		File file = getFileByName(name);
		InputStream in = new FileInputStream(file);
		byte[] data = IOUtils.toByteArray(in);
		in.close();
		return data;
	}
	
	private static void assertDateEquals(Date expected, Date actual){
		if (expected == null && actual == null){
			//
		}else if(expected == null || actual == null){
			fail("date not equals");
		}else{
			assertTrue(Math.abs(expected.getTime() - actual.getTime()) < 1000 );
		}
	}
}

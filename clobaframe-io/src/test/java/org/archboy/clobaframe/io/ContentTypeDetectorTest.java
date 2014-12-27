package org.archboy.clobaframe.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
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
public class ContentTypeDetectorTest {

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private ContentTypeDetector contentTypeAnalyzer;

	private String sampleFile1 = "sample/data/test.jpg";
	private String sampleFile2 = "sample/data/test.png";
	private String sampleFile3 = "sample/data/test.txt";
	private String sampleFile4 = "sample/data/test.xml";
	private String sampleFile5 = "sample/data/test.data"; // it's a png image file.
	private String sampleFile6 = "sample/data/test.css";
	private String sampleFile7 = "sample/data/test.js";
	private String sampleFile8 = "sample/data/test.m4a";
	private String sampleFile9 = "sample/data/test.mov";
	private String sampleFile10 = "sample/data/test.mp3";
	private String sampleFile11 = "sample/data/test.mp4";
	private String sampleFile12 = "sample/data/test-no-extension"; // it's a xml file
	
	private String[] sampleFiles = new String[]{
		sampleFile1, sampleFile2, sampleFile3, sampleFile4, 
		sampleFile5, sampleFile6, sampleFile7, sampleFile8, 
		sampleFile9, sampleFile10, sampleFile11, sampleFile12};
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}
	
	//@Test
	public void testGetByFile() throws IOException{
		
		String[] types = new String[]{
			"image/jpeg","image/png","text/plain","application/xml","image/png",
			"text/css","application/javascript","audio/mpeg","video/quicktime","audio/mpeg","video/mp4",
			"application/xml"};

		/**
		 * The SimpleContentTypeAnalyzerImpl does not support this method yet.
		 */
		
		for(int idx=0; idx<sampleFiles.length; idx++){
			String name = sampleFiles[idx];
			String exceptedType = types[idx];
			if (StringUtils.isNotEmpty(exceptedType)){
				File file = getFileByName(name);
				String type = contentTypeAnalyzer.getByFile(file);
				assertEquals(exceptedType, type);
			}
		}
	}

	@Test
	public void testGetByExtensionName(){
		String[] types = new String[]{
			"image/jpeg","image/png","text/plain","application/xml","application/octet-stream",
			"text/css","application/javascript","audio/mpeg","video/quicktime","audio/mpeg","video/mp4",
			"application/octet-stream"};

		for(int idx=0; idx<sampleFiles.length; idx++){
			String name = sampleFiles[idx];
			String exceptedType = types[idx];
			if (StringUtils.isNotEmpty(exceptedType)){
				String type = contentTypeAnalyzer.getByExtensionName(name);
				assertEquals(exceptedType, type);
			}
		}
	}

	//@Test
	public void testGetByContent() throws IOException{

		String[] types = new String[]{
			"image/jpeg","image/x-apple-ios-png","","","image/x-apple-ios-png",
			"","","video/mp4","video/quicktime","audio/mpeg","video/mp4",
			"application/xml"};
		
		for(int idx=0; idx<sampleFiles.length; idx++){
			String name = sampleFiles[idx];
			String exceptedType = types[idx];
			if (StringUtils.isNotEmpty(exceptedType)){
				InputStream in = getFileInputStream(name);
				String type = contentTypeAnalyzer.getByContent(in);
				assertEquals(exceptedType, type);
			}
		}
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
	 * Remember to close the input stream.
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private InputStream getFileInputStream(String name) throws IOException {
		File file = getFileByName(name);
		InputStream in = new FileInputStream(file);
		return in;
	}
}

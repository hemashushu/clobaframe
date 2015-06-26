package org.archboy.clobaframe.io.file.local;

import org.archboy.clobaframe.io.file.local.LocalResourceScanner;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.inject.Inject;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
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
public class LocalResourceScannerTest {
	
	private static final String DEFAULT_BASE_FOLDER = "sample/data";
	private String baseFolder = DEFAULT_BASE_FOLDER;

	@Inject
	private ResourceLoader resourceLoader;
	
	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}
	
	@Test
	public void testScan() throws IOException{
		File basePath = getFileByName(baseFolder);
		
		FileBaseResourceInfoFactory fileBaseResourceInfoFactory = new DefaultFileBaseResourceInfoFactory(mimeTypeDetector);
		LocalResourceScanner localResourceScanner = new DefaultLocalResourceScanner();
		
		Collection<FileBaseResourceInfo> resourceInfos1 = localResourceScanner.list(basePath, fileBaseResourceInfoFactory);
		
		assertContainsFile(resourceInfos1, getFileByName("sample/data/test.js"));
		assertContainsFile(resourceInfos1, getFileByName("sample/data/test.css"));
		assertContainsFile(resourceInfos1, getFileByName("sample/data/css/test1.css"));
		assertContainsFile(resourceInfos1, getFileByName("sample/data/css/test2.css"));
		assertContainsFile(resourceInfos1, getFileByName("sample/data/image/success-16.png"));
		
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
	
	private static void assertContainsFile(Collection<FileBaseResourceInfo> fileBaseResourceInfos, File expected) {
		boolean found = false;
		for(FileBaseResourceInfo fileBaseResourceInfo : fileBaseResourceInfos) {
			//FileBaseResourceInfo fileBaseResourceInfo = (FileBaseResourceInfo)resourceInfo;
			if (fileBaseResourceInfo.getFile().equals(expected)) {
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
	
}

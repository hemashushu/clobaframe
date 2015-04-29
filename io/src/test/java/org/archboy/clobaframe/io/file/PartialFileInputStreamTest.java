package org.archboy.clobaframe.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.io.file.impl.PartialFileInputStream;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class PartialFileInputStreamTest {

	/**
	 * The data of the sample file is: '0123456789'
	 */
	private static final String DEFAULT_SAMPLE_FILE = "sample/data/test.txt";
	private String sampleFile = DEFAULT_SAMPLE_FILE;

	@Inject
	private ResourceLoader resourceLoader;

	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testPartialFileInputStream() throws IOException {
		File file = getFileByName(sampleFile);
		byte[] sampleData = getFileContent(sampleFile);

		// test read single byte
		InputStream in1 = new PartialFileInputStream(file, 0, 2);
		assertTrue(in1.read() > -1);
		assertTrue(in1.read() > -1);
		assertEquals(-1, in1.read());
		in1.close();

		// test read block
		InputStream in2 = new PartialFileInputStream(file, 2, 5);
		byte[] buffer = new byte[3];
		int readBytes1 = in2.read(buffer, 0, 3);
		assertEquals(3, readBytes1);
		assertArrayEquals(
				Arrays.copyOfRange(sampleData, 2, 5),
				buffer);

		int readBytes2 = in2.read(buffer, 0, 3);
		assertEquals(2, readBytes2);
		assertArrayEquals(
				Arrays.copyOfRange(sampleData, 5, 7),
				Arrays.copyOfRange(buffer, 0, 2));

		int readBytes3 = in2.read(buffer, 0, 3);
		assertEquals(-1, readBytes3);

		in2.close();

		// test range
		InputStream in3 = new PartialFileInputStream(file, 3, 3);
		int readBytes4 = in3.read(buffer, 0, 10);
		assertEquals(3, readBytes4);
		assertArrayEquals(new byte[]{51,52,53}, buffer);
		
		int readBytes5 = in3.read(buffer, 0, 10);
		assertEquals(-1, readBytes5);

		in3.close();
		
		// test range 2
		InputStream in4 = new PartialFileInputStream(file, 3, 3);
		assertArrayEquals(new byte[]{51,52,53}, IOUtils.toByteArray(in4));
		in4.close();
		
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
}

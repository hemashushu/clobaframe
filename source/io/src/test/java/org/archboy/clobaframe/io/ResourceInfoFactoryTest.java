package org.archboy.clobaframe.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.impl.ResourceInfoFactoryImpl;
import org.archboy.clobaframe.io.impl.DefaultTextResourceInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ResourceInfoFactoryTest {

	@Inject
	private ResourceInfoFactory resourceInfoFactory; // = new ResourceInfoFactoryImpl();
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}
	
	@Test
	public void testMakeByFileByteArray() throws IOException{
		
		String mimeType = "application/octet-stream";
		byte[] data = new byte[]{0,1,2,3,4,5};
		Date now = new Date();
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(data, mimeType, now);
		assertNotNull(resourceInfo);
		
		assertEquals(6, resourceInfo.getContentLength());
		assertEquals(mimeType, resourceInfo.getMimeType());
		assertDateEquals(now, resourceInfo.getLastModified());
		assertTrue(resourceInfo.isSeekable());
		
		InputStream in1 = resourceInfo.getContent();
		assertArrayEquals(data, IOUtils.toByteArray(in1));
		in1.close();
		
		InputStream in2 = resourceInfo.getContent(1, 3);
		assertArrayEquals(new byte[]{1,2,3}, IOUtils.toByteArray(in2));
		in2.close();
	}
	
	@Test
	public void testMakeByInputStream() throws IOException{
		String mimeType = "application/octet-stream";
		byte[] data = new byte[]{0,1,2,3,4,5};
		InputStream in = new ByteArrayInputStream(data);
		Date now = new Date();
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(in, data.length, mimeType, now);
		assertNotNull(resourceInfo);
		
		assertEquals(6, resourceInfo.getContentLength());
		assertEquals(mimeType, resourceInfo.getMimeType());
		assertDateEquals(now, resourceInfo.getLastModified());
		assertFalse(resourceInfo.isSeekable());
		
		InputStream in1 = resourceInfo.getContent();
		assertArrayEquals(data, IOUtils.toByteArray(in1));
		in1.close();
		
		in.close();
	}
	
	@Test
	public void testMakeTextResourceInfo() throws IOException {
		//ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();
		Charset charset = Charset.forName("UTF-8");
		String text1 = "hello";
		String mimeType1 = "text/plain";
		Date lastModified1 = new Date();
		
		TextResourceInfo resourceInfo1 = resourceInfoFactory.make(text1, charset, mimeType1, lastModified1);
		assertDateEquals(lastModified1, resourceInfo1.getLastModified());
		assertEquals(mimeType1, resourceInfo1.getMimeType());
		assertEquals(text1, IOUtils.toString(resourceInfo1.getContent(), charset));
		
		// test update content
		String text2 = "world";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		Date lastModified2 = calendar.getTime();
		
		resourceInfo1.updateContent(text2, lastModified2);
		
		assertEquals(lastModified2, resourceInfo1.getLastModified());
		assertEquals(text2, IOUtils.toString(resourceInfo1.getContent(), charset));
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

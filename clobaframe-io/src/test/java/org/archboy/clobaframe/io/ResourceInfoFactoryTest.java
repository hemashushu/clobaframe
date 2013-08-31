package org.archboy.clobaframe.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class ResourceInfoFactoryTest {

	@Inject
	private ResourceInfoFactory resourceInfoFactory;
	
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
		
		String contentType = "application/octet-stream";
		byte[] data = new byte[]{0,1,2,3,4,5};
		Date now = new Date();
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(data, contentType, now);
		assertNotNull(resourceInfo);
		
		assertEquals(6, resourceInfo.getContentLength());
		assertEquals(contentType, resourceInfo.getContentType());
		assertDateEquals(now, resourceInfo.getLastModified());
		assertTrue(resourceInfo.isSeekable());
		
		InputStream in1 = resourceInfo.getInputStream();
		assertArrayEquals(data, IOUtils.toByteArray(in1));
		in1.close();
		
		InputStream in2 = resourceInfo.getInputStream(1, 3);
		assertArrayEquals(new byte[]{1,2,3}, IOUtils.toByteArray(in2));
		in2.close();
	}
	
	@Test
	public void testMakeByInputStream() throws IOException{
		String contentType = "application/octet-stream";
		byte[] data = new byte[]{0,1,2,3,4,5};
		InputStream in = new ByteArrayInputStream(data);
		Date now = new Date();
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(in, data.length, contentType, now);
		assertNotNull(resourceInfo);
		
		assertEquals(6, resourceInfo.getContentLength());
		assertEquals(contentType, resourceInfo.getContentType());
		assertDateEquals(now, resourceInfo.getLastModified());
		assertFalse(resourceInfo.isSeekable());
		
		InputStream in1 = resourceInfo.getInputStream();
		assertArrayEquals(data, IOUtils.toByteArray(in1));
		in1.close();
		
		in.close();
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

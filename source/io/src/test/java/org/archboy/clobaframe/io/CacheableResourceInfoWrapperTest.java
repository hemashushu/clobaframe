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
public class CacheableResourceInfoWrapperTest {

	@Inject
	private ResourceInfoFactory resourceInfoFactory; // = new ResourceInfoFactoryImpl();
	
	@Inject
	private CacheableResourceInfoWrapper cacheableResourceInfoWrapper; // resourceInfoFactory = new ResourceInfoFactoryImpl();
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}
	
	@Test
	public void testMakeCacheableResourceInfo() throws IOException, InterruptedException {
		//ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();
		Charset charset = Charset.forName("UTF-8");
		String text1 = "hello";
		String mimeType1 = "text/plain";
		Date lastModified1 = new Date();
		
		DefaultTextResourceInfo textResourceInfo1 = (DefaultTextResourceInfo)resourceInfoFactory.make(text1, charset, mimeType1, lastModified1);
		CacheableResourceInfo cacheableResourceInfo1 = cacheableResourceInfoWrapper.wrap(textResourceInfo1, 2);
		
		// test update content
		String text2 = "world";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		Date lastModified2 = calendar.getTime();
		
		textResourceInfo1.updateContent(text2, lastModified2);
		assertDateEquals(lastModified1, cacheableResourceInfo1.getLastModified());
		assertEquals(text1, IOUtils.toString(cacheableResourceInfo1.getContent(), charset));
		
		// wait two seconds
		Thread.sleep(2500);
		assertDateEquals(lastModified2, cacheableResourceInfo1.getLastModified());
		assertEquals(text2, IOUtils.toString(cacheableResourceInfo1.getContent(), charset));

		// update content again
		String text3 = "foo";
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		Date lastModified3 = calendar.getTime();
		textResourceInfo1.updateContent(text3, lastModified3);
		
		assertDateEquals(lastModified2, cacheableResourceInfo1.getLastModified());
		assertEquals(text2, IOUtils.toString(cacheableResourceInfo1.getContent(), charset));
		
		// wait two seconds
		Thread.sleep(2500);
		assertDateEquals(lastModified3, cacheableResourceInfo1.getLastModified());
		assertEquals(text3, IOUtils.toString(cacheableResourceInfo1.getContent(), charset));
		
		// test no cache
		DefaultTextResourceInfo textResourceInfo2 = (DefaultTextResourceInfo)resourceInfoFactory.make(text1, charset, mimeType1, lastModified1);
		CacheableResourceInfo cacheableResourceInfo2 = cacheableResourceInfoWrapper.wrap(textResourceInfo2, 0);
		textResourceInfo2.updateContent(text2, lastModified2);
		
		assertDateEquals(lastModified2, cacheableResourceInfo2.getLastModified());
		assertEquals(text2, IOUtils.toString(cacheableResourceInfo2.getContent(), charset));
		
		// test cache always
		DefaultTextResourceInfo textResourceInfo3 = (DefaultTextResourceInfo)resourceInfoFactory.make(text1, charset, mimeType1, lastModified1);
		CacheableResourceInfo cacheableResourceInfo3 = cacheableResourceInfoWrapper.wrap(textResourceInfo3, -1);
		textResourceInfo3.updateContent(text2, lastModified2);
		
		// wait two seconds
		Thread.sleep(2500);
		assertDateEquals(lastModified1, cacheableResourceInfo3.getLastModified());
		assertEquals(text1, IOUtils.toString(cacheableResourceInfo3.getContent(), charset));
		
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

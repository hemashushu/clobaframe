package org.archboy.clobaframe.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.ioc.BeanFactory;
import org.archboy.clobaframe.ioc.impl.DefaultBeanFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author yang
 */
public class ResourceManagerWithIoCTest {

	private BeanFactory beanFactory;
	private ResourceManager resourceManager;

	private final Logger logger = LoggerFactory.getLogger(ResourceManagerWithIoCTest.class);

	@Before
	public void setUp() throws Exception {
		this.beanFactory = new DefaultBeanFactory(
				"classpath:application.properties", 
				"classpath:clobaframe.properties");
		this.resourceManager = beanFactory.getBean(ResourceManager.class);
	}

	@After
	public void tearDown() throws Exception {
		beanFactory.close();
	}

	@Test
	public void testGetAllBaseResources() throws FileNotFoundException {
		// test get all base resources

		String[] names = new String[]{
			"test.css", "test.js", "test.png",
			"css/test2.css", "css/test3.css", "css/test4.css", "css/test5.css",
			"fonts/fontawesome-webfont.eot","fonts/fontawesome-webfont.svg","fonts/fontawesome-webfont.ttf","fonts/fontawesome-webfont.woff",
			"image/info-32.png", "image/success-16.png", "image/warn-16.png"
		};
		
		for (String name : names) {
			System.out.println(name);
			NamedResourceInfo webResourceInfo = resourceManager.getServedResource(name);
			assertTrue(webResourceInfo.getContentLength() > 0);
		}
		
		List<String> nameList1 = new ArrayList<String>();
		Collection<NamedResourceInfo> resourcesByManager1 = resourceManager.list();
		for(NamedResourceInfo resourceInfo : resourcesByManager1){
			nameList1.add(resourceInfo.getName());
		}
		
		for(String name : names) {
			assertTrue(nameList1.contains(name));
		}
		
		assertNull(resourceManager.get("test-none-exists.css"));
		assertNull(resourceManager.get("css/test-none-exists.css"));
		
		assertNull(resourceManager.getServedResource("test-none-exists.css"));
		assertNull(resourceManager.getServedResource("css/test-none-exists.css"));
	}
}

package org.archboy.clobaframe.setting.application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ApplicationSettingTest {

	private final Logger logger = LoggerFactory.getLogger(ApplicationSettingTest.class);
	
	@Inject
	private ApplicationSetting systemSetting;
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testGetValue(){
		String appVersion = (String)systemSetting.getValue("app.version");
		String testFoo = (String)systemSetting.getValue("test.foo");
		String testBar = (String)systemSetting.getValue("test.bar");
		String osName = (String)systemSetting.getValue("os.name");
		String itemValue = (String)systemSetting.getValue("item");
		
		assertEquals("1.0", appVersion);
		assertEquals("hello", testFoo);
		assertEquals("123456", testBar);
		
		String userName = (String)systemSetting.getValue("user.name");
		assertEquals(testFoo + " " + userName , systemSetting.getValue("test.com"));
		
		assertFalse("aaa".equals(osName)); // override by system properties
		assertEquals("application", itemValue);
		
		// test none-exists
		assertNull(systemSetting.getValue("test.none-exist"));
		assertEquals("defaultValue", systemSetting.getValue("test.none-exist", "defaultValue"));
		
		// test get original value
		assertEquals("${test.foo} ${user.name}", systemSetting.get("test.com"));
		
		// test get all
		assertTrue(systemSetting.getAll().size() > 1);
	
		logger.info("temp dir:" + systemSetting.getValue("java.io.tmpdir"));
		logger.info("user home dir:" + systemSetting.getValue("user.home"));
	}
	
	@Test
	public void testSet(){
		String testStatus = (String)systemSetting.getValue("app.set.status");
		String testUpdate = (String)systemSetting.getValue("app.set.update");

		if ("original".equals(testStatus)){
			assertEquals("bbb", testUpdate);
			
			systemSetting.set("app.set.status", "updated");
			systemSetting.set("app.set.update", "ccc");
			
		}else{
			assertEquals("ccc", testUpdate);
			
			systemSetting.set("app.set.status", "original");
			systemSetting.set("app.set.update", "bbb");
		}
	}
	
}

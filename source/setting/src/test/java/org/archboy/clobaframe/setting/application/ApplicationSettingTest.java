package org.archboy.clobaframe.setting.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
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
	private ApplicationSetting applicationSetting;
	
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
		String appVersion = (String)applicationSetting.getValue("app.version");
		String testFoo = (String)applicationSetting.getValue("test.foo");
		String testBar = (String)applicationSetting.getValue("test.bar");
		String osName = (String)applicationSetting.getValue("os.name");
		//String itemValue = (String)applicationSetting.getValue("item");
		
		assertEquals("1.0", appVersion);
		assertEquals("hello", testFoo);
		assertEquals("123456", testBar);
		
		String userName = (String)applicationSetting.getValue("user.name");
		assertEquals(testFoo + " " + userName , applicationSetting.getValue("test.com"));
		
		assertFalse("aaa".equals(osName)); // override by system properties
		//assertEquals("application", itemValue);
		
		// test none-exists
		assertNull(applicationSetting.getValue("test.none-exist"));
		assertEquals("defaultValue", applicationSetting.getValue("test.none-exist", "defaultValue"));
		
		// test get original value
		assertEquals("${test.foo} ${user.name}", applicationSetting.get("test.com"));
		
		// test get all
		assertTrue(applicationSetting.getAll().size() > 1);
	
		// test inject
		assertEquals("ok", applicationSetting.get("foo.inject"));
		
//		logger.info("temp dir:" + applicationSetting.getValue("java.io.tmpdir"));
//		logger.info("user home dir:" + applicationSetting.getValue("user.home"));
	}
	
	@Test
	public void testSet(){
		String testStatus = (String)applicationSetting.getValue("app.set.status");
		String testUpdate = (String)applicationSetting.getValue("app.set.update");

		if ("original".equals(testStatus)){
			assertEquals("bbb", testUpdate);
			
			applicationSetting.set("app.set.status", "updated");
			applicationSetting.set("app.set.update", "ccc");
			
		}else{
			assertEquals("ccc", testUpdate);
			
			applicationSetting.set("app.set.status", "original");
			applicationSetting.set("app.set.update", "bbb");
		}
	}
	
	public static class TestingPostApplicationSetting implements PostApplicationSetting {

		@Override
		public void execute(Map<String, Object> settings) throws Exception {
			settings.put("foo.inject", "ok");
		}
	}
}

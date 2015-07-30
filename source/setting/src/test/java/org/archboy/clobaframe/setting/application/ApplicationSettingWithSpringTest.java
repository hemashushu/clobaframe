package org.archboy.clobaframe.setting.application;

import java.util.LinkedHashMap;
import java.util.Map;
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
public class ApplicationSettingWithSpringTest {

	private final Logger logger = LoggerFactory.getLogger(ApplicationSettingWithSpringTest.class);
	
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
		
		// test manager
		assertEquals("clobaframe", applicationSetting.getApplicationName());
		
		// test root setting
		assertEquals("rootFileOk", applicationSetting.get("clobaframe.setting.test.root.file"));
		assertEquals("rootPropOk", applicationSetting.get("clobaframe.setting.test.root.prop"));
		
		// test default setting and other buildin settings.
		String testFoo = (String)applicationSetting.getValue("test.foo");
		String testBar = (String)applicationSetting.getValue("test.bar");
		String testCom = (String)applicationSetting.getValue("test.com");
		String osName = (String)applicationSetting.getValue("os.name");

		assertEquals("hello", testFoo);
		assertEquals("123456", testBar);
		
		// test get from system properties
		String userName = (String)applicationSetting.getValue("user.name");
		assertEquals(System.getProperty("user.name"), userName);
		
		// test concatenation
		assertEquals(testFoo + " " + userName, testCom);
		
		// test override system properties
		assertFalse("aaa".equals(osName)); 
		
		// test none-exists
		assertNull(applicationSetting.getValue("test.none-exist"));
		assertEquals("defaultValue", applicationSetting.getValue("test.none-exist", "defaultValue"));
		
		// test get original value
		assertEquals("${test.foo} ${user.name}", applicationSetting.get("test.com"));
		
		// test get all
		Map<String, Object> all = applicationSetting.list();
		assertTrue(all.containsKey("test.foo"));
		assertTrue(all.containsKey("test.bar"));
		assertTrue(all.containsKey("test.com"));
		assertTrue(all.containsKey("user.name"));
		assertTrue(all.containsKey("user.home"));
		assertTrue(all.containsKey("java.io.tmpdir"));
		
		// test other in-application-package settings
		assertEquals("ok", applicationSetting.get("test.other"));
		
		// test layer
		assertEquals("layerOk", applicationSetting.get("test.layer"));
		
		// test post setting
		assertEquals("ok", applicationSetting.get("test.postSetting"));
		
		// test inject
		assertNull(applicationSetting.get("test.inject"));
		
		ApplicationSettingProvider provider2 = new TestingInjectSettingProvider();
		applicationSetting.addProvider(provider2);
		
		assertNull(applicationSetting.get("test.inject"));
		applicationSetting.refresh();
		assertEquals("injectOk", applicationSetting.get("test.inject"));
		
		applicationSetting.removeProvider(provider2.getName());
		assertEquals("injectOk", applicationSetting.get("test.inject"));
		applicationSetting.refresh();
		assertNull(applicationSetting.get("test.inject"));
	}

	@Test
	public void testSet(){
		logger.info("user home dir:" + applicationSetting.getValue("user.home"));
		logger.info("temp dir:" + applicationSetting.getValue("java.io.tmpdir"));
		
		String testStatus = (String)applicationSetting.getValue("app.set.status");
		String testUpdate = (String)applicationSetting.getValue("app.set.update");

		if ("original".equals(testStatus)){
			applicationSetting.set("app.set.status", "updated");
			applicationSetting.set("app.set.update", "ccc");
			
			assertEquals("bbb", testUpdate);
			
		}else{
			applicationSetting.set("app.set.status", "original");
			applicationSetting.set("app.set.update", "bbb");
			
			assertEquals("ccc", testUpdate);
		}
	}
	
	public static class TestingPostApplicationSetting implements PostInitialedHandler {

		@Override
		public void execute(Map<String, Object> settings) throws Exception {
			settings.put("test.postSetting", "ok");
		}
	}
	
	public static class TestingInjectSettingProvider implements ApplicationSettingProvider {

		@Override
		public String getName() {
			return "testInjectSetting";
		}

		@Override
		public Map<String, Object> list() {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("test.inject", "injectOk");
			return map;
		}

		@Override
		public int getOrder() {
			return PRIORITY_NORMAL;
		}
		
	}
}

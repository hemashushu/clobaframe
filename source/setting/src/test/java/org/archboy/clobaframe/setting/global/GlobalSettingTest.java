package org.archboy.clobaframe.setting.global;

import org.archboy.clobaframe.setting.global.GlobalSettingRepository;
import org.archboy.clobaframe.setting.global.GlobalSettingProvider;
import org.archboy.clobaframe.setting.global.GlobalSetting;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.support.Utils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class GlobalSettingTest {
	
	private final Logger logger = LoggerFactory.getLogger(GlobalSettingTest.class);
	
	@Inject
	private GlobalSetting globalSetting;
	
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
		String testId = (String)globalSetting.getValue("test.id");
		String testName = (String)globalSetting.getValue("test.name");
		
		assertEquals("123456", testId);
		assertEquals("foobar", testName);
		
		// test  // override by 'global-layer2.json'
		
		assertEquals("layerOk", globalSetting.getValue("test.layer"));
		assertEquals(Boolean.TRUE, globalSetting.getValue("test.other"));
		
		// test none-exists
		assertNull(globalSetting.getValue("test.none-exist"));
		assertEquals("defaultValue", globalSetting.getValue("test.none-exist", "defaultValue"));
		
	}
	
	@Test
	public void testSet(){
		String testStatus = (String)globalSetting.getValue("global.set.status");
		String testUpdate = (String)globalSetting.getValue("global.set.update");

		if ("original".equals(testStatus)){
			globalSetting.set("global.set.status", "updated");
			globalSetting.set("global.set.update", "eee");

			assertEquals("ddd", testUpdate);
		}else{
			globalSetting.set("global.set.status", "original");
			globalSetting.set("global.set.update", "ddd");
			
			assertEquals("eee", testUpdate);
		}
	}
	
	@Named
	public static class TestingGlobalSettingProvider implements GlobalSettingProvider {

		@Inject
		private ResourceLoader resourceLoader;

		@Override
		public String getName() {
			return "testingGlobalSetting";
		}

		@Override
		public int getOrder() {
			return PRIORITY_NORMAL;
		}

		@Override
		public Map<String, Object> list() {
			try{
				File file = getFileByName("global-layer2.json");
				InputStream in = new FileInputStream(file);
				return Utils.readJson(in);
			}catch(IOException e){
				// ignore
			}
			
			return new LinkedHashMap<String, Object>();
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
	}
	
	@Named
	public static class TestingGlobalSettingRepository implements GlobalSettingProvider, GlobalSettingRepository {

		protected Map<String, Object> setting = new LinkedHashMap<String, Object>();

		@Override
		public String getName() {
			return "testingGlobalSettingRepository";
		}
		
		@Override
		public int getOrder() {
			return PRIORITY_HIGH;
		}

		@Override
		public Map<String, Object> list() {
			return setting;
		}
		
		@Override
		public void update(Map<String, Object> item) {
			setting = Utils.merge(setting, item);
		}

		@Override
		public void update(String key, Object value) {
			setting = Utils.merge(setting, key, value);
		}
	}
	
}

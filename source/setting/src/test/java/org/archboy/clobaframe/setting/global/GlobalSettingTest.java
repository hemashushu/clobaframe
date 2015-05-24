package org.archboy.clobaframe.setting.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.support.Utils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
		String siteTitle = (String)globalSetting.getValue("site.title");
		String itemValue = (String)globalSetting.getValue("item");
		
		assertEquals("the clobaframe project", siteTitle);
		assertEquals("abc", itemValue); // override by 'sample/test.json'
		
		assertEquals("xyz", globalSetting.getValue("sub.item"));
		assertEquals("123456", globalSetting.getValue("foo.bar.id"));
		assertEquals("world", globalSetting.getValue("foo.bar.name"));
		
		// test none-exists
		assertNull(globalSetting.getValue("test.none-exist"));
		assertEquals("defaultValue", globalSetting.getValue("test.none-exist", "defaultValue"));
		
	}
	
	@Test
	public void testSet(){
		String testStatus = (String)globalSetting.getValue("instance.set.status");
		String testUpdate = (String)globalSetting.getValue("instance.set.update");

		if ("original".equals(testStatus)){
			assertEquals("ddd", testUpdate);
			
			globalSetting.set("instance.set.status", "updated");
			globalSetting.set("instance.set.update", "eee");
			
		}else{
			assertEquals("eee", testUpdate);
			
			globalSetting.set("instance.set.status", "original");
			globalSetting.set("instance.set.update", "ddd");
		}
	}
	
	@Named
	public static class TestingInstanceSettingProvider implements GlobalSettingProvider {

		@Inject
		private ResourceLoader resourceLoader;

		public TestingInstanceSettingProvider() {
			super();
		}
		
		@Override
		public int getOrder() {
			return 5;
		}

		@Override
		public Map<String, Object> getAll() {
			try{
				File file = getFileByName("sample/test.json");
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
	public static class InMemoryInstanceSettingRepository implements GlobalSettingProvider, GlobalSettingRepository {

		protected Map<String, Object> setting = new LinkedHashMap<String, Object>();
		
		@Override
		public int getOrder() {
			return 1;
		}

		@Override
		public Map<String, Object> getAll() {
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

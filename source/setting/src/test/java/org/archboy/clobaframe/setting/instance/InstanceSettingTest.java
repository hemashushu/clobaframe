package org.archboy.clobaframe.setting.instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.support.AbstractJsonSettingAccess;
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
public class InstanceSettingTest {
	
	private final Logger logger = LoggerFactory.getLogger(InstanceSettingTest.class);
	
	@Inject
	private InstanceSetting instanceSetting;
	
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
		String siteTitle = (String)instanceSetting.getValue("site.title");
		String itemValue = (String)instanceSetting.getValue("item");
		
		assertEquals("the clobaframe project", siteTitle);
		assertEquals("abc", itemValue); // override by 'sample/test.json'
		
		assertEquals("xyz", instanceSetting.getValue("sub.item"));
		assertEquals("123456", instanceSetting.getValue("foo.bar.id"));
		assertEquals("world", instanceSetting.getValue("foo.bar.name"));
		
		// test none-exists
		assertNull(instanceSetting.getValue("test.none-exist"));
		assertEquals("defaultValue", instanceSetting.getValue("test.none-exist", "defaultValue"));
		
	}
	
	@Test
	public void testSet(){
		String testStatus = (String)instanceSetting.getValue("instance.set.status");
		String testUpdate = (String)instanceSetting.getValue("instance.set.update");

		if ("original".equals(testStatus)){
			assertEquals("ddd", testUpdate);
			
			instanceSetting.set("instance.set.status", "updated");
			instanceSetting.set("instance.set.update", "eee");
			
		}else{
			assertEquals("eee", testUpdate);
		}
	}
	
	@Named
	public static class TestingInstanceSettingProvider extends AbstractJsonSettingAccess implements InstanceSettingProvider {

		@Inject
		private ResourceLoader resourceLoader;

		public TestingInstanceSettingProvider() {
			super();
		}
		
		@Override
		public int getPriority() {
			return PRIORITY_NORMAL;
		}

		@Override
		public Map<String, Object> getAll() {
			try{
				File file = getFileByName("sample/test.json");
				InputStream in = new FileInputStream(file);
				return read(in);
			}catch(IOException e){
				// ignore
			}
			
			return new HashMap<String, Object>();
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
	public static class InMemoryInstanceSettingRepository implements InstanceSettingProvider, InstanceSettingRepository {

		protected Map<String, Object> setting = new LinkedHashMap<String, Object>();
		
		@Override
		public int getPriority() {
			return PRIORITY_HIGH;
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

package org.archboy.clobaframe.setting.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.archboy.clobaframe.setting.SettingProvider;
import org.archboy.clobaframe.setting.system.impl.EnvironmentSettingProvider;
import org.archboy.clobaframe.setting.system.impl.PropertiesSettingProvider;
import org.springframework.core.io.Resource;
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
public class SystemSettingTest {

	@Inject
	private SystemSetting systemSetting;
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testGetSystemSetting(){
		String osName = (String)systemSetting.get("os.name");
		System.out.println(osName);
		
		for(Map.Entry<String, Object> entry : systemSetting.getAll().entrySet()){
			System.out.println("item: " + entry.getKey() + "=" + entry.getValue());
		}
		
		System.out.println(systemSetting.get("setting.dataDir"));
		systemSetting.set(systemSetting.getAll());
	}
}

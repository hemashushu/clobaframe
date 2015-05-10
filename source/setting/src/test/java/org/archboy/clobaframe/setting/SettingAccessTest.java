package org.archboy.clobaframe.setting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.impl.AbstractJsonSettingAccess;
import org.archboy.clobaframe.setting.impl.AbstractPropertiesFileSettingAccess;
import org.archboy.clobaframe.setting.impl.SettingAccess;
import org.archboy.clobaframe.setting.impl.Support;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class SettingAccessTest {
	
	@Inject
	private ResourceLoader resourceLoader;
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testPropertiesFileSettingAccess() throws IOException{
		
		String text1 = "item=abc\n" +
				"sub.item=xyz\n" + 
				"foo.bar.id=123456\n" +
				"foo.bar.name=world\n" +					
				"foo.com.id=${foo.bar.id}789\n" +
				"foo.com.name=hello ${foo.bar.name}\n" +
				"foo.com.concat=${foo.bar.id}-${foo.bar.name}\n" +
				"foo.com.depth=Mr. ${foo.com.name}\n" +
				"foo.com.depthx2=hello ${foo.com.depth}\n" +
				"broken.name=hello ${foo.bar.firstName}\n" +
				"broken.part=${foo.bar.id}-${foo.bar.firstName}";
		
		SettingAccess settingAccess = new AbstractPropertiesFileSettingAccess() {};
		InputStream in1 = IOUtils.toInputStream(text1);
		
		Map<String, Object> setting1 = settingAccess.read(in1);
		assertEquals("abc", setting1.get("item"));
		assertEquals("xyz", setting1.get("sub.item"));
		assertEquals("123456", setting1.get("foo.bar.id"));
		assertEquals("world", setting1.get("foo.bar.name"));
		assertEquals("${foo.bar.id}789", setting1.get("foo.com.id"));
		assertEquals("hello ${foo.bar.name}", setting1.get("foo.com.name"));
		assertEquals("${foo.bar.id}-${foo.bar.name}", setting1.get("foo.com.concat"));
		assertEquals("Mr. ${foo.com.name}", setting1.get("foo.com.depth"));
		assertEquals("hello ${foo.com.depth}", setting1.get("foo.com.depthx2"));
		assertEquals("hello ${foo.bar.firstName}", setting1.get("broken.name"));
		assertEquals("${foo.bar.id}-${foo.bar.firstName}", setting1.get("broken.part"));
		
		in1.close();
		
		// test write
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		settingAccess.write(out1, setting1);
		String text2 = out1.toString();
		out1.close();
		
		assertLinesEquals(text1, text2, 0, 1); // cause of properties appended a timestamp to the file.
	}

	@Test
	public void testJsonSettingAccess() throws IOException{
		File file1 = getFileByName("sample/test.json");
		InputStream in1 = new FileInputStream(file1);
		
		SettingAccess settingAccess = new AbstractJsonSettingAccess() {};
		
		Map<String, Object> setting1 = settingAccess.read(in1);
		assertEquals("abc", setting1.get("item"));
		assertEquals("xyz", setting1.get("sub.item"));
		assertEquals("123456", setting1.get("foo.bar.id"));
		assertEquals("world", setting1.get("foo.bar.name"));
		assertEquals("${foo.bar.id}789", setting1.get("foo.com.id"));
		assertEquals("hello ${foo.bar.name}", setting1.get("foo.com.name"));
		assertEquals("${foo.bar.id}-${foo.bar.name}", setting1.get("foo.com.concat"));
		assertEquals("Mr. ${foo.com.name}", setting1.get("foo.com.depth"));
		assertEquals("hello ${foo.com.depth}", setting1.get("foo.com.depthx2"));
		assertEquals("hello ${foo.bar.firstName}", setting1.get("broken.name"));
		assertEquals("${foo.bar.id}-${foo.bar.firstName}", setting1.get("broken.part"));
		
		in1.close();
		
		// test write
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		settingAccess.write(out1, setting1);
		String text2 = out1.toString();
		out1.close();
		
		InputStream in2 = new FileInputStream(file1);
		String text1 = IOUtils.toString(in2);
		in2.close();
		
		assertLinesEquals(text1, text2, 0, 0);
	}
	
	private void assertLinesEquals(String text1, String text2, int offset1, int offset2) {
		String[] lines1 = text1.split("\r?\n");
		String[] lines2 = text2.split("\r?\n");
		
		boolean equals = true;
		for(int idx=0; idx<lines1.length; idx++){
			if (!lines1[idx + offset1].equals(lines2[idx + offset2])){
				equals = false;
				break;
			}
		}
		
		assertTrue("Head lines not equals", equals);
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

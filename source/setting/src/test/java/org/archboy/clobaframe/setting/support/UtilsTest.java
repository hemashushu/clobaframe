package org.archboy.clobaframe.setting.support;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
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
public class UtilsTest {
	
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
	public void testResolvePlaceholder() throws IOException{
		
		Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		setting1.put("item","abc");
		setting1.put("sub.item","xyz");
		setting1.put("foo.bar.id","123456");
		setting1.put("foo.bar.name","world");					
		setting1.put("foo.com.id","${foo.bar.id}789");
		setting1.put("foo.com.name","hello ${foo.bar.name}");
		setting1.put("foo.com.concat","${foo.bar.id}-${foo.bar.name}");
		setting1.put("foo.com.depth","Mr. ${foo.com.name}");
		setting1.put("foo.com.depthx2","hello ${foo.com.depth}");
		setting1.put("broken.name","hello ${foo.bar.firstName}");
		setting1.put("broken.part","${foo.bar.id}-${foo.bar.firstName}");
		
		// test resolve placeholder
		assertEquals("123456789", Utils.resolvePlaceholder(setting1, setting1.get("foo.com.id")));
		assertEquals("hello world", Utils.resolvePlaceholder(setting1, setting1.get("foo.com.name")));
		assertEquals("123456-world", Utils.resolvePlaceholder(setting1, setting1.get("foo.com.concat")));
		assertEquals("Mr. hello world", Utils.resolvePlaceholder(setting1, setting1.get("foo.com.depth")));
		assertEquals("hello Mr. hello world", Utils.resolvePlaceholder(setting1, setting1.get("foo.com.depthx2")));
		assertEquals("hello ${foo.bar.firstName}", Utils.resolvePlaceholder(setting1, setting1.get("broken.name")));
		assertEquals("123456-${foo.bar.firstName}", Utils.resolvePlaceholder(setting1, setting1.get("broken.part")));
	}
	
	@Test
	public void testMerge(){
		Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		setting1.put("item","abc");
		setting1.put("sub.item","xyz");
		setting1.put("foo.bar.id","123456");
		setting1.put("foo.bar.name","world");					
		setting1.put("foo.com.id","${foo.bar.id}789");
		setting1.put("foo.com.name","hello ${foo.bar.name}");
		setting1.put("foo.com.concat","${foo.bar.id}-${foo.bar.name}");
		setting1.put("foo.com.depth","Mr. ${foo.com.name}");
		
		// test merge
		setting1 = Utils.merge(setting1, "new.id", 123);
		setting1 = Utils.merge(setting1, "new.checked", true);
		setting1 = Utils.merge(setting1, "foo.bar.id", "000"); // override
		
		assertEquals(123, setting1.get("new.id"));
		assertEquals(Boolean.TRUE, setting1.get("new.checked"));
		assertEquals("000", setting1.get("foo.bar.id"));
		assertEquals("000789", Utils.resolvePlaceholder(setting1, setting1.get("foo.com.id")));
	
		// test merge Map
		Map<String, Object> setting2 = new LinkedHashMap<String, Object>();
		setting2.put("mer.id", 456);
		setting2.put("mer.name", "merge");
		setting2.put("foo.bar.name", "MM"); // override
		
		setting1 = Utils.merge(setting1, setting2);
		assertEquals(456, setting1.get("mer.id"));
		assertEquals("merge", setting1.get("mer.name"));
		assertEquals("MM", setting1.get("foo.bar.name"));
		assertEquals("hello MM", Utils.resolvePlaceholder(setting1, setting1.get("foo.com.name")));
	}

	@Test
	public void testFlat(){
		Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		setting1.put("item","abc");
		
		Map<String, Object> sub = new LinkedHashMap<String, Object>();
		sub.put("item", "xyz");
		
		Map<String, Object> bar = new LinkedHashMap<String, Object>();
		bar.put("id", "123456");
		bar.put("name", "world");

		Map<String, Object> com = new LinkedHashMap<String, Object>();
		com.put("id", "${foo.bar.id}789");
		com.put("name", "hello ${foo.bar.name}");
		com.put("concat", "${foo.bar.id}-${foo.bar.name}");
		com.put("depth", "Mr. ${foo.com.name}");

		setting1.put("sub", sub);
		
		Map<String, Object> foo = new LinkedHashMap<String, Object>();
		foo.put("bar", bar);
		foo.put("com", com);
		
		setting1.put("foo", foo);
		
		Map<String, Object> flat1 = Utils.flat(setting1);
		
		assertEquals(8, flat1.size());
		assertEquals("abc", flat1.get("item"));
		assertEquals("xyz", flat1.get("sub.item"));
		assertEquals("123456", flat1.get("foo.bar.id"));
		assertEquals("world", flat1.get("foo.bar.name"));					
		assertEquals("${foo.bar.id}789", flat1.get("foo.com.id"));
		assertEquals("hello ${foo.bar.name}", flat1.get("foo.com.name"));
		assertEquals("${foo.bar.id}-${foo.bar.name}", flat1.get("foo.com.concat"));
		assertEquals("Mr. ${foo.com.name}", flat1.get("foo.com.depth"));
	}
	
	@Test
	public void testCascade(){
		Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		setting1.put("item","abc");
		setting1.put("sub.item","xyz");
		setting1.put("foo.bar.id","123456");
		setting1.put("foo.bar.name","world");					
		setting1.put("foo.com.id","${foo.bar.id}789");
		setting1.put("foo.com.name","hello ${foo.bar.name}");
		setting1.put("foo.com.concat","${foo.bar.id}-${foo.bar.name}");
		setting1.put("foo.com.depth","Mr. ${foo.com.name}");
		
		Map<String, Object> cascade1 = Utils.cascade(setting1);
	
		assertEquals(3, cascade1.size());
		assertEquals("abc", cascade1.get("item"));
		assertTrue(cascade1.containsKey("sub"));
		assertTrue(cascade1.containsKey("foo"));
		
		Map<String, Object> sub = (Map<String, Object>)cascade1.get("sub");
		assertEquals(1, sub.size());
		assertEquals("xyz", sub.get("item"));
		
		Map<String, Object> foo = (Map<String, Object>)cascade1.get("foo");
		assertEquals(2, foo.size());
		assertTrue(foo.containsKey("bar"));
		assertTrue(foo.containsKey("com"));
		
		Map<String, Object> bar = (Map<String, Object>)foo.get("bar");
		assertEquals(2, bar.size());
		assertEquals("123456", bar.get("id"));
		assertEquals("world", bar.get("name"));
		
		Map<String, Object> com = (Map<String, Object>)foo.get("com");
		assertEquals(4, com.size());
		assertEquals("${foo.bar.id}789", com.get("id"));
		assertEquals("hello ${foo.bar.name}", com.get("name"));
		assertEquals("${foo.bar.id}-${foo.bar.name}", com.get("concat"));
		assertEquals("Mr. ${foo.com.name}", com.get("depth"));
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
		
		InputStream in1 = IOUtils.toInputStream(text1);
		
		Map<String, Object> setting1 = Utils.readProperties(in1);
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
		Utils.writeProperties(out1, setting1);
		String text2 = out1.toString();
		out1.close();
		
		assertLinesEquals(text1, text2, 0, 1); // cause of properties appended a timestamp to the file.
	}

	@Test
	public void testJsonSettingAccess() throws IOException{
		File file1 = getFileByName("sample/test.json");
		InputStream in1 = new FileInputStream(file1);
		
		//SettingAccess settingAccess = new AbstractJsonSettingAccess() {};
		
		Map<String, Object> setting1 = Utils.readJson(in1);
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
		Utils.writeJson(out1, setting1);
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

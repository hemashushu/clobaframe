package org.archboy.clobaframe.setting.support;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class UtilsTest {
	
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
}

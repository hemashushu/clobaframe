package org.archboy.clobaframe.common.collection;

import java.util.Date;
import java.util.Set;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author yang
 */
public class ObjectMapTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCreate() {
		Date now = new Date();

		ObjectMap model = new DefaultObjectMap();

		// add properties
		model.add("id", "123")
				.add("score", "456")
				.add("name", "token")
				.add("creation", now);

		assertEquals("123", model.get("id"));
		assertEquals("456", model.get("score"));
		assertEquals("token", model.get("name"));
		assertEquals(now, model.get("creation"));

		assertNull(model.get("none"));

		// add new property and update old value
		model.add("enable", Boolean.TRUE);
		model.add("count", 100);
		// update value
		model.add("score", "999");

		assertEquals("999", model.get("score"));
		assertEquals(Boolean.TRUE, model.get("enable"));
		assertEquals(100, model.get("count"));

		// iterate property names
		Set<String> keys = model.keySet();

		assertEquals(6, keys.size());
		assertTrue(keys.contains("id"));
		assertTrue(keys.contains("score"));
		assertTrue(keys.contains("name"));
		assertTrue(keys.contains("creation"));
		assertTrue(keys.contains("count"));
		assertTrue(keys.contains("enable"));
		assertFalse(keys.contains("none"));

		// test child model

		model.add("id", "123")
			.add("name", "foo")
			.addChild("child")
				.add("width", 800)
				.add("height", 600)
				.addChild("color")
					.add("r", 128)
					.add("g", 0)
					.add("b", 255)
				.parent()
				.addChild("font")
					.add("name", "monospace")
					.add("size", 16)
			.top()
			.add("lock", true)
			.add("lastModified", now);

		ObjectMap childModel = (ObjectMap)model.get("child");
		assertEquals(800, childModel.get("width"));
		assertEquals(600, childModel.get("height"));
		
		ObjectMap colorModel = (ObjectMap)childModel.get("color");
		assertEquals(128, colorModel.get("r"));
		assertEquals(0, colorModel.get("g"));
		assertEquals(255, colorModel.get("b"));
		
		ObjectMap fontModel = (ObjectMap)childModel.get("font");
		assertEquals("monospace", fontModel.get("name"));
		assertEquals(16, fontModel.get("size"));
		
		assertEquals("123", model.get("id"));
		assertEquals("foo", model.get("name"));
		assertEquals(Boolean.TRUE, model.get("lock"));
		assertEquals(now, model.get("lastModified"));
	}


}

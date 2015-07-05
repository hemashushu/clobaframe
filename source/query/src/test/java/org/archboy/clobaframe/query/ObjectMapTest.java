package org.archboy.clobaframe.query;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

	@Test
	public void testWrapModel() {
		Date now = new Date();// Calendar.getInstance().getTime();

		Member member = new Member();
		member.setId("123");
		member.setScore(456);
		member.setCreation(now);

		// wrap bean into WebModel
		ObjectMap model1 = DefaultObjectMap.Wrap(member);
		assertEquals("123", model1.get("id"));
		assertEquals(456, model1.get("score"));
		assertNull(model1.get("name")); // the name property has not set.
		assertEquals(now, model1.get("creation"));
		assertNull(model1.get("none"));

		// add new property and update old value
		model1.add("enable", Boolean.TRUE);
		model1.add("count", 100);
		model1.add("score", 999); // update value

		assertEquals(999, model1.get("score"));
		assertEquals(Boolean.TRUE, model1.get("enable"));
		assertEquals(100, model1.get("count"));

		// iterate property names
		Set<String> keys1 = model1.keySet();

		assertEquals(6, keys1.size());
		assertTrue(keys1.contains("id"));
		assertTrue(keys1.contains("score"));
		assertTrue(keys1.contains("name"));
		assertTrue(keys1.contains("creation"));
		assertTrue(keys1.contains("count"));
		assertTrue(keys1.contains("enable"));
		assertFalse(keys1.contains("none"));
		
		// test wrap with specify properties
		Set<String> names = new TreeSet<String>();
		names.add("id");
		names.add("score");
		
		ObjectMap model2 = DefaultObjectMap.Wrap(member, names);
		
		assertEquals("123", model2.get("id"));
		assertEquals(456, model2.get("score"));
		
		Set<String> keys2 = model2.keySet();
		assertEquals(2, keys2.size());
		assertTrue(keys2.contains("id"));
		assertTrue(keys2.contains("score"));
		assertFalse(keys2.contains("name"));
		assertFalse(keys2.contains("creation"));

	}

	public class Member{

		private String id;
		private String name;
		private int score;
		private Date creation;

		public Member() {
			//
		}

		public Member(String id, String name, int score, Date creation) {
			this.id = id;
			this.name = name;
			this.score = score;
			this.creation = creation;
		}

		public Date getCreation() {
			return creation;
		}

		public void setCreation(Date creation) {
			this.creation = creation;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(getId())
					.toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof Member) {
				Member other = (Member) obj;
				return new EqualsBuilder()
						.append(getId(), other.getId())
						.isEquals();
			}
			return false;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("id", getId())
					.toString();
		}
	}
}

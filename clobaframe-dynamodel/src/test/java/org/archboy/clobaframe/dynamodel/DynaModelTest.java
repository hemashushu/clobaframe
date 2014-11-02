package org.archboy.clobaframe.dynamodel;

import org.archboy.clobaframe.dynamodel.impl.DefaultDynaModel;
import org.archboy.clobaframe.dynamodel.impl.WrapDynaModel;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
public class DynaModelTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testDefaultDynaModel() {
		Date now = new Date(); // Calendar.getInstance().getTime();

		DynaModel model = new DefaultDynaModel();

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
//		DynaModel childModel = new DefaultDynaModel();
//		childModel.add("width", 2).add("height", 3);

		model.add("child", new DefaultDynaModel()
				.add("width", 2)
				.add("height", 3));

		DynaModel childModel = (DynaModel)model.get("child");
		assertNotNull(childModel);
		assertEquals(2, childModel.get("width"));
		assertEquals(3, childModel.get("height"));
	}

	@Test
	public void testWrapDynaModel() {
		Date now = new Date();// Calendar.getInstance().getTime();

		Bean bean = new Bean();
		bean.setId("123");
		bean.setScore(456);
		bean.setCreation(now);

		// wrap bean into WebModel
		DynaModel model = new WrapDynaModel(bean);


		assertEquals("123", model.get("id"));
		assertEquals(456, model.get("score"));
		assertNull(model.get("name"));
		assertEquals(now, model.get("creation"));

		assertNull(model.get("none"));

		// add new property and update old value
		model.add("enable", Boolean.TRUE);
		model.add("count", 100);
		// update value
		model.add("score", 999);

		assertEquals(999, model.get("score"));
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
	}

	public class Bean{

		private String id;
		private String name;
		private int score;
		private Date creation;

		public Bean() {
			//
		}

		public Bean(String id, String name, int score, Date creation) {
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
			if (obj instanceof Bean) {
				Bean other = (Bean) obj;
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

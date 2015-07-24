package org.archboy.clobaframe.query;

import org.archboy.clobaframe.common.collection.DefaultObjectMap;
import org.archboy.clobaframe.common.collection.ObjectMap;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archboy.clobaframe.query.simplequery.Utils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author yang
 */
public class UtilsTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testWrapModel() {
		Date now = new Date();// Calendar.getInstance().getTime();

		Member member = new Member();
		member.setId("123");
		member.setScore(456);
		member.setCreation(now);

		// wrap bean into WebModel
		ObjectMap model1 = Utils.Wrap(member);
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
		
		ObjectMap model2 = Utils.Wrap(member, names);
		
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

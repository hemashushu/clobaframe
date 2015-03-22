package org.archboy.clobaframe.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archboy.clobaframe.query.simplequery.PredicateFactory;
import org.archboy.clobaframe.query.simplequery.SimpleQuery;
import org.junit.After;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yang
 */
public class QueryTest {

	/**
	 * Objects for testing.
	 */
	private Date date1;
	private Date date2;

	private Member member1;
	private Member member2;
	private Member member3;
	private Member member4;
	private Member member5;

	private List<Member> members;

	private final Logger logger = LoggerFactory.getLogger(QueryTest.class);

	@Before
	public void setUp() {
		buildTestCollection();
	}

	@After
	public void tearDown() {
		members.clear();
	}

	@Test
	public void testWhere() {
		// test predicate builder
		Collection<Member> resultSet1 = SimpleQuery.from(members)
				.whereGreaterThan("score", 50)
				.whereLessThan("score", 70)
				.list();

		assertEquals(3, resultSet1.size());
		assertTrue(resultSet1.contains(member2));
		assertTrue(resultSet1.contains(member3));
		assertTrue(resultSet1.contains(member4));

		// test gt and lt (not include)
		Collection<Member> resultSet2 = SimpleQuery.from(members)
				.whereGreaterThan("score", 89)
				.whereLessThan("score", 99)
				.list();
		assertEquals(0, resultSet2.size());

		// test gt and lt (include)
		Collection<Member> resultSet3 = SimpleQuery.from(members)
				.whereGreaterThanOrEqual("score", 89)
				.whereLessThanOrEqual("score", 99)
				.list();
		assertEquals(2, resultSet3.size());
		assertTrue(resultSet3.contains(member1));
		assertTrue(resultSet3.contains(member5));

		// test not equals
		Collection<Member> resultSet4 = SimpleQuery.from(members)
				.whereNotEquals("score", 60)
				.list();
		assertEquals(2, resultSet4.size());
		assertTrue(resultSet4.contains(member1));
		assertTrue(resultSet4.contains(member5));

		// test or
		Collection<Member> resultSet5 = SimpleQuery.from(members)
				.where(PredicateFactory.or(
						PredicateFactory.equals("id","004"),
						PredicateFactory.equals("creation", date2)))
				.list();
		assertEquals(3, resultSet5.size());
		assertTrue(resultSet5.contains(member3));
		assertTrue(resultSet5.contains(member4));
		assertTrue(resultSet5.contains(member5));

		// test not
		Collection<Member> resultSet6 = SimpleQuery.from(members)
				.whereLessThanOrEqual("score", 70)
				.orderBy("id")
				.list();

		Collection<Member> resultSet7 = SimpleQuery.from(members)
				.where(PredicateFactory.not(
						PredicateFactory.greaterThan("score", 70)))
				.orderBy("id")
				.list();

		assertArrayEquals(resultSet6.toArray(), resultSet7.toArray());

	}

	@Test
	public void testWhereEquals() {
		// test where equals
		Query<Member> query1 = SimpleQuery.from(members);
		query1.whereEquals("score", 60);

		Collection<Member> resultSet1 = query1.list();
		assertEquals(3, resultSet1.size());
		assertEquals(member2, resultSet1.iterator().next());

		// test first
		Member result1 = query1.first();
		assertEquals(member2, result1);

		// test multiple where equals
		Collection<Member> resultSet2 = SimpleQuery
				.from(members)
				.whereEquals("score", 60)
				.whereEquals("creation", date2)
				.list();
		assertEquals(1, resultSet2.size());
		assertEquals(member3, resultSet2.iterator().next());

		// test where and order
		Collection<Member> resultSet3 = SimpleQuery
				.from(members)
				.whereEquals("creation", date2)
				.orderByDesc("score")
				.list();
		assertEquals(2, resultSet3.size());

		Iterator<Member> iterator1 = resultSet3.iterator();
		assertEquals(member5, iterator1.next());
		assertEquals(member3, iterator1.next());

	}

	public void testWhereNotEquals() {
		//
	}

	public void testWhereGreaterThan() {
		//
	}

	public void testWhereGreaterThanOrEqual() {
		//
	}

	public void testWhereLessThan() {
		//
	}

	public void testWhereLessThanOrEqual() {
		//
	}

	@Test
	public void testOrderBy() {
		// test order
		Member result1 = SimpleQuery.from(members).orderBy("name").first();
		assertEquals(member4, result1);

		// test order by desc
		Member result2 = SimpleQuery.from(members).orderByDesc("score").first();
		assertEquals(member5, result2);

		// test order by and then by
		Collection<Member> resultSet1 = SimpleQuery.from(members).orderBy("score").orderBy("creation").orderBy("name").list();
		Iterator<Member> iterator1 = resultSet1.iterator();
		assertEquals(member4, iterator1.next());
		assertEquals(member2, iterator1.next());
		assertEquals(member3, iterator1.next());
	}

	public void testOrderByDesc() {
		//
	}

	public void testOrderBy_Comparator() {
		//
	}

	@Test
	public void testSelect() {
		/*
		 * Object properties:
		 * {String id,
		 *	String name,
		 *	int score,
		 *	Date creation}
		*/

		// test key set 1
		List<ViewModel> result1 = SimpleQuery.from(members).select("id");
		assertEquals(5, result1.size());

		Map<String, Object> o1 = result1.iterator().next();
		assertEquals(1, o1.keySet().size());
		assertEquals("id", o1.keySet().iterator().next());
		assertEquals(member1.getId(), o1.get("id"));

		// test key set 2
		List<ViewModel> result2 = SimpleQuery.from(members).select("id", "creation", "score");

		Map<String, Object> o2 = result2.iterator().next();
		assertEquals(3, o2.keySet().size());
		assertEquals(member1.getId(), o2.get("id"));
		assertEquals(member1.getCreation(), o2.get("creation"));
		assertEquals(member1.getScore(), o2.get("score"));

	}

	public void testList() {
		//
	}

	public void testFirst() {
		//
	}
	
	@Test
	public void testLimit() {
		
		// test limit
		List<Member> result1 = SimpleQuery.from(members).limit(3).list();
		assertEquals(3, result1.size());
		assertEquals(member1, result1.get(0));
		assertEquals(member2, result1.get(1));
		assertEquals(member3, result1.get(2));

		// test limit with order
		List<Member> result2 = SimpleQuery.from(members)
				.orderByDesc("score")
				.limit(2)
				.list();
		assertEquals(2, result2.size());
		assertEquals(member5, result2.get(0));
		assertEquals(member1, result2.get(1));
		
		// test limit with select
		List<ViewModel> result3 = SimpleQuery.from(members)
				.orderBy("name")
				.limit(2)
				.select("name");
		assertEquals(2, result3.size());
		assertEquals("bar", result3.get(0).get("name"));
		assertEquals("foo", result3.get(1).get("name"));
		
	}

	@Test
	public void testQueryException(){

		MemberWithObjectProperty unbean1 = new MemberWithObjectProperty("abc", new UncomparableObject("v1"));
		MemberWithObjectProperty unbean2 = new MemberWithObjectProperty(null, new UncomparableObject("v2"));
		MemberWithObjectProperty unbean3 = new MemberWithObjectProperty("xyz", new UncomparableObject("v3"));

		List<MemberWithObjectProperty> members = new ArrayList<MemberWithObjectProperty>();
		members.addAll(Arrays.asList(unbean1, unbean2, unbean3));

		// test no result
		Collection<MemberWithObjectProperty> resultSet1 = SimpleQuery.from(members)
				.whereEquals("name", "def")
				.list();
		assertEquals(0, resultSet1.size());

		// test null compare
		Collection<MemberWithObjectProperty> resultSet2 = SimpleQuery.from(members)
				.whereEquals("name", null)
				.list();
		assertEquals(1, resultSet2.size());
		assertEquals(unbean2, resultSet2.iterator().next());

		// test gt
		Collection<MemberWithObjectProperty> resultSet3 = SimpleQuery.from(members)
				.whereGreaterThan("name", "def")
				.list();
		assertEquals(1, resultSet3.size());
		assertEquals(unbean3, resultSet3.iterator().next());

		// test wrong key
		try{
			SimpleQuery.from(members)
					.whereEquals("wrongkey", "abc")
					.first();
			fail("No this property.");
		}catch(QueryException e){
			// success
		}

		// test unsortable value
		try{
			SimpleQuery.from(members)
					.orderBy("content")
					.first();
			fail("Uncomparable property.");
		}catch(QueryException e){
			// pass
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPerformance(){
		// this performance test is not quantitative

		List<Member> members = new ArrayList<Member>();
		for (int idx=0; idx<2000; idx++){
			members.add(member1);
			members.add(member2);
			members.add(member3);
			members.add(member4);
		}

		// fetch once to exclude cache time
		SimpleQuery.from(members).whereEquals("id", "001").list();

		long startTime = new Date().getTime();

		for (int idx=0; idx<1000; idx++){
			SimpleQuery.from(members)
					.whereEquals("creation", date1)
					.whereGreaterThanOrEqual("score", 80)
					.orderBy("id")
					.orderByDesc("name")
					.list();
		}

		long endTime = new Date().getTime();

		logger.info("SimpleQuery, loop 1000 times, total spend time [{}] millisecond.", (endTime - startTime));

		// test directly query
		startTime = new Date().getTime();

		for (int idx=0; idx<1000; idx++){
			// list
			Collection<Member> selectedBeans = (Collection<Member>)
			CollectionUtils.select(members, new Predicate() {

				@Override
				public boolean evaluate(Object object) {
					Member bean = (Member)object;
					return (bean.getCreation().equals(date1) &&
						bean.getScore() >= 80);
				}
			});

			List<Member> selectedBeans2 = (List<Member>)selectedBeans;

			// sort
			Collections.sort(selectedBeans2, new Comparator<Member>() {

				@Override
				public int compare(Member o1, Member o2) {
					int result = o1.getId().compareTo(o2.getId());
					if (result == 0){
						result = - o1.getName().compareTo(o2.getName());
					}
					return result;
				}
			});
		}

		endTime = new Date().getTime();

		logger.info("Directly, loop 1000 times, total spend time [{}] millisecond.", (endTime - startTime));
	}

	private void buildTestCollection(){

		Calendar c1 = Calendar.getInstance();
		c1.set(2009, 1, 1, 12, 0, 0);
		date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.set(2010, 2, 2, 12, 0, 0);
		date2 = c2.getTime();

		member1 = new Member("001", "spark", 89, date1);
		member2 = new Member("002", "youngs", 60, date1);
		member3 = new Member("003", "foo", 60, date2);
		member4 = new Member("004", "bar", 60, date1);
		member5 = new Member("005", "hello", 99, date2);

		members = new ArrayList<Member>();
		members.addAll(Arrays.asList(member1, member2, member3, member4, member5));
	}

	public class MemberWithObjectProperty {

		private String name;
		private UncomparableObject content;

		public MemberWithObjectProperty(String name, UncomparableObject content) {
			this.name = name;
			this.content = content;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public UncomparableObject getContent() {
			return content;
		}

		public void setUnvalue(UncomparableObject content) {
			this.content = content;
		}
	}

	public class UncomparableObject {

		private String content;

		public UncomparableObject(String content) {
			this.content = content;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	/**
	 * A test object.
	 */
	public class Member implements Serializable{

		private static final long serialVersionUID = 1L;

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

			if (!(obj instanceof Member)) {
				return false;
			}

			Member other = (Member) obj;
			return new EqualsBuilder()
					.append(getId(), other.getId())
					.isEquals();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("id", getId())
					.toString();
		}
	}

}

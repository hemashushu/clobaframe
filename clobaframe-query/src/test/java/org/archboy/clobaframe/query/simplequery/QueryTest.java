/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.query.simplequery;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.archboy.clobaframe.query.Query;
import org.archboy.clobaframe.query.QueryException;

import static org.junit.Assert.*;

/**
 *
 * @author young
 */
public class QueryTest {

	private Date date1;
	private Date date2;

	private Bean bean1;
	private Bean bean2;
	private Bean bean3;
	private Bean bean4;
	private Bean bean5;

	private List<Bean> beans;

	private final Logger logger = LoggerFactory.getLogger(QueryTest.class);

	@Before
	public void setUp() {
		buildTestCollection();
	}

	@After
	public void tearDown() {
		beans.clear();
	}

	@Test
	public void testWhere() {
		// test predicate builder
		Collection<Bean> resultSet1 = SimpleQuery.from(beans)
				.whereGreaterThan("score", 50)
				.whereLessThan("score", 70)
				.list();

		assertEquals(3, resultSet1.size());
		assertTrue(resultSet1.contains(bean2));
		assertTrue(resultSet1.contains(bean3));
		assertTrue(resultSet1.contains(bean4));

		// test gt and lt (not include)
		Collection<Bean> resultSet2 = SimpleQuery.from(beans)
				.whereGreaterThan("score", 89)
				.whereLessThan("score", 99)
				.list();
		assertEquals(0, resultSet2.size());

		// test gt and lt (include)
		Collection<Bean> resultSet3 = SimpleQuery.from(beans)
				.whereGreaterThanOrEqual("score", 89)
				.whereLessThanOrEqual("score", 99)
				.list();
		assertEquals(2, resultSet3.size());
		assertTrue(resultSet3.contains(bean1));
		assertTrue(resultSet3.contains(bean5));

		// test not equals
		Collection<Bean> resultSet4 = SimpleQuery.from(beans)
				.whereNotEquals("score", 60)
				.list();
		assertEquals(2, resultSet4.size());
		assertTrue(resultSet4.contains(bean1));
		assertTrue(resultSet4.contains(bean5));

		// test or
		Collection<Bean> resultSet5 = SimpleQuery.from(beans)
				.where(PredicateFactory.or(
						PredicateFactory.equals("id","004"),
						PredicateFactory.equals("creation", date2)))
				.list();
		assertEquals(3, resultSet5.size());
		assertTrue(resultSet5.contains(bean3));
		assertTrue(resultSet5.contains(bean4));
		assertTrue(resultSet5.contains(bean5));

		// test not
		Collection<Bean> resultSet6 = SimpleQuery.from(beans)
				.whereLessThanOrEqual("score", 70)
				.orderBy("id")
				.list();

		Collection<Bean> resultSet7 = SimpleQuery.from(beans)
				.where(PredicateFactory.not(
						PredicateFactory.greaterThan("score", 70)))
				.orderBy("id")
				.list();

		assertArrayEquals(resultSet6.toArray(), resultSet7.toArray());

	}

	@Test
	public void testWhereEquals() {
		// test where equals
		Query<Bean> query1 = SimpleQuery.from(beans);
		query1.whereEquals("score", 60);

		Collection<Bean> resultSet1 = query1.list();
		assertEquals(3, resultSet1.size());
		assertEquals(bean2, resultSet1.iterator().next());

		// test first
		Bean result1 = query1.first();
		assertEquals(bean2, result1);

		// test multiple where equals
		Collection<Bean> resultSet2 = SimpleQuery
				.from(beans)
				.whereEquals("score", 60)
				.whereEquals("creation", date2)
				.list();
		assertEquals(1, resultSet2.size());
		assertEquals(bean3, resultSet2.iterator().next());

		// test where and order
		Collection<Bean> resultSet3 = SimpleQuery
				.from(beans)
				.whereEquals("creation", date2)
				.orderByDesc("score")
				.list();
		assertEquals(2, resultSet3.size());

		Iterator<Bean> iterator1 = resultSet3.iterator();
		assertEquals(bean5, iterator1.next());
		assertEquals(bean3, iterator1.next());

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
		Bean result1 = SimpleQuery.from(beans).orderBy("name").first();
		assertEquals(bean4, result1);

		// test order by desc
		Bean result2 = SimpleQuery.from(beans).orderByDesc("score").first();
		assertEquals(bean5, result2);

		// test order by than by
		Collection<Bean> resultSet1 = SimpleQuery.from(beans).orderBy("score").orderBy("creation").orderBy("name").list();
		Iterator<Bean> iterator1 = resultSet1.iterator();
		assertEquals(bean4, iterator1.next());
		assertEquals(bean2, iterator1.next());
		assertEquals(bean3, iterator1.next());
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
		 * Original bean properties:
		 *	String id;
		 *	String name;
		 *	int score;
		 *	Date creation;
		*/

		// test key set 1
		List<Map<String, Object>> result1 = SimpleQuery.from(beans).select("id");
		assertEquals(5, result1.size());

		Map<String, Object> o1 = result1.iterator().next();
		assertEquals(1, o1.keySet().size());
		assertEquals("id", o1.keySet().iterator().next());
		assertEquals(bean1.getId(), o1.get("id"));

		// test key set 2
		List<Map<String, Object>> result2 = SimpleQuery.from(beans).select("id", "creation", "score");

		Map<String, Object> o2 = result2.iterator().next();
		assertEquals(3, o2.keySet().size());
		assertEquals(bean1.getId(), o2.get("id"));
		assertEquals(bean1.getCreation(), o2.get("creation"));
		assertEquals(bean1.getScore(), o2.get("score"));

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
		List<Bean> result1 = SimpleQuery.from(beans).limit(3).list();
		assertEquals(3, result1.size());
		assertEquals(bean1, result1.get(0));
		assertEquals(bean2, result1.get(1));
		assertEquals(bean3, result1.get(2));

		// test limit with order
		List<Bean> result2 = SimpleQuery.from(beans)
				.orderByDesc("score")
				.limit(2)
				.list();
		assertEquals(2, result2.size());
		assertEquals(bean5, result2.get(0));
		assertEquals(bean1, result2.get(1));
		
		// test limit with select
		List<Map<String, Object>> result3 = SimpleQuery.from(beans)
				.orderBy("name")
				.limit(2)
				.select("name");
		assertEquals(2, result3.size());
		assertEquals("bar", result3.get(0).get("name"));
		assertEquals("foo", result3.get(1).get("name"));
		
	}

	@Test
	public void testQueryException(){

		BeanWithObjectProperty unbean1 = new BeanWithObjectProperty("abc", new BeanUncomparable("v1"));
		BeanWithObjectProperty unbean2 = new BeanWithObjectProperty(null, new BeanUncomparable("v2"));
		BeanWithObjectProperty unbean3 = new BeanWithObjectProperty("xyz", new BeanUncomparable("v3"));

		List<BeanWithObjectProperty> unbeans = new ArrayList<BeanWithObjectProperty>();
		unbeans.addAll(Arrays.asList(unbean1, unbean2, unbean3));

		// test no result
		Collection<BeanWithObjectProperty> resultSet1 = SimpleQuery.from(unbeans)
				.whereEquals("name", "def")
				.list();
		assertEquals(0, resultSet1.size());

		// test null compare
		Collection<BeanWithObjectProperty> resultSet2 = SimpleQuery.from(unbeans)
				.whereEquals("name", null)
				.list();
		assertEquals(1, resultSet2.size());
		assertEquals(unbean2, resultSet2.iterator().next());

		// test gt
		Collection<BeanWithObjectProperty> resultSet3 = SimpleQuery.from(unbeans)
				.whereGreaterThan("name", "def")
				.list();
		assertEquals(1, resultSet3.size());
		assertEquals(unbean3, resultSet3.iterator().next());

		// test wrong key
		try{
			SimpleQuery.from(unbeans)
					.whereEquals("wrongkey", "abc")
					.first();
			fail("No this property.");
		}catch(QueryException e){
			// success
		}

//		// test uncomparable value
//		try{
//			SimpleQuery.from(unbeans)
//					.whereGreaterThan("unvalue", new BeanUncomparable("v1"))
//					.first();
//			fail("Uncomparable property.");
//		}catch(QueryException e){
//			// pass
//		}

		// test unsortable value
		try{
			SimpleQuery.from(unbeans)
					.orderBy("unvalue")
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

		List<Bean> bigBeans = new ArrayList<Bean>();
		for (int idx=0; idx<2000; idx++){
			bigBeans.add(bean1);
			bigBeans.add(bean2);
			bigBeans.add(bean3);
			bigBeans.add(bean4);
		}

		// fetch once to exclude cache time
		SimpleQuery.from(bigBeans).whereEquals("id", "001").list();

		long startTime = new Date().getTime();

		for (int idx=0; idx<200; idx++){
			SimpleQuery.from(bigBeans)
					.whereEquals("creation", date1)
					.whereGreaterThanOrEqual("score", 80)
					.orderBy("id")
					.orderByDesc("name")
					.list();
		}

		long endTime = new Date().getTime();

		logger.info("SimpleQuery, loop 200 times, total spend time [{}] millisecond.", (endTime - startTime));

		// test directly query
		startTime = new Date().getTime();

		for (int idx=0; idx<200; idx++){
			// list
			Collection<Bean> selectedBeans = (Collection<Bean>)
			CollectionUtils.select(bigBeans, new Predicate() {

				@Override
				public boolean evaluate(Object object) {
					Bean bean = (Bean)object;
					return (bean.getCreation().equals(date1) &&
						bean.getScore() >= 80);
				}
			});

			List<Bean> selectedBeans2 = (List<Bean>)selectedBeans;

			// sort
			Collections.sort(selectedBeans2, new Comparator<Bean>() {

				@Override
				public int compare(Bean o1, Bean o2) {
					int result = o1.getId().compareTo(o2.getId());
					if (result == 0){
						result = - o1.getName().compareTo(o2.getName());
					}
					return result;
				}
			});
		}

		endTime = new Date().getTime();

		logger.info("Directly, loop 200 times, total spend time [{}] millisecond.", (endTime - startTime));
	}

	private void buildTestCollection(){

		Calendar c1 = Calendar.getInstance();
		c1.set(2009, 1, 1, 12, 0, 0);
		date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.set(2010, 2, 2, 12, 0, 0);
		date2 = c2.getTime();

		bean1 = new Bean("001", "spark", 89, date1);
		bean2 = new Bean("002", "youngs", 60, date1);
		bean3 = new Bean("003", "foo", 60, date2);
		bean4 = new Bean("004", "bar", 60, date1);
		bean5 = new Bean("005", "hello", 99, date2);

		beans = new ArrayList<Bean>();
		beans.addAll(Arrays.asList(bean1, bean2, bean3, bean4, bean5));
	}

	public class BeanWithObjectProperty {

		private String name;
		private BeanUncomparable unvalue;

		public BeanWithObjectProperty(String name, BeanUncomparable unvalue) {
			this.name = name;
			this.unvalue = unvalue;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BeanUncomparable getUnvalue() {
			return unvalue;
		}

		public void setUnvalue(BeanUncomparable unvalue) {
			this.unvalue = unvalue;
		}
	}

	public class BeanUncomparable {

		private String content;

		public BeanUncomparable(String content) {
			this.content = content;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	public class Bean implements Serializable{

		private static final long serialVersionUID = 1L;

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

			if (!(obj instanceof Bean)) {
				return false;
			}

			Bean other = (Bean) obj;
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

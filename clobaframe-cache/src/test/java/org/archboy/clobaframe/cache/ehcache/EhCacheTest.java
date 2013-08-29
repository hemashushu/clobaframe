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
package org.archboy.clobaframe.cache.ehcache;

import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.Expiration;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext_ehcache.xml" })
public class EhCacheTest {

	@Inject
	private Cache cache;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public void testClearAll() {
		//
	}

	public void testDelete() {
		//
	}

	public void testDeleteAll() {
		//
	}

	public void testGet() {
		//
	}

	public void testGetAll() {
		//
	}

	@Test
	public void testPut() throws InterruptedException {

		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";

		// clean up first
		cache.delete(key1);
		cache.delete(key2);
		cache.delete(key3);

		// test put
		assertTrue(cache.put(key1, "FOO", Expiration.bySeconds(1),
				Cache.SetPolicy.SET_ALWAYS));
		assertTrue(cache.put(key2, "BAR", Expiration.bySeconds(3),
				Cache.SetPolicy.SET_ALWAYS));

		assertEquals("FOO", cache.get(key1));
		assertEquals("BAR", cache.get(key2));
		assertNull(cache.get(key3));

		// test put with policy
		assertFalse(cache.put(key1, "FOO1", Expiration.bySeconds(1),
				Cache.SetPolicy.ADD_ONLY_IF_NOT_PRESENT));
		assertFalse(cache.put(key3, "FOOBAR", Expiration.bySeconds(1),
				Cache.SetPolicy.REPLACE_ONLY_IF_PRESENT));
		assertTrue(cache.put(key1, "FOO2", Expiration.bySeconds(1),
				Cache.SetPolicy.REPLACE_ONLY_IF_PRESENT));

		assertEquals("FOO2", cache.get(key1));

		// test clear all, NOTE:: be careful this will remove all cache items!
		// don't run this unit test on the product environment.
		// cache.clearAll();

		cache.delete(key1);
		cache.delete(key2);

		assertNull(cache.get(key1));
		assertNull(cache.get(key2));
	}

	@Test
	public void testPutWithExpiration() throws InterruptedException {

		String key1 = "key1e";
		String key2 = "key2e";
		String key3 = "key3e";

		// clean up first
		cache.delete(key1);
		cache.delete(key2);
		cache.delete(key3);

		Thread.sleep(1000);

		// test expire by date
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 3);
		Date date = calendar.getTime();

		cache.put(key1, "FOO", Expiration.bySeconds(1));
		cache.put(key2, "BAR", Expiration.onDate(date));
		cache.put(key3, "LONG");

		// This test may fail because the system clock not sync.

		assertNotNull(cache.get(key1));
		assertNotNull(cache.get(key2));
		assertNotNull(cache.get(key3));

		Thread.sleep(1500);

		assertNull(cache.get(key1));
		assertNotNull(cache.get(key2));
		assertNotNull(cache.get(key3));

		Thread.sleep(2500);
		assertNull(cache.get(key2));
		assertNotNull(cache.get(key3));
	}

	public void testPutWithExpirationAndPolicy() {
		//
	}

	public void testPutAll() {
		//
	}

}

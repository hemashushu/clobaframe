package org.archboy.clobaframe.cache.memcached;

import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.CacheManager;
import org.archboy.clobaframe.cache.Expiration;
import org.archboy.clobaframe.cache.Expiration;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class CacheTest {

	@Inject
	//@Named("defaultCache")
	private CacheManager cacheManager;

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

//	@Test
//	public void testIncrement() {
//		// test increment
//		String key1 = "inc1";
//		cache.delete(key1);
//		assertEquals(5, cache.increment(key1, 3, 5));
//		assertEquals(12, cache.increment(key1, 7));
//
//		cache.delete(key1);
//		assertEquals(9, cache.increment(key1, 1, 9));
//	}
//
//	@Test
//	public void testIncrementWithDefaultValue() {
//		// test preset
//		String key1 = "inc2";
//		cache.delete(key1);
//		cache.put(key1, "10");
//		assertEquals(11, cache.increment(key1, 1));
//		assertEquals(12, cache.increment(key1, 1));
//
//		// test increment a none-exist item
//		cache.delete(key1);
//		assertEquals(-1, cache.increment(key1, 100));
//		assertEquals(-1, cache.increment(key1, 5));
//	}

	@Test
	public void testPut() throws InterruptedException {
		Cache cache = cacheManager.getDefault();
		
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";

		// clean up first
		cache.delete(key1);
		cache.delete(key2);
		cache.delete(key3);

		// test put
		assertTrue(cache.put(key1, "FOO", Expiration.bySeconds(1),
				Cache.Policy.SET_ALWAYS));
		assertTrue(cache.put(key2, "BAR", Expiration.bySeconds(3),
				Cache.Policy.SET_ALWAYS));

		assertEquals("FOO", cache.get(key1));
		assertEquals("BAR", cache.get(key2));
		assertNull(cache.get(key3));

		// test put with policy
		assertFalse(cache.put(key1, "FOO1", Expiration.bySeconds(1),
				Cache.Policy.ADD_ONLY_IF_NOT_PRESENT));
		assertFalse(cache.put(key3, "FOOBAR", Expiration.bySeconds(1),
				Cache.Policy.REPLACE_ONLY_IF_PRESENT));
		assertTrue(cache.put(key1, "FOO2", Expiration.bySeconds(1),
				Cache.Policy.REPLACE_ONLY_IF_PRESENT));

		assertEquals("FOO2", cache.get(key1));

		// test clear all, NOTE:: be careful this will remove all cache items!
		// cache.clearAll();

		cache.delete(key1);
		cache.delete(key2);

		assertNull(cache.get(key1));
		assertNull(cache.get(key2));
	}

	@Test
	public void testPutWithExpiration() throws InterruptedException {
		Cache cache = cacheManager.getDefault();
		
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

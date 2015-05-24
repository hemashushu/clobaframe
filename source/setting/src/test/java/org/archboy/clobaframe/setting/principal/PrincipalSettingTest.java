package org.archboy.clobaframe.setting.principal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.SettingProvider;
import org.archboy.clobaframe.setting.support.Utils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class PrincipalSettingTest {

	private final Logger logger = LoggerFactory.getLogger(PrincipalSettingTest.class);
	
	@Inject
	private PrincipalSetting principalSetting;
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testGet(){
		User user1 = new User("001");
		User user2 = new User("002");
		User user3 = new User("003");
		Group group1 = new Group("001");
		Group group2 = new Group("002");
		Group group3 = new Group("003");
		
		assertEquals("page1", principalSetting.get(user1, "entry"));
		assertEquals("red", principalSetting.get(user1, "color"));
		
		assertEquals("page2", principalSetting.get(user2, "entry"));
		assertEquals("white", principalSetting.get(user2, "color"));
		
		assertEquals("index", principalSetting.get(user3, "entry"));
		assertEquals("white", principalSetting.get(user3, "color"));
		
		assertEquals("doc1", principalSetting.get(group1, "entry"));
		assertEquals("black", principalSetting.get(group1, "color"));
		
		assertEquals("doc2", principalSetting.get(group2, "entry"));
		assertNull(principalSetting.get(group2, "color"));
		
		assertNull(principalSetting.get(group3, "entry"));
		assertNull(principalSetting.get(group3, "color"));
	}
	
	@Test
	public void testSet(){
		User user1 = new User("001");
		
		if ("updated".equals(principalSetting.get(user1, "status"))){
			assertEquals(Boolean.TRUE, principalSetting.get(user1, "notify"));
			
			principalSetting.set(user1, "status", "original");
			principalSetting.set(user1, "notify", Boolean.FALSE);
		}else{
			assertEquals(Boolean.FALSE, principalSetting.get(user1, "notify"));
			
			principalSetting.set(user1, "status", "updated");
			principalSetting.set(user1, "notify", Boolean.TRUE);
		}
		
	}
	
	public static class User implements Principal<String> {

		private String id;

		public User(String id) {
			this.id = id;
		}
		
		@Override
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
	
	public static class Group implements Principal<String> {

		private String id;

		public Group(String id) {
			this.id = id;
		}
		
		@Override
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
	
	@Named
	public static class UserDefaultSettingProvider implements PrincipalSettingProvider {

		private Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		
		@PostConstruct
		public void init() {
			setting1.put("entry", "index");
			setting1.put("color", "white");
		}
		
		@Override
		public int getOrder() {
			return 10;
		}

		@Override
		public Object get(Principal profile, String key) {
			return setting1.get(key);
		}

		@Override
		public Map<String, Object> getAll(Principal profile) {
			return setting1;
		}

		@Override
		public boolean support(Principal profile) {
			return (profile instanceof User);
		}
		
	}
	
	@Named
	public static class InMemoryUserSettingRepository implements PrincipalSettingProvider, PrincipalSettingRepository {

		private Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		private Map<String, Object> setting2 = new LinkedHashMap<String, Object>();
		private Map<String, Map<String, Object>> settings = new HashMap<String, Map<String, Object>>();
		
		@PostConstruct
		public void init() {
			setting1.put("entry", "page1");
			setting1.put("color", "red");
			setting1.put("notify", Boolean.FALSE);
			
			setting2.put("entry", "page2");
			
			settings.put("001", setting1);
			settings.put("002", setting2);
			
//			profileSetting.addProfileSettingProvider(this);
//			profileSetting.addProfileSettingRepository(this);
		}
		
		@Override
		public int getOrder() {
			return 1;
		}

		@Override
		public Object get(Principal profile, String key) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				return s.get(key);
			}else{
				return null;
			}
		}

		@Override
		public Map<String, Object> getAll(Principal profile) {
			Map<String, Object> s = settings.get((String)profile.getId());
			return (s == null ? new LinkedHashMap<String, Object>() : s);
		}

		@Override
		public boolean support(Principal profile) {
			return (profile instanceof User);
		}

		@Override
		public void set(Principal profile, Map<String, Object> item) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, item));
			}
			
		}

		@Override
		public void set(Principal profile, String key, Object value) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, key, value));
			}
		}
	}
	
	@Named
	public static class InMemoryGroupSettingRepository implements PrincipalSettingProvider, PrincipalSettingRepository {

		private Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		private Map<String, Object> setting2 = new LinkedHashMap<String, Object>();
		private Map<String, Map<String, Object>> settings = new HashMap<String, Map<String, Object>>();
		
		@PostConstruct
		public void init() {
			setting1.put("entry", "doc1");
			setting1.put("color", "black");
			
			setting2.put("entry", "doc2");
			
			settings.put("001", setting1);
			settings.put("002", setting2);
			
//			profileSetting.addProfileSettingProvider(this);
//			profileSetting.addProfileSettingRepository(this);
		}
		
		@Override
		public int getOrder() {
			return 1;
		}

		@Override
		public Object get(Principal profile, String key) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				return s.get(key);
			}else{
				return null;
			}
		}

		@Override
		public Map<String, Object> getAll(Principal profile) {
			Map<String, Object> s = settings.get((String)profile.getId());
			return (s == null ? new LinkedHashMap<String, Object>() : s);
		}

		@Override
		public boolean support(Principal profile) {
			return (profile instanceof Group);
		}

		@Override
		public void set(Principal profile, Map<String, Object> item) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, item));
			}
			
		}

		@Override
		public void set(Principal profile, String key, Object value) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, key, value));
			}
		}
	}
	
}

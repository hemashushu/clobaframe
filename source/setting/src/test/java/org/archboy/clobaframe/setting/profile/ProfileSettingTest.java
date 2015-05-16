package org.archboy.clobaframe.setting.profile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.archboy.clobaframe.setting.application.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.SettingProvider;
import static org.archboy.clobaframe.setting.SettingProvider.PRIORITY_HIGH;
import org.archboy.clobaframe.setting.instance.InstanceSettingProvider;
import org.archboy.clobaframe.setting.instance.InstanceSettingRepository;
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
public class ProfileSettingTest {

	private final Logger logger = LoggerFactory.getLogger(ProfileSettingTest.class);
	
	@Inject
	private ProfileSetting profileSetting;
	
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
		
		assertEquals("page1", profileSetting.get(user1, "entry"));
		assertEquals("red", profileSetting.get(user1, "color"));
		
		assertEquals("page2", profileSetting.get(user2, "entry"));
		assertEquals("white", profileSetting.get(user2, "color"));
		
		assertEquals("index", profileSetting.get(user3, "entry"));
		assertEquals("white", profileSetting.get(user3, "color"));
		
		assertEquals("doc1", profileSetting.get(group1, "entry"));
		assertEquals("black", profileSetting.get(group1, "color"));
		
		assertEquals("doc2", profileSetting.get(group2, "entry"));
		assertNull(profileSetting.get(group2, "color"));
		
		assertNull(profileSetting.get(group3, "entry"));
		assertNull(profileSetting.get(group3, "color"));
	}
	
	@Test
	public void testSet(){
		User user1 = new User("001");
		
		if ("updated".equals(profileSetting.get(user1, "status"))){
			assertEquals(Boolean.TRUE, profileSetting.get(user1, "notify"));
			
			profileSetting.set(user1, "status", "original");
			profileSetting.set(user1, "notify", Boolean.FALSE);
		}else{
			assertEquals(Boolean.FALSE, profileSetting.get(user1, "notify"));
			
			profileSetting.set(user1, "status", "updated");
			profileSetting.set(user1, "notify", Boolean.TRUE);
		}
		
	}
	
	public static class User implements Profile<String> {

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
	
	public static class Group implements Profile<String> {

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
	public static class UserDefaultSettingProvider implements ProfileSettingProvider {

		private Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		
		@Inject
		public UserDefaultSettingProvider(ProfileSetting profileSetting) {
			setting1.put("entry", "index");
			setting1.put("color", "white");
			
			profileSetting.addProfileSettingProvider(this);
		}
		
		@Override
		public int getPriority() {
			return SettingProvider.PRIORITY_LOWER;
		}

		@Override
		public Object get(Profile profile, String key) {
			return setting1.get(key);
		}

		@Override
		public Map<String, Object> getAll(Profile profile) {
			return setting1;
		}

		@Override
		public boolean support(Profile profile) {
			return (profile instanceof User);
		}
		
	}
	
	@Named
	public static class InMemoryUserSettingRepository implements ProfileSettingProvider, ProfileSettingRepository {

		private Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		private Map<String, Object> setting2 = new LinkedHashMap<String, Object>();
		private Map<String, Map<String, Object>> settings = new HashMap<String, Map<String, Object>>();
		
		@Inject
		public InMemoryUserSettingRepository(ProfileSetting profileSetting) {
			setting1.put("entry", "page1");
			setting1.put("color", "red");
			setting1.put("notify", Boolean.FALSE);
			
			setting2.put("entry", "page2");
			
			settings.put("001", setting1);
			settings.put("002", setting2);
			
			profileSetting.addProfileSettingProvider(this);
			profileSetting.addProfileSettingRepository(this);
		}
		
		@Override
		public int getPriority() {
			return PRIORITY_HIGH;
		}

		@Override
		public Object get(Profile profile, String key) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				return s.get(key);
			}else{
				return null;
			}
		}

		@Override
		public Map<String, Object> getAll(Profile profile) {
			Map<String, Object> s = settings.get((String)profile.getId());
			return (s == null ? new LinkedHashMap<String, Object>() : s);
		}

		@Override
		public boolean support(Profile profile) {
			return (profile instanceof User);
		}

		@Override
		public void set(Profile profile, Map<String, Object> item) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, item));
			}
			
		}

		@Override
		public void set(Profile profile, String key, Object value) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, key, value));
			}
		}
	}
	
	@Named
	public static class InMemoryGroupSettingRepository implements ProfileSettingProvider, ProfileSettingRepository {

		private Map<String, Object> setting1 = new LinkedHashMap<String, Object>();
		private Map<String, Object> setting2 = new LinkedHashMap<String, Object>();
		private Map<String, Map<String, Object>> settings = new HashMap<String, Map<String, Object>>();
		
		@Inject
		public InMemoryGroupSettingRepository(ProfileSetting profileSetting) {
			setting1.put("entry", "doc1");
			setting1.put("color", "black");
			
			setting2.put("entry", "doc2");
			
			settings.put("001", setting1);
			settings.put("002", setting2);
			
			profileSetting.addProfileSettingProvider(this);
			profileSetting.addProfileSettingRepository(this);
		}
		
		@Override
		public int getPriority() {
			return PRIORITY_HIGH;
		}

		@Override
		public Object get(Profile profile, String key) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				return s.get(key);
			}else{
				return null;
			}
		}

		@Override
		public Map<String, Object> getAll(Profile profile) {
			Map<String, Object> s = settings.get((String)profile.getId());
			return (s == null ? new LinkedHashMap<String, Object>() : s);
		}

		@Override
		public boolean support(Profile profile) {
			return (profile instanceof Group);
		}

		@Override
		public void set(Profile profile, Map<String, Object> item) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, item));
			}
			
		}

		@Override
		public void set(Profile profile, String key, Object value) {
			Map<String, Object> s = settings.get((String)profile.getId());
			if (s != null) {
				settings.put((String)profile.getId(), Utils.merge(s, key, value));
			}
		}
	}
	
}

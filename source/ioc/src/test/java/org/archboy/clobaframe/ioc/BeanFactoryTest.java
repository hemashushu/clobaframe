package org.archboy.clobaframe.ioc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import org.archboy.clobaframe.ioc.bean.Animal;
import org.archboy.clobaframe.ioc.bean.DefaultFood;
import org.archboy.clobaframe.ioc.bean.Dog;
import org.archboy.clobaframe.ioc.bean.Food;
import org.archboy.clobaframe.ioc.bean.RubberDuck;
import org.archboy.clobaframe.ioc.bean.Status;
import org.archboy.clobaframe.ioc.bean.Zoo;
import org.archboy.clobaframe.ioc.impl.ApplicationSettingPlaceholderValueResolver;
import org.archboy.clobaframe.ioc.impl.Bean;
import org.archboy.clobaframe.ioc.impl.DefaultBeanFactory;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.impl.DefaultApplicationSetting;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class BeanFactoryTest {

	private BeanFactory beanFactory;
	private Status status;

	private boolean factoryCloseEventFired = false;
	
	private long start, span;
	
	private final Logger logger = LoggerFactory.getLogger(BeanFactoryTest.class);

//	public static void main( String[] args ) throws Exception {
//		BeanFactoryTest t = new BeanFactoryTest();
//		t.setUp();
//		t.testGet();
//		t.tearDown();
//	}
	
	@Before
	public void setUp() throws Exception {
		start = System.currentTimeMillis();

		beanFactory = new DefaultBeanFactory("classpath:application.properties");
		
//		ResourceLoader resourceLoader = new DefaultResourceLoader();
//		
//		ApplicationSetting applicationSetting = new DefaultApplicationSetting(
//				resourceLoader, null, null,
//				"classpath:root.properties", 
//				null, (String[])null);
//		
//		String beanDefineFileName = (String)applicationSetting.getValue(
//				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_BEAN_DEFINE_FILE_NAME);
//		
//		boolean requiredPlaceholderValue = (Boolean)applicationSetting.getValue(
//				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_REQUIRED_PLACEHOLDER_VALUE, 
//				ApplicationSettingPlaceholderValueResolver.DEFAULT_REQUIRED_PLACEHOLDER_VALUE);
//		
//		PlaceholderValueResolver valueResolver = new ApplicationSettingPlaceholderValueResolver(applicationSetting);
//		
//		beanFactory = new DefaultBeanFactory(
//				resourceLoader, valueResolver, 
//				beanDefineFileName, requiredPlaceholderValue,
//				Arrays.asList(resourceLoader, applicationSetting));
		
		beanFactory.addCloseEventListener(new BeanFactoryCloseEventListener() {
			@Override
			public void onClose() {
				factoryCloseEventFired = true;
			}
		});
	}

	@After
	public void tearDown() throws Exception {
		beanFactory.close();
		
		if (!"sleep".equals(status.getType())){
			throw new IllegalArgumentException("@PreDestory does not work.");
		}
		
		if (!factoryCloseEventFired) {
			throw new IllegalArgumentException("Factory close event does not work.");
		}
		
		span = System.currentTimeMillis() - start;
		logger.info("Time: {} ms", span);
	}

	@Test
	public void testGet() throws Exception {
		// test get bean by class
		status = beanFactory.getBean(Status.class);
		assertNotNull(status);
		assertEquals(Status.class, status.getClass());
		
		Food food = beanFactory.getBean(Food.class);
		assertNotNull(food);
		assertEquals(DefaultFood.class, food.getClass());
		
		// get by id
		Food foodById = (Food)beanFactory.getBean("food");
		assertNotNull(foodById);
		assertEquals(food, foodById);
		
		// get by auto id
		Dog dog = beanFactory.getBean(Dog.class);
		Dog dogById = (Dog)beanFactory.getBean("dog");
		assertEquals(dog, dogById);
		
		// get by class and check properties
		RubberDuck rubberDuck = beanFactory.getBean(RubberDuck.class);
		assertNotNull(rubberDuck);
		assertEquals("rubberDuck", rubberDuck.getName());
		
		// test get the prebuild bean
		assertNotNull(beanFactory.getBean(ResourceLoader.class));
		assertNotNull(beanFactory.getBean(ApplicationSetting.class));
		
		// test get beans
		Collection<Animal> animals = beanFactory.listBeans(Animal.class);
		assertEquals(3, animals.size());
		
		for(Animal animal : animals){
			switch (animal.getName()){
				case "cat":
					assertEquals("white", animal.getColor());
					assertEquals("color:white,food:default food", animal.say());
					break;
				
				case "dog":
					assertEquals("black", animal.getColor());
					assertEquals("color:black,food:default food", animal.say());
					break;
					
				case "duck":
					assertEquals("yellow", animal.getColor());
					assertEquals("color:yellow,food:default food", animal.say());
					assertEquals("active", status.getType());
					break;
			}
		}
		
		// test collection inject
		Zoo zoo = beanFactory.getBean(Zoo.class);
		assertNotNull(zoo);
		assertEquals(3, zoo.getAnimals().size());
		
		Collection<String> animalNames = Arrays.asList("cat", "dog", "duck");
		for (Animal animal : zoo.getAnimals()){
			assertTrue(animalNames.contains(animal.getName()));
		}
	}
}

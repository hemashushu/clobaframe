package org.archboy.clobaframe.ioc;

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
import org.archboy.clobaframe.ioc.bean.Animal;
import org.archboy.clobaframe.ioc.bean.DefaultFood;
import org.archboy.clobaframe.ioc.bean.Food;
import org.archboy.clobaframe.ioc.bean.Status;
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
		
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		
//		ApplicationSetting applicationSetting = new DefaultApplicationSetting(
//				resourceLoader, null, "classpath:application.properties");
		
		ApplicationSetting applicationSetting = new DefaultApplicationSetting(
				resourceLoader, null, null,
				"classpath:root.properties", 
				null, (String[])null);
		
		beanFactory = new DefaultBeanFactory(resourceLoader, applicationSetting);
	}

	@After
	public void tearDown() throws Exception {
		beanFactory.close();
		if (!"sleep".equals(status.getType())){
			throw new IllegalArgumentException("@PreDestory does not work.");
		}
		
		span = System.currentTimeMillis() - start;
		logger.info("Time: {} ms", span);
	}

	@Test
	public void testGet() throws Exception {
		// test get bean
		status = beanFactory.getBean(Status.class);
		assertNotNull(status);
		assertNull(status.getType());
		
		Food food = beanFactory.getBean(Food.class);
		assertNotNull(food);
		assertEquals(DefaultFood.class, food.getClass());
		
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
	}
}

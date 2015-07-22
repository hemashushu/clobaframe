package org.archboy.clobaframe.ioc;

import java.util.Arrays;
import java.util.Collection;
import org.archboy.clobaframe.ioc.bean.Animal;
import org.archboy.clobaframe.ioc.bean.DefaultFood;
import org.archboy.clobaframe.ioc.bean.Dog;
import org.archboy.clobaframe.ioc.bean.Food;
import org.archboy.clobaframe.ioc.bean.RubberDuck;
import org.archboy.clobaframe.ioc.bean.Special;
import org.archboy.clobaframe.ioc.bean.Status;
import org.archboy.clobaframe.ioc.bean.Zoo;
import org.archboy.clobaframe.ioc.impl.DefaultBeanFactory;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		// get by class
		status = beanFactory.get(Status.class);
		assertNotNull(status);
		assertEquals(Status.class, status.getClass());
		
		Food food = beanFactory.get(Food.class);
		assertNotNull(food);
		assertEquals(DefaultFood.class, food.getClass());
		
		// get by id
		Food foodById = (Food)beanFactory.get("food");
		assertNotNull(foodById);
		assertEquals(food, foodById);
		
		// get by class that without id define.
		Dog dog = beanFactory.get(Dog.class);
		Dog dogById = (Dog)beanFactory.get("dog");
		assertEquals(dog, dogById);
		
		// get by class and check properties
		RubberDuck rubberDuck = beanFactory.get(RubberDuck.class);
		assertNotNull(rubberDuck);
		assertEquals("rubberDuck", rubberDuck.getName());
		
		// get the prebuild bean
		assertNotNull(beanFactory.get(ResourceLoader.class));
		assertNotNull(beanFactory.get(ApplicationSetting.class));
		
		// get by interface
		Collection<Animal> animals = beanFactory.list(Animal.class);
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
		
		// get by annotation
		Collection<Object> specials = beanFactory.listByAnnotation(Special.class);
		assertEquals(2, specials.size());
		assertTrue(specials.contains(beanFactory.get(Dog.class)));
		assertTrue(specials.contains(beanFactory.get(RubberDuck.class)));
		
		// test collection inject
		Zoo zoo = beanFactory.get(Zoo.class);
		assertNotNull(zoo);
		assertEquals(3, zoo.getAnimals().size());
		
		Collection<String> animalNames = Arrays.asList("cat", "dog", "duck");
		for (Animal animal : zoo.getAnimals()){
			assertTrue(animalNames.contains(animal.getName()));
		}
	}
}

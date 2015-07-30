package org.archboy.clobaframe.ioc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.archboy.clobaframe.ioc.bean.Animal;
import org.archboy.clobaframe.ioc.bean.Cat;
import org.archboy.clobaframe.ioc.bean.DefaultFood;
import org.archboy.clobaframe.ioc.bean.Dog;
import org.archboy.clobaframe.ioc.bean.Duck;
import org.archboy.clobaframe.ioc.bean.Fish;
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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class BeanFactoryTest {

	private ListableBeanFactory beanFactory;
	private long start, span;
	
	private boolean factoryCloseEventFired = false;
	
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
	}

	@After
	public void tearDown() throws Exception {
		span = System.currentTimeMillis() - start;
		logger.info("Time: {} ms", span);
	}

	@Test
	public void testGet() throws Exception {
		
		// get by interface
		Food food = beanFactory.getBean(Food.class);
		assertEquals(DefaultFood.class, food.getClass());
		assertEquals("default", food.getType());
		
		// get by id
		Food foodById = (Food)beanFactory.getBean("food");
		assertEquals(food, foodById);
		
		// get by id - class without id define.
		Dog dog = (Dog)beanFactory.getBean("dog");
		assertEquals("dog", dog.getName());
		assertEquals("black", dog.getColor());
		assertEquals("color:black,food:default", dog.say());
		
		// get by id - class with @Named id specify
		Duck duck = (Duck)beanFactory.getBean("newDuck");
		assertEquals("duck", duck.getName());
		assertEquals("yellow", duck.getColor());

		// get by class
		Food foodByClass = beanFactory.getBean(DefaultFood.class);
		assertEquals(food, foodByClass);
		
		Cat cat = beanFactory.getBean(Cat.class);
		assertEquals("cat", cat.getName());
		assertEquals("white", cat.getColor());
		
		RubberDuck rubberDuck = beanFactory.getBean(RubberDuck.class);
		assertEquals("rubberDuck", rubberDuck.getName());
		assertEquals("undefine", rubberDuck.getColor()); // does not support inhert @Value and @Inject
		
		// get by class that inject by define file
		Fish fish = beanFactory.getBean(Fish.class);
		assertEquals("fish", fish.getName());
		assertEquals("grey", fish.getColor());
		
		// get the prebuild bean
		assertNotNull(beanFactory.getBean(ResourceLoader.class));
		assertNotNull(beanFactory.getBean(ApplicationSetting.class));
		
		// list by interface
		Map<String, ?> nameAnimals = beanFactory.getBeansOfType(Animal.class);
		assertEquals(4, nameAnimals.size());
		
		Collection<String> names1 = nameAnimals.keySet();
		assertTrue(names1.contains("cat"));
		assertTrue(names1.contains("dog"));
		assertTrue(names1.contains("newDuck"));
		assertTrue(names1.contains("fish"));
		
		Collection<Animal> animals = (Collection<Animal>)nameAnimals.values();
		assertTrue(animals.contains(cat));
		assertTrue(animals.contains(dog));
		assertTrue(animals.contains(duck));
		assertTrue(animals.contains(fish));
		
		// list by annotation
		Map<String, ?> nameSpecials = beanFactory.getBeansWithAnnotation(Special.class);
		assertEquals(2, nameSpecials.size());
		
		Collection<String> names2 = nameSpecials.keySet();
		assertTrue(names2.contains("dog"));
		assertTrue(names2.contains("rubberDuck"));
		
		Collection<?> specials = nameSpecials.values();
		assertTrue(specials.contains(dog));
		assertTrue(specials.contains(rubberDuck));
		
		// collection inject
		Zoo zoo = beanFactory.getBean(Zoo.class);
		
		Collection<Animal> animalsByZoo = zoo.getAnimals();
		assertEquals(4, animalsByZoo.size());
		assertTrue(animals.contains(cat));
		assertTrue(animals.contains(dog));
		assertTrue(animals.contains(duck));
		assertTrue(animals.contains(fish));
		
		// inject with @Named specified
		Animal seaAnimal = zoo.getSeaAnimal();
		assertEquals(fish, seaAnimal);
		
		// value inject with placeholder and empty default value
		assertEquals("middle", zoo.getSize());
		
		// inject by define file - string collection
		Collection<String> owners = zoo.getOwners();
		assertTrue(owners.contains("foo"));
		assertTrue(owners.contains(System.getProperty("os.name")));
		
		Collection<Animal> pets = zoo.getPets();
		assertTrue(pets.contains(cat));
		assertTrue(pets.contains(dog));
		
		// test life cycle maintain
		((DefaultBeanFactory)beanFactory).addApplicationListener(new ApplicationListener<BeanFactoryClosedEvent>() {
			@Override
			public void onApplicationEvent(BeanFactoryClosedEvent event) {
				factoryCloseEventFired = true;
			}
		});
				
		Status status = beanFactory.getBean(Status.class);
		assertEquals(Status.class, status.getClass());
		
		assertEquals("active", status.getType());

		((DefaultBeanFactory)beanFactory).close();
		
		// test life cycle maintain
		assertEquals("sleep", status.getType());
		
		// test factory close event
		assertTrue(factoryCloseEventFired);
	}
}

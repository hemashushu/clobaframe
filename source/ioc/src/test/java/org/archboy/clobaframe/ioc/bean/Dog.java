package org.archboy.clobaframe.ioc.bean;

import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
@Special // for test listing by annotation
public class Dog implements Animal {

	@Inject // bean inject
	private Food food;
	
	@Value("${test.dog.color}") // value inject with placeholder
	private String color;
	
	@Override
	public String getName() {
		return "dog";
	}

	@Override
	public String getColor() {
		return color;
	}
	
	@Override
	public String say() {
		return "color:" + color + ",food:" + food.getType();
	}
}

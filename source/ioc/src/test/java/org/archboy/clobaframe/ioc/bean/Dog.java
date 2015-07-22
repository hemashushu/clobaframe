package org.archboy.clobaframe.ioc.bean;

import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
@Special
public class Dog implements Animal {

	@Inject
	private Food food;
	
	@Value("${test.dog.color}")
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

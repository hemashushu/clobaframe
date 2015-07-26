package org.archboy.clobaframe.ioc.bean;

import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
public class Cat implements Animal {

	@Inject // bean inject
	private Food food;
	
	@Value("white") // @Value inject with direct value
	private String color;
	
	@Override
	public String getName() {
		return "cat";
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

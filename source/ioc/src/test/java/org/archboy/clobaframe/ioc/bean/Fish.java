package org.archboy.clobaframe.ioc.bean;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
public class Fish implements Animal {

	private Food food; // inject by define file
	private String color; // inject by define file
	private String name; // inject by define file

	public void setFood(Food food) {
		this.food = food;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public String say() {
		return "color:" + color + ",food:" + food.getType();
	}
}

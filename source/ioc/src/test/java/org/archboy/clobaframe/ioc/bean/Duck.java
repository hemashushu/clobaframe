package org.archboy.clobaframe.ioc.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
@Named("newDuck") // define bean id by annotation @Named
public class Duck implements Animal{
	
	@Inject // bean inject
	private Food food;

	@Value("${test.duck.color:yellow}") // value inject with placeholder and default value
	private String color = "undefine";
	
	@Inject // bean inject
	private Status status;
	
	@PostConstruct
	public void init() throws Exception {
		status.setType("active");
	}
	
	@PreDestroy
	public void close() throws Exception {
		status.setType("sleep");
	}
	
	@Override
	public String getName() {
		return "duck";
	}

	@Override
	public String getColor() {
		return color;
	}
	
	@Override
	public String say() {
		return "color:" + color + ",food:" + food.getType();
	}

	public Status getStatus() {
		return status;
	}
	
}

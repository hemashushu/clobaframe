package org.archboy.clobaframe.ioc.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
public class Duck implements Animal{
	
	@Inject
	private Food food;

	@Value("${test.duck.color:yellow}")
	private String color = "undefine";
	
	@Inject
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

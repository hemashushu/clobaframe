package org.archboy.clobaframe.ioc.bean;

import java.util.Collection;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
public class Zoo {
	
	@Inject
	private Collection<Animal> animals;

	@Value("test.zoo.size:")
	private String size;
	
	public Collection<Animal> getAnimals() {
		return animals;
	}

	public void setAnimals(Collection<Animal> animals) {
		this.animals = animals;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}

package org.archboy.clobaframe.ioc.bean;

import java.util.Collection;
import javax.inject.Inject;

/**
 *
 * @author yang
 */
public class Zoo {
	
	@Inject
	private Collection<Animal> animals;

	public Collection<Animal> getAnimals() {
		return animals;
	}

	public void setAnimals(Collection<Animal> animals) {
		this.animals = animals;
	}
	
}

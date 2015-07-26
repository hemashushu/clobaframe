package org.archboy.clobaframe.ioc.bean;

import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 */
public class Zoo {

	@Inject
	@Named("fish") // inject by specify bean id
	private Animal seaAnimal;
	
	@Inject // inject collection
	private Collection<Animal> animals;

	@Value("${test.zoo.size:}") // inject value with placeholder and with empty default value.
	private String size;
	
	private Collection<String> owners; // inject by bean define file.
	private Collection<Animal> pets; // inject by bean define file.
	
	public Collection<Animal> getAnimals() {
		return animals;
	}

	public void setOwners(Collection<String> owners) {
		this.owners = owners;
	}

	public Collection<String> getOwners() {
		return owners;
	}

	public void setPets(Collection<Animal> pets) {
		this.pets = pets;
	}

	public Collection<Animal> getPets() {
		return pets;
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

	public Animal getSeaAnimal() {
		return seaAnimal;
	}
	
}

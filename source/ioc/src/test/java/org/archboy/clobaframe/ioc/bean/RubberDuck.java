package org.archboy.clobaframe.ioc.bean;

/**
 *
 * @author yang
 */
@Special // for test listing by annotation
public class RubberDuck extends Duck {

	@Override // test override
	public String getName() {
		return "rubberDuck";
	}
	
}

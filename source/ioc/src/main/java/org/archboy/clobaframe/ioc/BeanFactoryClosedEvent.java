package org.archboy.clobaframe.ioc;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author yang
 */
public class BeanFactoryClosedEvent extends ApplicationEvent {

	public BeanFactoryClosedEvent(Object source) {
		super(source);
	}
	
}

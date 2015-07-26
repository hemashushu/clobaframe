package org.archboy.clobaframe.ioc.bean;

/**
 * Default implements of {@link Food}.
 * 
 * @author yang
 */
public class DefaultFood implements Food {

	@Override
	public String getType() {
		return "default";
	}
	
}

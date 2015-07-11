package org.archboy.clobaframe.ioc;

/**
 *
 * @author yang
 */
public interface PlaceholderValueResolver {
	
	Object getValue(String key);
	
	Object getValue(String key, Object defaultValue);
	
}

package org.archboy.clobaframe.setting.principal;

/**
 *
 * Principal refers to who has difference settings to the others, e.g. in a 
 * web application, the user and the group are typical two principals.
 * 
 * @author yang
 * @param <T>
 */
public interface Principal<T> {
	
	T getId();
	
}

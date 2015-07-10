package org.archboy.clobaframe.setting.common;

import java.util.Map;
import org.springframework.core.Ordered;

/**
 * The setting items provider.
 * One setting manager can contains several providers.
 * 
 * @author yang
 */
public interface SettingProvider extends Ordered {
	
	public static final int PRIORITY_HIGHEST = 0;
	public static final int PRIORITY_HIGHER = 20;
	public static final int PRIORITY_HIGH = 40;
	public static final int PRIORITY_NORMAL = 60;
	public static final int PRIORITY_LOW = 80;
	public static final int PRIORITY_LOWER = 100;
	
	/**
	 * The provider name.
	 * Optional.
	 * @return 
	 */
	String getName();
	
	/**
	 * 
	 * @return Never return null.
	 */
	Map<String, Object> list();
	
}

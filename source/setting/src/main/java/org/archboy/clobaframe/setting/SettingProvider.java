package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface SettingProvider {
	
	public static final int PRIORITY_HIGHEST = 0;
	public static final int PRIORITY_HIGHER = 20;
	public static final int PRIORITY_HIGH = 40;
	public static final int PRIORITY_NORMAL = 60;
	public static final int PRIORITY_LOW = 80;
	public static final int PRIORITY_LOWER = 100;
	
	/**
	 * The provider priority.
	 * 
	 * The higher priority item value will be selected when
	 * many items have the same item key (/name).
	 * @return 
	 */
	int getPriority();
	
	/**
	 * 
	 * @return 
	 */
	Map<String, Object> getAll();
	
}

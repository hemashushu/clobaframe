package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface SettingProvider {
	
	public static final int PRIORITY_HIGHEST = 0;
	public static final int PRIORITY_HIGHER = 2;
	public static final int PRIORITY_HIGH = 4;
	public static final int PRIORITY_NORMAL = 6;
	public static final int PRIORITY_LOW = 8;
	public static final int PRIORITY_LOWER = 10;
	
	int getPriority();
	
	Map<String, Object> get();

	boolean canWrite();
	
	void set(Map<String, Object> item);
	
	void set(String key, Object value);
}

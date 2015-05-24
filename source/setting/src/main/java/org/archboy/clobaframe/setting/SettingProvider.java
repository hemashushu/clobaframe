package org.archboy.clobaframe.setting;

import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
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
	 * Never return null.
	 * @return 
	 */
	Map<String, Object> getAll();
	
}

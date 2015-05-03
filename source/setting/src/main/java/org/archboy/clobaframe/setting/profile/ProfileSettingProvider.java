package org.archboy.clobaframe.setting.profile;

import java.util.Map;
import org.archboy.clobaframe.setting.SettingProvider;

/**
 *
 * @author yang
 */
public interface ProfileSettingProvider {
	
	public static final int PRIORITY_HIGHEST = 0;
	public static final int PRIORITY_HIGHER = 2;
	public static final int PRIORITY_HIGH = 4;
	public static final int PRIORITY_NORMAL = 6;
	public static final int PRIORITY_LOW = 8;
	public static final int PRIORITY_LOWER = 10;
	
	int getPriority();
	
	boolean support(Profile profile);
	
	Map<String, Object> get(Profile profile);

	boolean canWrite();
	
	void set(Profile profile, Map<String, Object> item);
	
	void set(Profile profile, String key, Object value);
}

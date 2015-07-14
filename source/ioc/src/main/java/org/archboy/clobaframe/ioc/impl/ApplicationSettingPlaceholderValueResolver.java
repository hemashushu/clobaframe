package org.archboy.clobaframe.ioc.impl;

import org.archboy.clobaframe.ioc.PlaceholderValueResolver;
import org.archboy.clobaframe.ioc.PlaceholderValueResolver;
import org.archboy.clobaframe.setting.application.ApplicationSetting;

/**
 *
 * @author yang
 */
public class ApplicationSettingPlaceholderValueResolver implements PlaceholderValueResolver {

	public static final boolean DEFAULT_REQUIRED_PLACEHOLDER_VALUE = true;
	
	public static final String SETTING_KEY_BEAN_DEFINE_FILE_NAME = "clobaframe.ioc.beanDefineFileName";
	public static final String SETTING_KEY_REQUIRED_PLACEHOLDER_VALUE = "clobaframe.ioc.requiredPlaceholderValue";

	private ApplicationSetting applicationSetting;
	
	public ApplicationSettingPlaceholderValueResolver(ApplicationSetting applicationSetting) {
		this.applicationSetting = applicationSetting;
	}
	
	@Override
	public Object getValue(String key) {
		return applicationSetting.getValue(key);
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		return applicationSetting.getValue(key, defaultValue);
	}
}

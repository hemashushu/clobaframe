package org.archboy.clobaframe.ioc.impl;

import org.archboy.clobaframe.ioc.PlaceholderValueResolver;
import org.archboy.clobaframe.setting.application.ApplicationSetting;

/**
 *
 * @author yang
 */
public class ApplicationSettingPlaceholderValueResolver implements PlaceholderValueResolver {

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

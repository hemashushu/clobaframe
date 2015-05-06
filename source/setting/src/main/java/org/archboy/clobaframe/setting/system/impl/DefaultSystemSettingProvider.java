package org.archboy.clobaframe.setting.system.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.impl.AbstractPropertiesFileSettingRepository;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class DefaultSystemSettingProvider extends AbstractPropertiesFileSettingRepository implements SystemSettingProvider {

	private Map<String, Object> setting = new HashMap<String, Object>();
	
	public DefaultSystemSettingProvider(ResourceLoader resourceLoader, String fileName) {
		load(resourceLoader, fileName);
	}
	
	private void load(ResourceLoader resourceLoader, String fileName){
		Resource resource = resourceLoader.getResource(fileName);
		if (resource.exists()) {
			InputStream in = null;
			try{
				in = resource.getInputStream();
				setting = read(in);
			}catch(IOException e) {
				// ignore
			}finally {
				IOUtils.closeQuietly(in);
			}
		}
	}
	
	@Override
	public int getPriority() {
		return PRIORITY_LOWER;
	}

	@Override
	public Map<String, Object> get() {
		return setting;
	}

	@Override
	public boolean canWrite() {
		return false;
	}

	@Override
	public void set(Map<String, Object> item) {
		//
	}

	@Override
	public void set(String key, Object value) {
		//
	}
	
}

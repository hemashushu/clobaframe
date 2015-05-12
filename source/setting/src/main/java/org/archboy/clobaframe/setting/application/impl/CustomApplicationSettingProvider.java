package org.archboy.clobaframe.setting.application.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.impl.AbstractJsonSettingAccess;
import org.archboy.clobaframe.setting.impl.Support;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSettingRepository;

/**
 *
 * @author yang
 */
public class CustomApplicationSettingProvider extends AbstractJsonSettingAccess implements ApplicationSettingProvider, ApplicationSettingRepository {

	private String dataDir;
	private String fileName;
	
	public CustomApplicationSettingProvider(String dataDir, String fileName) {
		this.dataDir = dataDir;
		this.fileName = fileName;
	}

	@Override
	public int getPriority() {
		return PRIORITY_HIGH;
	}

	@Override
	public Map<String, Object> getAll() {
		File folder = new File(dataDir);
		if (folder.exists()) {
			File file = new File(folder, fileName);
			if (file.exists() && file.isFile()) {
				InputStream in = null;
				try{
					in = new FileInputStream(file);
					return read(in);
				}catch(IOException e){
					// ignore
				}finally{
					IOUtils.closeQuietly(in);
				}
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}

	@Override
	public void update(Map<String, Object> item) {
		Map<String, Object> map = Support.merge(getAll(), item);
		
		// save
		OutputStream out = null;
		try {
			File folder = new File(dataDir);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			File file = new File(folder, fileName);
			if (!file.exists() || 
				(file.exists() && file.isFile())){
				out = new FileOutputStream(file);
				write(out, map);
			}
		} catch (IOException e) {
			// ignore
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void update(String key, Object value) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put(key, value);
		update(map);
	}
	
}

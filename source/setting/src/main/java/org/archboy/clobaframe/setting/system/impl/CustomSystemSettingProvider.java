package org.archboy.clobaframe.setting.system.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.impl.AbstractJsonSettingRepository;
import org.archboy.clobaframe.setting.impl.Support;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;

/**
 *
 * @author yang
 */
public class CustomSystemSettingProvider extends AbstractJsonSettingRepository implements SystemSettingProvider {

	private String dataDir;
	private String fileName;
	private Map<String, Object> setting = new HashMap<String, Object>();
	
	public CustomSystemSettingProvider(String dataDir, String fileName) {
		this.dataDir = dataDir;
		this.fileName = fileName;
		load();
	}

	private void load(){
		File folder = new File(dataDir);
		if (folder.exists()) {
			File file = new File(folder, fileName);
			if (file.exists()) {
				InputStream in = null;
				try{
					in = new FileInputStream(file);
					setting = read(in);
				}catch(IOException e){
					// ignore
				}finally{
					IOUtils.closeQuietly(in);
				}
			}
		}
	}
	
	@Override
	public int getPriority() {
		return PRIORITY_HIGH;
	}

	@Override
	public Map<String, Object> get() {
		return setting;
	}

	@Override
	public boolean canWrite() {
		return true;
	}

	@Override
	public void set(Map<String, Object> item) {
		Support.merge(setting, item);

		// save
		OutputStream out = null;
		try {
			File folder = new File(dataDir);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			File file = new File(folder, fileName);
			out = new FileOutputStream(file);
			write(out, setting);
		} catch (IOException e) {
			// ignore
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void set(String key, Object value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		set(map);
	}
	
}

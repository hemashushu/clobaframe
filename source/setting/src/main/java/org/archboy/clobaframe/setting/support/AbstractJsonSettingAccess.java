package org.archboy.clobaframe.setting.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 * @author yang
 */
public abstract class AbstractJsonSettingAccess implements SettingAccess {

	private TypeReference<Map<String, Object>> typeReference = 
			new TypeReference<Map<String, Object>>() {};

	private ObjectMapper objectMapper = new ObjectMapper();
	
	
	public AbstractJsonSettingAccess(){
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	@Override
	public Map<String, Object> read(InputStream in) throws IOException {
		Map<String, Object> map = objectMapper.readValue(in, typeReference);
		return Utils.flat(map);
	}

	@Override
	public void write(OutputStream outputStream, Map<String, Object> setting) throws IOException {
		Map<String, Object> map = Utils.cascade(setting);
		objectMapper.writeValue(outputStream, map);
	}
}

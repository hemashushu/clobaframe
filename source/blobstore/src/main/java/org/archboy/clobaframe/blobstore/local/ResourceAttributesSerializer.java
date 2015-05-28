package org.archboy.clobaframe.blobstore.local;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.mapdb.Serializer;

/**
 *
 * @author yang
 */
public class ResourceAttributesSerializer implements Serializer<ResourceAttributes>, Serializable {

	@Override
	public void serialize(DataOutput out, ResourceAttributes value) throws IOException {
		out.writeLong(value.getLastModified().getTime());
		out.writeUTF(value.getMimeType());
		
		if (value.getMetas() != null && StringUtils.isNotEmpty(value.getMetas())){
			out.writeUTF(value.getMetas());
		}
	}

	@Override
	public ResourceAttributes deserialize(DataInput in, int available) throws IOException {
		ResourceAttributes attributes = new ResourceAttributes();
		attributes.setLastModified(new Date(in.readLong()));
		attributes.setMimeType(in.readUTF());
		
		String metas = in.readUTF();
		if (StringUtils.isNotEmpty(metas)){
			//Map<String, Object> map = objectMapper.readValue(metas, mapTypeReference);
			attributes.setMetas(metas);
		}
		
		return attributes;
	}

	@Override
	public int fixedSize() {
		return -1;
	}
	
}

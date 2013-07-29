/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.archboy.clobaframe.blobstore.impl;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.blobstore.BlobInfo;
import org.archboy.clobaframe.blobstore.BlobInfoFactory;
import org.archboy.clobaframe.blobstore.BlobKey;

/**
 *
 * @author young
 *
 */
@Component
public class BlobInfoFactoryImpl implements BlobInfoFactory {

	@Override
	public BlobInfo createBlobInfo(BlobKey blobKey, long size,
			String contentType, InputStream content) {
		//Calendar calendar = Calendar.getInstance();
		Date lastModified = new Date(); // calendar.getTime();
		return new BlobInfoFromInputStream(blobKey, size, contentType,
				lastModified, content);
	}

	@Override
	public BlobInfo createBlobInfo(BlobKey blobKey, String contentType, byte[] content) {
		//Calendar calendar = Calendar.getInstance();
		Date lastModified = new Date(); // calendar.getTime();
		return new BlobInfoFromByteArray(blobKey, contentType, lastModified, content);
	}

}

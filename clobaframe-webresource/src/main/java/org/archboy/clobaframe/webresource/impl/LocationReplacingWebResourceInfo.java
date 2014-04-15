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
package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.util.Assert;

/**
 * Replace "[[RESOURCE_NAME]]" in the script or style sheet with the
 * actually resource location.
 *
 * @author arch
 */
public class LocationReplacingWebResourceInfo implements WebResourceInfo{

	private final Logger logger = LoggerFactory.getLogger(LocationReplacingWebResourceInfo.class);

	private WebResourceInfo webResourceInfo;
	private Map<String, String> locations;

	private Date lastModified;
	private byte[] content;
	private String hash;

	private static final String resourceNameRegex = "\\[\\[([\\/\\w\\.-]+)\\]\\]";
	private static final Pattern pattern = Pattern.compile(resourceNameRegex);

	public LocationReplacingWebResourceInfo(
			WebResourceInfo webResourceInfo,
			Map<String, String> locations) {

		Assert.notNull(webResourceInfo);
		Assert.notNull(locations);
		
		this.webResourceInfo = webResourceInfo;
		this.locations = locations;
		buildSnapshot();
	}

	@Override
	public String getHash() {
		refresh();
		return hash;
	}

	@Override
	public long getContentLength() {
		refresh();
		return content.length;
	}

	@Override
	public String getUniqueName() {
		return webResourceInfo.getUniqueName();
	}

	@Override
	public String getContentType() {
		return webResourceInfo.getContentType();
	}

	@Override
	public String getName() {
		return webResourceInfo.getName();
	}

	@Override
	public Date getLastModified() {
		return webResourceInfo.getLastModified();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		refresh();
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException {
		refresh();
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	private void refresh(){
		buildSnapshot();
	}

	private void buildSnapshot(){

		if (lastModified != null &&
				webResourceInfo.getLastModified().getTime() - lastModified.getTime() <= 0){
			// resource not changed
			return;
		}

		this.lastModified = webResourceInfo.getLastModified();

		String text = null;
		//ResourceContent resourceContent = null;
		InputStream in = null;

		try{
//			resourceContent = webResourceInfo.getContentSnapshot();
//			InputStream in = resourceContent.getInputStream();
			in = webResourceInfo.getInputStream();
			
			InputStreamReader reader = new InputStreamReader(in, "utf-8");
			text = IOUtils.toString(reader);
		} catch (IOException e) {
			logger.error("Fail to load web resource [{}].", webResourceInfo.getName());
		} finally{
			IOUtils.closeQuietly(in);
		}

		// convert replacing locations
		StringBuffer builder = new StringBuffer();
		Matcher matcher = pattern.matcher(text);

		while(matcher.find()){
			String name = matcher.group(1);
			String location = locations.get(name);
			if (location != null){
				matcher.appendReplacement(builder, location);
			}else{
				matcher.appendReplacement(builder, matcher.group());
			}
		}
		matcher.appendTail(builder);
		String replacedText = builder.toString();

		// convert text into input stream
		this.content = replacedText.getBytes(Charset.forName("utf-8"));
		this.hash = DigestUtils.sha256Hex(content);
	}

}

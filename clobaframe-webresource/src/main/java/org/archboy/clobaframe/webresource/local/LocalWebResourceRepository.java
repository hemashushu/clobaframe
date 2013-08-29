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
package org.archboy.clobaframe.webresource.local;

import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.annotation.PostConstruct;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.archboy.clobaframe.io.ContentTypeDetector;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;

@Named
public class LocalWebResourceRepository implements ResourceRepository{

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private ContentTypeDetector contentTypeAnalyzer;

	@Value("${webresource.local.path}")
	private String localPath;

	private List<WebResourceInfo> webResourceInfos;
	private final Logger logger = LoggerFactory.getLogger(LocalWebResourceRepository.class);

	@Override
	public List<WebResourceInfo> findAll() {
		return webResourceInfos;
	}

	@PostConstruct
	public void init() throws IOException {
		Resource resource = resourceLoader.getResource(localPath);
		File file = resource.getFile();
		if (!file.exists()){
			logger.error("Current default path is [{}], can not find the file [{}].",
					resourceLoader.getResource(".").getFile().getAbsolutePath(),
					localPath);
			throw new FileNotFoundException();
		}

		logger.debug("Scan web resource folder [{}].", file.getAbsolutePath());
		webResourceInfos = getLocalWebResources(file);
	}

	/**
	 * Scan all resources in the specify local directory
	 *
	 * @param resourceDir
	 */
	private List<WebResourceInfo> getLocalWebResources(File resourceDir) throws IOException{

		List<WebResourceInfo> webResourcesInfos = new ArrayList<WebResourceInfo>();

		Stack<File> dirs = new Stack<File>();
		dirs.push(resourceDir);

		while (!dirs.isEmpty()) {
			File dir = dirs.pop();
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					dirs.push(file);
				} else {
					String name = getName(resourceDir, file);
					String uniqueName = getUniqueName(resourceDir, file);
					String contentType = getContentType(file);

					WebResourceInfo webResourceInfo = new DefaultWebResourceInfo(
							file, name, uniqueName, contentType);
					webResourcesInfos.add(webResourceInfo);

					logger.debug(
							"Web static resource: [{}] , unique name: [{}], content type: [{}].",
							new Object[]{
							webResourceInfo.getName(),
							webResourceInfo.getUniqueName(),
							webResourceInfo.getContentType()}
							);
				}
			}
		}

		return webResourcesInfos;
	}

	private String getContentType(File file){
		String fileName = file.getName();
		return contentTypeAnalyzer.getByExtensionName(fileName);
	}

/**
 * It's the sha256hex the relation file name and the content data.
 */
	private String getUniqueName(File resourceDir, File file) throws IOException{


		String hash = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			hash = DigestUtils.sha256Hex(in);
		}finally{
			IOUtils.closeQuietly(in);
		}


//		String extensionName = FilenameUtils.getExtension(file.getName());
//		if (StringUtils.isNotEmpty(extensionName)) {
//			return hash + "." + extensionName;
//		}else{
//			return hash;
//		}

//		return hash;
		String name = file.getPath().substring(resourceDir.getPath().length() + 1);
		return DigestUtils.sha256Hex(hash + name);				
	}

	private String getName(File resourceDir, File file){
		String name = file.getPath().substring(resourceDir.getPath().length() + 1);
		return name.replace('\\', '/');
	}
}

package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.webresource.AbstractWebResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;

@Named
public class LocalWebResourceRepository extends AbstractWebResourceRepository{

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;

	@Value("${clobaframe.webresource.repository.local.path}")
	private String localPath;
	
	private File rootDir;
	private String rootPath;
	
	private final Logger logger = LoggerFactory.getLogger(LocalWebResourceRepository.class);

	@Override
	public String getName() {
		return "local";
	}

	@Override
	public int getPriority() {
		return PRIORITY_DEFAULT;
	}

	@PostConstruct
	public void init() throws IOException {
		Resource resource = resourceLoader.getResource(localPath);
		rootDir = resource.getFile();
		if (!rootDir.exists()){
			throw new FileNotFoundException(String.format(
					"Can not find the file [%s], p.s. the current path is [%s].",
					localPath,
					resourceLoader.getResource(".").getFile().getAbsolutePath()));
		}

		rootPath = rootDir.getPath();
	}

	@Override
	public WebResourceInfo getByName(String name) {
		File file = new File(rootDir, name);
		if (!file.exists()) {
			return null;
		}
		
		String mimeType = getMimeType(file);
		LocalWebResourceInfo webResourceInfo = new LocalWebResourceInfo(
				file, name, mimeType);
		return webResourceInfo;
	}
	
	@Override
	public Collection<String> getAllNames() {
		List<String> names = new ArrayList<String>();

		Stack<File> dirs = new Stack<File>();
		dirs.push(rootDir);

		while (!dirs.isEmpty()) {
			File dir = dirs.pop();
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					dirs.push(file);
				} else {
					String name = getResourceName(file);
//					String mimeType = getMimeType(file);
//
//					LocalWebResourceInfo webResourceInfo = new LocalWebResourceInfo(
//						file, name, mimeType);
//				
//					names.add(webResourceInfo);
					names.add(name);
				}
			}
		}

		return names;
	}

	private String getMimeType(File file){
		String fileName = file.getName();
		return mimeTypeDetector.getByExtensionName(fileName);
	}
		
	/**
	 * Return resource name by file.
	 * 
	 * By default, the resource name is the file name that excludes the resource dir path.
	 * e.g. 'css/common.css', 'js/moments.js'.
	 * 
	 * @param resourceDir
	 * @param file
	 * @return 
	 */
	private String getResourceName(File file){
		String name = file.getPath().substring(rootPath.length() + 1);
		return name.replace('\\', '/');
	}

}

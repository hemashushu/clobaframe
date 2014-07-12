package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.util.Assert;

/**
 * Replace "[[RESOURCE_NAME]]" in the script or style sheet with the
 * actually resource location.
 *
 * @author yang
 */
public class LocationReplacingWebResourceInfo implements WebResourceInfo{

	private final Logger logger = LoggerFactory.getLogger(LocationReplacingWebResourceInfo.class);

	private WebResourceInfo webResourceInfo;
	private Map<String, String> locations;
	
	private Date lastModified;
	private byte[] content;
	private String hash;

	private static final String resourceNameRegex = "([\\/\\w\\.-]+)([^'\"]*)";
	
	private static final String placeHoldRegex = "\\[\\[" + resourceNameRegex + "\\]\\]";
	private static final Pattern placeHoldPattern = Pattern.compile(placeHoldRegex);
	
	private static final String cssUrlRegex = "url\\(['|\"]" + resourceNameRegex + "['|\"]\\)";
	private static final Pattern cssUrlPattern = Pattern.compile(cssUrlRegex);

	private boolean autoConvertCssUrl;
	
	public LocationReplacingWebResourceInfo(
			WebResourceInfo webResourceInfo,
			Map<String, String> locations, boolean autoConvertCssUrl) {

		Assert.notNull(webResourceInfo);
		Assert.notNull(locations);
		
		this.webResourceInfo = webResourceInfo;
		this.locations = locations;
		this.autoConvertCssUrl = autoConvertCssUrl;
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
			
			// replace the css 'url' location.
			if (autoConvertCssUrl) {
				text = replaceLocation(cssUrlPattern, text);
			}
			
			// replace the location placehold.
			text = replaceLocation(placeHoldPattern, text);
			
		} catch (IOException e) {
			logger.error("Fail to load web resource [{}].", webResourceInfo.getName());
		} finally{
			IOUtils.closeQuietly(in);
		}
				
		// convert text into input stream
		this.content = text == null ? new byte[0]: text.getBytes(Charset.forName("utf-8"));
		this.hash = DigestUtils.sha256Hex(content);
	}

	/**
	 * convert replacing locations
	 * @param text
	 * @return 
	 */
	private String replaceLocation(Pattern pattern, String text) throws FileNotFoundException {
		StringBuffer builder = new StringBuffer();
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			String name = matcher.group(1);
			String canonicalName = getCanonicalName(name);
			
			String location = locations.get(canonicalName);
			if (location != null){
				
				if (pattern == cssUrlPattern) {
					String group = matcher.group();
					String result= group.replace(name, location);
					
					matcher.appendReplacement(builder, result);
				}else{
					matcher.appendReplacement(builder, location);
				}
			}else{
				// the specify resource can not be found.
				matcher.appendReplacement(builder, matcher.group());
			}
		}

		matcher.appendTail(builder);
		String replacedText = builder.toString();
		
		return replacedText;
	}
	
	/**
	 * Remove the ./ ../ and /
	 * @param pathName
	 * @return 
	 */
	private String getCanonicalName(String pathName) throws FileNotFoundException {
		if (pathName.startsWith("/")){
			pathName = pathName.substring(1);
		}
		
		if (pathName.startsWith("./")){
			pathName = pathName.substring(2);
		}
		
		String currentName = webResourceInfo.getName();
		int pathIdx = currentName.lastIndexOf('/');
		String currentPath = pathIdx < 0 ? "":currentName.substring(0, pathIdx);
		
		while(pathName.startsWith("../")){
			if (currentPath.equals("")){
				throw new FileNotFoundException(pathName);
			}
			
			pathName = pathName.substring(3);
			pathIdx = currentPath.lastIndexOf('/');
			currentPath = pathIdx < 0 ? "":currentPath.substring(0, pathIdx);
		}
		
		return currentPath.equals("") ? pathName : currentPath + "/" + pathName;
		
	}

}

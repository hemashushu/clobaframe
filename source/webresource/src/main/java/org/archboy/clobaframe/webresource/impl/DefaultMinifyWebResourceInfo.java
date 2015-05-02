package org.archboy.clobaframe.webresource.impl;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Minify resource.
 *
 * @author yang
 */
public class DefaultMinifyWebResourceInfo extends AbstractWebResourceInfo {

	private WebResourceInfo webResourceInfo;
	private String lastContentHash;
	private byte[] content;

	// see http://yui.github.io/yuicompressor/
	private static final int maxColumnsPerLine = 4096; // "0" means break each line.

	private final Logger logger = LoggerFactory.getLogger(DefaultMinifyWebResourceInfo.class);
	
	public DefaultMinifyWebResourceInfo(WebResourceInfo webResourceInfo) {
		Assert.notNull(webResourceInfo);
		this.webResourceInfo = webResourceInfo;
		addType(DefaultMinifyWebResourceInfo.class, webResourceInfo);
		rebuild();
	}

	@Override
	public String getContentHash() {
		return webResourceInfo.getContentHash();
	}

	@Override
	public long getContentLength() {
		rebuild();
		return content.length;
	}

	@Override
	public String getMimeType() {
		return webResourceInfo.getMimeType();
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
	public InputStream getContent() throws IOException {
		rebuild();
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		rebuild();
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	private void rebuild() {
		if (webResourceInfo.getContentHash().equals(lastContentHash)){
			// resource does not changed
			return;
		}

		this.lastContentHash = webResourceInfo.getContentHash();

		InputStream in = null;
		ByteArrayOutputStream out = null;

		try{
			in = webResourceInfo.getContent();
			out = new ByteArrayOutputStream();
			Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
			Writer writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
					
			if (webResourceInfo.getMimeType().equals(WebResourceManager.MIME_TYPE_STYLE_SHEET)){
				minifyStyleSheet(reader, writer);
			}else if(WebResourceManager.MIME_TYPE_JAVA_SCRIPT.contains(webResourceInfo.getMimeType())){
				try{
					minifyJavaScript(reader, writer);
				}catch(EvaluatorException e) {
					// can not minify the javascript, so just copy the source to target.
					InputStream copyIn = webResourceInfo.getContent();
					this.content = IOUtils.toByteArray(copyIn);
					copyIn.close();
					return;
				}
			}
			
			writer.flush();
			this.content = out.toByteArray();
			
		} catch(IOException e){
			// ignore
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		
	}

	private void minifyJavaScript(Reader reader, Writer writer) throws IOException {
		// javascript
		JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new ErrorReporter() {

			@Override
			public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
				logger.info("Minify javascript warning, message: {}, source name: {}, line: {}",
						message, sourceName, line);
			}

			@Override
			public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
				logger.info("Minify javascript error, message: {}, source name: {}, line: {}",
						message, sourceName, line);
			}

			@Override
			public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
				return new EvaluatorException(message, sourceName, lineOffset, lineSource, line);
			}
		});

		compressor.compress(writer, maxColumnsPerLine, false, false, false, false);
	}

	private void minifyStyleSheet(Reader reader, Writer writer) throws IOException {
		// style sheet
		CssCompressor compressor = new CssCompressor(reader);
		compressor.compress(writer, maxColumnsPerLine);
	}

}

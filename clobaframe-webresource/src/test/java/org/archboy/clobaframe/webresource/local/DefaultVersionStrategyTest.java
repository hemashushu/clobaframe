package org.archboy.clobaframe.webresource.local;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.impl.DefaultVersionStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml"})
public class DefaultVersionStrategyTest {
	
	@Inject
	@Named("defaultVersionStrategy")
	private VersionStrategy versionStrategy;
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	@Test
	public void testGetVersionName() {
		/**
		 * The name maybe includes the query and url hash.
		 * E.g.
		 * 
		 * webfont.eot?v=4.2.0
		 * webfont.eot?#iefix
		 * webfont.eot?#iefix&v=4.2.0
		 * webfont.eot?v=4.2.0#iefix
		 * 
		 */
		
		WebResourceInfo webResourceInfo1 = new TestWebResourceInfo("webfont.eof");
		WebResourceInfo webResourceInfo2 = new TestWebResourceInfo("webfont.eof?v=4.2.0");
		WebResourceInfo webResourceInfo3 = new TestWebResourceInfo("webfont.eof?#iefix");
		WebResourceInfo webResourceInfo4 = new TestWebResourceInfo("webfont.eof?#iefix&v=4.2.0");
		WebResourceInfo webResourceInfo5 = new TestWebResourceInfo("webfont.eof?v=4.2.0#iefix");
		
		assertEquals("webfont.eof?01234567", 
				versionStrategy.getVersionName(webResourceInfo1));
		assertEquals("webfont.eof?01234567&v=4.2.0", 
				versionStrategy.getVersionName(webResourceInfo2));
		assertEquals("webfont.eof?01234567#iefix", 
				versionStrategy.getVersionName(webResourceInfo3));
		assertEquals("webfont.eof?01234567#iefix&v=4.2.0", 
				versionStrategy.getVersionName(webResourceInfo4));
		assertEquals("webfont.eof?01234567&v=4.2.0#iefix", 
				versionStrategy.getVersionName(webResourceInfo5));
	}
	
	private class TestWebResourceInfo implements WebResourceInfo {

		private String name;

		public TestWebResourceInfo(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getContentHash() {
			return "0123456789abcdef";
		}

		@Override
		public long getContentLength() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getMimeType() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public InputStream getContent() throws IOException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public InputStream getContent(long start, long length) throws IOException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isSeekable() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Date getLastModified() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}

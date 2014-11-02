package org.archboy.clobaframe.media.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.image.impl.DefaultImage;
import org.archboy.clobaframe.media.image.impl.ImageLoader;
import org.archboy.clobaframe.io.ResourceInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.impl.DefaultTemporaryResources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ImagingTest {

	private static final String DEFAULT_SAMPLE_IMAGE_FOLDER = "sample/image/";
	private String sampleImageFolder = DEFAULT_SAMPLE_IMAGE_FOLDER;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MediaFactory mediaFactory; 

	@Inject
	private ImageGenerator imageGenerator;
	
	@Inject
	private Imaging imaging;

	private TemporaryResources temporaryResources = new DefaultTemporaryResources();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		temporaryResources.close();
	}

	@Test
	public void testMakeCrop() throws IOException {
		File file1 = getFileByName("test.png");
		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);
		Transform transform = imaging.crop(10, 10, 40, 40);
		Image image2 = imaging.apply(image1, transform);

		assertEquals(40, image2.getWidth());
		assertEquals(40, image2.getHeight());
		saveImage(image2, "imaging-crop-(10,10,40,40)");
	}
	
	@Test
	public void testMakeFixAspectRatioCrop() throws IOException {
		Transform transform = imaging.fixAspectRatioCrop(3D/4, 4D/3);
		
		File file1 = getFileByName("too-wide.jpg");
		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);
		Image image2 = imaging.apply(image1, transform);
		saveImage(image2, "imaging-fix-aspect-ratio-too-wide");
		double actualRatio1 = (double)image2.getWidth() / image2.getHeight();
		assertTrue(Math.abs(actualRatio1 - 4D/3) < 0.01);
		
		
		File file2 = getFileByName("too-high.jpg");
		Image image3 = (Image)mediaFactory.make(file2, temporaryResources);
		Image image4 = imaging.apply(image3, transform);
		double actualRatio2 = (double)image4.getWidth() / image4.getHeight();
		assertTrue(Math.abs(actualRatio2 - 3D/4) < 0.01);
		saveImage(image4, "imaging-fix-aspect-ratio-too-high");
	}

	@Test
	public void testMakeHorizontalFlip() {
		Image image1 = makeSampleImage();
		assertEquals(0, getImageMarkPosition(image1));

		Transform transform = imaging.horizontalFlip();
		Image image2 = imaging.apply(image1, transform);
		assertEquals(1, getImageMarkPosition(image2));
	}

	@Test
	public void testMakeVerticalFlip() {
		Image image1 = makeSampleImage();
		assertEquals(0, getImageMarkPosition(image1));

		Transform transform = imaging.verticalFlip();
		Image image2 = imaging.apply(image1, transform);
		assertEquals(3, getImageMarkPosition(image2));
	}

	@Test
	public void testMakeResize() throws IOException {
		File file = getFileByName("test.jpg");
		Image image1 = (Image)mediaFactory.make(file, temporaryResources);

		// resize to 500
		Transform transform1 = imaging.resize(500, 500);
		Image image2 = imaging.apply(image1, transform1);
		assertNotNull(image2);
		assertTrue(image2.getWidth() == 500 || image2.getHeight() == 500);

		// resize to 200
		Transform transform2 = imaging.resize(200, 200);
		Image image3 = imaging.apply(image1, transform2);
		assertNotNull(image3);
		assertTrue(image3.getWidth() == 200 || image3.getHeight() == 200);

		// save to file
		saveImage(image2, "imaging-resize-500x500");
		saveImage(image3, "imaging-resize-200x200");
	}

	@Test
	public void testMakeRotate() throws IOException {
		Image image0 = makeSampleImage();
		assertEquals(0, getImageMarkPosition(image0));

		Transform transform1 = imaging.rotate(90);
		Transform transform2 = imaging.rotate(180);
		Transform transform3 = imaging.rotate(270);

		Image image1 = imaging.apply(image0, transform1);
		Image image2 = imaging.apply(image0, transform2);
		Image image3 = imaging.apply(image0, transform3);

		assertEquals(1, getImageMarkPosition(image1));
		assertEquals(2, getImageMarkPosition(image2));
		assertEquals(3, getImageMarkPosition(image3));

		assertEquals(image0.getWidth(), image1.getHeight());
		assertEquals(image0.getWidth(), image2.getWidth());
		assertEquals(image0.getWidth(), image3.getHeight());

		// make a look
		File file1 = getFileByName("test.png");
		Image imageRotate1 = (Image)mediaFactory.make(file1, temporaryResources);
		Transform transformRotate90cw = imaging.rotate(90);
		Image imageRotate2 = imaging.apply(imageRotate1, transformRotate90cw);
		saveImage(imageRotate2, "imaging-rotate90cw");
	}

	@Test
	public void testMakeSquare() throws IOException{
		File file1 = getFileByName("test.jpg");
		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);
		assertTrue(image1.getWidth() != image1.getHeight());

		Transform transform1 = imaging.square();
		Image image2 = imaging.apply(image1, transform1);
		assertTrue(image2.getWidth() == image2.getHeight());
	}

	@Test
	public void testMakeResizeWithFixHeight() throws IOException{
		File file1 = getFileByName("test.jpg");
		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);

		File file2 = getFileByName("test1.jpg");
		Image image2 = (Image)mediaFactory.make(file2, temporaryResources);

		Transform transform1 = imaging.resizeWithFixHeight(100);
		Image image3 = imaging.apply(image1, transform1);
		Image image4 = imaging.apply(image2, transform1);

		assertEquals(100, image3.getHeight());
		assertEquals(100, image4.getHeight());
		assertEquals((int)(image1.getWidth() * 100 / image1.getHeight()), image3.getWidth());
		assertEquals((int)(image2.getWidth() * 100 / image2.getHeight()), image4.getWidth());
	}

	@Test
	public void testMakeResizeWithFixWidth() throws IOException{
		File file1 = getFileByName("test.jpg");
		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);

		File file2 = getFileByName("test1.jpg");
		Image image2 = (Image)mediaFactory.make(file2, temporaryResources);

		Transform transform1 = imaging.resizeWithFixWidth(100);
		Image image3 = imaging.apply(image1, transform1);
		Image image4 = imaging.apply(image2, transform1);

		assertEquals(100, image3.getWidth());
		assertEquals(100, image4.getWidth());
		assertEquals((int)(100 * image1.getHeight() / image1.getWidth()), image3.getHeight());
		assertEquals((int)(100 * image2.getHeight() / image2.getWidth()), image4.getHeight());
	}
	
	public void testApplyTransform() {
		//
	}

	@Test
	public void testMakeImageComposite() throws IOException {

		File file1 = getFileByName("test.bmp");
		File file2 = getFileByName("test.png");

		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);
		Image image2 = (Image)mediaFactory.make(file2, temporaryResources);

		Font font1 = new Font("Arial", Font.BOLD, 32);
		Composite composite1 = imaging.alpha(image1, 100, 100, 1F);
		Composite composite2 = imaging.alpha(image2, 120, 120, 0.5F);
		Composite composite3 = imaging.text("Hello world!", font1, Color.yellow, 200, 200, 1F);
		Composite composite4 = imaging.text("Watermark", font1, Color.blue, 200, 260, 0.6F);

		List<Composite> compositeList = Arrays.asList(composite1, composite2, composite3, composite4);
		Composite[] composites = (Composite[])compositeList.toArray();

		File file3 = getFileByName("test.jpg");
		Image image3 = (Image)mediaFactory.make(file3, temporaryResources);
		Image image3b = imaging.apply(image3, composites);
		saveImage(image3b, "imaging-composite-normal");

		Image image4 = imageGenerator.make(400, 400, new Color(0,0,0,0));
		Image image4b = imaging.apply(image4, composites);
		saveImage(image4b, "imaging-composite-transparent");
	}

	public void testMakeTextComposite() {
		//
	}

	public void testApplyComposite() {
		//
	}

	@Test
	public void testImageOutputSettings() throws IOException {
		File file1 = getFileByName("test2.jpg");
		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);
		assertEquals(Image.Format.JPEG, image1.getFormat());

		// reduce image quality
		ResourceInfo resourceInfo1 = image1.getResourceInfo(
				null,
				new OutputSettings(OutputSettings.OutputEncoding.PNG));
		
		ResourceInfo resourceInfo2 = image1.getResourceInfo();
		
		ResourceInfo resourceInfo3 = image1.getResourceInfo(
				null,
				new OutputSettings(OutputSettings.OutputEncoding.JPEG, 50));
		
		
		assertTrue(resourceInfo2.getContentLength() < resourceInfo1.getContentLength());
		assertTrue(resourceInfo3.getContentLength() < resourceInfo2.getContentLength());
		
		saveImage((Image)mediaFactory.make(resourceInfo1, temporaryResources), "outputsetting-png");
		saveImage((Image)mediaFactory.make(resourceInfo2, temporaryResources), "outputsetting-jpeg");
		saveImage((Image)mediaFactory.make(resourceInfo3, temporaryResources), "outputsetting-jpeg-q-50");
		
	}

	private File getFileByName(String fileName) throws IOException{
		Resource resource = resourceLoader.getResource(sampleImageFolder + fileName);
		return resource.getFile();
	}

	/**
	 * get the position of the black mark
	 *
	 * @param image
	 * @return 0=top-left, 1 = top-right, 2 = bottom-right, 3 = bottom-left
	 */
	private int getImageMarkPosition(Image image){
		BufferedImage bufferedImage = image.getBufferedImage();

		int width = bufferedImage.getWidth() -1;
		int height = bufferedImage.getHeight() -1;

		int[] pos = new int[4];
		pos[0] = bufferedImage.getRGB(0, 0);
		pos[1] = bufferedImage.getRGB(width, 0);
		pos[2] = bufferedImage.getRGB(width, height);
		pos[3] = bufferedImage.getRGB(0, height);

		for (int idx=0; idx<4; idx++){
			if (pos[idx] != -1){
				return idx;
			}
		}
		return -1;
	}

	/**
	 * save image to file, for human eyes check ;-)
	 *
	 * @param image
	 * @throws IOException
	 */
	private void saveImage(Image image, String filename) throws IOException{
		String formatName = image.getFormat().toString().toLowerCase();
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(tempDir, filename + "." + formatName);
		
		ResourceInfo resourceInfo = image.getResourceInfo();
		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();
		InputStream in = resourceInfo.getInputStream();
		
		FileOutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);
		
		out.close();
		in.close();
	}

	private Image makeSampleImage(){
		BufferedImage bufferedImage = new BufferedImage(
				100, 50 , BufferedImage.TYPE_INT_RGB);
    	Graphics2D graphics = bufferedImage.createGraphics();
    	graphics.setPaint(Color.WHITE);

		// fill background with white color
    	graphics.fillRect(0, 0, 100, 50);
    	graphics.setPaint(Color.BLACK);

		// draw a mark (black dot) on left-top corner
    	graphics.fillRect(0, 0, 1, 1);
    	graphics.dispose();

    	return new DefaultImage(null, Image.Format.PNG, bufferedImage);
	}
}

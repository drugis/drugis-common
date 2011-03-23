package org.drugis.common.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class FileChooserTest {

	private FileDialog.Filter d_simpleFilter;
	private FileDialog.Filter d_multiFilter;

	@Before
	public void setUp() {
		d_simpleFilter = new FileDialog.Filter(new String[] { "addis" }, "ADDIS data files");
		d_multiFilter = new FileDialog.Filter(new String[] { "addis", "xml" }, "ADDIS and Legacy XML files");
	}
	
	@Test
	public void TestExtensionFix() {
		assertEquals("/home/test.xml/test.xml", FileDialog.fixExtension("/home/test.xml/test.xml", "xml"));
		assertEquals("/home/test.xml/test.xm.xml", FileDialog.fixExtension("/home/test.xml/test.xm", "xml"));
		assertEquals("/home/test.xml/test.jpg.xml", FileDialog.fixExtension("/home/test.xml/test.jpg", "xml"));
		assertEquals("/home/test.xml/test.xml", FileDialog.fixExtension("/home/test.xml/test", "xml"));
		assertEquals("./.xml", FileDialog.fixExtension("./", "xml"));
		assertEquals("a.xml", FileDialog.fixExtension("a", "xml"));
	}
	
	@Test
	public void testAlwaysAcceptDirectory() {
		assertTrue(d_simpleFilter.accept(new File(".")));
	}
	
	@Test
	public void testAcceptFirstExtension() {
		assertTrue(d_simpleFilter.accept(new File("test.addis")));
		assertTrue(d_simpleFilter.accept(new File("/home/florin/test.addis")));
		assertTrue(d_simpleFilter.accept(new File("/home/florin/test.xml.addis")));
		assertFalse(d_simpleFilter.accept(new File("test.xml.addi")));
		assertFalse(d_simpleFilter.accept(new File("test.xml")));
		assertFalse(d_simpleFilter.accept(new File("test.xml.add")));
		assertFalse(d_simpleFilter.accept(new File("test.add")));
		assertFalse(d_simpleFilter.accept(new File("addis")));
		assertFalse(d_simpleFilter.accept(new File("/home/florin/../addis")));
	}
	
	@Test
	public void testAcceptMultipleExtensions() {
		assertTrue(d_multiFilter.accept(new File("test.addis")));
		assertTrue(d_multiFilter.accept(new File("test.xml")));
		assertTrue(d_multiFilter.accept(new File("/home/florin/test.addis")));
		assertTrue(d_multiFilter.accept(new File("/home/florin/test.xml.addis")));
		assertFalse(d_multiFilter.accept(new File("test.xml.addi")));
		assertFalse(d_multiFilter.accept(new File("test.xml.add")));
		assertFalse(d_multiFilter.accept(new File("test.add")));
		assertFalse(d_multiFilter.accept(new File("addis")));
		assertFalse(d_multiFilter.accept(new File("/home/florin/../addis")));
	}
}

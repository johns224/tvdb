package org.rossjohnson.tvdb;

import junit.framework.TestCase;

public class FileBasedSeriesMappingsTest extends TestCase {

	public void testSimpleMappings() throws Exception {
		FileBasedSeriesMappings mappings = new FileBasedSeriesMappings();
		assertEquals("73244", mappings.getValue("The Office"));
		assertEquals("The Human Target (2010)", mappings.getValue("The Human Target"));
		assertEquals(2, mappings.mappings.size());
	}
	
	public void testNonFileMappings() throws Exception {
		
	}
}

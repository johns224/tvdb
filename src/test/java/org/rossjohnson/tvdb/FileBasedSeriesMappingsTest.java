package org.rossjohnson.tvdb;

import junit.framework.TestCase;

public class FileBasedSeriesMappingsTest extends TestCase {

	public void testSimpleMappings() throws Exception {
		FileBasedSeriesMappings mappings = new FileBasedSeriesMappings();
		assertEquals("The Office", mappings.getValue("The Office"));
		assertEquals(0, mappings.mappings.size());
	}
	
	public void testNonFileMappings() throws Exception {
		
	}
}

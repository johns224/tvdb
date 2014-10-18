package org.rossjohnson.tvdb.encode;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class HandbrakeEncoderTest extends TestCase{

	private HandbrakeEncoder enc;
	
    @Override
    protected void setUp() throws Exception {
    	enc = new HandbrakeEncoder(File.createTempFile("foo", "bar"));
    }
	
	@Test
	public void testGetDestinationFileDefaultFormat() throws Exception {
		File f = new File("c:/a/b.txt");
		File expectedFile = new File(FileUtils.getTempDirectory(), "b." + enc.DEFAULT_FILE_FORMAT);
		assertEquals(expectedFile.getAbsolutePath(), enc.getDestinationFile(f, enc.DEFAULT_FILE_FORMAT).getAbsolutePath());		
	}
}

package org.rossjohnson.tvdb.encode;

import java.io.File;

public interface TVFileEncoder {

	/**
	 * Encodes (transcodes) one file to another 
	 * @param file the file to Encode
	 * @return the encoded file
	 * @throws Exception On any issues during the encode
	 */
	File encode(File file) throws Exception;
}

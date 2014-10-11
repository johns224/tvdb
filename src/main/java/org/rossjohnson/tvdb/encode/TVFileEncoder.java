package org.rossjohnson.tvdb.encode;

import java.io.File;

public interface TVFileEncoder {

	/**
	 * Encodes (transcodes) one file to another 
	 * @param file the file to Encode
	 * @return the encoded file
	 */
	File encode(File file);
}

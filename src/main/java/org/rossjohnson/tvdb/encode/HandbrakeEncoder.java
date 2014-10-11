package org.rossjohnson.tvdb.encode;

import java.io.File;
import java.io.FileNotFoundException;

public class HandbrakeEncoder implements TVFileEncoder {

	public static final String DEFAULT_FILE_FORMAT = "mp4";
	public static final String DEFAULT_CMDLINE = 
			"";
//	"C:\Program Files\Handbrake\HandBrakeCLI.exe" -i %1 -t 1 --angle 1 -c 1 -o "E:\Video\%~n1.mp4" -f mp4 -w 1280 --loose-anamorphic --modulus 2 -e x264 
//	-q 20 -r 30 --pfr -a 1 -E faac -6 dpl2 -R Auto -B 128 -D 0 --gain 0 --audio-fallback ffac3 --x264-profile=main --h264-level="3.1" --verbose=1
	
	public HandbrakeEncoder(File handbrakeExecutable) throws Exception {
		validateFile(handbrakeExecutable);
		
	}
	
	private void validateFile(File handbrakeExecutable) throws Exception {
		if (!handbrakeExecutable.exists()) {
			throw new FileNotFoundException("No Handbrake executable found at " + handbrakeExecutable.getAbsolutePath());
		}
	}

	public File encode(File file) {
		return null;
	}

}

package org.rossjohnson.tvdb.encode;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandbrakeEncoder implements TVFileEncoder {

	public static final String DEFAULT_FILE_FORMAT = "mp4";
	public static final String[] DEFAULT_CMDLINE = {
		null, "-i", null, "-t", "1", "--angle", "1", "-c", "1", "-o", null, "-f", null, "-w", "1280", 
		"--loose-anamorphic", "--modulus", "2", "-e", "x264", "-q", "20", "-r", "30", "--pfr", "-a", "1",
		"-E", "faac", "-6", "dpl2", "-R", "Auto", "-B", "128", "-D", "0", "--gain", "0", "--audio-fallback",
		"ffac3", "--x264-profile=main", "--h264-level=\"3.1\"", "--verbose=1" 
	};
	
	private static final Log log = LogFactory.getLog(HandbrakeEncoder.class);
	private File handbrakeExecutable;
	
	public HandbrakeEncoder(File handbrakeExecutable) throws Exception {
		validateFile(handbrakeExecutable);
		this.handbrakeExecutable = handbrakeExecutable;
	}
	
	private void validateFile(File handbrakeExecutable) throws Exception {
		if (!handbrakeExecutable.exists()) {
			throw new FileNotFoundException("No Handbrake executable found at " + handbrakeExecutable.getAbsolutePath());
		}
	}

	public File encode(File fileToEncode) throws Exception {
		File destinationFile = getDestinationFile(fileToEncode, DEFAULT_FILE_FORMAT);
		String[] cmd = getCommand(fileToEncode, destinationFile, DEFAULT_FILE_FORMAT);
		log.info("Encoding with command:\n" + Arrays.toString(cmd));
		ProcessBuilder pb = new ProcessBuilder(cmd);getClass();
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		pb.start().waitFor();
		return destinationFile;
	}
	
	private String[] getCommand(File fileToEncode, File destFile, String fileFormat) {
		String[] command = DEFAULT_CMDLINE;
		command[0] = handbrakeExecutable.getAbsolutePath();
		command[2] = fileToEncode.getAbsolutePath();
		command[10] = destFile.getAbsolutePath();
		command[12] = fileFormat;
		return command;
	}

	File getDestinationFile(File file, String fileFormat) {
		String filename = FilenameUtils.getBaseName(file.getName());
		return new File(FileUtils.getTempDirectory(), filename + "." + fileFormat);
	}

}

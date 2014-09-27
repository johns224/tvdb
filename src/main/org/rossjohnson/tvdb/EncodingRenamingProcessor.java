package org.rossjohnson.tvdb;

import java.io.File;

import org.rossjohnson.tvdb.encode.TVFileEncoder;

public class EncodingRenamingProcessor implements TVFileProcessor {

	TVFileRenamer renamer;
	TVFileEncoder encoder;
	File tvShowsBaseDir;
	
	public EncodingRenamingProcessor(TVFileRenamer renamer, TVFileEncoder encoder, File tvShowsBaseDir) {
		this.renamer = renamer;
		this.encoder = encoder;
		this.tvShowsBaseDir = tvShowsBaseDir;
	}
	
	public void process(File file) throws Exception {
		encoder.encode(file);
		renamer.rename(file, tvShowsBaseDir);
	}
	
	public static void main(String[] args) {
		if (!validateArgs(args)) {
			printUsage();
			System.exit(1);
		}
//		EncodingRenamingProcessor proc = new EncodingRenamingProcessor(renamer, encoder, tvShowsBaseDir);
	}

	private static void printUsage() {
		System.out.println("USAGE:\n");
		System.out.println("THISPROCESS CONFIG_FILE ");
	}

	private static boolean validateArgs(String[] args) {
		boolean retVal = true;
		
		if (!new File(args[0]).exists()) {
			System.out.println(args[0] + " is not a valid directory");
			retVal = false;
		}
		
		// TODO more validations
		
		
		return retVal;
	}

}

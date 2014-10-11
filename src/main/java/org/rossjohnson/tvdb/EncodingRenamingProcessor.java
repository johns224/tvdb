package org.rossjohnson.tvdb;

import java.io.File;

import org.apache.commons.jci.monitor.FilesystemAlterationMonitor;
import org.rossjohnson.tvdb.encode.TVFileEncoder;
import org.rossjohnson.tvdb.file.TVFileListener;
import org.rossjohnson.tvdb.io.TvDbDAO;

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
	
	public static void main(String[] args) throws Exception {
		exitIfArgsNoGood(args);
		FilesystemAlterationMonitor fam = new FilesystemAlterationMonitor();
		EncodingRenamingProcessor erp = new EncodingRenamingProcessor(new TVFileRenamer(new TvDbDAO(new FileBasedSeriesMappings())), getEmptyEncoder(), new File(args[1]));
		fam.addListener(new File(args[0]), new TVFileListener(5000, erp));
		fam.setInterval(3000);
		fam.run();
	}

	private static void exitIfArgsNoGood(String[] args) {
		if (!validateArgs(args)) {
			printUsage();
			System.exit(1);
		}
	}

	private static TVFileEncoder getEmptyEncoder() {
		return new TVFileEncoder() {
			public File encode(File file) {
				return file;
			}
		};
	}

	private static void printUsage() {
		System.out.println("USAGE:\n");
		System.out.println("THISPROCESS DIRECTORY_TO_MONITOR TV_LIBRARY_BASE_DIR");
	}

	private static boolean validateArgs(String[] args) {
		boolean retVal = true;
		if (args.length != 2) retVal = false;
		
		for (int i=0; i<args.length; i++) {
			if (!new File(args[i]).exists()) {
				System.out.println(args[i] + " is not a valid directory");
				retVal = false;
			}
		}
		
		return retVal;
	}

}

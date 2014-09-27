package org.rossjohnson.tvdb.file;

import java.io.File;

import org.apache.commons.jci.listeners.AbstractFilesystemAlterationListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rossjohnson.tvdb.TVFileProcessor;

public class TVFileListener extends AbstractFilesystemAlterationListener {

	private TVFileProcessor processor;
	private long interval;
	private static final Log log = LogFactory.getLog(TVFileListener.class);
	
	public TVFileListener(long intervalTimeMillis, TVFileProcessor processor) {
		interval = intervalTimeMillis;
		this.processor = processor;
	}
	
	public void onFileCreate(File pFile) {
		long size = pFile.length();
		boolean doneWriting = false;
		
		log.info("Found file " + pFile.getName() + ", waiting " + interval + " ms to see if it's fully written");
		
		while (!doneWriting) {
			try {
				Thread.sleep(interval);
				
				long newSize = pFile.length();
				if (newSize == size) {
					doneWriting = true;
				}
				else {
					log.debug(pFile.getName() + " grew by " + (newSize - size) + " since last check.  Sleeping...");
					size = newSize;
				}
			}
			catch (InterruptedException e) {
				log.error("Interrupted scan of file " + pFile, e);
			}
		}
		
		// File should be written now, so process it
		try {
			processor.process(pFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package org.rossjohnson.tvdb.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.io.IOUtils;
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
		waitToBeWritten(pFile);
		processFile(pFile);
	}

	private void waitToBeWritten(File pFile) {
		boolean doneWriting = false;
		log.info("Found file " + pFile.getName() + ", waiting " + interval + " ms to see if it's fully written");
		
		try {
			while (!doneWriting) {
				Thread.sleep(interval);
				RandomAccessFile stream = null;
				try {
					stream = new RandomAccessFile(pFile, "rw");
					doneWriting = true;
				} catch (Exception e) {
					log.info("Waiting for file " + pFile.getName()
							+ " to be completely written");
				} finally {
					IOUtils.closeQuietly(stream);
				}
			}
		} catch (InterruptedException e) {
			log.error("Interrupted scan of file " + pFile, e);
		}
	}

	private void processFile(File pFile) {
		try {
			processor.process(pFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

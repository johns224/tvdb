package org.rossjohnson.tvdb.xbmc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XBMCManager {

	private XBMCConfig config;
	private static final Log log = LogFactory.getLog(XBMCManager.class);
	
	public XBMCManager(XBMCConfig config) {
		this.config = config;
	}
	
	public void updateAllLibraries() {
		for (String url : config.getXBMCHosts()) {
			updateLibrary(url);
		}
	}

	private void updateLibrary(String url) {
		try {
			log.info("Updating library " + url);
			new XBMCController(url, config.getPassword(), config.getPassword()).updateLibrary();
		}
		catch (Exception e) {
			// keep going if one doesn't work
			log.error("Problem updating XBMC library on " + url + ": " + e.getMessage());
		}
	}
}

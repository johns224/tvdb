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
		for (String host : config.getXBMCHosts()) {
			updateLibrary(host);
		}
	}

	private void updateLibrary(String host) {
		try {
			new XBMCController(host, config.getPassword(), config.getPassword()).updateLibrary();
		}
		catch (Exception e) {
			// keep going if one hosts doesn't work
			log.error("Problem updating XBMC library on " + host + ": " + e.getMessage());
		}
	}
}

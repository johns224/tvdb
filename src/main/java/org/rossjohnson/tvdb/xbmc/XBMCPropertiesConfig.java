package org.rossjohnson.tvdb.xbmc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class XBMCPropertiesConfig implements XBMCConfig {

	private static final String XBMC_PROPS_FILENAME = "xbmc.properies";
	private Properties props = new Properties();
	
	public XBMCPropertiesConfig() throws IOException {
		this(XBMCPropertiesConfig.class.getClassLoader().getResourceAsStream(XBMC_PROPS_FILENAME));
	}
	
	public XBMCPropertiesConfig(String propsFilename) throws IOException {
		this(XBMCPropertiesConfig.class.getClassLoader().getResourceAsStream(propsFilename));
	}
	
	public XBMCPropertiesConfig(InputStream instream) throws IOException {
		props.load(instream);
	}
	
	public List<String> getXBMCHosts() {
		String[] splitHosts = props.getProperty("xbmc.hosts").split(",");
		trimStringArray(splitHosts);
		createUrls(splitHosts);
		return Arrays.asList( splitHosts );
	}

	public String getUsername() {
		return props.getProperty("xbmc.username");
	}
	
	public String getPassword() {
		return props.getProperty("xbmc.password");
	}

	private void createUrls(String[] splitHosts) {
		addHttpPrefix(splitHosts);
		addJsonPath(splitHosts);
	}

	private void addHttpPrefix(String[] splitHosts) {
		for (int i=0; i<splitHosts.length; i++) {
			if (!splitHosts[i].startsWith("http://")) {
				splitHosts[i] = "http://" + splitHosts[i];
			}
		}
	}

	private void addJsonPath(String[] splitHosts) {
		for (int i=0; i<splitHosts.length; i++) {
			if (!splitHosts[i].endsWith("/jsonrpc")) {
				splitHosts[i] += "/jsonrpc";
			}
		}
	}

	private void trimStringArray(String[] splitHosts) {
		for (int i = 0; i < splitHosts.length; i++) {
			splitHosts[i] = splitHosts[i].trim();
		}
	}
	
}

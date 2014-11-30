package org.rossjohnson.tvdb.xbmc;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;


public class XBMCPropertiesConfigTest {
	private static final String HOSTS_CONFIG = "xbmc.hosts=192.168.1.1:8080,http://192.168.1.2:90, http://192.168.1.3/jsonrpc,192.168.1.4";

	@Test
	public void testGetXBMCHosts() throws Exception { 
		XBMCPropertiesConfig config = new XBMCPropertiesConfig(new ByteArrayInputStream(HOSTS_CONFIG.getBytes(StandardCharsets.UTF_8)));
		List<String> xbmcHosts = config.getXBMCHosts();

		assertEquals(4, xbmcHosts.size());
		assertEquals("http://192.168.1.1:8080/jsonrpc", xbmcHosts.get(0));
		assertEquals("http://192.168.1.2:90/jsonrpc", xbmcHosts.get(1));
		assertEquals("http://192.168.1.3/jsonrpc", xbmcHosts.get(2));
		assertEquals("http://192.168.1.4/jsonrpc", xbmcHosts.get(3));
	}
}

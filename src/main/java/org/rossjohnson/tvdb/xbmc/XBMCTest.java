package org.rossjohnson.tvdb.xbmc;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.xbmc.android.jsonrpc.io.JsonApiRequest;


public class XBMCTest {

	private static final String URL = "http://localhost:8080/jsonrpc"; 
	public static final JsonNodeFactory NF = JsonNodeFactory.instance;
	
	public static void main(String[] args) {
		try {
			ObjectNode entity = NF.objectNode();
			entity.put("jsonrpc", "2.0");
			entity.put("method", "VideoLibrary.Scan");
			entity.put("id", "scan1");
			System.out.println(entity);
			JsonApiRequest.execute(URL, "xbmc", "xbmc", entity);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}

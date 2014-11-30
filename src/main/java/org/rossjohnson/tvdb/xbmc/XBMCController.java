package org.rossjohnson.tvdb.xbmc;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.xbmc.android.jsonrpc.io.JsonApiRequest;

public class XBMCController {

	private String url;
	private String password;
	private String username;
	
	public static final JsonNodeFactory NF = JsonNodeFactory.instance;
	
	public XBMCController(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public void updateLibrary() throws Exception {
		ObjectNode node = getBaseNode("scan1");
		node.put("method", "Library.Scan");
		JsonApiRequest.execute(url, username, password, node);
	}
	
	private ObjectNode getBaseNode(String id) {
		ObjectNode entity = NF.objectNode();
		entity.put("jsonrpc", "2.0");
		entity.put("id", id);
		return entity;
	}
}

/*
 *      Copyright (C) 2005-2015 Team XBMC
 *      http://xbmc.org
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XBMC Remote; see the file license.  If not, write to
 *  the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *  http://www.gnu.org/copyleft/gpl.html
 *
 */

package org.xbmc.android.jsonrpc.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

/**
 * Performs HTTP POST requests on the XBMC JSON API and handles the parsing from
 * and to {@link ObjectNode}.
 * <p/>
 * <i>Note</i>: All in here is synchronous.
 *
 * @author Joel Stemmer <stemmertech@gmail.com>
 * @author freezy <freezy@xbmc.org>
 */
public class JsonApiRequest {

	private static final Log log = LogFactory.getLog(JsonApiRequest.class);

	private static final int REQUEST_TIMEOUT = 5000; // 5 sec
	private static final ObjectMapper OM = new ObjectMapper();

	/**
	 * Executes a POST request to the URL using the JSON Object as request body
	 * and returns a JSON Object if the response was successful.
	 *
	 * @param url
	 *            Complete URL with schema, host, port if not default and path.
	 * @param entity
	 *            Object being serialized as message body
	 * @return JSON Object of the JSON-RPC response.
	 * @throws Exception
	 */
	public static ObjectNode execute(String url, String user, String pass, ObjectNode entity) throws Exception {
		String response = postRequest(new URL(url), user, pass, entity.toString());
		return parseResponse(response);
	}

	/**
	 * Execute a POST request on URL using entity as request body.
	 *
	 * @param url
	 * @param entity
	 * @return The response as a string
	 * @throws Exception
	 * @throws IOException
	 */
	private static String postRequest(URL url, String user, String pass, String entity) throws Exception {
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("POST");

		// http basic authorization
		if (user != null && !user.isEmpty() && pass != null && !pass.isEmpty()) {
			final String token = new String(Base64.encodeBase64((user + ":" + pass).getBytes(), false));
			conn.setRequestProperty("Authorization", "Basic " + token);
		}
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("User-Agent", buildUserAgent());

		conn.setConnectTimeout(REQUEST_TIMEOUT);
		conn.setReadTimeout(REQUEST_TIMEOUT);

		conn.setDoOutput(true);

		OutputStreamWriter output = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
		output.write(entity);
		output.close();

		log.info("POST request: " + conn.getURL());
		log.info("POST entity:" + entity);

		StringBuilder response = new StringBuilder();
		BufferedReader reader = null;

		final int code = conn.getResponseCode();
		if (code == 200) {
			try {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"), 8192);
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			log.info("POST response: " + response.toString());
			return response.toString();

		} else {
			switch (code) {
			case 400:
				throw new Exception("Server says \"400 Bad HTTP request\".");
			case 401:
				throw new Exception("Server says \"401 Unauthorized\".");
			case 403:
				throw new Exception("Server says \"403 Forbidden\".");
			case 404:
				throw new Exception("Server says \"404 Not Found\".");
			default:
				if (code >= 100 && code < 200) {
					throw new Exception("Server returned informational code " + code + " instead of 200.");
				} else if (code >= 200 && code < 300) {
					throw new Exception("Server returned success code " + code + " instead of 200.");
				} else if (code >= 300 && code < 400) {
					throw new Exception("Server returned redirection code " + code + " instead of 200.");
				} else if (code >= 400 && code < 500) {
					throw new Exception("Server returned client error " + code + ".");
				} else if (code >= 500 && code < 600) {
					throw new Exception("Server returned server error " + code + ".");
				} else {
					throw new Exception("Server returned unspecified code " + code + ".");
				}
			}
		}

	}

	/**
	 * Parses the JSON response string and returns a {@link ObjectNode}.
	 *
	 * If the response is not valid JSON, contained an error message or did not
	 * include a result then a HandlerException is thrown.
	 *
	 * @param response
	 * @return ObjectNode Root node of the server response, unserialized as
	 *         ObjectNode.
	 * @throws Exception
	 */
	private static ObjectNode parseResponse(String response) throws Exception {
		try {
			final ObjectNode node = (ObjectNode) OM.readTree(response.toString());

			if (node.has("error")) {
				if (node.get("error").isTextual()) {
					final TextNode error = (TextNode) node.get("error");
					log.error("[JSON-RPC] " + error.getTextValue());
					log.error("[JSON-RPC] " + response);
					throw new Exception("Error: " + error.getTextValue(), null);
				} else {
					final ObjectNode error = (ObjectNode) node.get("error");
					log.error("[JSON-RPC] " + error.get("message").getTextValue());
					log.error("[JSON-RPC] " + response);
					throw new Exception("Error " + error.get("code").getIntValue() + ": " + error.get("message").getTextValue(), null);
				}
			}

			if (!node.has("result")) {
				log.error("[JSON-RPC] " + response);
				throw new Exception("Neither result nor error object found in response.", null);
			}

			if (node.get("result").isNull()) {
				return null;
			}

			return node;
		} catch (JsonProcessingException e) {
			throw new Exception("Parse error: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new Exception("Parse error: " + e.getMessage(), e);
		}
	}

	/**
	 * Build user agent used for the HTTP requests
	 *
	 * TODO: include version information
	 *
	 * @return String containing the user agent
	 */
	private static String buildUserAgent() {
		return "xbmc-jsonrpclib-android";
	}
}

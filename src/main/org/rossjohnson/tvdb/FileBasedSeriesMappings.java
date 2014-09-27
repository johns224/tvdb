package org.rossjohnson.tvdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Copyright 2010 Ross Johnson
 *  <p/>
 *  This file is part of TVDB Renamer.
 *  <p/>
 *  TVDB Renamer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  <p/>
 *  TVDB Renamer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <p/>
 *  You should have received a copy of the GNU General Public License
 *  along with TVDB Renamer.  If not, see <http://www.gnu.org/licenses/>.
 *  <p/>
 */
public class FileBasedSeriesMappings implements SeriesMappings {
	private static final Log log = LogFactory.getLog(FileBasedSeriesMappings.class);
	Map<String, String> mappings;

	public FileBasedSeriesMappings() {
		mappings = new HashMap<String, String>();
		
	}
	
	public FileBasedSeriesMappings(File mappingsFile) {
		this();
		try {
			InputStream inStream = new FileInputStream(mappingsFile); //ClassLoader.getSystemResourceAsStream("tvdb.mappings");
			mappings = loadMap(inStream);
		}
		catch (Exception e) {
			log.info("Could not load tvdb.mappings.  Will try to perform lookups based on best guesses.");
		}
	}
	
	public FileBasedSeriesMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}
	
	/* (non-Javadoc)
	 * @see org.rossjohnson.tvdb.SeriesMappings#getValue(java.lang.String)
	 */
	public String getValue(String seriesName) {
		String value = mappings.get(seriesName);

		return value == null ? seriesName : value;
	}
	
	protected Map<String, String> loadMap(InputStream is) throws Exception {
		if (is == null) 
			throw new IOException ("Input stream is null!");

		Map<String, String> map = new HashMap<String, String>();
		try {
			
			List<String> lines = IOUtils.readLines(is);
			for (String line : lines) {
				if (line.startsWith("#") || line.trim().isEmpty())
					continue;
				
				String[] keyValues = line.split("=");
				
				if (keyValues.length == 2) {
					String key = keyValues[0].trim();
					String value = keyValues[1].trim();
					map.put(key, value);
					log.debug("Added mapping " + key + " = " + value);
				}
				else {
					log.info("Ignoring invalid line: " + line);
				}
			}
			return map;
		}
		finally {
			if (is != null) is.close();
		}
	}
}

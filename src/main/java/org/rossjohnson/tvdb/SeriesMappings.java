package org.rossjohnson.tvdb;
/**
 * Copyright 2010 Ross Johnson
 *
 * This file is part of TVDB Renamer.
 *
 *  TVDB Renamer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TVDB Renamer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TVDB Renamer.  If not, see <http://www.gnu.org/licenses/>.
 */
public interface SeriesMappings {

	/**
	 *  Gets the actual TVDB series name or series id for a given tv series name. 
	 *  <p/>
	 *  For example, "The Human Target" should actually be mapped to "The Human Target (2010)"
	 *  
	 * @param seriesName 
	 * @return either the tvdb series name, tvdb series id, or the originally provided seriesName if there is no mapping for it 
	 */
	String getValue(String seriesName);

}
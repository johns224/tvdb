package org.rossjohnson.tvdb.io;

import java.util.List;

import org.rossjohnson.tvdb.EpisodeInfo;
import org.rossjohnson.tvdb.SeriesInfo;
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
public interface TvDAO {

	/**
	 * Gets the (largest) TVDB.com series id for the show name provided
	 * @param seriesName the show name
	 * @return null, if no matches, or the largest seriesID found matching, which assumes it's the newest show
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	SeriesInfo getSeriesInfo(String seriesName) throws Exception;
	
	/**
	 * 
	 * @param seriesId the tvdb seriesId
	 * @return null, if no matches, or the matching Series
	 * @throws Exception
	 */
	SeriesInfo getSeriesInfo(int seriesId) throws Exception;
	
	/**
	 * Gets all matches from thetvdb.com series query
	 * @param seriesName the show name
	 * @return null if no matches, or all matches against thetvdb.com for the given seriesName
	 * @throws Exception
	 */
	List<SeriesInfo> getAllSeriesInfo(String seriesName) throws Exception;

	/**
	 * 
	 * @param seriesName 
	 * @param episodeName
	 * @return the episode info for any matching series/episode.  Null if no match.
	 * @throws Exception
	 */
	EpisodeInfo getEpisodeInfo(String seriesName, String episodeName) throws Exception;

	/**
	 * 
	 * @param series SeriesInfo for the series
	 * @param episodeName the episode name
	 * @return the episode info for any matching series/episode.  Null if no match.
	 * @throws Exception
	 */
	EpisodeInfo getEpisodeInfo(SeriesInfo series, String episodeName) throws Exception;

}
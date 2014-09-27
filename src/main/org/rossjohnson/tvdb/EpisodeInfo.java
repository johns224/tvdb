package org.rossjohnson.tvdb;

import java.util.Date;

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
public class EpisodeInfo {

	private SeriesInfo series;
	private int episodeNumber;
	private int seasonNumber;
	private String description;
	private Date firstAirDate;
	
	
	public Date getFirstAirDate() {
		return firstAirDate;
	}
	public void setFirstAirDate(Date firstAirDate) {
		this.firstAirDate = firstAirDate;
	}
	public SeriesInfo getSeries() {
		return series;
	}
	public void setSeries(SeriesInfo series) {
		this.series = series;
	}
	public int getEpisodeNumber() {
		return episodeNumber;
	}
	public void setEpisodeNumber(int episodeNumber) {
		this.episodeNumber = episodeNumber;
	}
	public int getSeasonNumber() {
		return seasonNumber;
	}
	public void setSeasonNumber(int seasonNumber) {
		this.seasonNumber = seasonNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "EpisodeInfo [description=" + description + ", episodeNumber="
				+ episodeNumber + ", firstAirDate=" + firstAirDate
				+ ", seasonNumber=" + seasonNumber + ", series=" + series + "]";
	}
	
	
}

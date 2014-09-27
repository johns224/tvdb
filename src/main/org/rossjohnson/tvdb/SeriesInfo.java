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
public class SeriesInfo implements Comparable<SeriesInfo> {

	private String seriesName;
	private String seriesId;
	private Date firstAired;
	
	

	public Date getFirstAired() {
		return firstAired;
	}
	public void setFirstAired(Date firstAired) {
		this.firstAired = firstAired;
	}
	public String getSeriesName() {
		return seriesName;
	}
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	public String getSeriesId() {
		return seriesId;
	}
	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}
	@Override
	public String toString() {
		return "SeriesInfo [firstAired=" + firstAired + ", seriesId="
				+ seriesId + ", seriesName=" + seriesName + "]";
	}
	public String getSafeSeriesName() {
		if (seriesName == null)
			return null;

		String s = getSeriesName();
		
		s = s.replaceAll("\\*", "");
		s = s.replaceAll("\"", "");
		s = s.replaceAll("'", "");
		s = s.replaceAll(":", "");
		s = s.replaceAll(";", "");
		s = s.replaceAll("!", "");
		s = s.replaceAll("\\?", "");
		s = s.replaceAll("&", "and");
		s = s.replaceAll("\\\\", "");
		s = s.replaceAll("\\$", "");

		return s;
	}
	
	@Override
	public int compareTo(SeriesInfo otherSeries) {
		if (otherSeries == null || otherSeries.getFirstAired() == null)
			return 1;
		
		if (getFirstAired() == null) 
			return -1;
		
		return getFirstAired().after(otherSeries.getFirstAired()) ? 1 : -1;
	}


}

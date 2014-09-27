package org.rossjohnson.tvdb;

public class RepeatingSeriesMappings implements SeriesMappings {

	@Override
	public String getValue(String seriesName) {
		return seriesName;
	}

}

package org.rossjohnson.tvdb.io;

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.rossjohnson.tvdb.EpisodeInfo;
import org.rossjohnson.tvdb.SeriesInfo;
import org.rossjohnson.tvdb.SeriesMappings;


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
public class TvDbDAO implements TvDAO {
	private static final Log log = LogFactory.getLog(TvDbDAO.class);
	private DateFormat dateFormat;

	private HttpClient client;
	private SeriesMappings mappings;
	
	public TvDbDAO(SeriesMappings mappings) { 
		this.mappings = mappings;
		client = new DefaultHttpClient();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	}
	
	@Override
	public EpisodeInfo getEpisodeInfo(String seriesName, String episodeName) throws Exception {
		// todo maybe this method should go in another class
		if (seriesName.equals(mappings.getValue(seriesName))) { 
			// if there is no mapping, we have to go through all series, looking for a hit
			
			List<SeriesInfo> allSeries = getAllSeriesInfo(seriesName);
			Collections.sort(allSeries);
			
			// go thru sorted list of series matches, starting with most recent 
			// "first aired date", looking for episode name match
			for (SeriesInfo series : allSeries) {
				EpisodeInfo ep = getEpisodeInfo(series, episodeName);
				if (ep != null) {
					return ep;
				}
			}
			return null;
		}
		else { // they mapped it to another series name/id, so search directly for that one 
			return getEpisodeInfo(getSeriesInfo(seriesName), episodeName);
		}
	}

	@Override
	public EpisodeInfo getEpisodeInfo(SeriesInfo series, String episodeName) throws Exception {
		String showInfoUrl = "http://www.thetvdb.com/data/series/" + series.getSeriesId() + "/all/";
		log.info("Executing " + showInfoUrl);
		HttpGet get = new HttpGet(showInfoUrl);
		HttpEntity entity = client.execute(get).getEntity();
		InputStream is = entity.getContent();
		
		try {
	        Document doc = new SAXReader().read(is);
	        String season = null;
	        String epNum = null;
	        String description = null;
	        Date firstAired = null;
	        List<Element> list = doc.selectNodes("/Data/Episode");
	        int matches = 0;
	        for(Element episode : list) {
	        	Element epName = (Element) episode.selectSingleNode("EpisodeName");
	        	if(episodeName.equalsIgnoreCase(epName.getText())) {
	        		matches++;
	        		season = episode.selectSingleNode("Combined_season").getText();
	        		epNum = episode.selectSingleNode("EpisodeNumber").getText();
	        		firstAired = dateFormat.parse(episode.selectSingleNode("FirstAired").getText());
	        		description = episode.selectSingleNode("Overview").getText();
	        	}
	        }
	        if (matches > 1) {
	        	log.warn("Found " + matches + " matches for episode " + episodeName + " of series " + series.getSeriesName());
	        }
	        
	        if (matches > 0) {
		        // for now, just go with last match found.  Eventually we might want to check first airdate
		        EpisodeInfo episode = new EpisodeInfo();
		        episode.setSeries(series);
		        episode.setEpisodeNumber(Integer.parseInt(epNum));
		        episode.setSeasonNumber(Integer.parseInt(season));
		        episode.setDescription(description); 
		        episode.setFirstAirDate(firstAired);
		        return episode;
	        }
		}
		finally {
			is.close();
		}
		return null;
	}

	@Override
	public List<SeriesInfo> getAllSeriesInfo(String seriesName) throws Exception {
		String showName = URLEncoder.encode(seriesName, "utf-8");
		String query = "http://www.thetvdb.com/api/GetSeries.php?seriesname=" + showName;
		log.info("Executing " + query);
		HttpGet get = new HttpGet(query);
		HttpEntity entity = client.execute(get).getEntity();
		InputStream is = entity.getContent();
		List<SeriesInfo> allMatches = new ArrayList<SeriesInfo>();
		
		try {
	        Document doc = new SAXReader().read(is);
	        SeriesInfo seriesInfo;
	        List<Element> seriesList = doc.selectNodes("/Data/Series"); // /seriesid");
	        switch (seriesList.size()) {
	        case 0:
	        	// no match
	        	log.warn("Didn't find a match for series named '" + showName + "'");
	        	break;
	        case 1:
	            seriesInfo = new SeriesInfo();
	        	Element singleSeries = seriesList.get(0);
	        	seriesInfo.setSeriesName(singleSeries.selectSingleNode("SeriesName").getText());
	        	seriesInfo.setSeriesId( singleSeries.selectSingleNode("seriesid").getText() );
	        	allMatches.add(seriesInfo);
	        	break;
	        default:
	        	log.info("Found multiple matches for show '" + showName + "'");
	        	for (Element series : seriesList) {
	        		Node firstAiredNode = series.selectSingleNode("FirstAired");
	        		if (firstAiredNode != null) {
	        			Date firstAired = dateFormat.parse(firstAiredNode.getText());
		        		seriesInfo = new SeriesInfo();
		        		seriesInfo.setFirstAired(firstAired);
			        	seriesInfo.setSeriesName(series.selectSingleNode("SeriesName").getText());
			        	seriesInfo.setSeriesId( series.selectSingleNode("seriesid").getText() );
			        	log.info("Found match: " + seriesInfo);
			        	allMatches.add(seriesInfo);
	        		}
	        	}
	        }
		}
		finally {
			is.close();
		}
	        
		return allMatches;
	}
	
	@Override
	public SeriesInfo getSeriesInfo(final String seriesName) throws Exception {
		// todo this method should also go into another class
		String mappedSeries = mappings.getValue(seriesName);
		log.info("Mapped " + seriesName + " to " + mappedSeries);
		int seriesId = -1;
		try {
			seriesId = Integer.parseInt(mappedSeries);
		} catch (NumberFormatException nfe) {} // it wasn't a series id, rather a name
		
		// if they provide the series ID here, use direct series lookup, don't use getAllSeriesInfo()
		// we have to lookup the series so that we get the actual TVDB series name, rather than 
		// what we have mapped, so that future TVDB lookups based on this name work.
		if (seriesId != -1) {
			return getSeriesInfo(seriesId);
		}
		
		List<SeriesInfo> allMatches = getAllSeriesInfo(seriesId == -1 ? mappedSeries : seriesName);
		if (allMatches.size() == 0) {
			return null;
		}
		else if (allMatches.size() == 1) {
			return allMatches.get(0);
		}
		
		SeriesInfo retVal;
		// for multiple matches, check for mapped name or id
		if (seriesId != -1) {
			for (SeriesInfo series : allMatches ) {
				if (seriesId == Integer.parseInt(series.getSeriesId())) {
					log.info("Found match using series id mapping: " + seriesName + "=" + seriesId);
					return series;
				}
			}
		}
		
		// if no match, use one with latest first airdate 
		retVal = getSeriesWithLatestFirstAirDate(allMatches);
		
		return retVal;
	}

	public SeriesInfo getSeriesInfo(int seriesId) throws Exception {
		SeriesInfo series = null;
		String query = "http://www.thetvdb.com/data/series/" + seriesId + "/all/";
		log.info("Executing " + query);
		Document doc = new SAXReader().read(client.execute(new HttpGet(query)).getEntity().getContent());
		Node seriesElement = doc.selectSingleNode("/Data/Series");
		if (seriesElement != null) {
			series = new SeriesInfo();
			series.setSeriesId(String.valueOf(seriesId));
			series.setSeriesName(seriesElement.selectSingleNode("SeriesName").getText());
			try {
				Date firstAired = dateFormat.parse(seriesElement.selectSingleNode("FirstAired").getText());
	    		series.setFirstAired(firstAired);
			} catch (Exception e) {}
		}
		return series;
	}

	
	private SeriesInfo getSeriesWithLatestFirstAirDate(List<SeriesInfo> allSeries) {
		Date newest = null;
		for (SeriesInfo series : allSeries) { 
			if (newest == null || series.getFirstAired().after(newest)) {
				newest = series.getFirstAired();
			}
		}
    	for ( SeriesInfo series : allSeries ) {
    		if (series.getFirstAired().equals(newest)) {
    			log.info("Selecting " + series.getSeriesName() + " with first air date " + series.getFirstAired());
    			return series;
    		}
    	}
    	return null;
	}
}

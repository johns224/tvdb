package org.rossjohnson.tvdb;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.rossjohnson.tvdb.io.TvDAO;
import org.rossjohnson.tvdb.io.TvDbDAO;

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
public class TVFileRenamer {

	protected static final Pattern FILENAME_PATTERN = Pattern.compile("(.*) - (.*) (\\(.*\\).*)");
//	protected static final Pattern SECONDARY_FILENAME_PATTERN = Pattern.compile("(.*) - (.*)(\\..*)");
//	protected static final Pattern TERTIARY_FILENAME_PATTERN = Pattern.compile("(.*)(\\..*)"); // (.*)(\\.*)");
	private TvDAO tvDAO;
	
	public TVFileRenamer(TvDAO tvDAO) throws Exception {
		this.tvDAO = tvDAO;
	}
	
	public void rename(File tvFile, File tvShowsBaseDir) throws Exception {
		Matcher matcher = FILENAME_PATTERN.matcher(tvFile.getName());
		if (!matcher.matches()) {
			System.out.println("Filename does not match pattern XXX - YYY (ZZZ)*");
			return;
		}
		
		String seriesName = getSeriesName(matcher);
		String episodeName = getEpisodeName(matcher);
		String endOfFilename = getEndOfFilename(matcher);
		
		EpisodeInfo episode = tvDAO.getEpisodeInfo(seriesName, episodeName);
		if (episode == null) {
			System.out.println("Could not get Episode information for " + tvFile.getAbsolutePath());
			return;
		}
		String seasonEpisode = getSeasonAndEpisode(episode);
		
		File destinationDir;
		if (tvShowsBaseDir == null) {
			// if they didn't specify a destination tvshows dir, just rename in current directory
			destinationDir = tvFile.getParentFile();
		}
		else {
			destinationDir = createDestinationDirectory(tvShowsBaseDir, episode.getSeries().getSafeSeriesName());
		}
		
		File newFile = new File(destinationDir, seriesName + "." + seasonEpisode + "." + episodeName + "." + endOfFilename);
		
		if (newFile.exists()) {
			System.out.println("File " + newFile.getAbsolutePath() + " already exists.  Renaming cancelled.");
			return;
		}
		
		try {
			FileUtils.moveFile(tvFile, newFile);
			System.out.println("Renamed " + tvFile.getAbsolutePath() + " to " + newFile.getAbsolutePath());
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}	

	protected String getSeasonAndEpisode(EpisodeInfo episode) throws Exception {
		String season = "" + episode.getSeasonNumber();
		if (season.length() == 1) {
			season = "0" + season;
		}
		
		String episodeNum = "" + episode.getEpisodeNumber();
		if (episodeNum.length() == 1) {
			episodeNum = "0" + episodeNum;
		}
		
		return "S" + season + "E" + episodeNum;
	}

	protected File createDestinationDirectory(File tvShowsBaseDir, String seriesName) throws Exception {
		File destDir = new File(tvShowsBaseDir, seriesName);
		if (!destDir.exists() && !destDir.mkdirs()) {
			throw new IOException("Unable to create destination directory " + destDir.getAbsolutePath());
		}
		return destDir;
	}

	protected String getSeriesName(Matcher matcher) throws Exception {
		return matcher.group(1).trim();
	}
	
	protected String getEpisodeName(Matcher matcher) {
		return matcher.group(2).trim();
	}
	
	protected String getEndOfFilename(Matcher matcher) {
		return matcher.group(3);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			printUsage();
		}
		
		File tvFile = new File(args[0]);
		if (!tvFile.exists() || tvFile.isDirectory()) {
			System.err.println("File " + tvFile.getAbsolutePath() + " does not exist or is not a valid file!");
			printUsage();
		}
		
		File tvShowsDir = null;
		if (args.length > 1) {
			tvShowsDir = new File(args[1]);
			if (!tvShowsDir.exists() || !tvShowsDir.isDirectory()) {
				System.err.println(tvShowsDir.getAbsolutePath() + " is not a valid directory!");
				printUsage();
			}
		}

		FileBasedSeriesMappings mappings;
		if (System.getProperty("map") == null) {
			mappings = new FileBasedSeriesMappings();
		}
		else {
			mappings = new FileBasedSeriesMappings(new File(System.getProperty("map")));
		}
		new TVFileRenamer(new TvDbDAO(mappings)).rename(tvFile, tvShowsDir);
	}

	private static void printUsage() {
		System.out.println("\nUSAGE: TVDBRenamer fileToRename [baseTVShowsDirectory]");
		System.out.println("\nThis program will use theTVDB.com to parse a file of name \"Series Name - Episode Name (Date)*\"");
		System.out.println("to \"Series Name.SnnEmm.Episode Name (Date)*\" ");
		System.out.println("\nWhere nn = season number and mm = episode number");
		System.out.println("\nIf baseTVShowsDirectory is specified, it will place the file in the \"baseTVShowsDirectory/Series Name\" directory");
		
		System.exit(1);
	}
}

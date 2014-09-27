package org.rossjohnson.tvdb;

import java.io.File;
import java.util.regex.Matcher;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.rossjohnson.tvdb.io.TvDbDAO;

public class TVFileRenamerTest extends TestCase {

	private static final String test1 = "Chuck - Chuck Versus the Anniversary (09_20_2010).mpg";
	private static final String test2 = "Human Target - Christopher Chance (04_14_2010).qsfix.mpg";
	private static final String test3 = "Handy Manny - Motorcycle Adventure (10_11_2010).mpeg";
	private static final String badMatch = "Human Target - Christopher Chance.(04_14_2010).mpg";

	// todo these patterns as well
	private static final String secondary1 = "Chuck - Chuck Versus the Anniversary.mpg";
	private static final String tertiary1 = "Chuck.Chuck Versus the Anniversary.mpg";
	
	private TVFileRenamer renamer;
	private File baseTempDir;
	
	@Override
	protected void setUp() throws Exception {
		renamer = new TVFileRenamer(new TvDbDAO(new RepeatingSeriesMappings()));
		baseTempDir = new File(FileUtils.getTempDirectory(), "org.rossjohnson.tvdb.TestTVFileRenamer");
	}
	
	@Override
	protected void tearDown() throws Exception {
		FileUtils.deleteQuietly(baseTempDir);
	}
	
//	public void testSecondaryPattern() throws Exception {
//		Matcher matcher = TVFileRenamer.SECONDARY_FILENAME_PATTERN.matcher(secondary1);
//		assertTrue(matcher.matches());
//		assertEquals("Chuck", matcher.group(1));
//		assertEquals("Chuck Versus the Anniversary", matcher.group(2));
//		assertEquals(".mpg", matcher.group(3));
//	}
//
//	public void testTertiaryPattern() throws Exception {
//		Matcher matcher = TVFileRenamer.TERTIARY_FILENAME_PATTERN.matcher(tertiary1);
//		assertTrue(matcher.matches());
//		assertEquals("Chuck", matcher.group(1));
////		assertEquals("Chuck Versus the Anniversary", matcher.group(2));
////		assertEquals(".mpg", matcher.group(3));
//	}
	
	public void testRename() throws Exception {
		File origDir = createTempDir();
		Thread.sleep(5);
		File destDir = createTempDir();
		
		File testFile = new File(origDir, test1);
		if (!testFile.createNewFile()) {
			throw new Exception ("Couldn't create temp file " + testFile.getAbsolutePath());
		}
		System.out.println("Created temp file " + testFile.getAbsolutePath());
		
		renamer.rename(testFile, destDir);
		
		File expectedFile = new File(destDir, "Chuck/Chuck.S04E01.Chuck Versus the Anniversary.(09_20_2010).mpg");
		
		assertTrue("Expected renamed file not found", expectedFile.exists());
		
	}
	
	public void testRename_DiffSeriesName() throws Exception {
		File origDir = createTempDir();
		Thread.sleep(5);
		File destDir = createTempDir();
		
		File testFile = new File(origDir, test2);
		if (!testFile.createNewFile()) {
			throw new Exception ("Couldn't create temp file " + testFile.getAbsolutePath());
		}

		File expectedFile = new File(destDir, "Human Target (2010)/Human Target.S01E12.Christopher Chance.(04_14_2010).qsfix.mpg");
		
		renamer.rename(testFile, destDir);
		
		assertTrue("Expected renamed file not found", expectedFile.exists());
		
	}
	
	public void testGetSeasonAndEpisode() throws Exception {
		EpisodeInfo ep = new EpisodeInfo();
		ep.setEpisodeNumber(1);
		ep.setSeasonNumber(2);
		
		assertEquals("S02E01", renamer.getSeasonAndEpisode(ep));
	}
	
	public void testGetSeriesName() throws Exception {
		assertEquals("Chuck", renamer.getSeriesName(getMatcher(test1)));
		assertEquals("Human Target", renamer.getSeriesName(getMatcher(test2)));
		assertEquals("Handy Manny", renamer.getSeriesName(getMatcher(test3)));
		
		try {
			renamer.getSeriesName(getMatcher(badMatch));
			fail("Should have thrown exception on bad file pattern");
		}
		catch (Exception e) {
			// success
		}
	}

	public void testGetEpisodeName() throws Exception {
		assertEquals("Chuck Versus the Anniversary", renamer.getEpisodeName(getMatcher(test1)));
		assertEquals("Christopher Chance", renamer.getEpisodeName(getMatcher(test2)));
	}
	
	public void testGetEndOfFilename() throws Exception {
		assertEquals("(09_20_2010).mpg", renamer.getEndOfFilename(getMatcher(test1)));
		assertEquals("(04_14_2010).qsfix.mpg", renamer.getEndOfFilename(getMatcher(test2)));
	}
	
	private Matcher getMatcher(String name) throws Exception {
		Matcher matcher = renamer.FILENAME_PATTERN.matcher(name);
		if (!matcher.matches()) {
			throw new Exception("Filename '" + name + "' does not match pattern XXX - YYY (ZZZ).extension");
		}
		return matcher;
	}
	
	private File createTempDir() throws Exception {
		File dir = new File(baseTempDir, "" + System.currentTimeMillis());
		dir.mkdirs();
		System.out.println("Creatd " + dir.getAbsolutePath());
		return dir;
	}
}

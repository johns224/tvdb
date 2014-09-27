package org.rossjohnson.tvdb.io;

import static org.mockito.Mockito.*;
import junit.framework.TestCase;

import org.rossjohnson.tvdb.EpisodeInfo;
import org.rossjohnson.tvdb.RepeatingSeriesMappings;
import org.rossjohnson.tvdb.SeriesInfo;
import org.rossjohnson.tvdb.SeriesMappings;

public class TvDbDAOTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	public void testGetSeriesInfo_SingleMatch_NoFirstAirdate() throws Exception {
		TvDbDAO dao = new TvDbDAO(new RepeatingSeriesMappings());
		SeriesInfo series = dao.getSeriesInfo("Handy Manny");
		assertEquals("81587", series.getSeriesId());
		assertEquals("Handy Manny", series.getSeriesName());
	}

	public void testGetSeriesInfo_MultipleMatchesSelectsLatestAirdate()
			throws Exception {
		TvDbDAO dao = new TvDbDAO(new RepeatingSeriesMappings());
		SeriesInfo series = dao.getSeriesInfo("Human Target");
		assertEquals("94801", series.getSeriesId());
		assertEquals("Human Target (2010)", series.getSeriesName());
	}

	public void testGetEpisodeInfo() throws Exception {
		TvDbDAO dao = new TvDbDAO(new RepeatingSeriesMappings());
		SeriesInfo series = new SeriesInfo();
		series.setSeriesId("94801"); // human target

		EpisodeInfo ep = dao.getEpisodeInfo(series, "Ilsa Pucci");

		System.out.println(ep);

		assertEquals(1, ep.getEpisodeNumber());
		assertEquals(2, ep.getSeasonNumber());
	}

	public void testGetEpisodeInfo_SeriesNameOnly() throws Exception {
		TvDbDAO dao = new TvDbDAO(new RepeatingSeriesMappings());

		EpisodeInfo ep = dao.getEpisodeInfo("Human Target", "Ilsa Pucci");

		System.out.println(ep);

		assertEquals(1, ep.getEpisodeNumber());
		assertEquals(2, ep.getSeasonNumber());
	}
	
	public void testGetEpisodeInfo_SeriesWithAnd() throws Exception {
		TvDbDAO dao = new TvDbDAO(new RepeatingSeriesMappings());

		EpisodeInfo ep = dao.getEpisodeInfo("Eastbound & Down", "Chapter 9");

		System.out.println(ep);

		assertEquals(2, ep.getSeasonNumber());
		assertEquals(3, ep.getEpisodeNumber());
	}

	public void testGetEpisodeInfo_SeriesWithAndMappedToId() throws Exception {
		SeriesMappings mappings = mock(SeriesMappings.class);
		stub(mappings.getValue(eq("Eastbound and Down"))).toReturn("82467");
		
		TvDbDAO dao = new TvDbDAO(mappings);
		
		EpisodeInfo ep = dao.getEpisodeInfo("Eastbound and Down", "Chapter 9");
		
		System.out.println(ep);
		
		assertEquals(2, ep.getSeasonNumber());
		assertEquals(3, ep.getEpisodeNumber());
		
		// I think this is wrong - this should be "Eastbound & Down"
		assertEquals("Eastbound & Down", ep.getSeries().getSeriesName());
	}
	
	public void testGetEpisodeInfo_SeriesWithAndMappedToAmpersand() throws Exception {
		SeriesMappings mappings = mock(SeriesMappings.class);
		stub(mappings.getValue(eq("Eastbound and Down"))).toReturn("Eastbound & Down");
		
		TvDbDAO dao = new TvDbDAO(mappings);
		
		EpisodeInfo ep = dao.getEpisodeInfo("Eastbound and Down", "Chapter 9");
		
		System.out.println(ep);
		
		assertEquals(2, ep.getSeasonNumber());
		assertEquals(3, ep.getEpisodeNumber());
		assertEquals("Eastbound & Down", ep.getSeries().getSeriesName());
	}
	
	public void testGetEpisodeInfo_MappedSeriesWithAmpersandAndColon() throws Exception {
		SeriesMappings mappings = mock(SeriesMappings.class);
		stub(mappings.getValue(eq("Law and Order Special Victims Unit"))).toReturn("75692");
		
		TvDbDAO dao = new TvDbDAO(mappings);
		
		EpisodeInfo ep = dao.getEpisodeInfo("Law and Order Special Victims Unit", "Penetration");
		
		System.out.println(ep);
		
		assertEquals(12, ep.getSeasonNumber());
		assertEquals(8, ep.getEpisodeNumber());
		assertEquals("Law & Order: Special Victims Unit", ep.getSeries().getSeriesName());
	}
	
	// with the Office, the Israeli version aired after the US version, but we
	// actually want this US version in this test.
	public void testGetEpisodeInfo_TheOffice_NoMapping_LatestAirDateDoesntMatch()
			throws Exception {
		TvDbDAO dao = new TvDbDAO(new RepeatingSeriesMappings());

		EpisodeInfo ep = dao.getEpisodeInfo("The Office", "Costume Contest");

		System.out.println(ep);

		assertEquals(7, ep.getSeasonNumber());
		assertEquals(6, ep.getEpisodeNumber());
		assertEquals("The Office (US)", ep.getSeries().getSeriesName());
	}

	public void testEpisodeInfo_MappingContainsSeriesId() throws Exception {
		SeriesMappings mappings = mock(SeriesMappings.class);
		TvDbDAO dao = new TvDbDAO(mappings);

		// map "The Office" to id 73244
		stub(mappings.getValue(eq("The Office"))).toReturn("73244");
		
		EpisodeInfo ep = dao.getEpisodeInfo("The Office", "Costume Contest");
		
		System.out.println(ep);

		assertEquals(7, ep.getSeasonNumber());
		assertEquals(6, ep.getEpisodeNumber());
		assertEquals("The Office (US)", ep.getSeries().getSeriesName());

	}
	
	public void testEpisodeInfo_MappingContainsSeriesName() throws Exception {
		SeriesMappings mappings = mock(SeriesMappings.class);
		TvDbDAO dao = new TvDbDAO(mappings);
		
		// map "The Office" to "The Office (US)" 
		stub(mappings.getValue(eq("The Office"))).toReturn("The Office (US)");
		
		EpisodeInfo ep = dao.getEpisodeInfo("The Office", "Costume Contest");
		
		System.out.println(ep);
		
		assertEquals(7, ep.getSeasonNumber());
		assertEquals(6, ep.getEpisodeNumber());
		assertEquals("The Office (US)", ep.getSeries().getSeriesName());
		
	}
}

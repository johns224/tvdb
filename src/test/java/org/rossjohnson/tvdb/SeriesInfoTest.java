package org.rossjohnson.tvdb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class SeriesInfoTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		
	} 
	
	public void testCompareTo() {
		final SeriesInfo s1 = new SeriesInfo() {{ setSeriesName("Today"); setFirstAired(getToday()); }};
		final SeriesInfo s2 = new SeriesInfo() {{ setSeriesName("Tomorrow"); setFirstAired(getTomorrow()); }};
		final SeriesInfo s3 = new SeriesInfo() {{ setSeriesName("None"); setFirstAired(null); }};
		final SeriesInfo s4 = new SeriesInfo() {{ setSeriesName("Yesterday"); setFirstAired(getYesterday()); }};
		
		List<SeriesInfo> seriesList = new ArrayList<SeriesInfo>() {{ add(s1); add(s3); add(s2); add(s4); }};
		assertEquals(4, seriesList.size());
		
		Collections.sort(seriesList);
		
		assertEquals("None", seriesList.get(0).getSeriesName()); // todo: should never aired be first or last?  Not sure.
		assertEquals("Yesterday", seriesList.get(1).getSeriesName());
		assertEquals("Today", seriesList.get(2).getSeriesName());
		assertEquals("Tomorrow", seriesList.get(3).getSeriesName());
	}
	
	public void testGetSafeSeriesName() throws Exception {
		SeriesInfo si = new SeriesInfo();
		si.setSeriesName("Law & Order: Special Victims Unit");
		assertEquals("Law and Order Special Victims Unit", si.getSafeSeriesName());
	}
	
	private Date getToday() {
		return new Date();
	}
	
	private Date getTomorrow() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	private Date getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}
	
}

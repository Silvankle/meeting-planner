package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class CalendarTest {
	// Add test methods here. 
	// You are not required to write tests for all classes.
	
	@Test
	public void testAddMeeting_holiday() {
		// Create Midsommar holiday
		Calendar calendar = new Calendar();
		// Add to calendar object.
		try {
			Meeting midsommar = new Meeting(6, 26, "Midsommar");
			calendar.addMeeting(midsommar);
			// Verify that it was added.
			Boolean added = calendar.isBusy(6, 26, 0, 23);
			assertTrue("Midsommar should be marked as busy on the calendar",added);
		} catch(TimeConflictException e) {
			fail("Should not throw exception: " + e.getMessage());
		}
	}
}

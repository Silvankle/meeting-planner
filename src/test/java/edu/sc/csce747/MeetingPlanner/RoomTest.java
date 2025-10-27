package edu.sc.csce747.MeetingPlanner;

import org.junit.Before;
import org.junit.Test;
// import org.junit.Ignore; // Хэрэв FAIL тестүүдийг түр хойшлуулах бол ашиглаж болно.

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * RoomTest (JUnit4)
 *
 * Юуг шалгах вэ:
 *  a) Зөв өгөгдлөөр амжилттай ажиллах кейсүүд 
 *  b) Буруу/валид бус өгөгдөлд зөв Exception шидэх
 *  c) FAIL (санаатай унагах) — Calendar-ийн одоогийн сул талуудыг барих
 *
 * Тайлбар:
 *  - Room.addMeeting → Calendar.addMeeting руу дамжиж ажилладаг.
 *  - Давхцал гарвал Room түвшинд "Conflict for room <ID>:\n..." гэж мессежээ боож шиддэг.
 */
public class RoomTest {
	// Add test methods here. 
    // You are not required to write tests for all classes.
    private Room r1;
    private Person alice, bob;

    @Before
    public void setUp() {
        r1 = new Room("R1");
        alice = new Person("alice");
        bob   = new Person("bob");
    }

    /** Туслах: attendees жагсаалт үүсгэх */
    private ArrayList<Person> attendees(Person... ps) {
        ArrayList<Person> list = new ArrayList<>();
        for (Person p : ps) list.add(p);
        return list;
    }

    /** Туслах: уулзалт үүсгэх (Room r1-д, тодорхой тайлбартай) */
    private Meeting mk(int month, int day, int start, int end, String desc) {
        return new Meeting(month, day, start, end, attendees(alice, bob), r1, desc);
    }

    // =========================================================
    // 1) ЭЕРЭГ (HAPPY PATH)
    // =========================================================

    /** addMeeting + isBusy — хэвийн урсгал. */
    @Test
    public void add_and_isBusy_happyPath() throws Exception {
        r1.addMeeting(mk(6, 20, 10, 11, "Standup"));
        assertTrue("10–11 хооронд R1 завгүй байх ёстой", r1.isBusy(6, 20, 10, 11));
        assertFalse("11–12 хооронд R1 завтай байх ёстой", r1.isBusy(6, 20, 11, 12));
    }
    /** printAgenda(day) — нэмсэн уулзалтын текст хэвлэлтэд харагдана (зөөлөн шалгалт). */
    @Test
    public void printAgenda_shouldContainMeetingText() throws Exception {
        r1.addMeeting(mk(9, 9, 14, 15, "Review"));
        String text = r1.printAgenda(9, 9);
        assertTrue(text.contains("Review"));
        assertTrue(text.startsWith("Agenda for 9/9"));
    }
}

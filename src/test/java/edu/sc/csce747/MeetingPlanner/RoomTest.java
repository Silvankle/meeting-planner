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
    /** printAgenda(day) — нэмсэн уулзалтын текст хэвлэлтэд харагдана. */
    @Test
    public void printAgenda_shouldContainMeetingText() throws Exception {
        r1.addMeeting(mk(9, 9, 14, 15, "Review"));
        String text = r1.printAgenda(9, 9);
        assertTrue(text.contains("Review"));
        assertTrue(text.startsWith("Agenda for 9/9"));
    }
    /** getMeeting — нэмсэн уулзалтыг буцаан авч чадна. */
    @Test
    public void getMeeting_afterAdd_shouldReturnSame() throws Exception {
        Meeting m = mk(10, 1, 9, 10, "Planning");
        r1.addMeeting(m);
        Meeting got = r1.getMeeting(10, 1, 0);
        assertEquals("Planning", got.getDescription());
        assertEquals(9, got.getStartTime());
        assertEquals(10, got.getEndTime());
    }
    // =========================================================
    // 2) (EXCEPTION) — ЗӨВ УНАЛТ
    // =========================================================

    /** start > end үед TimeConflictException шидэх ёстой. */
    @Test(expected = TimeConflictException.class)
    public void addMeeting_startGreaterThanEnd_shouldThrow() throws Exception {
        r1.addMeeting(mk(4, 10, 15, 10, "Bad interval"));
    }

    /** Хүчингүй цаг — start == -1 → Exception. */
    @Test(expected = TimeConflictException.class)
    public void addMeeting_illegalStartHour_shouldThrow() throws Exception {
        r1.addMeeting(mk(4, 10, -1, 10, "Bad hour"));
    }

    /** Хүчингүй цаг — end == 24 → Exception. */
    @Test(expected = TimeConflictException.class)
    public void addMeeting_illegalEndHour_shouldThrow() throws Exception {
        r1.addMeeting(mk(4, 10, 9, 24, "Bad hour"));
    }
}

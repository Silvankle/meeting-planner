package edu.sc.csce747.MeetingPlanner;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Meeting ангид чиглэсэн unit test-үүд.
 *
 * ЗОРИЛГО:
 *  - Конструкторууд талбаруудыг зөв тохируулж байгаа эсэх
 *  - Attendee нэмэх/хасах үйлдэл
 *  - Setter/Getter-үүдийн ажиллах байдал
 *  - toString() мэдээллийн цогц байдлыг шалгах
 *
 * ТАЙЛБАР:
 *  - Meeting ангид огноо/цагийн валидаци хийгддэггүй (Calendar.checkTimes хийдэг).
 *    Тиймээс энэ файлд зөвхөн Meeting доторх төлөв, формат, жагсаалт удирдлагыг шалгана.
 */

public class MeetingTest {
	// Add test methods here. 
    // You are not required to write tests for all classes.
    private Room room;
    private Person alice;
    private Person bob;

    @Before
    public void setUp() {
        room = new Room("R1");
        alice = new Person("alice");
        bob   = new Person("bob");
    }

    /** Туслах: нэг хүний оролцоотой цагтай уулзалт үүсгэнэ. */
    private Meeting mk(int month, int day, int start, int end, Room r, Person p, String desc) {
        ArrayList<Person> at = new ArrayList<>();
        at.add(p);
        return new Meeting(month, day, start, end, at, r, desc);
    }

    // ----------------------------------------------------------------------
    // 1) Конструктор шалгалтууд
    // ----------------------------------------------------------------------

    /**
     * [Constructor: all-day (month, day)]
     * Бүтэн өдрийн уулзалт 0–23 цагийн хүрээтэй байх ёстой, тайлбар null байж болно,
     * өрөө/attendees тохируулаагүй тул зөвхөн талбаруудыг шалгана.
     */
    @Test
    public void ctor_allDay_withoutDescription_sets0to23() {
        Meeting h = new Meeting(6, 26);
        assertEquals(6, h.getMonth());
        assertEquals(26, h.getDay());
        assertEquals(0, h.getStartTime());
        assertEquals(23, h.getEndTime());
        assertNull("Description can be null in this ctor", h.getDescription());
    }

    /**
     * [Constructor: all-day (month, day, description)]
     * Бүтэн өдөр + тайлбар өгөх үед start=0, end=23, description тохирно.
     */
    @Test
    public void ctor_allDay_withDescription_setsFields() {
        Meeting h = new Meeting(12, 1, "Holiday");
        assertEquals(12, h.getMonth());
        assertEquals(1,  h.getDay());
        assertEquals(0,  h.getStartTime());
        assertEquals(23, h.getEndTime());
        assertEquals("Holiday", h.getDescription());
    }

    /**
     * [Constructor: timed + attendees + room + description]
     * Талбарууд яг дамжуулсан утгаар хадгалагдаж буйг шалгана.
     */
    @Test
    public void ctor_timed_setsAllFields() {
        ArrayList<Person> at = new ArrayList<>();
        at.add(alice);
        Meeting m = new Meeting(7, 1, 10, 11, at, room, "Standup");

        assertEquals(7,  m.getMonth());
        assertEquals(1,  m.getDay());
        assertEquals(10, m.getStartTime());
        assertEquals(11, m.getEndTime());
        assertEquals("Standup", m.getDescription());
        assertSame(room, m.getRoom());
        assertEquals(1, m.getAttendees().size());
        assertEquals("alice", m.getAttendees().get(0).getName());
    }

    // ----------------------------------------------------------------------
    // 2) Attendees удирдлага
    // ----------------------------------------------------------------------

    /**
     * [addAttendee/removeAttendee]
     * Нэмж, дараа нь нэгийг нь хасахад жагсаалтын хэмжээ зөв өөрчлөгдөх ёстой.
     */
    @Test
    public void add_and_remove_attendee_updatesList() {
        ArrayList<Person> at = new ArrayList<>();
        at.add(alice);
        Meeting m = new Meeting(8, 3, 9, 10, at, room, "Sync");

        // add second attendee
        m.addAttendee(bob);
        assertEquals(2, m.getAttendees().size());

        // remove one
        m.removeAttendee(alice);
        assertEquals(1, m.getAttendees().size());
        assertEquals("bob", m.getAttendees().get(0).getName());
    }

     /**
     * [getAttendees exposure]
     * getAttendees() нь дотоод жагсаалтыг шууд буцаадаг (modifiable).
     * Энэ бол одоогийн загварын нэг ШИНЖ — гаднаас өөрчлөхөд хэмжээ нэмэгдэж буйг батална.
     * (Хэрвээ энэхүү үйлдлийг өөрчлөх бол буцаахдаа хамгаалалттай хуулбар өгөхийг бодолцоно.)
     */
    @Test
    public void getAttendees_returnsBackedList_currentBehavior() {
        Meeting m = mk(9, 9, 14, 15, room, alice, "Review");
        m.getAttendees().add(bob); // гаднаас шууд өөрчилж байна
        assertEquals(2, m.getAttendees().size());
    }
    // ----------------------------------------------------------------------
    // 3) Setter/Getter-үүд
    // ----------------------------------------------------------------------

    /**
     * [Setters]
     * setMonth/setDay/setStartTime/setEndTime/setRoom/setDescription нь төлөвийг шинэчилнэ.
     * (Валидаци Meeting ангид байхгүй — Calendar дээр хийдэг.)
     */
    @Test
    public void setters_shouldUpdateState() {
        Meeting m = mk(3, 10, 9, 10, room, alice, "A");
        Room r2 = new Room("R2");

        m.setMonth(4);
        m.setDay(11);
        m.setStartTime(12);
        m.setEndTime(13);
        m.setRoom(r2);
        m.setDescription("Updated");

        assertEquals(4, m.getMonth());
        assertEquals(11, m.getDay());
        assertEquals(12, m.getStartTime());
        assertEquals(13, m.getEndTime());
        assertSame(r2, m.getRoom());
        assertEquals("Updated", m.getDescription());
    }
    // ----------------------------------------------------------------------
    // 4) toString() формат
    // ----------------------------------------------------------------------

    /**
     * [toString формат шалгалт]
     * Формат: "m/d, start - end,ROOM: desc\nAttending: name1,name2"
     *  - сарын/өдрийн хэсэг орсон
     *  - цагийн мэдээлэл орсон
     *  - өрөөний ID ба тайлбар орсон
     *  - Attending: жагсаалт төгсгөлд **таслалгүй** байна
     */
    @Test
    public void toString_containsCoreFields_and_noTrailingComma() {
        ArrayList<Person> at = new ArrayList<>();
        at.add(alice);
        at.add(bob);
        Meeting m = new Meeting(10, 2, 15, 16, at, room, "Design");

        String s = m.toString();
        // month/day, time window
        assertTrue(s.contains("10/2"));
        assertTrue(s.contains("15 - 16"));
        // room and description
        assertTrue(s.contains("R1"));
        assertTrue(s.contains("Design"));
        // attendees listing
        assertTrue(s.contains("Attending: alice,bob"));
        assertFalse("Should not end with comma", s.trim().endsWith(","));
    }
        /**
     * [FAIL] room == null үед toString() дотор room.getID() → NullPointerException.
     */
    @Test
    public void toString_shouldHandleNullRoom_withoutNPE() {
        ArrayList<Person> at = new ArrayList<>();
        at.add(alice);
        Meeting m = new Meeting(8, 3, 9, 10, at, null, "Sync");
        String s = m.toString();             // Одоогоор NullPointerException (FAIL)
        assertTrue(s.contains("Sync"));
    }
        /**
     * [FAIL] all-day ctor (month, day, description) бас attendees == null.
     * removeAttendee() → NPE болох ёсгүй.
     */
    @Test
    public void removeAttendee_onAllDayCtor_shouldNotNPE() {
        Meeting h = new Meeting(12, 1, "Holiday");
        // хамгаалалттай байх ёстой — одоогоор NPE
        h.removeAttendee(alice);              // Одоогоор NPE (FAIL)
        assertNotNull(h.getAttendees());
    }
}

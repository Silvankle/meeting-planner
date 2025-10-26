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
}

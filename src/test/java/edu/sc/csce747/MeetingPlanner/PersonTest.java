package edu.sc.csce747.MeetingPlanner;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * PersonTest (JUnit4)
 *
 * Зорилго:
 *  - Person.addMeeting / isBusy / printAgenda / get/remove wrapper-ууд зөв ажиллах эсэх
 *  - Буруу огноо/цагт Calendar-аас ирэх Exception-уудыг Person түвшинд баталгаажуулах
 *  - Одоо байгаа Calendar имплементацийн сулралыг Person API-гаар илрүүлэх FAIL тестүүд
 *
 * Тайлбар:
 *  - Person дотор Calendar дуудагддаг тул олон assert нь Calendar-ийн занг дамжин баталгаажина.
 */
public class PersonTest {
	// Add test methods here. 
    // You are not required to write tests for all classes.
    private Person alice;
    private Room r1;

    @Before
    public void setUp() {
        alice = new Person("alice");
        r1 = new Room("R1");
    }

    /** Туслах: нэг хүний оролцоотой цагтай уулзалт үүсгэх. */
    private Meeting mk(int month, int day, int start, int end, String desc) {
        ArrayList<Person> at = new ArrayList<>();
        at.add(alice);
        return new Meeting(month, day, start, end, at, r1, desc);
    }

    // =========================================================
    // 1) ЭЕРЭГ
    // =========================================================

    /** addMeeting + isBusy — хэвийн урсгал. */
    @Test
    public void add_and_isBusy_happyPath() throws Exception {
        alice.addMeeting(mk(6, 20, 10, 11, "1:1"));
        assertTrue("10–11 хооронд завгүй байх ёстой", alice.isBusy(6, 20, 10, 11));
        assertFalse("11–12 хооронд завтай байх ёстой", alice.isBusy(6, 20, 11, 12));
    }

    /** printAgenda(month, day) — агуулах текстийг шалгах. */
    @Test
    public void printAgenda_shouldContainMeetingDescription() throws Exception {
        alice.addMeeting(mk(9, 9, 14, 15, "Review"));
        String text = alice.printAgenda(9, 9);
        assertTrue(text.contains("Review"));
    }
    // =========================================================
    // 2) (EXCEPTION) — ЗӨВ УНАЛТ
    // =========================================================

    /** start>end үед TimeConflictException шидэх ёстой (try/catch + fail() хэлбэр). */
    @Test
    public void addMeeting_startGreaterThanEnd_shouldThrow() {
        try {
            alice.addMeeting(mk(4, 10, 15, 10, "Bad interval"));
            fail("Expected TimeConflictException when start > end");
        } catch (TimeConflictException e) {
            assertTrue(e.getMessage() == null
                    || e.getMessage().toLowerCase().contains("before it ends"));
        }
    }
}

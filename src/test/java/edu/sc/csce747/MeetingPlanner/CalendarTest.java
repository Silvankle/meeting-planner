package edu.sc.csce747.MeetingPlanner;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * --------------------------------------------------------------
 * CalendarTest (JUnit4) — Calendar ангид төвлөрсөн unit тестүүд
 * --------------------------------------------------------------
 * Юуг шалгах вэ:
 *  - Хэвийн уулзалт/амралт нэмэх ба isBusy шалгалт (happy path)
 *  - Хүчингүй огноо/цаг (exception гарах ёстой) — validation
 *  - Давхцлын логик (ижил интервал)
 *  - Индексийн хил (get/remove)
 *  - Агенда хэвлэл (smoke)
 *
 * Тайлбар:
 *  - Meeting цагтай конструктор нь:
 *      Meeting(int month, int day, int start, int end,
 *              ArrayList<Person> attendees, Room where, String description)
 *    тул тестүүдэд attendees/room объектоор өгч байна.
 *  - Бүтэн өдрийн тэмдэглэгээ (амралт/“holiday”):
 *      Meeting(int month, int day, String description)
 * --------------------------------------------------------------
 */
public class CalendarTest {

    private Calendar cal;

    /** Тест бүрийн өмнө шинэ Calendar үүсгэж цэвэр орчин бэлдэнэ. */
    @Before
    public void setUp() {
        cal = new Calendar();
    }

    // ---------------------------
    // Туслах аргууд
    // ---------------------------

    /** Нэг хүний оролцоотой энгийн “цагтай” уулзалт үүсгэнэ. */
    private Meeting mk(int month, int day, int start, int end, String roomId, String who, String desc) {
        ArrayList<Person> attendees = new ArrayList<>();
        attendees.add(new Person(who));
        Room room = new Room(roomId);
        return new Meeting(month, day, start, end, attendees, room, desc);
    }

    // ===========================================================
    // 1) ЭЕРЭГ КЕЙСҮҮД
    // ===========================================================

    /**
     * [Эерэг кейс] Амралтын өдрийг бүхэл өдрөөр (0–23) завгүй болгохыг шалгана.
     * Хүлээгдэж буй үр дүн: addMeeting үед Exception шидэхгүй,
     * isBusy(6,26,0,23) → true.
     */
    @Test
    public void testAddMeeting_holiday() {
        try {
            // Arrange: 6/26-ны "Midsommar" амралтын өдөр
            Meeting midsommar = new Meeting(6, 26, "Midsommar");

            // Act: календарт нэмэх
            cal.addMeeting(midsommar);

            // Assert: тухайн өдөр бүхэлдээ завгүй тэмдэглэгдэх ёстой
            assertTrue("Midsommar should be marked as busy on the calendar",
                    cal.isBusy(6, 26, 0, 23));

        } catch (TimeConflictException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    /**
     * [Эерэг кейс] Хэвийн уулзалт нэмэхэд алдаа гарахгүй,
     * тухайн өдөр/цаг "завгүй" (busy) гэж тэмдэглэгдэж буйг батална.
     */
    @Test
    public void addMeeting_normal() throws Exception {
        Meeting m = mk(6, 26, 10, 11, "R101", "alice", "Standup");
        cal.addMeeting(m); // Act
        assertTrue("Day should be busy 10-11", cal.isBusy(6, 26, 10, 11)); // Assert
    }

    /**
     * [Эерэг кейс] clearSchedule нь тухайн өдрийн бүх уулзалтыг арилгана.
     */
    @Test
    public void clearSchedule_shouldRemoveAllMeetingsOfTheDay() throws Exception {
        cal.addMeeting(mk(7, 1, 9, 10, "R1", "bob", "Daily"));
        assertTrue(cal.isBusy(7, 1, 9, 10));
        cal.clearSchedule(7, 1);
        assertFalse("Цэвэрлэсний дараа тухайн цаг завтай байх ёстой", cal.isBusy(7, 1, 9, 10));
    }

    // ===========================================================
    // 2) ВАЛИДАЦИ — ОГНОО/ЦАГ (exception гарах ёстой)
    // ===========================================================

    /** [Сөрөг кейс] 13-р сар хүчинтэй биш → TimeConflictException. */
    @Test(expected = TimeConflictException.class)
    public void month_13_should_throw() throws Exception {
        cal.addMeeting(mk(13, 1, 9, 10, "R1", "bob", "Bad"));
    }

    /** [Сөрөг кейс] 2-р сарын 30 → TimeConflictException. */
    @Test(expected = TimeConflictException.class)
    public void feb_30_should_throw() throws Exception {
        cal.addMeeting(mk(2, 30, 9, 10, "R1", "bob", "Nope"));
    }

    /** [Сөрөг кейс] 11-р сарын 31 → TimeConflictException. */
    @Test(expected = TimeConflictException.class)
    public void nov_31_should_throw() throws Exception {
        cal.addMeeting(mk(11, 31, 9, 10, "R1", "bob", "Nope"));
    }

    /**
     * [Хил зааг — Одоогийн имплементацид тааруулсан] start==end нь хориглогдоно.
     * Calendar.checkTimes() нь mStart >= mEnd үед "Meeting starts before it ends." гэж шиддэг.
     * Тиймээс одоогоор Exception гарахыг хүлээе.
     */
    @Test(expected = TimeConflictException.class)
    public void start_equals_end_isNotAllowed_inCurrentImpl() throws Exception {
        cal.addMeeting(mk(5, 20, 10, 10, "R1", "bob", "Zero-length"));
    }

    // ===========================================================
    // 3) ДАВХЦАЛ — ЛОГИК
    // ===========================================================

    /**
     * [Давхцал] Нэг өрөөг ижил цагт хоёр удаа захиалах үед TimeConflictException.
     */
    @Test(expected = TimeConflictException.class)
    public void double_booking_same_room_not_allowed() throws Exception {
        Meeting a = mk(3, 10, 9, 10, "R1", "alice", "A");
        Meeting b = mk(3, 10, 9, 10, "R1", "bob",   "B");
        cal.addMeeting(a); // OK
        cal.addMeeting(b); // should throw
    }

    // ===========================================================
    // 4) ИНДЕКС — get/remove хязгаар
    // ===========================================================

    /** [Хязгаар] Хоосон өдөр getMeeting(…, index) → IndexOutOfBoundsException. */
    @Test(expected = IndexOutOfBoundsException.class)
    public void getMeeting_index_out_of_bounds() {
        cal.getMeeting(1, 1, 0);
    }

    /** [Хязгаар] Хоосон өдөр removeMeeting(…, index) → IndexOutOfBoundsException. */
    @Test(expected = IndexOutOfBoundsException.class)
    public void removeMeeting_index_out_of_bounds() {
        cal.removeMeeting(1, 1, 0);
    }

    // ===========================================================
    // 5) Агенда хэвлэл — формат/агуулга (smoke)
    // ===========================================================

    /**
     * [Smoke] printAgenda(month, day) нь нэмсэн уулзалтаа багтаах ёстой (форматыг зөөлөн шалгана).
     */
    @Test
    public void printAgenda_day_shouldContainMeetingText() throws Exception {
        cal.addMeeting(mk(11, 2, 14, 15, "R7", "zoe", "Review"));
        String text = cal.printAgenda(11, 2);
        assertTrue("Агенда нь тайлбарыг агуулах ёстой", text.contains("Review"));
        assertTrue("Агенда нь 11/2 гэсэн толгойтой байх ёстой", text.startsWith("Agenda for 11/2"));
    }

    /**
     * [Smoke] printAgenda(month) нь тухайн сарын бүх өдрийг түүвэрлэж гаргах — 
     * smoke байдлаар зүгээр л нэмсэн текст байгааг шалгана.
     */
    @Test
    public void printAgenda_month_shouldIncludeEntries() throws Exception {
        cal.addMeeting(mk(3, 3, 9, 10, "R9", "kim", "Sync"));
        String text = cal.printAgenda(3);
        assertTrue(text.contains("Sync"));
        assertTrue(text.startsWith("Agenda for 3"));
    }

	/**
     * [a) Exception-ыг try/catch + fail() маягаар шалгах жишээ]
     * start>end үед "Meeting starts before it ends." гэж алдаа шидэх ёстой.
     */
    @Test
    public void addMeeting_startGreaterThanEnd_shouldThrow_withFailGuard() {
        try {
            cal.addMeeting(mk(4, 10, 15, 10, "R1", "sam", "Bad interval"));
            fail("Expected TimeConflictException when start > end");
        } catch (TimeConflictException e) {
            // OK — зөв алдаа шидлээ
            assertTrue("Message should mention starts before it ends",
                    e.getMessage() == null || e.getMessage().toLowerCase().contains("before it ends"));
        }
    }

    /**
     * [a) Exception-ыг @Test(expected=...) маягаар шалгах жишээ]
     * Хүчингүй цаг: start == -1
     */
    @Test(expected = TimeConflictException.class)
    public void addMeeting_illegalStartHour_shouldThrow() throws Exception {
        cal.addMeeting(mk(4, 10, -1, 10, "R1", "sam", "Bad hour"));
    }

    /**
     * [a) Exception-ыг @Test(expected=...) маягаар шалгах жишээ]
     * Хүчингүй цаг: end == 24
     */
    @Test(expected = TimeConflictException.class)
    public void addMeeting_illegalEndHour_shouldThrow() throws Exception {
        cal.addMeeting(mk(4, 10, 9, 24, "R1", "sam", "Bad hour"));
    }

    /**
     * [b) Програм “FAIL” болохгүй байх ёстой кейс — эерэг урсгал]
     * Ямар ч уулзалтгүй өдөр isBusy нь false байх ёстой.
     */
    @Test
    public void isBusy_onEmptyDay_shouldReturnFalse() throws Exception {
        assertFalse(cal.isBusy(8, 8, 10, 11));
    }

    /**
     * [b) FAIL зөв унах эсэх — зөрчилтэй нэмэлт]
     * A(9–11) нэмсний дараа B(10–12) нэмэх оролдлого → давхцал тул Exception.
     */
    @Test
    public void addMeeting_overlappingIntervals_shouldFailProperly() {
        try {
            cal.addMeeting(mk(9, 9, 9, 11, "R1", "ann", "A"));
            cal.addMeeting(mk(9, 9, 10, 12, "R1", "bob", "B"));
            fail("Expected TimeConflictException on overlapping meeting");
        } catch (TimeConflictException e) {
            // OK — зөв FAIL
        }
    }

    /**
     * [Эерэг кейс] Огт давхцахгүй хоёр уулзалт OK.
     */
    @Test
    public void addMeeting_nonOverlapping_shouldSucceed() throws Exception {
        cal.addMeeting(mk(5, 5, 9, 10, "R1", "a", "M1"));
        cal.addMeeting(mk(5, 5, 12, 13, "R1", "b", "M2"));
        assertTrue(cal.isBusy(5, 5, 9, 10));
        assertTrue(cal.isBusy(5, 5, 12, 13));
        assertFalse("10–12 хооронд завгүй биш байх ёстой", cal.isBusy(5, 5, 10, 12));
    }

    /**
     * [b) FAIL зөв унах эсэх — ирмэг дээрх давхцал (inclusive)]
     * A(9–12) байгаа үед B(12–13) нэмэх → mStart=12 нь A.end=12-тэй тэнцүү тул давхцсан гэж үзнэ.
     */
    @Test(expected = TimeConflictException.class)
    public void addMeeting_edgeTouch_atEnd_shouldFail() throws Exception {
        cal.addMeeting(mk(6, 6, 9, 12, "R1", "a", "A"));
        cal.addMeeting(mk(6, 6, 12, 13, "R1", "b", "B")); // inclusive логикоор давхцана
    }

    /**
     * [Эерэг кейс] getMeeting — нэмсэн уулзалтаа буцааж авч чадна.
     */
    @Test
    public void getMeeting_afterAdd_shouldReturnSameMeeting() throws Exception {
        Meeting m = mk(10, 1, 14, 15, "R2", "tom", "Review");
        cal.addMeeting(m);
        Meeting got = cal.getMeeting(10, 1, 0);
        assertEquals("Review", got.getDescription());
        assertEquals(14, got.getStartTime());
        assertEquals(15, got.getEndTime());
    }

	/**
     * [BUG] 12-р сар хүчинтэй байх ёстой (mMonth > 12 гэж шалгах ёстой).
     * Одоогийн код: mMonth >= 12 → 12-г буруу хориглоно.
     */
    //@Ignore("Fix Calendar.checkTimes(): allow month==12 (use mMonth>12). Then unignore.")
    @Test
    public void month12_shouldBeValid() throws Exception {
        cal.addMeeting(mk(12, 1, 9, 10, "R1", "x", "Dec OK"));
        assertTrue(cal.isBusy(12, 1, 9, 10));
    }

    /**
     * [BUG] isBusy — “агуулах” (containment) давхцлыг барихгүй байна.
     * A: 9–12, Query: 8–13 → завгүй гэж тооцох ёстой боловч одоо false болж магадгүй.
     * Засах: интервал давхцлын шалгалтыг max(start1,start2) <= min(end1,end2) болгож өөрчлөх.
     */
    //@Ignore("Fix isBusy(): handle full containment overlap. Then unignore.")
    @Test
    public void isBusy_containingInterval_shouldBeTrue() throws Exception {
        cal.addMeeting(mk(9, 10, 9, 12, "R1", "x", "Big"));
        assertTrue("8–13 нь 9–12-г бүхэлд нь агуулах тул завгүй гэж тооцох ёстой",
                cal.isBusy(9, 10, 8, 13));
    }

    /**
     * [BUG/Spec] 0 минутын уулзалт (start==end) — зөвшөөрөх бодлого байж болно.
     * Одоогоор хориглодог. Хэрэв зөвшөөрөхөөр шийдвэл checkTimes() дахь >=-г > болго.
     */
    //@Ignore("If zero-length meetings should be allowed, change mStart>=mEnd to mStart>mEnd, then unignore.")
    @Test
    public void zeroDuration_shouldBeAllowed_byPolicy() throws Exception {
        cal.addMeeting(mk(5, 20, 10, 10, "R1", "x", "Zero"));
        assertTrue(cal.isBusy(5, 20, 10, 10));
    }

    /**
     * [BUG/Validation] 2/30 — хүчинтэй биш өдөр. Одоогийн код зөвхөн 1..31 гэж шалгадаг тул
     * зарим хүчингүй өдрийг нэвтрүүлж магадгүй. Логикыг сар бүрийн хоногийн тоотой нийцүүлж шалга.
     */
    //@Ignore("Add real month-day validation (e.g., daysInMonth) in checkTimes(). Then unignore.")
    @Test(expected = TimeConflictException.class)
    public void isBusy_invalidDate_shouldThrow() throws Exception {
        cal.isBusy(2, 30, 0, 23);
    }
	    /** clearSchedule нь “Day does not exist” sentinel-ийг арилгачихвал буруу өдөр нэвтэрч магадгүй. */
    //@Ignore("clearSchedule() sentinel-ийг устгаснаас 11/31 шалгалт нэвтэрч болзошгүй — кодыг сайжруулсны дараа UNIGNORE.")
    @Test(expected = TimeConflictException.class)
    public void clearSchedule_should_not_enable_invalid_day() throws Exception {
        // 11/31-д constructor sentinel хийдэг. Харин clearSchedule устгавал хүчингүй өдрийг нэвтрүүлж мэднэ.
        cal.clearSchedule(11, 31);
        // Одоо 11/31 дээр шалгалт ажиллаад Exception гарах ёстой (одоо бол гарахгүй байж болно).
        cal.addMeeting(mk(11, 31, 10, 11, "R1", "a", "Invalid but added"));
    }

	    /** Бүтэн өдөр (0–23) амралт нэмэхэд доторх хэсэг (10–11) ч BUSY байх ёстой. */
    @Test
    public void wholeDayHoliday_blocksPartialWindow() throws Exception {
        cal.addMeeting(new Meeting(6, 26, "Holiday"));
        assertTrue(cal.isBusy(6, 26, 10, 11));
    }

    /** Өөр өдөр isBusy == false (хоёр өдөр андуурагдахгүй). */
    @Test
    public void busy_is_scoped_to_that_day_only() throws Exception {
        cal.addMeeting(mk(6, 26, 10, 11, "R1", "a", "X"));
        assertFalse(cal.isBusy(6, 27, 10, 11));
    }

    /** add → remove хийсний дараа тэр цонх дахин завтай байх ёстой. */
    @Test
    public void removeMeeting_then_window_is_free() throws Exception {
        Meeting m = mk(8, 8, 9, 10, "R1", "a", "X");
        cal.addMeeting(m);
        assertTrue(cal.isBusy(8, 8, 9, 10));
        cal.removeMeeting(8, 8, 0);
        assertFalse(cal.isBusy(8, 8, 9, 10));
    }

    /** Ирмэгийн цагууд: 0–23 хооронд хэвийн ажиллана. */
    @Test
    public void boundary_hours_0_to_23_valid() throws Exception {
        cal.addMeeting(mk(9, 1, 0, 23, "R1", "a", "All day"));
        assertTrue(cal.isBusy(9, 1, 0, 23));
    }

    /** Хүчингүй сар: 0 → Exception. */
    @Test(expected = TimeConflictException.class)
    public void month0_shouldThrow() throws Exception {
        cal.addMeeting(mk(0, 10, 9, 10, "R1", "a", "Bad month"));
    }

    /** Хүчингүй өдөр: 32 → Exception. */
    @Test(expected = TimeConflictException.class)
    public void day32_shouldThrow() throws Exception {
        cal.addMeeting(mk(6, 32, 9, 10, "R1", "a", "Bad day"));
    }

}

package edu.sc.csce747.MeetingPlanner;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * OrganizationTest (JUnit4)
 *
 * Зорилго:
 *  a) Зөв өгөгдлөөр амжилттай ажиллах кейсүүд
 *  b) Буруу/олддоггүй өгөгдлийн үед зөв Exception шидэж буйг батлах
 *     - @Test(expected=...) ба try/catch + fail() хоёр аргаар шалгана
 *
 *  - Organization() нь default employees/rooms-ийг инициализ хийдэг (constructor)
 *  - getRoom/getEmployee нь олдохгүй бол Exception("...does not exist") шиддэг.
 */
public class OrganizationTest {
	// Add test methods here. 
    // You are not required to write tests for all classes.
    private Organization org;

    @Before
    public void setUp() {
        org = new Organization();
    }

    // -------------------------
    // a) HAPPY PATH кейсүүд
    // -------------------------

    /** Инициализ болсон employees/rooms жагсаалтууд null биш, хоосон биш байх ёстой. */
    @Test
    public void ctor_shouldInitializeEmployeesAndRooms() {
        List<Person> employees = org.getEmployees();
        List<Room>   rooms     = org.getRooms();

        assertNotNull("employees null байж болохгүй", employees);
        assertNotNull("rooms null байж болохгүй", rooms);
        assertFalse("employees хоосон биш байх ёстой", employees.isEmpty());
        assertFalse("rooms хоосон биш байх ёстой", rooms.isEmpty());
    }
    /**
     *Байгаа өрөөг нэрээр нь авч чадна (default: 2A01..2A05).
     *  - getRoom("2A03") буцаасан объектын ID яг таарах ёстой.
     */
    @Test
    public void getRoom_existingId_shouldReturnRoom() throws Exception {
        Room r = org.getRoom("2A03");
        assertEquals("2A03", r.getID());
    }
    /**
     * Байгаа хүнийг нэрээр нь авч чадна (default нэрсийн нэгийг сонгоно).
     *  - getEmployee("Greg Gay") нэр таарч буй Person буцах ёстой.
     */
    @Test
    public void getEmployee_existingName_shouldReturnPerson() throws Exception {
        Person p = org.getEmployee("Greg Gay");
        assertEquals("Greg Gay", p.getName());
    }
}

# Planning System – Unit Testing Report

Энэ тайлан нь **MeetingPlanner** системийн нэгжийн (JUnit4) тестүүдийн дүн, илэрсэн алдааны нөхцөл, үндсэн шалтгаан (root cause) болон засварын зөвлөмжүүдийг нэгтгэн харуулна.

## 1) Тестийн хамрах хүрээ

Шалгасан гол боломжууд:

- Уулзалт / амралт бүртгэх, завгүй эсэхийг тогтоох  
- Огноо/цагийн хил зааг, хүчингүй оролт (validation)  
- Давхцлын логик (interval overlap)  
- Хүн/өрөөний агендa хэвлэх  
- Индексийн хил зааг (`getMeeting`, `removeMeeting`)

Тестийн багцууд (suites):

- `CalendarTest`, `MeetingTest`, `OrganizationTest`, `PersonTest`, `RoomTest`

## 2) Нийт дүн (Consolidated)

| Suite | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| CalendarTest | **31** | **6** | **2** | 0 |
| MeetingTest | **9** | 0 | **2** | 0 |
| OrganizationTest | **7** | 0 | 0 | 0 |
| PersonTest | **9** | **3** | **1** | 0 |
| RoomTest | **11** | **2** | **1** | 0 |
| **Нийт** | **67** | **11** | **6** | **0** |

> **Тайлбар:**  
> - **Failure** = тестийн хүлээлт буруу/зөрсөн (assert унасан).  
> - **Error** = тестийн гүйцэтгэлийн явцад алдаа (ихэвчлэн Exception/nullpointexception).

## 3) Илэрсэн гол асуудлууд (Юунаас болов?)

### A. `Calendar.checkTimes(...)` – сар/өдрийн шалгалт буруу
- Одоогийн шалгалт: `if (mMonth < 1 || mMonth >= 12)` → **12-р сар хүчингүй** болж байна.  
  **Зөв**: `if (mMonth < 1 || mMonth > 12)`  
- Өдөр: зөвхөн `1..31` гэж шалгаж байгаа тул **2/30, 11/31** гэх мэт **онцгой сар, өдөр** шууд баригдахгүй байна. (Exception болж “Day does not exist” уулзалт нэмдэг нь тестийн layer-д илрэхгүй.)

### B. `Calendar.isBusy(...)` – интервал давхцлын логик дутуу
- Одоогийн логик **start** эсвэл **end** нь уулзалтын интервалд **дотроос** таарахыг л шалгадаг.  
- **Бүрэн агуулах** (ж. 8–13 vs 9–12) болон ирмэгийн (edge) тохиолдлуудыг **алдаатай үнэлнэ**.  
- **Стандарт шийдэл:**  
  - Хэрэв интервалуудыг `[start, end)` (баруун талдаа хаалтгүй) гэж үзвэл  
    `overlap = max(start1, start2) < min(end1, end2)`

### C. 0 минутын уулзалт (start == end) – бодлогын шийдвэр дутуу
- Одоогийн код **хориглодог** (`mStart >= mEnd` → Exception).  
- Зарим тестүүд 0 минутыг **зөвшөөрөх** хувилбар (policy) гэж тайлбарласан тул **код–тест зөрчил** үүсч байна.  
- **Шийдвэрлэлт:** 0 минутыг **allow** эсвэл **deny** гэж нэг мөр болгон, тест/кодыг тааруулах.

### D. `Meeting` ангид nullpointexception
- `toString()` нь `room == null` үед `room.getID()` дуудаж **nullpointexception** гаргаж байна.  
- `removeAttendee()` нь all-day constructor ашигласан үед `attendees == null` байж болох эрсдэлтэй → **nullpointexception**.

## 4) Жишээ FAIL/ERROR нөхцөлүүд (тестийн кейсүүд)

- **Хүчингүй сар/өдөр**  
  - 13-р сар (`month_13_should_throw`) → **TimeConflictException** хаях ёстой.  
  - 2/30, 11/31 (`feb_30_should_throw`, `nov_31_should_throw`) → **TimeConflictException** хаях ёстой.  

- **Давхцал**  
  - Нэг өрөөг ижил цагт хоёр удаа: **Exception** (double booking).  
  - Бүрэн агуулах интервал (8–13 vs 9–12): **busy=true** байх ёстой (одоогоор алдаа гарах магадлалтай).  
  - Ирмэгийн тэгш тааралдах нөхцөл (12 дээр таарах) – `[start, end)` бодлогоор **давхцахгүй**, `[start, end]` бодлогоор **давхцана** → бодлогоо сонгох.

- **0 минутын уулзалт**  
  - `start == end` – одоогийн код **Exception** шиднэ.

- **nullpointexception**  
  - `Meeting.toString()` – `room == null` үед **nullpointexception**  
  - `removeAttendee()` – `attendees == null` үед **nullpointexception**

## 5) Засварын зөвлөмж (Fix Plan)

### 5.1. `Calendar.checkTimes(...)`
```java
// Сар
if (mMonth < 1 || mMonth > 12) {
    throw new TimeConflictException("Month does not exist.");
}

// Өдөр (жинхэнэ шалгалт)
int[] daysInMonth = { 0,31,28,31,30,31,30,31,31,30,31,30,31 }; // энгийн жил
int maxDay = daysInMonth[mMonth]; // (хэрэв leap year ярьвал 2-р сард 29-г тооцно)
if (mDay < 1 || mDay > maxDay) {
    throw new TimeConflictException("Day does not exist.");
}
```

### 5.2. `Calendar.isBusy(...)` – интервал
```java
// [start, end) бодлогоор шалгах (баруун талдаа хаалтгүй)
boolean overlap = false;
for (Meeting toCheck : occupied.get(month).get(day)) {
    if (toCheck.getDescription().equals("Day does not exist")) continue;
    int s1 = start, e1 = end;
    int s2 = toCheck.getStartTime(), e2 = toCheck.getEndTime();
    if (Math.max(s1, s2) < Math.min(e1, e2)) {
        overlap = true;
        break;
    }
}
return overlap;
```

> **Жич:** `addMeeting(...)` доторх давхцлын шалгалтаа ч **ижил томьёогоор** нэг мөр болговол тестүүдийн хүлээлттэй таарна.

### 5.3. 0 минутын уулзалт
- **Allow** хийх бол: `if (mStart > mEnd) throw ...` (>= биш)  
- **Deny** хэвээр бол: одоогийн тестүүдэд алдааны тайлбар өгөх.

### 5.4. `Meeting` ангид nullpointexception хамгаалалт
```java
// Бүх constructor-д:
this.attendees = (attendees != null) ? attendees : new ArrayList<>();

// toString():
String roomId = (room != null) ? room.getID() : "<no-room>";
String info = month + "/" + day + ", " + start + " - " + end + "," + roomId + ": " + description + "\nAttending: ";
if (attendees == null || attendees.isEmpty()) return info + "<none>";
```

## 6) Хийсэн тестүүдийн тоо, илрүүлсэн алдаанууд

- **Нийт unit tests:** **67**  
- **Илэрсэн Failures:** **11** (assert-ын зөрчил)  
- **Илэрсэн Errors:** **6** (голдуу nullpointexception/exception)  
- **Нийт илрүүлсэн алдааны бүлгүүд (root cause):**  
  1) Сарын шалгалт (>= → >) буруу  
  2) Сарын хоногийн тоо шалгадаггүйгээс хүчингүй огноо нэвтэрч байгаа  
  3) Интервал давхцлын логик дутуу (агуулах/ирмэг)  
  4) 0 минутын уулзалтын бодлого тодорхой бус  
  5) `Meeting` ангид nullpointexception хамгаалалт дутуу





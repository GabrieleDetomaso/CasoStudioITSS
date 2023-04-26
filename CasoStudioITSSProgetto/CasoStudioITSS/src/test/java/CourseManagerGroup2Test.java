import entities.CourseManager;
import entities.CourseSubscription;
import entities.Student;
import exceptions.CourseEmptyException;
import exceptions.RangeDateException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe di test per CourseManger del gruppo 2
 *
 * @author Giacomo Detomaso, Roberto Scorrano
 * */
public class CourseManagerGroup2Test {
    private static Student s1 = new Student("Gianni", "Verdi", "111111");
    private static Student s2 = new Student("Marco", "Bari", "111112");
    private static Student s3 = new Student("Angelo", "Valentino", "111113");
    private static Student s4 = new Student("Oscar", "Bravo", "111114");
    private static CourseManager courseManager1; // usato per il metodo getSubscriptionByDate
    private static CourseManager courseManager2; // usato per il metodo getStudentWithHigherMark
    @BeforeAll
    static void setup() {
        courseManager1 = new CourseManager(
                "Integrazione e test Gruppo 2 - getSubscriptionByDate",
                LocalDate.now());

        courseManager2 = new CourseManager(
                "Integrazione e test Gruppo 2 - getStudentWithHigherMark",
                LocalDate.now());
    }

    @BeforeEach
    void init()  {
        // Aggiunta studenti per courseManager1
        try {
            courseManager1.addNewCourseAttender(s1, LocalDate.parse("2022-11-04"));
            courseManager1.addNewCourseAttender(s2, LocalDate.parse("2022-11-10"));
            courseManager1.addNewCourseAttender(s3, LocalDate.parse("2022-11-11"));
            courseManager1.addNewCourseAttender(s4, LocalDate.parse("2022-11-11"));
        }
        catch(Exception e) {
            System.out.println("Eccezione lanciata 2");
        }

        try {
            courseManager2.addNewCourseAttender(s1, LocalDate.parse("2022-11-04"));
            courseManager2.addNewCourseAttender(s2, LocalDate.parse("2022-11-10"));
            courseManager2.addNewCourseAttender(s3, LocalDate.parse("2022-11-11"));
            courseManager2.addNewCourseAttender(s4, LocalDate.parse("2022-11-11"));
        }
        catch(Exception e) {
            System.out.println("Eccezione lanciata 2");
        }
    }

    @AfterEach
    void tearDown(){
        courseManager1.deleteCourseStudents();
    }

    @AfterAll
    static void clear() {
        courseManager1 = null;
    }

    // CASI DI TEST PER IL METODO: getSubscriptionsByDate

    @Test // Uniti test: T1 e T2
    @DisplayName("fromDate and toDate null")
    void fromDateToDateNull() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(Exception.class, () ->
                        courseManager1.getSubscriptionsByDate(null, LocalDate.now(), false)),
                () ->  Assertions.assertThrows(Exception.class, () ->
                        courseManager1.getSubscriptionsByDate(LocalDate.now(), null, false))
        );
    }

    @Test // Uniti test: T3 e T4
    @DisplayName("Wrong date format")
    void wrongDateFormat() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(DateTimeException.class, () ->
                        courseManager1.getSubscriptionsByDate(LocalDate.parse("04-08-2023"), LocalDate.now(), false)),

                () -> Assertions.assertThrows(DateTimeException.class, () ->
                        courseManager1.getSubscriptionsByDate(LocalDate.now(), LocalDate.parse("04-08-2023"), false))
        );
    }

    @ParameterizedTest // Uniti test: T5 e  T7
    @DisplayName("Subscriptions found")
    @ValueSource(booleans = {false, true})
    void subscriptionsFound(boolean inclusive) throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-03");
        LocalDate toDate = LocalDate.parse("2022-11-10");

        int equal; // parametro da usare nell'assert equals

        // In base al valore di inclusive cambia il numero di studenti resituiti
        if (inclusive) {
            equal = 2; // Se inclusive è vero vi sono
        } else {
            equal = 1;
        }

        Assertions.assertEquals(equal, courseManager1.getSubscriptionsByDate(fromDate, toDate, inclusive).size());
    }

    @ParameterizedTest // Uniti test: T6 e  T8
    @DisplayName("Subscriptions not found")
    @ValueSource (booleans = {false, true})
    void subscriptionsNotFound(boolean inclusive) throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-12-03");
        LocalDate toDate = LocalDate.parse("2022-12-10");

        Assertions.assertEquals(0, courseManager1.getSubscriptionsByDate(fromDate, toDate, inclusive).size());
    }

    @Test // T9
    @DisplayName("fromDate greater then toDate")
    void fromDateGreaterThanToDate() {
        LocalDate fromDate = LocalDate.parse("2022-11-10");
        LocalDate toDate = LocalDate.parse("2022-11-03");
        Assertions.assertThrows(RangeDateException.class, () -> courseManager1.getSubscriptionsByDate(fromDate, toDate, true));
    }

    @Test // T10
    @DisplayName("fromDate and toDate are the same with inclusive false")
    void fromDateEqualsToDateNotInclusive() {
        LocalDate date = LocalDate.parse("2022-11-10");
        Assertions.assertThrows(RangeDateException.class, () -> courseManager1.getSubscriptionsByDate(date, date, false));
    }

    @Test //T11
    @DisplayName("fromDate toDate are the same with inclusive true")
    void fromDateEqualsToDateInclusive() throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-10");
        LocalDate toDate = LocalDate.parse("2022-11-10");

        Assertions.assertEquals(1, courseManager1.getSubscriptionsByDate(fromDate, toDate, true).size());
    }

    @ParameterizedTest // Uniti test: T12 e  T14
    @DisplayName("Students on fromDate: inclusive true and false")
    @ValueSource (booleans = {false, true})
    void studentsOnFromDate(boolean inclusive) throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-04");
        LocalDate toDate = LocalDate.parse("2022-11-05");

        Set<CourseSubscription> subs = courseManager1.getSubscriptionsByDate(fromDate, toDate, inclusive);
        Set<Student> students = new HashSet<>();

        // Recupera tutti gli studenti presenti nelle iscrizioni
        for (CourseSubscription sub: subs) {
            students.add(sub.getStudent());
        }

        // Controlla che lo studente s1 che risiede sul limite inferiore di questo range
        // se inclusive è true sia presente, altrimenti non presente
        if (inclusive) {
            Assertions.assertTrue(students.contains(s1));
        } else {
            Assertions.assertFalse(students.contains(s1));
        }
    }

    @ParameterizedTest // Uniti test: T13 e  T15
    @DisplayName("Students on toDate: inclusive true and false")
    @ValueSource (booleans = {false, true})
    void studentsOnToDate(boolean inclusive) throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-01");
        LocalDate toDate = LocalDate.parse("2022-11-04");

        Set<CourseSubscription> subs = courseManager1.getSubscriptionsByDate(fromDate, toDate, inclusive);
        Set<Student> students = new HashSet<>();

        // Recupera tutti gli studenti presenti nelle iscrizioni
        for (CourseSubscription sub: subs) {
            students.add(sub.getStudent());
        }

        // Controlla che lo studente s1 che risiede sul limite inferiore di questo range
        // se inclusive è true sia presente, altrimenti non presente
        if (inclusive) {
            Assertions.assertTrue(students.contains(s1));
        } else {
            Assertions.assertFalse(students.contains(s1));
        }
    }

    @Test // T16
    @DisplayName("No students in course")
    void noStudentsInCourse (){
        LocalDate fromDate = LocalDate.parse("2022-11-01");
        LocalDate toDate = LocalDate.parse("2022-11-10");

        courseManager1.deleteCourseStudents(); // azzera il corso

        Assertions.assertThrows(CourseEmptyException.class,
                () -> courseManager1.getSubscriptionsByDate(fromDate, toDate, true));
    }

    @Test // T17
    @DisplayName("Students on toDate inclusive multiple")
    void studentsOnToDateInclusiveMultiple() throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-01");
        LocalDate toDate = LocalDate.parse("2022-11-11");

        Set<CourseSubscription> subs = courseManager1.getSubscriptionsByDate(fromDate, toDate, true);
        Set<Student> students = new HashSet<>();

        // Recupera tutti gli studenti presenti nelle iscrizioni
        for (CourseSubscription sub: subs) {
            students.add(sub.getStudent());
        }

        // Controlla che lo studente s3 ed s4 (che risiedono sul range superiore) siano present
        Assertions.assertAll(
                () -> Assertions.assertTrue(students.contains(s3)),
                () -> Assertions.assertTrue(students.contains(s4)),
                // Controllo comunque la dimensione dell'output (controllo ridondante -> controllato in test
                // precedenti. Lo si lascia per chiarezza e sicurezza). Il controllo è stato aggiunto perchè
                // nel range testato vi sono tutti e 4 gli studenti del corso
                () -> Assertions.assertEquals(4, students.size()));
    }

    @Test // T18
    @DisplayName("Students on fromDate inclusive multiple")
    void studentsOnFromDateInclusiveMultiple() throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-11");
        LocalDate toDate = LocalDate.parse("2022-11-20");

        Set<CourseSubscription> subs = courseManager1.getSubscriptionsByDate(fromDate, toDate, true);
        Set<Student> students = new HashSet<>();

        // Recupera tutti gli studenti presenti nelle iscrizioni
        for (CourseSubscription sub: subs) {
            students.add(sub.getStudent());
        }

        // Controlla che lo studente s3 ed s4 (che risiedono sul range superiore) siano present
        Assertions.assertAll(
                () -> Assertions.assertTrue(students.contains(s3)),
                () -> Assertions.assertTrue(students.contains(s4)));
    }

    // CASI DI TEST PER IL METODO: getStudentWithHigherMark
    @Test // T1
    @DisplayName("One student with an assigned mark")
    void oneStudentWithAssignedMark() {
        courseManager1.assignMarkToStudent(30, "111111");

        Set<Student> studentSet = courseManager1.getStudentsWithHigherMark();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, studentSet.size()),
                () -> Assertions.assertTrue(studentSet.contains(s1))
        );
    }

    @Test // T2
    @DisplayName("")
    void metodo2() {
        courseManager1.assignMarkToStudent(30, "111111");
        courseManager1.assignMarkToStudent(29, "111112");

        Set<Student> studentSet = courseManager1.getStudentsWithHigherMark();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, studentSet.size()),
                () -> Assertions.assertTrue(studentSet.contains(s1))
        );
    }
}

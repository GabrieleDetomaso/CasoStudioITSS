import entities.CourseManager;
import entities.CourseSubscription;
import entities.Student;
import exceptions.CourseEmptyException;
import exceptions.NullStudentException;
import exceptions.RangeDateException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * Classe di test per CourseManager
 *
 * @author Gabriele Detomaso, Giacomo Detomaso, Alessandra Pipoli, Roberto Scorrano
 * */
public class CourseManagerGroup1Test {

    // Definizione di studenti di test
    private static Student s1 = new Student("Gianni", "Verdi", "111111");
    private static Student s2 = new Student("Marco", "Bari", "111112");
    private static Student s3 = new Student("Angelo", "Valentino", "111113");
    private static Student s4 = new Student("Oscar", "Bravo", "111114");
    private static Student s5 = new Student("Leo", "DiCaprio", "111115");

    //GRUPPO 1
    private static CourseManager courseManager1;
    private static CourseManager courseManager3;

    //GRUPPO 2
    private static CourseManager courseManager2;

    // Metodi del ciclo di vita JUnit

    @BeforeAll
    static void setup() {
        courseManager1 = new CourseManager("Integrazione e test 1", LocalDate.now().plusDays(10));
        courseManager3 = new CourseManager("Integrazione e test 3", LocalDate.now().minusDays(1));
        courseManager2 = new CourseManager("Integrazione e test 2", LocalDate.now());
    }

    @BeforeEach
    void init()  {
        // Aggiunta studenti per courseManager1
        try {
            courseManager1.addNewCourseAttender(s1, LocalDate.now());
        }
        catch(Exception e){
            System.out.println("Eccezione lanciata 1");
        }

        // Aggiunta studenti per courseManager2
        try {
            //courseManager2.addNewCourseAttender(s5, LocalDate.parse("2022-11-03"));
            courseManager2.addNewCourseAttender(s1, LocalDate.parse("2022-11-04"));
            courseManager2.addNewCourseAttender(s2, LocalDate.parse("2022-11-10"));
            courseManager2.addNewCourseAttender(s3, LocalDate.parse("2022-11-11"));
            courseManager2.addNewCourseAttender(s4, LocalDate.parse("2022-11-11"));
        }
        catch(Exception e){
            System.out.println("Eccezione lanciata 2");
        }
    }

    @AfterEach
    void tearDown(){
        courseManager1.deleteCourseStudents();
        courseManager2.deleteCourseStudents();
        courseManager3.deleteCourseStudents();

    }

    @AfterAll
    static void clear() {
        courseManager1 = null;
        courseManager2 = null;
        courseManager3 = null;
    }



    // GRUPPO 1
    // metodo testato: addNewCourseAttender;

    // NON VIENE LANCIATA NESSUNA ECCEZIONE
    @Test // T1
    @DisplayName("Studente nullo")
    void studentNull(){
        Assertions.assertThrows(Exception.class, () ->
            courseManager1.addNewCourseAttender(null, LocalDate.now())
        );
    }

    @ParameterizedTest // Uniti tet: T2 e T3
    @MethodSource("studentsProviderOfRegistrationStudent")
    @DisplayName("Studente già iscritto e studente non iscritto")
    void registrationStudent(Boolean expectedResult, Student s) throws NullStudentException {

        Assertions.assertEquals(expectedResult, courseManager1.addNewCourseAttender(s, LocalDate.now()));
    }

    private static Stream<Arguments> studentsProviderOfRegistrationStudent(){

        return Stream.of(
                Arguments.of(false, s1),
                Arguments.of(true, s4)
        );
    }


    //FALLISCE: T6 NON LANCIA ECCEZIONI
    @ParameterizedTest //Uniti test: T4 e T6
    @NullSource
    @MethodSource("datesProviderOfDateWrong")
    @DisplayName("Data nulla e data non scaduta ma non odierna")
    void dateWrong(LocalDate testDate){
        Assertions.assertThrows(Exception.class, () -> courseManager1.addNewCourseAttender(s4, testDate));
    }

    private static Stream<LocalDate> datesProviderOfDateWrong(){
        return Stream.of( LocalDate.now().plusDays(2) );
    }


    @Test //T5
    @DisplayName("Data corretta")
    void dateRight() throws NullStudentException {

        Assertions.assertTrue(courseManager1.addNewCourseAttender(new Student("a", "a", "111112")
                , LocalDate.now()));
    }


    @Test //T7
    @DisplayName("Data scaduta")
    void dateExpired() {

        Assertions.assertThrows(Exception.class, () ->
                courseManager3.addNewCourseAttender(s4, LocalDate.now())
        );
    }


    //FALLISCE: NON VIENE LANCIATA NESSUNA ECCEZIONE
    @Test //T8
    @DisplayName("Data reale scaduta data iscrizione falsata ")
    void dataRealeScadutaDataIscrizioneFalsata(){
        Assertions.assertThrows(Exception.class, () ->
                courseManager3.addNewCourseAttender(s4, LocalDate.now().minusDays(1))
        );
    }

    // GRUPPO 1
    // metodo testato: assignMarkToStudent;

    @ParameterizedTest //Uniti test: T2, T4 e T5
    @MethodSource("marksProviderOfmarkWrong")
    @DisplayName("Inserimento di vari voti non accettabili")
    void markWrong(int mark)
    {

        Assertions.assertThrows(Exception.class, () ->
                courseManager1.assignMarkToStudent(mark, s1.getMat())
        );
    }

    private static Stream<Integer> marksProviderOfmarkWrong(){
        return Stream.of(17, 32, -1);
    }


    //FALLISCE: T3 LANCIA UN'ECCEZIONE
    @ParameterizedTest //Uniti test: T1 e T3
    @MethodSource("marksProviderOfmarkRight")
    @DisplayName("Inserimento di vari voti accettabili ad uno studente iscritto")
    void markRight(int mark)
    {
        Assertions.assertDoesNotThrow(() ->
                courseManager1.assignMarkToStudent(mark, s1.getMat())
        );
    }

    private static Stream<Integer> marksProviderOfmarkRight(){
        return Stream.of(18, 31);
    }

    @ParameterizedTest //Uniti test: T6, T7, T8, T10, T11, T12 e T13
    @NullAndEmptySource
    @MethodSource("matsProviderOfRegisterWrong")
    @DisplayName("Assegnazione voto a studenti con matricole di formato sbagliato o non iscritti/esistenti")
    void RegisterWrong(String mat)
    {

        Assertions.assertThrows(Exception.class, () ->
                courseManager1.assignMarkToStudent(28, mat) );
    }

    private static Stream<String> matsProviderOfRegisterWrong() {
        return Stream.of("11111",
                "A2G27T",
                "1234567",
                s4.getMat(),
                "999999"
        );
    }







































    // GRUPPO 2: Giacomo Detomaso e Roberto Scorrano
    // Test relativi al metodo: getSubrsciptionByDate

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
    @ValueSource (booleans = {false, true})
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

        Assertions.assertEquals(equal, courseManager2.getSubscriptionsByDate(fromDate, toDate, inclusive).size());
    }

    @ParameterizedTest // Uniti test: T6 e  T8
    @DisplayName("Subscriptions not found")
    @ValueSource (booleans = {false, true})
    void subscriptionsNotFound(boolean inclusive) throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-12-03");
        LocalDate toDate = LocalDate.parse("2022-12-10");

        Assertions.assertEquals(0, courseManager2.getSubscriptionsByDate(fromDate, toDate, inclusive).size());
    }

    @Test // T9
    @DisplayName("fromDate greater then toDate")
    void fromDateGreaterThanToDate() {
        LocalDate fromDate = LocalDate.parse("2022-11-10");
        LocalDate toDate = LocalDate.parse("2022-11-03");
        Assertions.assertThrows(RangeDateException.class, () -> courseManager2.getSubscriptionsByDate(fromDate, toDate, true));
    }

    @Test // T10
    @DisplayName("fromDate and toDate are the same with inclusive false")
    void fromDateEqualsToDateNotInclusive() {
        LocalDate date = LocalDate.parse("2022-11-10");
        Assertions.assertThrows(RangeDateException.class, () -> courseManager2.getSubscriptionsByDate(date, date, false));
    }

    @Test //T11
    @DisplayName("fromDate toDate are the same with inclusive true")
    void fromDateEqualsToDateInclusive() throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-10");
        LocalDate toDate = LocalDate.parse("2022-11-10");

        Assertions.assertEquals(1, courseManager2.getSubscriptionsByDate(fromDate, toDate, true).size());
    }

    @ParameterizedTest // Uniti test: T12 e  T14
    @DisplayName("Students on fromDate: inclusive true and false")
    @ValueSource (booleans = {false, true})
    void studentsOnFromDate(boolean inclusive) throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-04");
        LocalDate toDate = LocalDate.parse("2022-11-05");

        Set<CourseSubscription> subs = courseManager2.getSubscriptionsByDate(fromDate, toDate, inclusive);
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

        Set<CourseSubscription> subs = courseManager2.getSubscriptionsByDate(fromDate, toDate, inclusive);
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

        courseManager2.deleteCourseStudents(); // azzera il corso
        
        Assertions.assertThrows(CourseEmptyException.class,
                () -> courseManager2.getSubscriptionsByDate(fromDate, toDate, true));
    }

    @Test // T17
    @DisplayName("Students on toDate inclusive multiple")
    void studentsOnToDateInclusiveMultiple() throws RangeDateException, CourseEmptyException {
        LocalDate fromDate = LocalDate.parse("2022-11-01");
        LocalDate toDate = LocalDate.parse("2022-11-11");

        Set<CourseSubscription> subs = courseManager2.getSubscriptionsByDate(fromDate, toDate, true);
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

        Set<CourseSubscription> subs = courseManager2.getSubscriptionsByDate(fromDate, toDate, true);
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
}

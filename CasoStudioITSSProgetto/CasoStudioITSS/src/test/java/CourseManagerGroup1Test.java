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
    void init() {
        // Aggiunta studenti per courseManager1
        try {
            courseManager1.addNewCourseAttender(s1, LocalDate.now());
        } catch (Exception e) {
            System.out.println("Eccezione lanciata 1");
        }

        // Aggiunta studenti per courseManager2
        try {
            //courseManager2.addNewCourseAttender(s5, LocalDate.parse("2022-11-03"));
            courseManager2.addNewCourseAttender(s1, LocalDate.parse("2022-11-04"));
            courseManager2.addNewCourseAttender(s2, LocalDate.parse("2022-11-10"));
            courseManager2.addNewCourseAttender(s3, LocalDate.parse("2022-11-11"));
            courseManager2.addNewCourseAttender(s4, LocalDate.parse("2022-11-11"));
        } catch (Exception e) {
            System.out.println("Eccezione lanciata 2");
        }
    }

    @AfterEach
    void tearDown() {
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
    // metodo testato: assignMarkToStudent;

    @ParameterizedTest //Uniti test: T2, T4 e T5
    @MethodSource("marksProviderOfmarkWrong")
    @DisplayName("Inserimento di vari voti non accettabili")
    void markWrong(int mark) {

        Assertions.assertThrows(Exception.class, () ->
                courseManager1.assignMarkToStudent(mark, s1.getMat())
        );
    }

    private static Stream<Integer> marksProviderOfmarkWrong() {
        return Stream.of(17, 32, -1);
    }


    @ParameterizedTest //Uniti test: T1 e T3
    @MethodSource("marksProviderOfmarkRight")
    @DisplayName("Inserimento di vari voti accettabili ad uno studente iscritto")
    void markRight(int mark) {
        Assertions.assertDoesNotThrow(() ->
                courseManager1.assignMarkToStudent(mark, s1.getMat())
        );
    }

    private static Stream<Integer> marksProviderOfmarkRight() {
        return Stream.of(18, 31);
    }

    @ParameterizedTest //Uniti test: T6, T7, T8, T9, T10, T11 e T12
    @NullAndEmptySource
    @MethodSource("matsProvider")
    @DisplayName("Assegnazione voto a studenti con matricole di formato sbagliato o non iscritti/esistenti")
    void registerWrong(String mat) {

        Assertions.assertThrows(Exception.class, () ->
                courseManager1.assignMarkToStudent(28, mat));
    }

    private static Stream<String> matsProvider() {
        return Stream.of("11111",
                "A2G27T",
                "1234567",
                s4.getMat(),
                "999999"
        );
    }


    // metodo testato: addNewCourseAttender;

    @Test // T1
    @DisplayName("Studente nullo")
    void studentNull() {
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

    private static Stream<Arguments> studentsProviderOfRegistrationStudent() {

        return Stream.of(
                Arguments.of(false, s1),
                Arguments.of(true, s4)
        );
    }


    @ParameterizedTest //Uniti test: T4 e T6
    @NullSource
    @MethodSource("datesProviderOfDateWrong")
    @DisplayName("Data nulla e data non scaduta ma non odierna")
    void dateWrong(LocalDate testDate) {
        Assertions.assertThrows(Exception.class, () -> courseManager1.addNewCourseAttender(s4, testDate));
    }

    private static Stream<LocalDate> datesProviderOfDateWrong() {
        return Stream.of(LocalDate.now().plusDays(2));
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


    @Test //T8
    @DisplayName("Data reale scaduta data iscrizione falsata ")
    void realDateExpiredRegistrationDateFalsed() {
        Assertions.assertThrows(Exception.class, () ->
                courseManager3.addNewCourseAttender(s4, LocalDate.now().minusDays(1))
        );
    }


    // metodo testato: getSpecificSubscription;

    @Test // T1
    @DisplayName("matricola nulla")
    void RegisterNull() {
        Assertions.assertThrows(Exception.class, () ->
                courseManager1.getSpecificSubscription(null)
        );
    }


    @ParameterizedTest //Uniti test:  T2, T3, T4, T6, T7 e T8
    @EmptySource
    @MethodSource("matsProvider")
    @DisplayName("Inserimento di matricole di formato sbagliato o non iscritti/esistenti")
    void subscriberSearchWrong( String mat) {

        Assertions.assertNull( courseManager1.getSpecificSubscription(mat) );
    }

    @Test // T5
    @DisplayName("Inserimento matricola di uno studente iscritto")
    void registerCorrect(){

        Assertions.assertInstanceOf( CourseSubscription.class, courseManager1.getSpecificSubscription(s1.getMat()) );
    }


}
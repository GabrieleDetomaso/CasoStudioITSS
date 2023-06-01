import entities.CourseManager;
import entities.CourseSubscription;
import entities.Student;
import exceptions.CourseEmptyException;
import exceptions.NullStudentException;
import net.jqwik.api.*;

import net.jqwik.api.arbitraries.ListArbitrary;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.Histogram;
import net.jqwik.api.statistics.Statistics;
import net.jqwik.api.statistics.StatisticsReport;
import net.jqwik.time.api.constraints.DateRange;
import net.jqwik.time.internal.properties.arbitraries.DefaultLocalDateArbitrary;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * Classe di test per CourseManager
 *
 * @author Gabriele Detomaso, Alessandra Pipoli
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
    private static CourseManager courseManager4;

    public String MINDATE = "2023-05-01";
    public String MAXDATE = "2023-05-31";

    // Metodi del ciclo di esecuzione su JUnit

    @BeforeAll
    static void setup() {
        courseManager1 = new CourseManager("Integrazione e test 1", LocalDate.now().plusDays(10));
        courseManager3 = new CourseManager("Integrazione e test 3", LocalDate.now().minusDays(1));
    }

    @BeforeEach
    void init() {
        // Aggiunta studenti per courseManager1
        try {
            courseManager1.addNewCourseAttender(s1, LocalDate.now());
            courseManager1.addNewCourseAttender(s2, LocalDate.now());
            courseManager1.addNewCourseAttender(s3, LocalDate.now());
        } catch (Exception e) {
            System.out.println("Eccezione lanciata 1");
        }
    }

    @AfterEach
    void tearDown() {
        courseManager1.deleteCourseStudents();
        courseManager3.deleteCourseStudents();
    }

    @AfterAll
    static void clear() {
        courseManager1 = null;
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
    void matWrong(String mat) {

        Assertions.assertThrows(Exception.class, () ->
                courseManager1.assignMarkToStudent(28, mat));
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


    @Test //T4
    @DisplayName("Data nulla")
    void dateNull() {

        Assertions.assertThrows(Exception.class, () -> courseManager1.addNewCourseAttender(s4, null));
    }


    @ParameterizedTest //Uniti test: T5 e T6
    @MethodSource("datesProviderOfDateRight")
    @DisplayName("Data antecedente e uguale alla data di chiusura iscrizione")
    void dateRight(LocalDate testDate) throws NullStudentException {

        Assertions.assertTrue(courseManager1.addNewCourseAttender(new Student("a", "a", "111116")
                , testDate));
    }

    private static Stream<LocalDate> datesProviderOfDateRight() {
        return Stream.of(
                LocalDate.now().plusDays(9),
                LocalDate.now().plusDays(10)
        );
    }


    @Test //T7
    @DisplayName("Data scaduta")
    void dateExpired() {

        Assertions.assertThrows(Exception.class, () ->
                courseManager3.addNewCourseAttender(s4, LocalDate.now())
        );
    }


    // metodo testato: getSpecificSubscription;

    @Test // T1
    @DisplayName("matricola nulla")
    void matNull() {
        Assertions.assertThrows(Exception.class, () ->
                courseManager1.getSpecificSubscription(null)
        );
    }


    @ParameterizedTest //Uniti test:  T2, T3, T4, T6, T7 e T8
    @EmptySource
    @MethodSource("matsProvider")
    @DisplayName("Inserimento di matricole di formato sbagliato o non iscritti/esistenti")
    void subscriberSearchWrong(String mat) {

        Assertions.assertNull(courseManager1.getSpecificSubscription(mat));
    }

    @Test // T5
    @DisplayName("Inserimento matricola di uno studente iscritto")
    void matCorrect() {

        Assertions.assertInstanceOf(CourseSubscription.class, courseManager1.getSpecificSubscription(s1.getMat()));
    }

    /*
     *Il metodo viene richiamato in più test
     */
    private static Stream<String> matsProvider() {
        return Stream.of("11111",
                "A2G27T",
                "1234567",
                s4.getMat(),
                "999999"
        );
    }


    // metodo testato: countMarksInInclusiveRange;
    @ParameterizedTest
    @MethodSource("intsProviderOfHomework2Task2_1")
    @DisplayName("Eccezioni del metodo countMarksInInclusiveRange")
    void homework2Task2_1(int from, int to) {
        Assertions.assertThrows(Exception.class, ()
                -> courseManager3.countMarksInInclusiveRange(from, to));
    }

    public static Stream<Arguments> intsProviderOfHomework2Task2_1() {
        return Stream.of(
                Arguments.of(17, 18),
                Arguments.of(32, 18),
                Arguments.of(18, 17),
                Arguments.of(18, 32),
                Arguments.of(25, 18),
                Arguments.of(18, 25)
        );
    }

    @ParameterizedTest
    @MethodSource("intsProviderOfHomework2Task2_2")
    @DisplayName("Confronto valore ritornato dal metodo countMarksInInclusiveRange")
    void homework2Task2_2(int from, int to, int countExpeted){
        try {
            courseManager3.addNewCourseAttender(s1, LocalDate.now().minusDays(2));
        } catch (Exception e) {
            e.getMessage();
        }
        courseManager3.assignMarkToStudent(20, s1.getMat());

        try{
        Assertions.assertEquals(countExpeted, courseManager3.countMarksInInclusiveRange(from, to));
        }catch (CourseEmptyException cee){
            cee.getMessage();
        }

    }

    public static Stream<Arguments> intsProviderOfHomework2Task2_2() {
       return Stream.of(
                Arguments.of(18, 25, 1),
                Arguments.of(18, 19, 0),
                Arguments.of(21, 25, 0)
        );
    }





    // metodo testato: getStudentsWithHigherMark;

    @Provide
    public ListArbitrary<Student> studentsProvider (){
        Arbitrary <String> names = Arbitraries.strings().alpha().ofLength(10);
        Arbitrary <String> surnames = Arbitraries.strings().alpha().ofLength(15);
        Arbitrary <String> mats = Arbitraries.strings().numeric().ofLength(6);

        Arbitrary<Student> students = Combinators.combine(names, surnames, mats).as(Student:: new);

        return students.list().uniqueElements(Student::getMat);
    }


    @Property(tries = 50)
    @Report(Reporting.GENERATED)
    @StatisticsReport(format = Histogram.class)
    @Label("Restituzione insieme pieno")
    void studentsWithMarkAndWithout(@ForAll("studentsProvider") @Size(min = 20, max = 35) List<Student> students,
                          @ForAll @Size(value = 35) List<@DateRange(min = "2023-05-01", max = "2023-05-31") LocalDate> localDates,
                          @ForAll @Size(value = 35) List<@IntRange(min = 18,max = 31) Integer> marks,
                          @ForAll @Size(value = 5) @UniqueElements List<@IntRange(max = 34) Integer> pos
                            ) throws NullStudentException{

        courseManager4 = new CourseManager("Corso1", LocalDate.parse("2023-10-31"));
        Student s = null;
        int mark = 0;
        int higherMark = 0;
        Set<Student> highMarkStudents = new LinkedHashSet<>(); // Insieme che conterrà gli studenti con voto più alto

        // calcolo del voto più alto generato
        for (int i = 0; i < students.size(); i++)
            if (!pos.contains(i))
                if (marks.get(i) > higherMark)
                    higherMark = marks.get(i);

        // Iscrizione degli studenti nel corso
        for (int i = 0; i < students.size(); i++)
        {
            s = students.get(i);
            mark = marks.get(i);

            courseManager4.addNewCourseAttender(s, localDates.get(i)); //Iscrizione studente

            //Assegnazione del voto se l'indice non è contenuto nell'insieme delle posizioni da lasciare senza voto
            if (!pos.contains(i))
            {

                if (mark == higherMark)
                    highMarkStudents.add(s); // Studente con voto più alto inserito nell'insieme

                courseManager4.assignMarkToStudent(marks.get(i), s.getMat()); // voto assegnato
                //Statistics.label("Mark range assigned").collect(mark);
            }
        }

        Statistics.label("Higher mark range assigned").collect(mark); // Statistica del voto più alto assegnato ad ogni prova

        // statistica del range di voti generato
        for (int m: marks)
            Statistics.label("Mark range generated").collect(m);

        Assertions.assertEquals(highMarkStudents, courseManager4.getStudentsWithHigherMark());
    }


    @Property (tries = 50)
    @Report(Reporting.GENERATED)
    @Label("Restituzione insieme vuoto")
    void allStudentsWithoutMark (@ForAll("studentsProvider") @Size(min = 0, max = 35) List<Student> students,
                                 @ForAll @Size(value = 35) List<@DateRange(min = "2023-05-01", max = "2023-05-31") LocalDate> localDates
                                    ) throws NullStudentException{

        courseManager4 = new CourseManager("Corso1", LocalDate.parse("2023-10-31"));

        //Riempmento del corso con i valori da generare
        for (int i = 0; i < students.size(); i++){
            courseManager4.addNewCourseAttender(students.get(i),localDates.get(i));
        }

        Assertions.assertTrue(courseManager4.getStudentsWithHigherMark().isEmpty());
    }


}
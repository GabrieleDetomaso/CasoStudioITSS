import entities.CourseManager;
import entities.CourseSubscription;
import entities.Student;
import exceptions.CourseEmptyException;
import exceptions.NullStudentException;
import exceptions.RangeDateException;
import net.jqwik.api.*;
import static net.jqwik.api.Tuple.Tuple2;

import net.jqwik.api.statistics.Histogram;
import net.jqwik.api.statistics.Statistics;
import net.jqwik.api.statistics.StatisticsReport;
import net.jqwik.time.api.arbitraries.LocalDateArbitrary;
import net.jqwik.time.api.constraints.DateRange;
import net.jqwik.time.internal.properties.arbitraries.DefaultLocalDateArbitrary;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * Classe di test per CourseManger del gruppo 2
 *
 * @author Giacomo Detomaso, Roberto Scorrano
 * */
public class CourseManagerGroup2Test {
    private static final Student s1 = new Student("Gianni", "Verdi", "111111");
    private static final Student s2 = new Student("Marco", "Bari", "111112");
    private static final Student s3 = new Student("Angelo", "Valentino", "111113");
    private static final Student s4 = new Student("Oscar", "Bravo", "111114");
    private static CourseManager courseManager1; // usato per il metodo getSubscriptionByDate
    private static CourseManager courseManager2; // usato per il metodo getStudentWithHigherMark
    private static final String FROM_MIN = "2023-02-01";
    private static final String FROM_MAX = "2023-02-10";
    private static final String TO_MIN = "2023-02-15";
    private static final String TO_MAX = "2023-02-25";
    private static final int LIMIT_GENERATIONS = 1000; // Definisce il numero di iscrizioni da generare per i PB

    // METODI PER JUNIT LIFECYCLE

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
            System.out.println("Eccezione lanciata 1: " + e.getMessage());
        }

        try {
            courseManager2.addNewCourseAttender(s1, LocalDate.parse("2022-11-04"));
        }
        catch(Exception e) {
            System.out.println("Eccezione lanciata 2: " + e.getMessage());
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

    // METODI PER GENERARE INPUT PER TEST PARAMETRICI

    public static Stream<Arguments> getInputMarkPairs() {
        return Stream.of(
                Arguments.of(18, 17), // T1 di countMarksInInclusiveRange
                Arguments.of(18, 31), // T2 di countMarksInInclusiveRange
                Arguments.of(17, 18), // T3 di countMarksInInclusiveRange
                Arguments.of(31, 17) // T4 di countMarksInInclusiveRange
        );
    }

    // METODI PROVIDERS PER I PROPERTY BASED TESTS

    /**
     * Questo metodo usato nel PBT success, genera una lista di iscrizioni
     * arbitrarie uniche per un corso.
     *
     * @return un'arbitrary di una lista di iscrioni, in cui non sono
     * presenti studenti con matricole duplicate
     * */
    @Provide
    Arbitrary<List<Tuple2<Student, LocalDate>>> generateRandomSubscriptions() {
        Arbitrary<String> name = Arbitraries.strings().ofLength(4);
        Arbitrary<String> secondName = Arbitraries.strings().ofLength(5);
        Arbitrary<String> mat = Arbitraries.strings().numeric().ofLength(6);

        // Combinando gli input viene generato lo studente arbitrario
        Arbitrary<Student> studentArbitrary =
                Combinators
                .combine(name, secondName, mat)
                .as(Student::new);

        LocalDate from = LocalDate.parse(FROM_MIN);
        LocalDate to = LocalDate.parse(TO_MAX);

        LocalDateArbitrary localDateArbitrary = new DefaultLocalDateArbitrary();
        localDateArbitrary = localDateArbitrary.between(from, to);

        // Restituisce, dopo la combinazione, una lista di Tuple2.
        return Combinators
                .combine(studentArbitrary, localDateArbitrary)
                .as(Tuple::of)
                .list()
                // La prima entry di ogni tupla nella lista (lo Studente) deve essere univoca.
                // Lo studente possiede un metodo equals che stabilisce che due
                // studenti uguali possiedono la stessa matricola. Pertanto, nell'arbitrary in output,
                // nessuno studente possiederà la stessa matricola.
                .uniqueElements(Tuple.Tuple1::get1)
                .ofSize(LIMIT_GENERATIONS);
    }

    /**
     * Il metodo riempie e restituisce un corso sulla base delle iscrizioni
     * generate arbitrariamente.
     *
     * @param subscriptions le iscrizioni generate
     * @return il corso riempito con le iscrizione generate
     * */
    private CourseManager fillCourseManager(List<Tuple2<Student, LocalDate>> subscriptions) {
        CourseManager courseManager = new CourseManager(
                "Integrazione e test Gruppo 2 - getSubscriptionByDate",
                LocalDate.now());

        for (Tuple2<Student, LocalDate> k : subscriptions) {
            try {
                courseManager.addNewCourseAttender(k.get1(), k.get2());
            } catch (Exception e) {
                System.out.println("Eccezione lanciata: " + e.getMessage());
            }
        }

        return courseManager;
    }

    @Provide
    CourseManager generateFixedCourse() throws NullStudentException {
        CourseManager courseManagerPBT = new CourseManager(
                "Integrazione e test Gruppo 2 - PBT",
                LocalDate.now());

        courseManagerPBT.addNewCourseAttender(s1, LocalDate.parse("2023-01-01"));
        courseManagerPBT.addNewCourseAttender(s2, LocalDate.parse("2023-01-02"));
        courseManagerPBT.addNewCourseAttender(s3, LocalDate.parse("2023-01-03"));
        courseManagerPBT.addNewCourseAttender(s4, LocalDate.parse("2023-01-04"));

        return courseManagerPBT;
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
        Assertions.assertThrows(RangeDateException.class, () -> courseManager1
                .getSubscriptionsByDate(fromDate, toDate, true));
    }

    @Test // T10
    @DisplayName("fromDate equals toDate with inclusive false")
    void fromDateEqualsToDateNotInclusive() {
        LocalDate date = LocalDate.parse("2022-11-10");
        Assertions.assertThrows(RangeDateException.class, () -> courseManager1
                .getSubscriptionsByDate(date, date, false));
    }

    @Test // T11
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
    void oneStudentWithAssignedMark() throws CourseEmptyException {
        courseManager1.assignMarkToStudent(30, "111111");

        Set<Student> studentSet = courseManager1.getStudentsWithHigherMark();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, studentSet.size()),
                () -> Assertions.assertTrue(studentSet.contains(s1))
        );
    }

    @Test // T2
    @DisplayName("More students with an assigned mark, but with one highest one")
    void moreStudentWithAssignedMarkWithOneHighestMark() throws CourseEmptyException {
        courseManager1.assignMarkToStudent(30, "111111");
        courseManager1.assignMarkToStudent(29, "111112");

        Set<Student> studentSet = courseManager1.getStudentsWithHigherMark();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, studentSet.size()),
                () -> Assertions.assertTrue(studentSet.contains(s1))
        );
    }

    @Test // T3
    @DisplayName("More students with an assigned mark, but with more highest ones")
    void moreStudentWithAssignedMarkWithMoreHighestMarks() throws CourseEmptyException {
        courseManager1.assignMarkToStudent(30, "111111");
        courseManager1.assignMarkToStudent(29, "111112");
        courseManager1.assignMarkToStudent(30, "111113");

        Set<Student> studentSet = courseManager1.getStudentsWithHigherMark();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, studentSet.size()),
                () -> Assertions.assertTrue(studentSet.contains(s1)),
                () -> Assertions.assertTrue(studentSet.contains(s3))
        );
    }

    @Test //T4 e T5
    @DisplayName("No Students with marks")
    void noStudentsWithMarks() {
        Set<Student> studentSet1 = courseManager1.getStudentsWithHigherMark();
        Set<Student> studentSet2 = courseManager2.getStudentsWithHigherMark();
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, studentSet1.size()),
                () -> Assertions.assertEquals(0, studentSet2.size())
        );
    }

    @Test //T6: sviluppato in seguito ai white box test
    @DisplayName("No students in course 2")
    void noStudentInCourse2() {
        courseManager1.deleteCourseStudents();
        Assertions.assertEquals(0, courseManager1.getStudentsWithHigherMark().size());
    }

    // CASI DI TEST PER IL METODO: countMarksInInclusiveRange

    @ParameterizedTest //Uniti test: T1, T2, T3 e T4
    @DisplayName("Wrong mark input")
    @MethodSource("getInputMarkPairs")
    void wrongMarkInput(int from, int to) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> courseManager1.countMarksInInclusiveRange(from, to));
    }

    @Test // Uniti T5 e T6
    @DisplayName("Students in range and not")
    void studentsInRangeAndNot () {
        courseManager1.assignMarkToStudent(29, "111111");
        courseManager1.assignMarkToStudent(28, "111112");
        courseManager1.assignMarkToStudent(29, "111113");

        // Testiamo che nel primo caso il range contenga gli studenti, nel secondo caso no
        Assertions.assertAll(
                () -> Assertions.assertTrue(courseManager1.countMarksInInclusiveRange(27, 30) > 0),
                () -> Assertions.assertEquals(0, courseManager1.countMarksInInclusiveRange(25, 27))
        );
    }

    @Test // T7
    @DisplayName("One student on from")
    void oneStudentOnFrom() throws CourseEmptyException {
        courseManager1.assignMarkToStudent(27, "111111");
        Assertions.assertEquals(1, courseManager1.countMarksInInclusiveRange(27, 30));
    }

    @Test // T8
    @DisplayName("One student on to")
    void oneStudentOnTo() throws CourseEmptyException {
        courseManager1.assignMarkToStudent(30, "111111");
        Assertions.assertEquals(1, courseManager1.countMarksInInclusiveRange(27, 30));
    }

    @Test // T9
    @DisplayName("From equal to")
    void fromEqualTo() throws CourseEmptyException {
        courseManager1.assignMarkToStudent(27, "111111");
        Assertions.assertTrue(courseManager1.countMarksInInclusiveRange(27, 27) >= 1);
    }

    @Test // T10
    @DisplayName("From greater than to")
    void fromGreaterThanTo() {
        courseManager1.assignMarkToStudent(27, "111111");
        Assertions.assertThrows(IllegalArgumentException.class, () -> courseManager1.countMarksInInclusiveRange(27, 22));
    }

    @Test // T11
    @DisplayName("No student in course")
    void noStudentInCourse3() {
        courseManager1.deleteCourseStudents();
        Assertions.assertThrows(CourseEmptyException.class, () -> courseManager1.countMarksInInclusiveRange(19, 29));
    }

    @Test // T12
    @DisplayName("No marks in course")
    void noMarksInCourse() throws CourseEmptyException {
        Assertions.assertEquals(0, courseManager1.countMarksInInclusiveRange(20, 25));
    }


    // TASK 2 HOMEWORK 2
    // CASI DI TEST PER IL METODO studentsAboveAverage()

    @Test // T1
    @DisplayName("Course is empty")
    void courseEmpty() {
        courseManager1.deleteCourseStudents();
        Assertions.assertEquals(0, courseManager1.studentsAboveAverage(true));
    }

    @Test // T2 e T3
    @DisplayName("Course is full with mark assigned")
    void courseFullWithMarks() {
        courseManager1.assignMarkToStudent(26, "111112");
        courseManager1.assignMarkToStudent(27, "111113");
        courseManager1.assignMarkToStudent(28, "111114");

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, courseManager1.studentsAboveAverage(true)),
                () -> Assertions.assertEquals(1, courseManager1.studentsAboveAverage(false))
        );
    }

    // HOMEWORK 3: PBT TEST
    @Property // T1
    @Report(Reporting.GENERATED)
    @StatisticsReport(format = Histogram.class)
    @Label("PBT - FAIL")
    void fail(
            @ForAll @DateRange(min = FROM_MIN, max = FROM_MAX) LocalDate from,
            @ForAll @DateRange(min = TO_MIN, max = TO_MAX) LocalDate to) throws NullStudentException,
            RangeDateException, CourseEmptyException {
        CourseManager courseManagerPBT = generateFixedCourse();
        Assertions.assertEquals(0, courseManagerPBT
                .getSubscriptionsByDate(from, to, false).size());

        // Dimostra che ogni possibile combinazione delle date fornite dal range di input è considerata
        Statistics.collect(from, to);
    }

    @Property // T2
    @Report(Reporting.GENERATED)
    @Label("PBT - INVALID")
    void invalid(
            @ForAll @DateRange(min = TO_MIN, max = TO_MAX) LocalDate from,
            @ForAll @DateRange(min = FROM_MAX, max = TO_MIN) LocalDate to) throws NullStudentException {
        CourseManager courseManagerPBT = generateFixedCourse();
        Assertions.assertThrows(
                RangeDateException.class, () ->
                        courseManagerPBT.getSubscriptionsByDate(from, to, false)
        );

        // Dimostra la presenza di date uguali o non uguali
        Statistics.collect(from.equals(to) ? "Uguali" : "from > to");
    }

    @Property(tries = 40)
    @Report(Reporting.GENERATED)
    @StatisticsReport(format = Histogram.class)
    @Label("PBT - SUCCESS [INCLUSIVE TRUE AND FALSE]")
    void success(@ForAll("generateRandomSubscriptions")List<Tuple2<Student, LocalDate>> subscriptions) {
        LocalDate from = LocalDate.parse(FROM_MIN);
        LocalDate to = LocalDate.parse(TO_MAX);

        // Conta quante date sono state generate in range escludendo il limite
        // superiore ed inferiore
        int notInclusiveSubsCounter = 0;

        String rangeGlobal = "";

        // Questo ciclo è usato per collezionare la statistica e per
        // controllare quante date di iscrizione sono state generate nel range
        // non inclusivo dei due limit inferiore e superiore
        for (Tuple2<Student, LocalDate> sub : subscriptions) {
            LocalDate subDate = sub.get2();

            // Colleziona valori per il range usato nella statistica
            if (subDate.equals(from) || subDate.equals(to)) {
                rangeGlobal = "Inclusive sub";
            } else {
                notInclusiveSubsCounter++;
                rangeGlobal = "Not inclusive sub";
            }

            // Genera due statistiche. La prima individua in che percentuali vengono generate iscrizioni
            // in range inclusive (quindi iscrizioni che corrispondono ai due limiti del range) e
            // quante vole sono generate iscrizioni in date appartenenti al range ma diverse dai due limiti.
            Statistics.label("Global range").collect(rangeGlobal);
        }

        // Le lambda expression ammettono valori final come parametri
        final int finalNotInclusiveSubsCounter = notInclusiveSubsCounter;
        CourseManager courseManager = fillCourseManager(subscriptions);

        // Testa i casi in cui inclusive sia false e true
        Assertions.assertAll(
                // Con inclusive true, ogni studente iscritto nel range specificato
                // deve essere presente nell'output.
                () -> Assertions
                        .assertEquals(LIMIT_GENERATIONS,
                                courseManager.getSubscriptionsByDate(from, to, true).size()),

                // Con inclusive false l'output è composto da tutti gli studenti, tranne quelli
                // iscritti in una data che coincide con uno dei due limiti.
                () -> Assertions
                        .assertEquals(finalNotInclusiveSubsCounter,
                                courseManager.getSubscriptionsByDate(from, to, false).size())
        );
    }
}
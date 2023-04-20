package console;

import entities.CourseManager;
import entities.Student;

import java.time.LocalDate;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        CourseManager courseManager = new CourseManager("ITSS", LocalDate.now());

        courseManager.addNewCourseAttender(
                new Student("Giacomo", "Detomaso", "XYC"),
                LocalDate.parse("2023-04-02")
        );

        courseManager.addNewCourseAttender(
                new Student("Marco", "Bari", "XYZ"),
                LocalDate.parse("2023-04-02")
        );

        courseManager.addNewCourseAttender(
                new Student("Giulio", "Bari", "XYK"),
                LocalDate.parse("2023-04-02")
        );

        courseManager.addNewCourseAttender(
                new Student("Novax", "Djokovic", "XYP"),
                LocalDate.parse("2023-04-02")
        );

        courseManager.addNewCourseAttender(
                new Student("Angelo", "Valentino", "XYL"),
                LocalDate.parse("2023-04-02")
        );

        courseManager.assignMarkToStudent(30, "XYC");
        courseManager.assignMarkToStudent(30, "XYZ");
        courseManager.assignMarkToStudent(19, "XYL");
        courseManager.assignMarkToStudent(22, "XYK");
        courseManager.assignMarkToStudent(18, "XYP");

        courseManager.getStudentsWithHigherMark();
        System.out.print(courseManager.countMarksInInclusiveRange(18, 21));
    }
}
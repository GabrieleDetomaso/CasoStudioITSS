package entities;

import java.time.LocalDate;
import java.util.Objects;

public class CourseSubscription implements Comparable<CourseSubscription> {
    private Student student;
    private LocalDate subDate; // it will have some constraint
    private int mark;

    CourseSubscription(Student student, LocalDate subDate) {
        this.student = student;
        this.subDate = subDate;
        mark = -1;
    }

    /**
     * Assign the mark to this subscription
     *
     * @param mark the mark value that is in the range [18, 30]
     * */
    public void setMark(int mark) {
        if (mark >= 18 && mark <= 31)
            this.mark = mark;
        else
            throw new IllegalArgumentException("Mark value must be in range [18, 31]");
    }

    /**
     * @return return -1 if the mark is not set
     * */
    public int getMark() {
        return mark;
    }

    public Student getStudent() {
        return student;
    }

    public LocalDate getSubDate() {
        return subDate;
    }

    @Override
    public int compareTo(CourseSubscription o) {
        return getStudent().getMat().compareTo(o.getStudent().getMat());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseSubscription that = (CourseSubscription) o;
        return Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student);
    }
}

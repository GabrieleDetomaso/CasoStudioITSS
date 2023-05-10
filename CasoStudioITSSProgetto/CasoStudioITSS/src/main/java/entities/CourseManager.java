package entities;

import exceptions.CourseEmptyException;
import exceptions.NullStudentException;
import exceptions.RangeDateException;
import org.w3c.dom.ranges.Range;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.*;

public class CourseManager {
    private String courseName;
    private Set<CourseSubscription> subscriptions;
    private LocalDate endSubDate;

    /**
     * @param courseName the course name
     * @param limitDate  the date where subscriptions to the course ends
     */
    public CourseManager(String courseName, LocalDate limitDate) {
        this.courseName = courseName;
        this.endSubDate = limitDate;

        subscriptions = new LinkedHashSet<>();
    }

    /**
     * Add a new student to the course
     *
     * @param student the student to be added
     * @param subDate the date of subscription
     * @return true if the attender is added to the course, false otherwise
     */
    public boolean addNewCourseAttender(Student student, LocalDate subDate) throws NullStudentException {
        if (subDate.isAfter(endSubDate))
            throw new DateTimeException("The subscriptions are ended");

        if (student == null)
            throw new NullStudentException("Lo studente è nullo");

        return subscriptions.add(new CourseSubscription(student, subDate));
    }

    /**
     * This method is used to assign a mark to a specific student
     *
     * @param mark the mark to assign
     * @param mat  the matriculation number of the student
     */
    public void assignMarkToStudent(int mark, String mat) {
        CourseSubscription courseSubscription = getSpecificSubscription(mat);
        courseSubscription.setMark(mark);
    }

    /**
     * This method is used to calculate the number of student whose mark is above the average of the course.
     *
     * @param inclusive determines if the students with the mark equals to the average are counted
     * @return the number of students  whose mark is above the average of the course
     */
    public int studentsAboveAverage(boolean inclusive) {
        double avg = 0;
        double sum = 0;
        int hasMark = 0;
        int nStudent = 0;

        for (CourseSubscription courseSubscription : subscriptions) {
            if (courseSubscription.getMark() != -1) {
                hasMark++;
                sum += courseSubscription.getMark();
            }
        }

        if (hasMark != 0) {
            avg = sum / hasMark;
        }

        for (CourseSubscription courseSubscription : subscriptions) {
            int mark = courseSubscription.getMark();

            if ((inclusive && mark >= avg) || (!inclusive && mark > avg)) {
                nStudent++;
            }

        }

        return nStudent;
    }

    /**
     * This method is used to retrieve a specific student from the course,
     * given his matriculation number.
     *
     * @param mat the matriculation number
     * @return the student
     */
    public Student getSpecificStudent(String mat) {
        return getSpecificSubscription(mat).getStudent();
    }

    /**
     * Returns a course subscription by a specific matriculation number.
     * If the matriculation number does not exist null is returned.
     *
     * @param mat the matriculation number of the course to search
     */
    public CourseSubscription getSpecificSubscription(String mat) {
        TreeSet<CourseSubscription> orderedSetByMat = new TreeSet<>(subscriptions);
        List<CourseSubscription> allMat = new ArrayList<>(orderedSetByMat);

        // Perform a binary search
        int low = 0;
        int high = allMat.size() - 1;
        int middle = (high + low) / 2;

        boolean flag = false;

        CourseSubscription courseSubscription = null;

        while (low <= high) {
            String currentMat = allMat.get(middle).getStudent().getMat();
            if (currentMat.equals(mat)) {
                courseSubscription = allMat.get(middle);
                flag = true;
            }
            if (currentMat.compareTo(mat) < 0) {
                low = middle + 1;
            }
            if (currentMat.compareTo(mat) > 0) {
                high = middle - 1;
            }

            if (flag)
                break;

            middle = (low + high) / 2;
        }

        return courseSubscription;
    }

    /**
     * Returns the students with the higher mark
     *
     * @return the student with the higher mark
     */
    public Set<Student> getStudentsWithHigherMark() {
        LinkedHashSet<Student> higherMarkStudents = new LinkedHashSet<>();

        TreeSet<CourseSubscription> orderedSet = new TreeSet<>(Comparator
                .comparingInt(CourseSubscription::getMark)
                .thenComparing(o -> o.getStudent().getMat()));
        orderedSet.addAll(subscriptions);

        orderedSet = (TreeSet<CourseSubscription>) orderedSet.descendingSet();

        int higherMark = -1; // inserimento di un valore a caso < 0

        if (orderedSet.size() > 0) {
            higherMark = orderedSet.first().getMark();

            if (higherMark == -1) {
                return higherMarkStudents;
            }
        }

        for (CourseSubscription courseSubscription : orderedSet) {
            if (courseSubscription.getMark() == higherMark) {
                higherMarkStudents.add(courseSubscription.getStudent());
            } else {
                break;
            }
        }

        return higherMarkStudents;
    }

    /**
     * Count the number of marks inside the input range. The upper
     * and lower bound are included. Input parameter must be >= 18 and <= 30.
     *
     * @param from lower bound mark
     * @param to   upper bound mark
     * @return the number of marks inside the input range
     */
    public int countMarksInInclusiveRange(int from, int to) throws CourseEmptyException {
        if (from < 18 || from > 30)
            throw new IllegalArgumentException("From parameter should be in range [18, 30]");

        if (to < 18 || to > 30)
            throw new IllegalArgumentException("To parameter should be in range [18, 30]");

        if (from > to)
            throw new IllegalArgumentException("from is greater than to");

        if (subscriptions.size() == 0)
            throw new CourseEmptyException();

        int count = 0;

        for (CourseSubscription courseSubscription : subscriptions) {
            if (courseSubscription.getMark() >= from && courseSubscription.getMark() <= to) {
                ++count;
            }
        }

        return count;
    }

    /**
     * Retrieve a set of CourseSubscription whose subscription date is
     * inside a specified date range. If specified, the subscription dates that
     * match the lower or upper bound of the range are not excluded from the
     * output set.
     *
     * @param fromDate  the lower bound of the range
     * @param toDate    the upper bound of the range
     * @param inclusive true if the upper and lower bound of the range are included, false otherwise
     * @return a set of CourseSubscription inside a specified date range
     */
    public Set<CourseSubscription> getSubscriptionsByDate(LocalDate fromDate, LocalDate toDate, boolean inclusive)
            throws RangeDateException, CourseEmptyException {
        Set<CourseSubscription> subsInRange = new LinkedHashSet<>();
        long fromDateLong = fromDate.getLong(ChronoField.EPOCH_DAY);
        long toDateLong = toDate.getLong(ChronoField.EPOCH_DAY);

        // Check the dates
        if (fromDate.isAfter(toDate)) {
            throw new RangeDateException("fromDate is greater then to date");
        }

        if (!inclusive && (fromDate.equals(toDate))) {
            throw new RangeDateException("fromDate is equal to toDate but inclusive is false. They cannot be equals");
        }

        // Check the course number
        if (subscriptions.isEmpty()) {
            throw new CourseEmptyException("The course is empty");
        }

        for (CourseSubscription courseSubscription : subscriptions) {
            LocalDate subDate = courseSubscription.getSubDate();
            long subDateLong = subDate.getLong(ChronoField.EPOCH_DAY);

            // True if the subDate belongs to the range (bounds excluded)
            boolean condition = subDateLong > fromDateLong && subDateLong < toDateLong;

            // True if inclusive param is true and the subDate is equals to
            // the lower or the upper bound of the interval
            boolean inclusiveCondition = inclusive && (subDateLong == fromDateLong || subDateLong == toDateLong);

            // If the inclusive parameter is set to true but the subDate is in the range (not equals
            // to one of the two bounds) inclusiveCondition will be evaluated as false,
            // but condition is evaluated to true. The behavior is identical to have inclusive set to false.
            if (condition || inclusiveCondition) {
                subsInRange.add(courseSubscription);
            }
        }

        return subsInRange;
    }

    public void deleteCourseStudents() {
        subscriptions.clear();
    }
}

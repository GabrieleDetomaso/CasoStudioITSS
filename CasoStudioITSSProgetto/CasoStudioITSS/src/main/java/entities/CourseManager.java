package entities;

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
     * @param limitDate the date where subscriptions to the course ends
     * */
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
     *
     * @return true if the attender is added to the course, false otherwise
     * */
    public boolean addNewCourseAttender(Student student, LocalDate subDate) {
        if (subDate.isAfter(endSubDate))
            throw new DateTimeException("The subscriptions are ended");

        return subscriptions.add(new CourseSubscription(student, subDate));
    }

    /**
     * This method is used to assign a mark to a specific student
     *
     * @param mark the mark to assign
     * @param mat the matriculation number of the student
     * */
    public void assignMarkToStudent(int mark, String mat) {
        CourseSubscription courseSubscription = getSpecificSubscription(mat);
        courseSubscription.setMark(mark);
    }

    /**
     * This method is used to retrieve a specific student from the course,
     * given his matriculation number.
     *
     * @param mat the matriculation number
     *
     * @return the student
     * */
    public Student getSpecificStudent(String mat) {
        return getSpecificSubscription(mat).getStudent();
    }

    /**
     * Returns a course subscription by a specific matriculation number.
     * If the matriculation number does not exist null is returned.
     *
     * @param mat the matriculation number of the course to search
     * */
    private CourseSubscription getSpecificSubscription(String mat) {
        TreeSet<CourseSubscription> orderedSetByMat = new TreeSet<>(subscriptions);
        List<CourseSubscription> allMat = new ArrayList<>(orderedSetByMat);

        // Perform a binary search
        int low = 0;
        int high = allMat.size() - 1;
        int middle = (high + low) / 2;

        while (low <= high) {
            String currentMat = allMat.get(middle).getStudent().getMat();
            if (currentMat.equals(mat)) {
                return allMat.get(middle);
            } else if (currentMat.compareTo(mat) < 0) {
                low = middle + 1;
            } else if (currentMat.compareTo(mat) > 0) {
                high = middle - 1;
            }

            middle = (low + high) / 2;
        }

        return null;
    }

    /**
     * Returns the students with the higher mark
     *
     * @return the student with the higher mark
     * */
    public Set<Student> getStudentsWithHigherMark() {
        LinkedHashSet<Student> higherMarkStudents = new LinkedHashSet<>();

        TreeSet<CourseSubscription> orderedSet = new TreeSet<>(Comparator
                .comparingInt(CourseSubscription::getMark)
                .thenComparing(o -> o.getStudent().getMat()));
        orderedSet.addAll(subscriptions);

        orderedSet = (TreeSet<CourseSubscription>) orderedSet.descendingSet();

        int higherMark = orderedSet.first().getMark();

        for (CourseSubscription courseSubscription : orderedSet) {
            if (courseSubscription.getMark() == higherMark) {
                higherMarkStudents.add(courseSubscription.getStudent());
                System.out.println(courseSubscription.getStudent().getMat());
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
     * @param to upper bound mark
     *
     * @return the number of marks inside the input range
     */
    public int countMarksInInclusiveRange(int from, int to) {
        if (from < 18 || from > 30)
            throw new IllegalArgumentException("From parameter should be in range [18, 30]");

        if (to < 18 || to > 30)
            throw new IllegalArgumentException("To parameter should be in range [18, 30]");

        int count = 0;

        for (CourseSubscription courseSubscription : subscriptions) {
            if (courseSubscription.getMark() > from && courseSubscription.getMark() < to) {
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
     * @param fromDate the lower bound of the range
     * @param toDate the upper bound of the range
     * @param inclusive true if the upper and lower bound of the range are included, false otherwise
     *
     * @return a set of CourseSubscription inside a specified date range
     * */
    public Set<CourseSubscription> getSubscriptionsByDate(LocalDate fromDate, LocalDate toDate, boolean inclusive) {
        Set<CourseSubscription> subsInRange = new LinkedHashSet<>();
        long fromDateLong = fromDate.getLong(ChronoField.EPOCH_DAY);
        long toDateLong = toDate.get(ChronoField.EPOCH_DAY);

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
}

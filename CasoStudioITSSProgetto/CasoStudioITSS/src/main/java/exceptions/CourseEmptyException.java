package exceptions;

public class CourseEmptyException extends Exception {
    public CourseEmptyException() {

    }

    public CourseEmptyException(String message) {
        super(message);
    }
}

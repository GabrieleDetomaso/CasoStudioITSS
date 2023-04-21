package exceptions;

public class NullStudentException extends Exception{
    public NullStudentException() {
    }

    public NullStudentException(String message) {
        super(message);
    }
}

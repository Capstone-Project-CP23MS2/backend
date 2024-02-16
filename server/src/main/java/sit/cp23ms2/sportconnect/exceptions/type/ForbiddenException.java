package sit.cp23ms2.sportconnect.exceptions.type;

public class ForbiddenException extends Exception{
    public ForbiddenException() {
        super("Forbidden");
    }

    public ForbiddenException(String message) {
        super("Forbidden: " + message);
    }
}

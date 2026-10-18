package inhabit;

// Custom exception thrown when complaint validation fails
public class InvalidComplaintException extends Exception {
    public InvalidComplaintException(String msg) {
        super(msg);
    }
}

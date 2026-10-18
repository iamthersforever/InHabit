package inhabit;

// Custom exception for invalid login or access errors (OOP concept: Exception Handling)
public class AuthenticationException extends Exception {
    public AuthenticationException(String msg) {
        super(msg);
    }
}

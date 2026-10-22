package inhabit;

// Custom exception thrown when duplicate voting or self-voting occurs
public class InvalidVoteException extends Exception {
    public InvalidVoteException(String msg) {
        super(msg);
    }
}

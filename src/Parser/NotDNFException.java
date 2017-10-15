package Parser;
import java.io.*;

public class NotDNFException extends RuntimeException{
	public NotDNFException() {
        super();
    }
    public NotDNFException(String s) {
        super(s);
    }
    public NotDNFException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public NotDNFException(Throwable throwable) {
        super(throwable);
    }
}

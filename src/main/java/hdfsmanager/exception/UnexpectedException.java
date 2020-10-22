package hdfsmanager.exception;

public class UnexpectedException extends RuntimeException {
    public UnexpectedException()            {super();}
    public UnexpectedException(String msg)  {super(msg);}
    public UnexpectedException(Throwable e) {super(e);}
}

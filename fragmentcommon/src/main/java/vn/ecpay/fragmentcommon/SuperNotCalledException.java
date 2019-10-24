package vn.ecpay.fragmentcommon;

public class SuperNotCalledException extends RuntimeException {

    public SuperNotCalledException() {
    }

    public SuperNotCalledException(String message) {
        super(message);
    }

    public SuperNotCalledException(String message, Throwable cause) {
        super(message, cause);
    }

    public SuperNotCalledException(Throwable cause) {
        super(cause);
    }

    public SuperNotCalledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

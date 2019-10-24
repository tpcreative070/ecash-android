package vn.ecpay.fragmentcommon.util;

public abstract class BaseValidationRule<T> {
    private int errorCode = 0;

    public int getErrorCode() {
        return errorCode;
    }

    public abstract boolean isValid(T input);
}

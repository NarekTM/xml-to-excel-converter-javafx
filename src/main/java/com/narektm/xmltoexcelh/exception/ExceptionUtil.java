package com.narektm.xmltoexcelh.exception;

public class ExceptionUtil {

    public static String getRootCauseMessage(Throwable throwable) {
        Throwable rootCause = getRootCause(throwable);
        return rootCause.getMessage();
    }

    private static Throwable getRootCause(Throwable throwable) {
        if (throwable.getCause() == null) {
            return throwable;
        } else {
            return getRootCause(throwable.getCause());
        }
    }
}

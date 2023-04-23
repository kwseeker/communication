package top.kwseeker.rpc.client.exception;

public class ClientException extends Exception {

    private static final long serialVersionUID = 2110984421259775841L;

    protected int errorCode;
    protected String message;
    protected Object errorObject;
    protected ErrorType errorType = ErrorType.GENERAL;

    public ClientException(String message) {
        this(0, message, null);
    }

    public ClientException(int errorCode) {
        this(errorCode, null, null);
    }

    public ClientException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public ClientException(Throwable chainedException) {
        this(0, null, chainedException);
    }

    public ClientException(String message, Throwable chainedException) {
        this(0, message, chainedException);
    }

    public ClientException(int errorCode, String message, Throwable chainedException) {
        super((message == null && errorCode != 0) ? ", code=" + errorCode + "->" + ErrorType.getName(errorCode): message,
                chainedException);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ClientException(ErrorType error) {
        this(error.ordinal(), null, null);
        this.errorType = error;
    }

    public ClientException(ErrorType error, String message) {
        this(error.ordinal(), message, null);
        this.errorType = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(Object errorObject) {
        this.errorObject = errorObject;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public ClientException(ErrorType error, String message, Throwable chainedException) {
        super((message == null && error.ordinal() != 0) ? ", code=" + error.ordinal() + "->" + error.name() : message,
                chainedException);
        this.errorCode = error.ordinal();
        this.message = message;
        this.errorType = error;
    }

    public enum ErrorType {
        GENERAL,
        NO_VALID_SERVER_EXCEPTION;

        private static final ErrorType[] ERROR_TYPE_VALUES = values();

        static String getName(int errorCode) {
            if (ERROR_TYPE_VALUES.length >= errorCode) {
                return ERROR_TYPE_VALUES[errorCode].name();
            } else {
                return "UNKNOWN ERROR CODE";
            }
        }
    }
}

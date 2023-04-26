package top.kwseeker.rpc.server.exception;

public class RPCServerException extends RuntimeException {

    public RPCServerException(String message) {
        super(message);
    }

    public RPCServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
